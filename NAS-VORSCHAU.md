# Lokale Website mit NAS-Projektdaten

Stand: 24. Oktober 2026. Dieser separat freigegebene Versuch verbindet die lokale Website mit echten Projektdaten und Bildern. Die öffentliche Website und ihre bisherigen Container/Compose-Dateien wurden nicht aktualisiert.

## Vorschau starten

Im Projektordner in PowerShell:

```powershell
.\scripts\start-nas-preview.ps1
```

Das startet im Hintergrund einen ausschliesslich lokalen Leseproxy auf `127.0.0.1:8081`. Wenn dieser bereits läuft, wird die echte NAS-API geprüft und der laufende Prozess weiterverwendet. Port 8081 muss sonst frei sein; nicht gleichzeitig das bisherige lokale Docker-Backend darauf starten.

Die vorhandene Frontend-Vorschau läuft aktuell unter http://127.0.0.1:5180/projects. Bei einem späteren Neustart das Frontend wie gewohnt mit `start-frontend.bat` starten; den dort ausgegebenen Vite-Port verwenden. Standard ist 5173. Wenn ausdrücklich wieder Port 5180 gewünscht ist, im Frontendordner `npm run start -- --host 127.0.0.1 --port 5180 --strictPort` ausführen. Keine Frontend-Codeänderung für die Verbindung erforderlich: relative API-Aufrufe laufen über den vorhandenen Vite-Proxy; die lokale DE-Konfiguration mit `VITE_API_URL=http://localhost:8081` funktioniert ebenfalls.

Voraussetzungen: Tailscale-Zugriff zum NAS, Node.js 22, OpenSSH und der lokale Schlüssel `~/.ssh/basel_homefixer_nas_ed25519`. Nur der öffentliche Schlüssel wurde auf dem NAS hinterlegt. Keine Passwörter in Skripten oder im Frontend.

Den lokalen Proxy stoppen: Die Startausgabe nennt Prozess-ID und passenden `Stop-Process -Id ...`-Befehl. Die PID steht zusätzlich unter `$env:TEMP/basel-homefixer-nas-preview-8081.pid`; die lokalen Proxy-Logs liegen daneben. Vor manuellem Stoppen einer später aus dieser Datei gelesenen ID immer prüfen, ob der Prozess noch derselbe ist.

## Wo die Daten liegen

| Teil | Ablage |
| --- | --- |
| Vorschau-Daten | Separate Datenbank `basel_homefixer_preview` auf dem bisherigen MongoDB-Atlas-Cluster |
| Projekte/Medienmetadaten | Collections `projects` und `media`, jeweils sechs Dokumente |
| Blog | Separate Collection `blogpost`, derzeit leer; keine zusätzlichen Blogartikel erstellt |
| Bilder | NAS: `/volume1/docker/basel-homefixer-preview/media/<id>/small.jpg` und `large.jpg` |
| Lesende API | Container `basel-homefixer-content-preview`, NAS-Loopback `127.0.0.1:28081` |
| Runtime/Importdaten | NAS: `/volume1/docker/basel-homefixer-preview/releases/20261024` |
| Serverkonfiguration | NAS: `/volume1/docker/basel-homefixer-preview/preview.env`, nur serverseitig, Modus 0600 |
| Ausgangssicherung | NAS: `/volume1/docker/basel-homefixer-preview/backups/before-first-import-20261024.json` |

Die Datenbank läuft weiterhin bei Atlas; API und Bilddateien liegen auf dem NAS. Die Vorschau verwendet die vorhandene serverseitige Atlas-Anmeldung, aber einen eigenen Datenbanknamen. Die bestehende öffentliche Basel-Seite verwendet `DEMO`, die persönliche Website `mywebapp`; diese Datenbanken wurden nicht beschrieben. Für einen späteren dauerhaften Betrieb ist ein eigener, auf die benötigte Datenbank begrenzter Datenbankbenutzer vorzusehen.

Die sechs übernommenen Projekte haben innerhalb der isolierten Vorschau `status=published`, damit die echte Lese-API sie ausliefert. Das ist keine Internetveröffentlichung. Das ursprüngliche Importmanifest im Repository bleibt vollständig `draft`. `publishedAt` wurde für diesen Vorschauimport auf den Importzeitpunkt gesetzt; öffentliche Listen sortieren dann deterministisch zusätzlich nach ID. `homeOrder` erhält die bisherige Startseitenauswahl Küche, Balkon, Heizung. Projektzeitpunkte und Gesamtprojektdauern aus dem Kundenstand bleiben unverändert.

## Verbindung und Schutz

Der NAS-SSH-Server erlaubt Anmeldung und Befehle, blockiert aber TCP-Portweiterleitungen. Deshalb verwendet diese Vorschau keinen SSH-Tunnel und ändert die SSH-Konfiguration nicht. `nas-content-proxy.cjs` führt begrenzte GET-/HEAD-Anfragen über SSH an `request.py` aus. Dieses liest nur die erlaubten Inhalts-/Medienpfade der NAS-Loopback-API. Keine Client-Dateipfade, Schreibzugriffe oder Datenbankzugänge werden weitergegeben.

Lokaler Proxy: maximal sechs gleichzeitige SSH-Abfragen, begrenzte Warteschlange, Zeitlimit, lokale Host-/Origin-Prüfung und nur Inhaltsrouten. Fremde Origins, Konto-, Kontakt- und Schreibpfade werden abgewiesen. Dadurch bleibt Kontaktversand in dieser reinen Inhaltsvorschau nicht verfügbar. Jedes Bild wird weiterhin durch die serverseitige Veröffentlichungsprüfung geschützt. Nach einem Verbindungsabbruch können vorhandene Wiederholen-Aktionen verwendet werden.

`ContentNasPreview.java` startet ausschliesslich die Lese-Controller, Inhaltsdienste und Security. Kein allgemeiner Component-Scan: bestehende Konto-Endpunkte, Initializer und Kontaktversand werden nicht eingebunden. Die Runtime enthält Klassen und Bibliotheken ohne lokale `application-dev.properties` oder andere lokale Konfigurationsdateien. Konfiguration wird ausschliesslich serverseitig übergeben.

Der Container nutzt ein bereits vorhandenes, per Image-ID fixiertes Java-Image, ein schreibgeschütztes Root-Dateisystem, einen schreibgeschützten Medienmount und ein Speicherlimit. Keine Traefik-Registrierung, kein Watchtower-Update. Restart-Policy `unless-stopped`. Der NAS-Kernel unterstützt das angeforderte zusätzliche PID-Limit nicht; Docker meldet dies und verwirft dieses Limit. Der Dienst ist nur auf Loopback gebunden und erhält keine öffentliche Route.

## Betrieb und Wiederherstellung

Lesend prüfen:

```powershell
ssh -i "$env:USERPROFILE/.ssh/basel_homefixer_nas_ed25519" -p 22022 c.jaquiery@gundelinas.tailad725b.ts.net 'docker ps --filter name=basel-homefixer-content-preview'
```

Nur die Vorschau auf dem NAS bei Bedarf stoppen/starten: `docker stop basel-homefixer-content-preview` bzw. `docker start basel-homefixer-content-preview` über denselben SSH-Zugang. Keine bestehenden Stacks neu erstellen. Ein gestoppter Vorschau-Container, der lokale Proxy oder das Löschen des lokalen Browsercaches verändern keine Daten.

Daten und Medien immer gemeinsam sichern. Der erste Ausgangssnapshot war leer. Nicht zum Zurücksetzen einer später bearbeiteten Datenbank verwenden und keine pauschalen Löschbefehle ausführen. Ein Rückbau/Restore ist ein eigener, gegen aktuelle Daten geprüfter Schritt.

Die Importdaten und Runtime wurden nur für diese Vorschau bereitgestellt. Änderungen an Texten/Bildern sind technisch ohne Frontend-Deployment möglich, die geschützte Verwaltungsoberfläche ist aber weiterhin nicht gebaut. Kein allgemeines Upload-/Veröffentlichungsrecht wurde durch diesen Schritt geschaffen. Nach Backend-Codeänderungen muss auch die separate Vorschau-Runtime bewusst aktualisiert werden; ein lokaler Maven-Build aktualisiert den NAS-Container nicht automatisch.

## Tatsächlich geprüft

- SHA-256 des übertragenen Pakets geprüft; Datenbank vor erstem Import gelesen und gesichert: null Projekte, Blogbeiträge und Medien.
- Sechs Projekte, sechs Medienmetadatensätze und zwölf optimierte JPEG-Varianten importiert; identischer zweiter Import erfolgreich ohne Duplikate.
- Echte API/Bilder und Startseitenauswahl geprüft, Blogliste bleibt leer, Schreibpfade und fremde Origins gesperrt.
- DE/EN im lokalen Edge mit echten NAS-Daten bei 390/768/1280 px: sechs Karten, geladene Bilder, kein horizontaler Überlauf, Tastaturfilter, Startseite und asynchrone Anker.
- Beim Integrationstest erkannte Fehlerbehandlung gezielt korrigiert: Spring-ERROR-Dispatch darf die vom Controller vorgesehenen 400-/404-Antworten ausliefern. Keine fachliche Inhaltsfreigabe oder zusätzliche Schreibroute. Eine Sicherung der zuvor verwendeten Vorschau-Security-Klasse liegt im NAS-Backupordner.
- Neuer Test mit echtem eingebettetem HTTP-Server; alle 32 Backend-Tests erfolgreich. Die vorherigen Frontend-Typecheck-/Build-Prüfungen bleiben gültig; in diesem Schritt wurde kein Frontend-Produktcode verändert.

Weitere Grundlage und Importformat: `My-App_Backend/CONTENT.md`. Neue Blogartikel, Blogoberfläche, Kundenverwaltung und öffentlicher Rollout bleiben eigene Aufträge.

Abschliessender echter MongoDB-/NAS-Test erfolgreich: ein ausschliesslich in der Vorschau kurzfristig auf Entwurf gesetztes Projekt und dessen Bild waren beide 404; der ursprüngliche Status wurde im `finally`-Block wiederhergestellt und der Bildzugriff danach erneut erfolgreich geprüft. Beide Collections besitzen den eindeutigen Slug-Index. Endbestand: sechs Projekte, sechs Medienmetadatensätze, null Blogartikel. Bestehende öffentliche Container liefen währenddessen ohne Neustart weiter.

## Abgrenzung zum regulären Betrieb – 25. Oktober 2026

Die spätere reguläre Website ist separat vorbereitet: `deploy/nas/README.md`. Dort bestätigte produktive Datenbank **DEMO**, dauerhafter privater Medienmount und direkter HTTPS-/API-Weg über den bestehenden Cloudflare-Tunnel/Traefik. Vorschau-Launcher und lokaler SSH-Proxy sind keine Produktionsabhängigkeit. Keine komplette Kopie von basel_homefixer_preview: selektiertes, prüfbares Paket aus sechs Projekten und drei Artikelentwürfen, zunächst ausschliesslich draft. Diese Vorbereitung hat Vorschau und laufende Website nicht verändert. Echt isolierter Container-/MongoDB-/NAS-Test, Backup/Restore-Nachweis und gesonderte Rollout-/Veröffentlichungsfreigaben bleiben offen.


## Aktuelle Entscheidung des Betreibers: getrennte reguläre Datenbank

Die frühere Empfehlung, die beobachtete DEMO-Datenbank weiterzuverwenden, ist aufgehoben. DEMO gehört zur Demo-Website und bleibt unberührt. Neues vorgesehenes Ziel ist basel_homefixer; noch nicht angelegt. basel_homefixer_preview bleibt die isolierte Test-/Vorschauumgebung. Cluster und Datenbank unterscheiden: Der endgültige Cluster wird bewusst durch den Betreiber gewählt, kein Demo-exklusiver Cluster wird automatisch übernommen. Details: deploy/nas/DATENBANK.md und Projekt-README. Blog bleibt auf ausdrücklichen Wunsch sichtbar; Projekte sind Referenzfälle, Blog ist redaktioneller Ratgeber. Keine produktive Änderung durch diese Dokumentationskorrektur.
