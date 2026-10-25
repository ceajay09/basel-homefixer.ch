> **Aktuelles Betriebsziel:** neue Datenbank basel_homefixer, noch nicht angelegt. DEMO gehört zur Demo-Website und bleibt unverändert; preview bleibt Test. Frühere DEMO-Angaben unten sind historischer Istzustand, keine aktuelle Zielvorgabe. Siehe [Datenbankentscheidung](../deploy/nas/DATENBANK.md) und [Portainer](../deploy/nas/PORTAINER.md).

# Anmeldung für die spätere Inhaltsverwaltung

Stand: 24. Oktober 2026. Dieser Abschnitt dokumentiert die damalige Authentifizierungsgrundlage. Inzwischen sind geschützte Inhalts-Endpunkte und die lokale Frontend-Verwaltung unter /admin ergänzt; siehe [ADMIN.md](ADMIN.md). Keine produktiven Konten wurden angelegt, migriert oder verändert. NAS, laufende Vorschau, öffentliche Stacks und Workflows bleiben auf ihrem bisherigen Stand.

## Konzept und bewusst gesetzte Grenzen

Bestehende MongoDB-Collection `account`, IDs und das serverseitige Feld `admin` werden weiterverwendet. Neue oder ausdrücklich zurückgesetzte Zugangsdaten bestehen aus einem BCrypt-Hash (Kostenfaktor 12) und einer zufälligen `authRevision`. Ohne diese gültige Revision und einen unterstützten Hash ist keine Anmeldung möglich. Ein vorhandenes Klartextkonto oder ein alter Hash wird nicht automatisch übernommen, gehasht oder zum Administrator gemacht.

Nur der lokale Einrichtungsweg kann einen neuen Administrator anlegen. Öffentliche Registrierung ist standardmässig deaktiviert. Falls später bewusst aktiviert, erzeugt sie ausschliesslich normale Konten; Client-Felder wie `admin`, `roles` oder `authRevision` werden nicht übernommen. Bestehende Konto-IDs/Profile bleiben beim expliziten Passwortreset erhalten; ein Reset verändert keine Berechtigung.

Der vorhandene JWT-Ansatz verwendet jetzt einen gemeinsamen `TokenService` für Ausstellung und Prüfung. `TokenUtil` delegiert dorthin statt einen zweiten Schlüssel zu erzeugen. Es gibt keinen externen Anmeldedienst, keine Cookies, kein HTTP Basic, keine Refresh-Tokens und keine neue Kontoverwaltung.

## Serverkonfiguration

Siehe `auth.env.example`, ohne Geheimnisse:

| Variable | Bedeutung |
| --- | --- |
| `AUTH_JWT_SECRET_BASE64` | Base64-kodierte 32 kryptografisch zufällige Bytes, einmal erzeugen und dauerhaft serverseitig verwahren |
| `AUTH_TOKEN_TTL_SECONDS` | 60–3600 Sekunden; Standard 900 = 15 Minuten |
| `AUTH_PUBLIC_REGISTRATION_ENABLED` | Standard false; nur bewusst aktivieren, wenn normale Websitekonten überhaupt gebraucht werden |
| `AUTH_SETUP_MONGO_URI` | Ausschliesslich für den expliziten lokalen Einrichtungs-/Reset-Weg, nicht für das Frontend |

Ohne Signaturschlüssel bleibt das öffentliche Frontend mit Inhalts-Lese-API und Kontakt funktionsfähig; Anmeldung meldet 503 und geschützte Zugriffe sind nicht möglich. Ein gesetzter, ungültig kodierter oder zu kurzer/langer Schlüssel führt zu einem klaren Konfigurationsfehler beim Start. Es gibt keinen Standard- oder Zufallsschlüssel als Fallback.

Einen Schlüssel nur bei der bewussten Einrichtung erzeugen, nicht bei jedem Anwendungsstart. Beispiel für eine PowerShell-Sitzung (gibt den Schlüssel nicht aus):

```powershell
$authBytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Fill($authBytes)
$env:AUTH_JWT_SECRET_BASE64 = [Convert]::ToBase64String($authBytes)
[Array]::Clear($authBytes, 0, $authBytes.Length)
```

Danach den Wert über eine geeignete private Serverkonfiguration dauerhaft bereitstellen. Eine nur in dieser Shell gesetzte Variable verschwindet beim Schliessen der Shell. Kein Commit, keine VITE_-Variable, kein Versand im Chat, keine Schlüssel in URLs/Logs oder öffentlich abgelegten Dateien. Bei einem Schlüsselwechsel werden bisherige Tokens ungültig. Testschlüssel aus Tests sind ausschliesslich Testfixtures und keine Einrichtungsvorgabe.

## API für den nächsten Frontend-Auftrag

| Methode/Pfad | Verhalten |
| --- | --- |
| POST `/api/login` | JSON `{email,password}`; bei Erfolg Token, `tokenType=Bearer`, `expiresIn`, `expiresAt` und minimales eigenes Kontoprofil |
| POST `/api/register` | Standard 403 `registration_disabled`; optional erlaubte Registrierung mit gehashtem Passwort und `admin=false` |
| GET `/api/dashboard` | Eigenes minimales Profil, nur mit gültigem Bearer-Token |
| GET `/api/account/{id}` | Nur das eigene Konto; fremde IDs auch für Administratoren hier nicht zugänglich |
| POST `/api/logout` | Geschützt; bestätigt ausschliesslich lokale Abmeldung, keine serverseitige Token-Widerrufsliste |
| `/api/admin/**` | Vor der alten `/api/**`-Freigabe ausdrücklich auf aktuelle serverseitige ADMIN-Berechtigung beschränkt; noch keine produktiven Handler |

Antworten sind typisiertes gültiges JSON. Kontoantworten enthalten ID, E-Mail, Vor-/Nachname und aktuelles Verwaltungsrecht; keine Hashes, Revisionen, Telefonnummern oder Firmenangaben. Die Account-Klasse selbst blendet Passwort und Revision zusätzlich bei JSON-Serialisierung aus. Das alte Konto-Dumping beim Backendstart wurde entfernt. Die öffentliche Registrierung verlangt keine Verwaltungsdaten und akzeptiert solche Felder nicht als Berechtigung.

Fehler haben stabile Codes, die die kommende DE-/EN-Oberfläche übersetzen kann: `invalid_request`, `invalid_password`, `invalid_credentials`, `unauthorized`, `forbidden`, `rate_limited`, `registration_disabled`, `authentication_unavailable` und `registration_unavailable`. Falsche, unbekannte und noch nicht zurückgesetzte Konten erhalten dieselbe neutrale Anmeldeantwort. Bestehende und neu angenommene Registrierungsanfragen erhalten dieselbe 202-Antwort, ohne zu behaupten, dass bereits ein neues Konto angelegt wurde.

Login-/Kontodaten und Fehlerantworten verwenden `Cache-Control: no-store`. Passwort-/Token-DTOs besitzen redigierte `toString`-Darstellungen. HTTP-Body-/Authorization-Logging in Reverse-Proxy oder TRACE-Diagnose trotzdem nicht aktivieren; das echte Login muss sein Token bestimmungsgemäss über die HTTPS-Antwort übertragen.

## Token-Vertrag und Anmeldung im künftigen Frontend

Das Token wird nur mit `Authorization: Bearer <token>` an geschützte API-Routen gesendet. Keine Tokenparameter in Bild-URLs. CORS erlaubt die bekannte Produktionsdomain und lokale Ports 5173/5180 für localhost und 127.0.0.1; Authorization/Content-Type sind gezielt freigegeben, Cookie-Credentials nicht.

Empfehlung/Vertrag für die kommende Verwaltung: Token ausschliesslich im Arbeitsspeicher halten, nicht in localStorage/sessionStorage oder URLs. Ein Neuladen/Schliessen des Tabs erfordert erneute Anmeldung. Nach 401/Tokenablauf Token entfernen und zur Anmeldung führen; 403 als fehlende Berechtigung behandeln. Bei Abmeldung das Token unabhängig vom Erfolg des Logout-Aufrufs lokal löschen. Noch kein Frontend-Code umgesetzt.

JWT: HS256, fester Issuer `basel-homefixer.ch`, Audience `website-api`, Konto-ID als Subject, `iat`, `exp` und Revision. Keine Passwort-, E-Mail- oder Rollen-Claims erforderlich. Signatur, Algorithmus, Issuer, Audience und Zeitgrenzen werden geprüft. Fehlendes Ablaufdatum wird abgewiesen. Der Schlüssel ist über Neustarts stabil, wenn dieselbe Serverkonfiguration bereitgestellt wird.

Bei jeder geschützten Anfrage wird das aktuelle Konto aus MongoDB geladen. Revision muss übereinstimmen; ADMIN stammt ausschliesslich aus dem aktuellen Datenbankfeld, nicht aus einem Rollen-Claim. Passwortreset invalidiert alte Tokens durch neue Revision; Rechteentzug und Kontolöschung wirken beim nächsten geschützten Abruf. Bei Datenbankausfall wird kein Verwaltungszugriff aus einem ungeprüften Token freigegeben.

**Abmeldegrenze:** Ein kopiertes Token bleibt nach lokaler Abmeldung bis zum Ablauf gültig. Es gibt keine per-Token-Widerrufsliste. Standardfenster maximal 15 Minuten. Für sofortige vollständige Sperrung muss die Revision bewusst geändert bzw. der Zugang entzogen werden. Wenn spätere Anforderungen sofortigen Token-Widerruf verlangen, ist dieser separat umzusetzen.

CSRF ist für diesen headerbasierten, zustandslosen Weg deaktiviert; es gibt keine automatisch vom Browser mitgeschickte Anmeldecookie-/Basic-Authentifizierung. Eine spätere Umstellung auf Cookies braucht eine neue CSRF-Entscheidung und entsprechende Tests.

## Anmeldebegrenzung

Pro Backendprozess innerhalb von 15 Minuten: höchstens fünf Versuche pro normalisiertem Konto, zwanzig pro Peer-IP und hundert insgesamt. Erfolgreiche Versuche zählen mit; keine einfache Rücksetzung durch Wechseln von IP oder Konto. Bei Sperre 429 mit `Retry-After`.

Es werden begrenzte Speicherstrukturen verwendet; Kontoschlüssel werden als SHA-256 statt Klartext-E-Mail gehalten. Die Begrenzung ist nicht zwischen mehreren Instanzen geteilt und wird bei Prozessneustart zurückgesetzt. Sie ist kein Ersatz für einen vorgeschalteten Missbrauchsschutz bei späterem grösserem Betrieb.

Ungeprüfte `X-Forwarded-For`-Angaben werden nicht verwendet. Hinter einem Proxy kann die Peer-IP für mehrere Benutzer gleich sein und der gemeinsame Grenzwert früher greifen. Vor Betrieb vertrauenswürdige Proxy-/Forwarding-Konfiguration gesondert prüfen; nicht pauschal beliebige Client-Header vertrauen. Die kleine Verwaltung mit wenigen Benutzern braucht dafür keinen neuen Authentifizierungsdienst.

## Ersten Verwaltungszugang bewusst vorbereiten

Voraussetzungen: JDK 17, Maven-Abhängigkeiten, normales interaktives Terminal und ausdrücklich ausgewählte Datenbank. Es gibt keinen öffentlichen Bootstrap-Endpunkt und keinen automatisch beim Start erzeugten Administrator.

Aus `My-App_Backend` zunächst Dry-Run, ohne Datenbankverbindung oder Passwortabfrage:

```powershell
.\setup-admin.ps1 -Email admin@example.com -Database basel_auth_test
```

Der Dry-Run prüft Optionen und E-Mail-Format. Er prüft keine vorhandenen Konten oder Datenbankkonflikte. Platzhalteradresse durch die beabsichtigte Adresse ersetzen.

Ein tatsächliches Anlegen wäre erst nach bewusstem Auftrag, gesichtetem Ziel und Sicherung in einem interaktiven Terminal möglich:

```powershell
# AUTH_SETUP_MONGO_URI ausschliesslich lokal/serverseitig bereitstellen.
.\setup-admin.ps1 -Email admin@example.com -Database basel_auth_test `
  -Apply -ConfirmDatabase basel_auth_test
```

Das Java-Werkzeug fragt das neue Passwort zweimal verdeckt am Terminal ab. Keine Passwortargumente, keine Umleitung in Dateien/Pipelines und keine Passwörter über Chat. Mindestens zwölf Zeichen, höchstens 72 UTF-8-Bytes; führende/nachlaufende Zeichen im Passwort werden nicht stillschweigend abgeschnitten. Unicode-Passwörter können die Bytegrenze vor der Zeichengrenze erreichen. Kein eingebautes Standardpasswort.

`create-admin` legt nur ein neues Konto an. Existiert diese E-Mail bereits, wird weder überschrieben noch ein bestehender normaler Benutzer hochgestuft. Der lokale Ablauf legt nach Prüfung einen eindeutigen, gross-/kleinschreibungsunabhängigen E-Mail-Index an. Vorhandene widersprüchliche E-Mails führen zu einem Abbruch statt zu einer automatischen Reparatur/Löschung. Indexänderung und Kontoänderung sind nicht gemeinsam transaktional; bei späterem Fehler kann der Index bereits angelegt sein.

Für optionale öffentliche Registrierung wird das Vorhandensein eines passenden eindeutigen E-Mail-Index zusätzlich lesend geprüft. Ohne diese Voraussetzung keine Registrierung, damit parallele Anfragen keine doppelten Konten erzeugen. Kein Index wird beim allgemeinen Backendstart automatisch angelegt.

## Umgang mit Altpasswörtern und Wiederherstellung

Altkonten ohne gültige Revision bzw. ohne unterstützten BCrypt-Hash können sich nicht anmelden. Es findet keine automatische Klartextprüfung oder stille Migration statt. IDs und Datensätze bleiben erhalten. Kein neues Konto mit derselben E-Mail zur Umgehung der Prüfung anlegen.

Nach Sicherung, Prüfung der betroffenen Identität und ausdrücklicher Freigabe kann ein bestehendes Konto zurückgesetzt werden:

```powershell
.\setup-admin.ps1 -Mode reset-password -Email person@example.com `
  -Database basel_auth_test -Apply -ConfirmDatabase basel_auth_test
```

Reset setzt einen neuen Hash und eine neue Revision. ID, E-Mail, Profil und bestehende Berechtigung bleiben erhalten. Ein normaler Benutzer wird dadurch kein Administrator. Das Werkzeug prüft alte Zugangsdaten beim Schreiben erneut, damit eine konkurrierende Passwortänderung nicht still überschrieben wird. Uneindeutige E-Mail-Zuordnungen werden abgewiesen. Es gibt keine öffentliche Passwort-vergessen-Funktion in diesem Schritt.

Sicherungen von Konten enthalten schützenswerte Daten, möglicherweise alte Klartextpasswörter. Nur privat und mit beschränktem Zugriff sichern, nie ins Repository oder in normale Diagnoseausgaben kopieren. Rücknahme einer späteren Credential-Migration separat planen; nicht aus einer alten Sicherung ungeprüft Klartextpasswörter wieder aktivieren. Keine tatsächliche Migration oder Einrichtung wurde durch diesen Auftrag ausgeführt.

## Anschluss und Prüfgrenzen

Der nächste Auftrag kann geschützte Inhalts-Endpunkte unter `/api/admin/**` ergänzen. Ein gültiges Token eines normalen Kontos reicht nicht. Die aktuellen Produktivquellen enthalten keine solchen Schreib-Handler; eine Test-Probe existiert ausschliesslich im Testquellbaum.

Die laufende reine NAS-Inhaltsvorschau und deren lokaler Proxy wurden nicht geändert. Sie transportieren weiterhin nur Inhalts-Lesezugriffe und sind kein Anmelde-/Verwaltungszugang. Für die spätere Verwaltungsprüfung ist ein passend konfiguriertes, isoliertes Auth-/Backend nötig.

Tests verwenden isolierte Kontofix­tures, Repository-/Mail-Doubles und echte eingebettete HTTP-Server auf zufälligen lokalen Ports. Keine produktive oder NAS-Datenbank wurde verbunden. Der lokale Einrichtungsweg wurde als Dry-Run und seine Konto-/Reset-Vorbereitung isoliert geprüft; ein echter MongoDB-Apply/Indexaufbau für Konten wurde in diesem Schritt nicht ausgeführt und bleibt vor Betrieb in einer isolierten Datenbank zu prüfen.

Bestehende öffentliche Inhalts-/Entwurfs- und Kontaktprüfungen laufen weiter. Die Anwendung bleibt ohne Signaturschlüssel bei der Anmeldung bewusst nicht verfügbar. Authentifizierungsoberfläche, Inhaltsverwaltung, Upload, produktive Zugangseinrichtung und öffentlicher Rollout sind weitere getrennte Aufträge.

Prüfergebnis dieses Schritts: 57 Backend-Tests einschliesslich echter HTTP-/Security-Prüfungen erfolgreich; Maven `verify` samt JAR-Build erfolgreich. Der PowerShell-Einrichtungshelfer wurde im Standard-Dry-Run ausgeführt: keine DB-Verbindung, Passwortabfrage, Index- oder Kontoänderung. Kein Frontend-Produktcode, NAS oder produktiver Bestand geändert. Auch die abschliessend verschärfte Prüfung des Registrierungsindexes (Teilindex und Case-Level) ist im erfolgreichen Verify enthalten.

Konkreter Befund für die spätere Betriebsfreigabe: Das bestehende Maven-Ressourcenmuster nimmt `application-dev.properties` weiterhin in das erzeugte JAR auf. Darin liegt die bisherige lokale Entwicklungs-/Datenbankkonfiguration. Die neue Authentifizierung schreibt keine Signaturschlüssel dorthin; für einen regulären produktiven Build muss diese Entwicklungsressource trotzdem ausgeschlossen und die Konfiguration extern bereitgestellt werden. Die bestehende lokale Startkonfiguration wurde in diesem Authentifizierungsschritt nicht umgebaut. Das hier erzeugte Entwicklungsartefakt wurde nicht verteilt oder deployt. Dies beim vorbereitenden Deployment-Auftrag verbindlich berücksichtigen.

## Nachtrag 25. Oktober 2026: Verwaltungsrouten

Die zuvor als Anschluss bezeichneten Inhalts-/Medienrouten sind jetzt implementiert, weiterhin ausschliesslich unter dem serverseitig geschützten /api/admin/**. Verträge und Prüfgrenzen: [ADMIN.md](ADMIN.md). CORS unterstützt für erlaubte Ursprünge jetzt auch PUT und If-Match sowie die Antwortheader ETag, Location und Retry-After. Keine Cookie-Anmeldung oder öffentliche Vorschautokens. Die laufende NAS-Vorschau wurde nicht aktualisiert; Verwaltungsoberfläche und produktive Zugangseinrichtung bleiben getrennte Aufgaben.


Frontend-Nachtrag 25. Oktober 2026: Der dokumentierte Arbeitsspeicher-/Bearer-Vertrag ist unter /admin umgesetzt. Ablauf bzw. 401 erfordern erneute Anmeldung bei erhaltenen geöffneten Eingaben; Reload/Tab-Schliessen verliert Sitzung und ungespeicherte Eingaben. ETag/If-Match-Konflikte überschreiben nichts automatisch. Keine Token-Speicherung im Browserstorage, keine URL-Tokens, kein serverseitiger Widerruf bei Abmeldung.

## Konkrete reguläre Umgebung (25. Oktober 2026)

NAS-Backend lesend geprüft: Inhalts-/Kontodatenbank DEMO. Zwei getrennte Website-Admins in DEMO.account sind vorgesehen; technische MongoDB-Zugangsdaten sind keine Website-Anmeldung. Konkrete Anleitung ohne variable Datenbankabfrage: `../design/Kundenprojekt_PJA/ADMIN-ZUGANG.md`. Reguläre vorbereitete Konfiguration: `../deploy/nas/README.md`. Keine produktiven Konten angelegt. Alte Backendversion vor Kontoanlage vom öffentlichen Zugriff nehmen; keine automatische Änderung/Hochstufung vorhandener Konten.


## Aktuelle Entscheidung des Betreibers: getrennte reguläre Datenbank

Die frühere Empfehlung, die beobachtete DEMO-Datenbank weiterzuverwenden, ist aufgehoben. DEMO gehört zur Demo-Website und bleibt unberührt. Neues vorgesehenes Ziel ist basel_homefixer; noch nicht angelegt. basel_homefixer_preview bleibt die isolierte Test-/Vorschauumgebung. Cluster und Datenbank unterscheiden: Der endgültige Cluster wird bewusst durch den Betreiber gewählt, kein Demo-exklusiver Cluster wird automatisch übernommen. Details: deploy/nas/DATENBANK.md und Projekt-README. Blog bleibt auf ausdrücklichen Wunsch sichtbar; Projekte sind Referenzfälle, Blog ist redaktioneller Ratgeber. Keine produktive Änderung durch diese Dokumentationskorrektur.

