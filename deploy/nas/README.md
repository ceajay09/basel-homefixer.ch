> **Aktueller CI-Stand:** Bestehende Workflows/Dockerfiles sind nun gezielt angepasst: bereinigtes JAR, Non-root/Nginx, Tests, dev plus sha-Commit-Tag. [Einfacher Ablauf](PIPELINE.md). Frühere Aussagen zu noch unveränderten Workflows und zwingend manueller Digest-Verwaltung sind historisch. Push nur auf basel-homefixer startet noch keine Image-Pipelines; diese veröffentlichen bei Push auf main.

> **Aktueller Einstieg:** [Projekt-README](../../README.md), [Portainer-Schritte](PORTAINER.md), [Datenbankentscheidung](DATENBANK.md). Diese Datei beschreibt die ausführliche Betriebsvariante compose.mvp.yaml; die ähnlich zum bisherigen Stack aufgebaute Webeditor-Vorlage ist compose.portainer.yaml.

# Regulärer NAS-Betrieb – vorbereitet, nicht ausgerollt

Stand: 25. Oktober 2026. Diese Dateien ändern den laufenden Betrieb nicht. Alle folgenden Betriebsbefehle sind **später auszuführende Schritte nach Freigabe**, keine bereits erfolgte Umstellung.

## Bestätigter Ausgangsstand

Lesend geprüft: Portainer-Stack `basel-homefixer_portainer`, Compose 2.20.1, Backend/Frontend/Traefik laufen. Die Compose-Datei liegt aus Sicht von Portainer unter `/data/compose/3/docker-compose.yml`; daraus folgt kein verifizierter Host-Dateipfad. Das laufende alte Backend verweist auf **DEMO**. Dies ist nur der beobachtete Istzustand, nicht das neue Ziel. Gemäss Korrektur des Betreibers gehört DEMO zur Demo-Website und bleibt unberührt. Das vorbereitete Ziel ist die **neue** Datenbank basel_homefixer; diese wurde nicht angelegt. `DB_INSTANCE=mywebapp` ist eine alte Kontextvariable, nicht der Datenbankname. Die private Vorschau `basel_homefixer_preview` bleibt getrennt.

Öffentliches HTTPS antwortet bereits mit gültiger TLS-Verbindung. Ein Cloudflare-Tunnel läuft im Hostnetz; eine passende DSM-Reverse-Proxy-Regel wurde nicht gefunden. Das Zielbild ist:

```text
Browser https://basel-homefixer.ch
  → Cloudflare / bestehender Tunnel
  → NAS 192.168.178.38:8095 / Traefik
      → /api und /api/*: Backend 8081 → Atlas basel_homefixer + privater Medienordner
      → sonst: Frontend 8082, einschliesslich direkter /admin- und /blog/...-URLs
```

**Noch zu bestätigen:** exakte Hostname-/Service-Zuordnung im Cloudflare-Dashboard (vorgesehen `http://192.168.178.38:8095`), tatsächliche Socket-Peer-/Forwarded-Header-Kette und Cache-Regeln. Der Tunnel ist tokenverwaltet; kein Token ist hier hinterlegt. HTTPS am Cloudflare-Rand und HTTP zum lokalen Origin sind mit dem Tunnel vereinbar ([Cloudflare](https://developers.cloudflare.com/tunnel/concepts/routing/)). Kein lokaler SSH-Proxy oder Vorschau-Launcher ist im Regelbetrieb nötig.

`observed-state.json` enthält Container-/Netzwerknamen, private Pfade und die aktuell beobachteten unveränderlichen Image-Digests als Rückkehrreferenz. Vor einem späteren Rollout erneut prüfen; der heutige Stand ist kein dauerhaft aktuelles Backup.

## Dateien und notwendige Änderungen

| Datei | Zweck |
| --- | --- |
| `compose.mvp.yaml` | Ersatzvorlage für den regulären Stack, bestehende Netzwerke/Domain/Ports 8081/8082/8095 |
| `application-production.properties` | Obligatorische externe Spring-Konfiguration, basel_homefixer, keine Entwicklungsdefaults |
| `release.env.example` | Nicht geheime Pfade, geprüfte Image-Digests und freizugebende Proxy-Konfiguration |
| `private.env.example` | Leere Vorlage für technischen MongoDB-Zugang, JWT-Schlüssel, Gmail-App-Passwort |
| `Dockerfile.*`, `nginx.production.conf` | Separate bereinigte Release-Images mit relativer `/api` und SPA-Routing |
| `prepare-release.ps1`, `PrepareJar.java`, `ValidateRelease.java` | Isolierte Build-Kontexte, Ausschluss eingebetteter Entwicklungsdateien, Archivprüfung |
| `compose.verify.yaml`, `nginx.verify.conf`, `verify.private.env.example` | Separater, nicht öffentlicher Integrationstest mit eigener DB und Medienablage |
| `transfer/` | Gesonderte Auswahl, Paketvorbereitung und lesender Zielkonfliktcheck |

Im bestehenden Portainer-Stack müssten Images, Umgebungsvariablen, privater Mount, Konfigurationsmount, Benutzerrechte und Traefik-Labels ersetzt werden. Bestehende externe Netzwerke bleiben erhalten. Die ungenutzte 8444→8443-Veröffentlichung ist im Zielbild entfernt: vor Übernahme bestätigen, dass kein anderer Verbraucher daran hängt. Fehlerhafte alte Middleware-Labels und pauschale private Proxy-IP-Bereiche werden nicht übernommen. AWS-Zugangsdaten gehören zum geschützten Image-Pull-Verfahren auf dem Host, nicht in den Anwendungscontainer.

Neuer privater Zielordner: `/volume1/docker/basel-homefixer/content`, davon Medien unter `media/`. Bisheriger Mount: `/volume1/webdata/basel-homefixer/data/content`. Diesen nicht löschen oder ungeprüft vollständig übernehmen. Eine noch benötigte `homepage.json` gesondert prüfen und in den privaten Zielordner übernehmen; die aktuellen statischen Startseitenbereiche benötigen sie nicht. Keine Freigabe des neuen Ordners über Web Station, SMB-Gastzugriff oder Cloudflare. Bilder werden ausschliesslich von der Medien-API nach Inhaltsprüfung ausgeliefert. Port 8081/8082 und MongoDB werden nicht auf dem Host veröffentlicht.

Zielordner später bewusst anlegen, UID/GID `10001:10001` Schreibzugriff auf den Inhaltsordner geben, anderen Nutzern Zugriff entziehen. Operationsdateien nur für den Betreiber lesbar; Konfigurationsdatei muss zusätzlich im Container für UID 10001 lesbar sein (z.B. Eigentümer root, Gruppe 10001, Modus 640 und geschütztes Elternverzeichnis). Frontend läuft als 101:101. Keine rekursiven Rechteänderungen am bisherigen Webdatenordner.

## Konfiguration ohne Geheimnisse

Privater Operationsordner, z.B. `/volume1/docker/basel-homefixer/ops/releases/mvp-20261025`: Compose, Produktionsproperties, `release.env`, `private.env`, später `transfer/`. Private Dateien mit restriktiven Rechten ausserhalb Repository/Build-Kontext/öffentlichem Webpfad verwahren. Compose-/Portainer-Verwalter können Container-Umgebung lesen; diese Personen müssen entsprechend berechtigt sein.

| Variable | Festlegung |
| --- | --- |
| `BACKEND_IMAGE`, `FRONTEND_IMAGE` | Geprüfte ECR-Images `basel-homefixer-backend`/`basel-homefixer-frontend`, jeweils `@sha256:…`; keine beweglichen `:dev`-Tags |
| `OPS_RELEASE_DIR`, `NAS_CONTENT_ROOT` | Absolute private NAS-Pfade, zuvor gezielt angelegt |
| `MONGODB_URI` | Neuer technischer Atlas-Nutzer mit **readWrite nur auf basel_homefixer**, keine clusterweiten Rechte; URI-Datenbank basel_homefixer |
| `AUTH_JWT_SECRET_BASE64` | Einmalig 32 kryptografisch zufällige Bytes, Base64; geschützt sichern und bei Neustarts beibehalten |
| `EDGE_TRUSTED_IPS` | Exakte bestätigte lokale Tunnel-Peer-Adressen für Traefik, derzeit absichtlich leer/Start gesperrt |
| `CONTACT_TRUSTED_PROXIES` | Exakte vertrauenswürdige Peer-Kette für den Kontaktendpunkt, derzeit leer |
| `CONTACT_MAIL_ENABLED` | Anfangs `false`; Aktivierung und echter Testversand benötigen eigene Freigabe |
| `CONTACT_SMTP_PASSWORD` | Gmail-App-Passwort, nicht normales Google-Passwort; bestehende private lokale Konfiguration nicht in Images kopieren |

Compose setzt serverseitig basel_homefixer, 900 Sekunden Token-Gültigkeit, deaktivierte öffentliche Registrierung, Gmail/587/STARTTLS, maximal 15 MiB je Upload und einen parallelen Upload. Absender/Empfänger sind serverseitig baselhomefixer@gmail.com, Besucheradresse nur Reply-To. Weitere Details: `My-App_Backend/CONTACT.md` bzw. vorhandene Kontakt-Konfigurationsdokumentation und `AUTH.md`, `ADMIN.md`, `CONTENT.md`.

Atlas: `authSource=admin` bezeichnet die Authentifizierungsdatenbank und verleiht keine Adminrolle. Nur notwendige NAS-Ausgangs-IP in der Atlas-Netzfreigabe zulassen. Für Backup und isolierten Test getrennte, passend begrenzte Nutzer verwenden. Bestehende weitreichende Zugangsdaten nicht ungeprüft weiterverwenden. Ein historischer kommentierter Zugangswert liegt in einer alten Entwicklungs-Compose-Datei: nicht in den Release-Kontext übernehmen, bei fortbestehender Gültigkeit vor Regelbetrieb kontrolliert ersetzen.

Cloudflare muss `/api/**` und `/admin*` von öffentlichem Caching ausnehmen. Kein Cache-Everything für geschützte Antworten; Backend-No-Store respektieren. `EDGE_TRUSTED_IPS` erst nach tatsächlicher Verifikation setzen, keine pauschalen 10/8-/172.16/12-/192.168/16-Freigaben. Der Kontaktcode unterstützt derzeit genaue IP-Adressen, keine CIDRs, und läuft die X-Forwarded-For-Kette von rechts ab. Nur die Traefik-IP einzutragen genügt bei zusätzlichem Tunnel-Peer nicht zwingend. Bei leerer Liste bleibt die Ratebegrenzung sicher, kann aber mehrere Besucher gemeinsam begrenzen. Vor öffentlicher Mailaktivierung diese Grenze mit verifizierten Headern auflösen. Keine Tokens, Mailinhalte oder vollständigen IP-Headerketten in Berichte übernehmen ([Traefik](https://doc.traefik.io/traefik/routing/entrypoints/)).

Compose 2.20.1 unterstützt die verwendeten normalen `--env-file`-Dateien; kein neueres `env_file.format: raw`. Werte mit `$` in privaten env-Dateien als literale, einfach gequotete Werte gemäss Compose-Syntax pflegen; URI-Passwörter korrekt URL-kodieren. `docker compose config` mit echten Werten niemals ausgeben, nur `config --quiet` ([Docker](https://docs.docker.com/compose/how-tos/environment-variables/variable-interpolation/)).

## Release erzeugen und isoliert prüfen

Die vorhandenen GitHub Actions bleiben **unverändert**. Sie bauen weiterhin mit den alten Root-Dockerfiles und publizieren `:dev`; die Backend-Ressourcen enthalten lokal Entwicklungsdateien. Dieser Weg ist noch kein Nachweis für die hier vorbereiteten Produktionsimages. Nötige spätere Abweichung: bereinigte Build-Kontexte/Images verwenden, Tests und Typecheck ausführen, relative API sicherstellen und unveränderliche Digests übernehmen. Workflow-Anpassung nur in eigenem Auftrag. Ein vorhandener Test benötigt ein Kundenbild als Testfixture; bei einem neuen CI-Paket gezielt bereitstellen oder durch eine geeignete synthetische Fixture ersetzen, nicht sämtliche Kundenoriginale hochladen.

Im Repository, PowerShell 7 und JDK 17, später bewusst ausführen:

```powershell
.\deploy\nas\prepare-release.ps1 -OutputDirectory C:\Temp\basel-release-NEU
java --source 17 .\deploy\nas\ValidateRelease.java C:\Temp\basel-release-NEU\backend\app.jar
node .\deploy\nas\validate.mjs
node .\deploy\nas\transfer\prepare-transfer.mjs C:\Temp\basel-transfer-NEU
```

Ohne `-SkipBuild` werden Backend-Verify, Typecheck, gezielte echte Frontend-Tests und Build ausgeführt. Neue Ausgabeordner erforderlich, keine automatische Überschreibung. `-SkipBuild` ist nur zum Verpacken bereits geprüfter Artefakte geeignet. Das Werkzeug verändert das ursprüngliche JAR nicht; es entfernt aus der Release-Kopie Entwicklungs-/Vorschaukonfiguration und alte eingebettete Inhaltsdateien. Eine inhaltsleere application.properties bleibt für eine bestehende @PropertySource-Annotation erhalten; keine alten Werte werden kopiert. Externe `SPRING_CONFIG_LOCATION` ist obligatorisch; fehlt die Datei, soll der Start abbrechen ([Spring Boot](https://docs.spring.io/spring-boot/reference/features/external-config.html)).

Auf einem freigegebenen Docker-Buildhost anschliessend Images bauen, nicht den Repository-Stamm als Kontext senden:

```sh
docker build --build-arg RUNTIME_IMAGE='amazoncorretto:17-al2023@sha256:GEPRUEFTER_DIGEST' -t basel-backend:release backend/
docker build --build-arg NGINX_IMAGE='nginx:alpine@sha256:GEPRUEFTER_DIGEST' -t basel-frontend:release frontend/
```

Platzhalter vor Ausführung ersetzen. Passende gepflegte Java-17-/Nginx-Basisversion und linux/amd64 für DS224+ auswählen und pinnen. Paketinstallation/Curl, Nginx-Konfiguration, Non-root und Read-only-Dateisystem müssen mit den tatsächlich gewählten Basisimages getestet werden. Danach nur nach eigener Freigabe in die bestehenden ECR-Repositories übertragen und RepoDigests in `release.env` eintragen. Ein früherer `:dev`-Build ist nicht automatisch dieses geprüfte Release.

Vor Produktion **echten isolierten MongoDB-/NAS-Test nachholen**: `compose.verify.yaml` nutzt ausschliesslich `basel_homefixer_releasecheck`, eigenen DB-Nutzer/Signaturschlüssel und eigenen Medienordner, SMTP aus, keine Traefik-Routen. Konfigurationsdateien im privaten Testoperationsordner hinterlegen. Parserprüfung:

```sh
docker compose --env-file release.env --env-file verify.private.env -f compose.verify.yaml config --quiet
```

Später freigegebener Teststart mit derselben Auswahl plus `up -d`. Standardbindung 127.0.0.1:38082 ist nicht öffentlich erreichbar. Für einen Testbrowser gegebenenfalls bewusst eine NAS-Tailscale-/LAN-Adresse und passende Firewall wählen; keine Cloudflare-Route und keine Freigabe ins Internet. Der bisherige SSH-Zugang erlaubt kein allgemeines TCP-Forwarding; darauf beruht dieser Weg nicht. Testdaten nur in diese isolierte DB importieren; Produktionsdaten/-Schlüssel nicht dafür einsetzen. CRUD, optimistische Konflikte, reale Indizes, zwei Rollen, Upload/EXIF/Dateirechte, geschützte Vorschau, Veröffentlichung/Rücknahme und Dateizugriffe mit echten Containern prüfen. Testveröffentlichung ausschliesslich dort. Volumes/DB später gezielt separat aufräumen, nicht per pauschalem Prune.

## Gesicherte Datenübertragung, keine Vorschaukopie

`transfer/review.json` wählt sechs bestehende Projekt-IDs und drei Kundenartikel aus geprüften Repository-Importdaten aus. `prepare-transfer.mjs` erzeugt ein neues Paket mit sechs benötigten Originalbildern, Prüfsummen und `reviewed.json`: **alle neun Inhalte draft, kein Veröffentlichungsdatum, keine Konten, keine Vorschau-DB-Kopie**. Fakten, Englisch-Übersetzungen und Bildauswahl vor Übertragung sichten. Vorhandene Marketingkennzahlen werden nicht aus der Projektanzahl berechnet. Projektanker/IDs und Reihenfolgeinformationen bleiben erhalten; die spätere Startseitenauswahl lässt sich in der Verwaltung ändern.

Vor einem Apply Zielkollisionen lesend prüfen: `transfer/check-target.js` benötigt `TRANSFER_MANIFEST` als Pfad und `CONTENT_CHECK_MONGO_URI` als private Prozessumgebung. Mit `mongosh --nodb --quiet --file transfer/check-target.js` ausführen; URI nicht als Argument. Anschliessend die Variablen entfernen. Der Check liest nur die ausgewählten IDs/Slugs in basel_homefixer. Bestehende IDs/Slugs führen zu `REVIEW_REQUIRED`; dann nicht einfach überschreiben, sondern Altbestand individuell entscheiden. Der Import-Dry-Run allein verbindet sich nicht mit MongoDB und ersetzt diesen Zielcheck nicht.

Gesichtetes Paket später in `OPS_RELEASE_DIR/transfer` ablegen. Regulärer Import-Dry-Run:

```sh
docker compose --env-file release.env --env-file private.env -f compose.mvp.yaml \
  run --rm --no-deps content-import
```

Erst nach Backup, Zielcheck und eigener Freigabe denselben Werkzeugdienst mit vollständigen Argumenten und zusätzlichem `--apply` starten:

```sh
docker compose --env-file release.env --env-file private.env -f compose.mvp.yaml \
  run --rm --no-deps content-import --manifest /imports/reviewed.json \
  --source-root /imports/assets --media-root /data/content/media \
  --database basel_homefixer --confirm-database basel_homefixer --apply
```

Keine Optionen zum automatischen Publizieren oder Überschreiben bestehender Entwürfe ergänzen. Wiederholungen anhand stabiler IDs sind überprüfbar; bestehende Inhalte nicht blind ersetzen. Ein fehlgeschlagener Import kann bereits Dateien/Dokumente angelegt haben: Zustand und Prüfergebnis sichten, nicht automatisch löschen oder erneut anwenden.

**Reihenfolge zwingend:** Der alte öffentliche Backendstand kennt die neue Entwurfssperre möglicherweise nicht. Deshalb weder neue Entwürfe noch neue Admin-Konten in basel_homefixer anlegen, solange dieser alte Backendstand öffentlich erreichbar ist. Erst Wartungsfenster/Schreibsperre, alter Backendzugriff aus, neuer Backendstand mit geprüfter Entwurfssperre; danach genehmigter Import und Kontoanlage. Import ist keine Publikationsfreigabe. Kundenartikel bleiben Entwürfe. Projekte erst nach eigener Abnahme bewusst veröffentlichen; davor ist eine ehrliche leere Projektliste zu erwarten.

## Zwei Website-Admins einrichten

Dein Atlas-/MongoDB-Zugang ist nur der technische Zugang des Backends. Du und der Kunde erhalten **je ein eigenes Website-Konto in basel_homefixer.account**, mit eigener E-Mail, BCrypt-Passworthash und serverseitiger ADMIN-Berechtigung. Kein Klartextpasswort, kein MongoDB-Konto für den Kunden. Beide haben im MVP dieselben Rechte. Kein Konto wurde in diesem Vorbereitungsschritt angelegt.

Konkrete Dry-Run-/Apply-Befehle und verdeckte Passwortabfrage stehen in `design/Kundenprojekt_PJA/ADMIN-ZUGANG.md`. Kundenvorschlag: baselhomefixer@gmail.com; deine E-Mail wird einmal eingesetzt. Datenbank ist fest basel_homefixer, kein unklarer Datenbank-Read-Host mehr. Vorher auf bereits bestehende Adresse prüfen; keine automatische Hochstufung alter Konten. Keine eingebauten Standardzugänge. Kundenpasswort möglichst direkt vom Kunden setzen lassen; Passwortänderung/Reset läuft derzeit über das geschützte Betreiberwerkzeug, nicht über eine Kundenoberfläche. Token nur im Browserspeicher, 15 Minuten, Abmeldung entfernt ihn lokal; serverseitige Sperrung über Rollen-/Revisionsänderung gemäss AUTH.md.

## Sicherung und Rolloutreihenfolge

1. Release und isolierten Test vollständig abschliessen; Bilder/Fakten und Veröffentlichung gesondert abnehmen. Betreiber bestätigt basel_homefixer, Atlas-Rechte, Cloudflare-Ziel/Cache, Image-Digests, Platz/RAM und Dateirechte. Ein noch gültiger historischer Zugangswert muss kontrolliert ersetzt werden.
2. Wartungsfenster festlegen und sämtliche Schreibwege einschliesslich alter APIs/Verwaltung/Kontaktjobs anhalten. Nicht nur Frontend ausblenden: Backendzugriff tatsächlich sperren. Keine parallelen Inhaltsänderungen während Sicherung und Umschaltung.
3. T0 sichern: vollständige DB basel_homefixer, bisheriger Inhaltsordner, neuer Medienordner falls vorhanden, alte Compose-/env-Dateien, Signaturschlüssel, vorherige Image-Digests und gegebenenfalls Image-Exporte. Cloudflare-Einstellungen dokumentieren, keine Tunnel-Tokens ins Repository. Backup verschlüsselt/zugriffsbeschränkt ausserhalb öffentlicher Ordner und zusätzlich ausserhalb dieses NAS verwahren.
4. Private neue Verzeichnisse/Konfigurationen einrichten, `config --quiet` prüfen. Geprüfte Images bereitstellen. Geprüften neuen Backendstand zuerst in Wartung aktivieren, neue Entwurfs-/Admin-Grenzen kontrollieren. Alte und neue Backendversion dürfen nicht gleichzeitig auf basel_homefixer schreiben.
5. Zielcheck/Dry-Run, ausdrücklich freigegebener Datenimport, zwei interaktive Adminanlagen, interne Sichtung. Keine komplette Vorschau-Datenbank restaurieren. Bei Kollisionen stoppen.
6. Neues Frontend im bestehenden Stack aktivieren; HTTPS/API/Medien/Admin durchgängig prüfen. SMTP zunächst aus. Erst separat freigegebene Projekte veröffentlichen und Startseitenauswahl kontrollieren; Blog bleibt ohne zusätzliche Freigabe draft. Wartung erst nach Freigabe aufheben.
7. Gmail separat aktivieren und einen zuvor freigegebenen echten Test vom NAS aus durchführen; SMTP-Annahme ist keine Zustellgarantie. Dieser Vorbereitungsschritt sendet keine E-Mail.

Beispiel für eine spätere konsistente logische Sicherung bei angehaltenen Schreibern (MongoDB Database Tools erforderlich):

```sh
mongodump --config /PRIVATE/backup.private.yaml --db basel_homefixer --archive /PRIVATE/T0-basel_homefixer.archive.gz --gzip
```

`mongodump.private.example.yaml` ist die leere Vorlage; separates Lesekonto für basel_homefixer, keine URI im Prozessargument. Auch alle alten Collections sichern, nicht nur projects/blogpost/media/content_settings/account. Passwörter/Hashes in Altbeständen machen das Archiv besonders vertraulich. Dateien während derselben Schreibpause sichern, Prüfsummen und Zeitpunkte protokollieren. DB und Medien sind ohne Schreibpause nicht automatisch transaktionskonsistent.

Wiederherstellung zuerst in eine **andere isolierte Datenbank** mit dafür begrenzten Rechten testen:

```sh
mongorestore --config /PRIVATE/restorecheck.private.yaml --archive /PRIVATE/T0-basel_homefixer.archive.gz \
  --gzip --nsInclude 'basel_homefixer.*' --nsFrom 'basel_homefixer.*' --nsTo 'basel_homefixer_restorecheck.*' --stopOnError
```

Nur mit neuer leerer Testdatenbank ausführen; nicht auf basel_homefixer zeigen. Danach Dokumentzahlen, repräsentative Inhalte/Accounts, Indizes, Medienreferenzen und Bildprüfsummen prüfen. Ein erfolgreiches Dump allein beweist keine Wiederherstellbarkeit ([mongodump](https://www.mongodb.com/docs/database-tools/mongodump/), [mongorestore](https://www.mongodb.com/docs/database-tools/mongorestore/)). Produktive Restore-/Drop-Befehle werden hier absichtlich nicht automatisiert.

## Rücknahme

Vorherige Images anhand der beobachteten Digests sichern. **Alte Images nicht einfach in die neue Compose-Vorlage einsetzen**: Benutzer, externe Konfiguration, Entrypoint und Medienpfad unterscheiden sich. Für eine Rückkehr den zusammenpassenden alten Stack, seine geschützten env-Dateien, Image-Digests und benötigten alten Mount wiederherstellen.

Nach Import neuer Entwürfe/Admin-Konten ist ein blosser Rücksprung auf das alte Backend sicherheitskritisch: es kennt die neuen Entwurfs-/Berechtigungsregeln nicht. Bevorzugt Wartung beibehalten und Fehler vorwärts beheben. Ein vollständiger Rücksprung auf T0 benötigt eine gesondert freigegebene, zusammenpassende Rücknahme von DB **und** Dateien; vorher auch den fehlgeschlagenen neuen Zustand sichern.

Nach zwischenzeitlichen Kundenänderungen darf T0 nicht blind restauriert werden: neuere Texte, Uploads und Konten gingen verloren oder Medienreferenzen würden brechen. Dann Schreibsperre, Sicherung des aktuellen Stands, gezielter Abgleich/Übernahme und eigene Entscheidung. Nie automatisch alte Klartextkonten reaktivieren oder neue Dateien löschen. Der stabile neue JWT-Schlüssel gehört zur Sicherung; ein bewusstes Ändern invalidiert Sitzungen und ist kein Neustartstandard.

## Prüfungen nach einem späteren Rollout

- HTTPS und HTTP→HTTPS-Verhalten, direkte Aufrufe von /projects, /blog/:slug, /admin; Frontend/API gleiche Domain, keine lokalen Proxyadressen oder CORS-Umwege. `/api` muss eine API-Antwort liefern, niemals HTML-Index-Fallback.
- Backend-/Frontend-Health intern, keine öffentlichen Actuator-Details, keine direkt öffentlichen App-/DB-Ports oder Dateifreigaben.
- Ohne Token alle /api/admin-Schreib- und Vorschauwege gesperrt; normale Nutzer ohne ADMIN gesperrt. Beide Admins können sich getrennt anmelden; Ablauf, erneute Anmeldung und lokale Abmeldung prüfen.
- Entwürfe fehlen in öffentlichen Listen und direkten Artikel-/Projektzugriffen. Nur von Entwürfen verwendete Bilder öffentlich gesperrt; gemeinsam von veröffentlichten Inhalten verwendete Bilder dürfen erreichbar bleiben. Geschützte Vorschau und Upload funktionieren.
- Projektfilter, Startseitenauswahl/Anker, Blogfilter/Pagination, Medienvarianten/Cache, DE/EN und Mobilansicht. Neue Bilder ohne Frontend-Deployment austauschbar.
- Speicherung/Veröffentlichung/Rücknahme, Slug-/Bearbeitungskonflikte und reale MongoDB-Indizes. Cache bei Rücknahme darf Entwürfe nicht weiter öffentlich ausliefern; Cloudflare-Regeln kontrollieren.
- Kontakt zunächst ehrlich deaktiviert. Erst nach separater Freigabe SMTP, Reply-To, Fehlerzustände und Ratebegrenzung mit verifizierter Proxy-Kette prüfen. Keine Passwörter/Tokens/Mailtexte in Logs.

## Tatsächlich geprüft und noch nicht ausgeführt

Erledigt: lesende Bestandsaufnahme, HTTPS-Abfrage, lokale YAML-/Policyprüfung mit synthetischen Werten, beide Compose-Vorlagen vom tatsächlichen NAS-Compose-Parser 2.20.1 mit `config --quiet` akzeptiert. Separate lokale Release-Kontexte aus bereits geprüften Buildartefakten erstellt, sieben nichtproduktive Archiveinträge entfernt, 305 verbleibende JAR-Einträge/CRC/Startklasse/Boot-Loader geprüft; Boot-Archivhilfe ausführbar. Übertragungspaket: sechs Projekte, drei Artikel, sechs Bildquellen, ausschliesslich draft. Lesender Zielcheck mit isolierten Doubles auf konfliktfreien/konflikthaften Bestand geprüft. Workflow-Prüfsummen unverändert.

Nicht ausgeführt: Docker-Image-Build/Push (lokal kein Docker), echte Container-/Nginx-Startprüfung, echter Atlas-Integrationstest, Backup/Restore, neue Atlas-Nutzer/Rechte, Kontoanlage, produktiver Zielcheck/Import, NAS-Datei-/Stack-/Firewall-/Cloudflare-Änderung, SMTP-Test, Deployment oder Veröffentlichung. Cloudflare-Service-Ziel und genaue Proxy-Peers müssen vor dem Einsatz noch bestätigt werden. Die Testkonfiguration ersetzt den noch ausstehenden echten isolierten NAS-Test nicht.


