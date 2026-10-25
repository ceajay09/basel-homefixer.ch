# Datenbankentscheidung für Basel Home Fixer

Aktuelle Entscheidung des Betreibers: **DEMO gehört zur Demo-Website und wird für diesen MVP nicht verwendet.** Die vorherige Empfehlung war zu stark vom beobachteten Altbetrieb abgeleitet und ist aufgehoben. Keine Datenbank wurde umbenannt, kopiert oder geändert.

| Name | Zweck | Weiteres Vorgehen |
| --- | --- | --- |
| DEMO | Demo-Website / beobachteter alter Stack | Unberührt lassen, keine neuen Konten oder Kundeninhalte dort anlegen |
| basel_homefixer_preview | Separater, bereits eingerichteter Vorschauversuch | Als Testdatenbank erhalten, nicht zur Produktion erklären |
| **basel_homefixer** | Ziel für die reguläre Website | Laut Betreiber angelegt, technischer Nutzer baselhomefixer eingerichtet; Verbindung noch nicht geprüft |
| basel_homefixer_releasecheck | Optionaler separater Integrationstest | Nur isolierte Konten/Inhalte/Medien verwenden |

`preview` war absichtlich so benannt: echte Kundenbilder/Projekttexte wurden dort isoliert für die lokale Vorschau bereitgestellt. Die sechs Projekte wurden für diese private Sichtung als veröffentlicht markiert; Zeitstempel und Status sind deshalb keine Freigabe für die öffentliche Website. Der damalige API-Container ist ein eigener alter Runtime-Stand. Eine komplette Kopie würde diese Testentscheidungen übernehmen.

Empfehlung: neue leere DB **basel_homefixer**, neuer technischer DB-Nutzer nur mit readWrite auf diese DB, gesichteter Import der sechs Projekte und optional drei Blogartikel als Entwürfe. Bilder in einen neuen privaten Produktions-Medienordner importieren; nicht die Vorschauablage überschreiben. Einzelne Inhalte erst nach Sichtung bewusst veröffentlichen. Website-Admins anschliessend getrennt neu anlegen, keine Testkonten übernehmen.

## Cluster ist nicht Datenbank

Im Screenshot steht unter der Verbindung mywebapp die Datenbank DEMO neben basel_homefixer_preview. Ein Datenbankname und ein Atlas-Cluster sind unterschiedliche Ebenen. Ein eigener DB-Name plus eingeschränkter Nutzer trennt Daten und Rechte, aber nicht Rechenleistung, Speicher, Ausfälle oder Sicherung des Clusters.

Wenn der vorhandene Cluster ausschliesslich für die Demo reserviert ist, einen anderen vorgesehenen produktiven Cluster auswählen bzw. separat anlegen. Dieses Dokument beauftragt keinen kostenpflichtigen neuen Cluster. `MONGODB_URI` bleibt ohne festen Clusterhost vorbereitet. Weder den Demo-Cluster noch einen anderen bestehenden Cluster stillschweigend übernehmen. Auf einem ausdrücklich gemeinsam vorgesehenen Cluster wären getrennte Datenbanken mit getrennten DB-Nutzern grundsätzlich möglich ([MongoDB](https://www.mongodb.com/docs/atlas/build-multi-tenant-arch/)).

## Erste Schritte in Atlas

1. Vorgesehenen Cluster auswählen. Nicht die Verbindung mit dem MongoDB-Website-Adminlogin verwechseln.
2. Neue DB basel_homefixer mit leerer Collection projects anlegen; Account-/Medien-/Blog-Collections entstehen beim kontrollierten Einrichten/Import. Kein Klonen von DEMO oder preview.
3. Technischen Nutzer anlegen, Rolle readWrite ausschliesslich auf basel_homefixer; kein readWriteAnyDatabase/admin. NAS-Ausgangsadresse für Zugriff zulassen, keine pauschale Internetfreigabe.
4. Verbindung privat in Portainer hinterlegen, URI mit /basel_homefixer und passendem authSource; niemals im Frontend/Chat/Repository.
5. Nach isoliertem Test und Backup: Dry-Run/Zielcheck, genehmigter Import und zwei Website-Konten. Details: [Portainer](PORTAINER.md), [Adminzugang](../../design/Kundenprojekt_PJA/ADMIN-ZUGANG.md), [Übertragung/Backup/Rücknahme](README.md).

## Zugang jetzt hinterlegen

Laut Betreiber wurden Datenbank und technischer Nutzer **baselhomefixer** mit Berechtigung erstellt. Angenommener Datenbankname gemäss bisheriger Planung: **basel_homefixer**. Kein Passwort wurde angefordert oder gelesen, Verbindung/Rollen wurden nicht unabhängig geprüft. Falls tatsächlich anders benannt, vor Nutzung URI und serverseitigen Datenbankwert gemeinsam anpassen.

Für die **neue** Stack-Vorlage in Portainer → Stacks → basel-homefixer_portainer → Editor → Environment variables den Eintrag **MONGODB_URI** setzen:

```text
mongodb+srv://baselhomefixer:<URL-KODIERTES-PASSWORT>@<ATLAS-HOST>/basel_homefixer?retryWrites=true&w=majority&authSource=admin
```

Atlas liefert den tatsächlichen Host unter Connect → Drivers. Platzhalter ersetzen, Kennwortanteil korrekt URL-kodieren; keine echten Werte in Chat, Code, Frontend-VITE-Variablen, GitHub-Actions-Secrets oder Beispieldateien kopieren. Die Pipeline benötigt keinen produktiven MongoDB-Zugang. Vorlage mit leeren Werten: portainer-stack.env.example. Speichern/Update des laufenden Stacks würde eine Änderung auslösen und erfolgt erst beim freigegebenen Rollout mit neuen Images.

Das gilt für compose.portainer.yaml bzw. compose.mvp.yaml: dort wird MONGODB_URI serverseitig als spring.data.mongodb.uri eingebunden; das Datenbankziel bleibt basel_homefixer. Den aktuell alten Stack nicht unabhängig von Anwendung/Konfiguration auf die neue DB umschalten. Alte spring.data.mongodb.*-Variablen nicht parallel übernehmen. AUTH_SETUP_MONGO_URI / CONTENT_IMPORT_MONGO_URI sind nur für lokale Betreiberwerkzeuge; die optionale Compose-Werkzeugvorlage leitet denselben MONGODB_URI-Wert bereits passend weiter. Der technische Nutzer ist **kein Website-Adminlogin**; eure zwei /admin-Konten werden separat eingerichtet.
