# basel-homefixer.ch lokal starten

Das Frontend läuft direkt mit Node.js/Vite. Backend und Traefik laufen über eine portable lokale Compose-Datei. Die vorhandenen Backend- und MongoDB-Einstellungen werden weiterverwendet. Es wird keine lokale MongoDB gestartet.

## Voraussetzungen

- Node.js **22.x** inklusive npm.
- JDK **17** und korrekt gesetztes `JAVA_HOME` für den vorhandenen Maven-Wrapper.
- Docker Desktop mit laufender Linux-Engine; unter Windows WSL 2.
- Vorhandene Backend-Dev-Konfiguration und Zugriff auf den gemeinsamen Maven-Parent (siehe Backend-README).

Nach einer Installation VS Code und Terminal neu starten, damit Umgebungsvariablen geladen werden.

## Frontend

Beim ersten Start im Projekt-Hauptordner:

```powershell
npm --prefix My-App_Frontend ci
npm run start
```

Später genügt `npm run start`. Alternativ `start-frontend.bat` doppelklicken. Auch direkt im Frontend-Ordner funktionieren `npm ci` und `npm run start`, wie bisher.

Website: **http://localhost:5173**. Terminal offen lassen; mit **Strg+C** stoppen. Änderungen unter `src` und `public` werden automatisch nachgeladen. Bei einer PowerShell-Sperre für `npm.ps1` stattdessen `npm.cmd` verwenden.

Vite bindet nur an `127.0.0.1`; Zugriff aus dem Netzwerk ist nicht vorgesehen. Eine Unternehmens-Firewallregel wird dadurch nicht verändert.

Die bisherige API-Konfiguration über `VITE_API_URL` bleibt erhalten. Für den lokalen Backend-Zugriff in der ignorierten `My-App_Frontend/.env.development` setzen:

```dotenv
VITE_API_URL=http://localhost:8081
```

`npm run dev` verwendet wie bisher diese Datei. Ohne Backend kann die Oberfläche angezeigt werden; API-Funktionen benötigen das Backend.

## Backend und Traefik

`start-backend.bat` im Hauptordner ausführen. Die Datei baut das Backend mit dem bestehenden Maven-Wrapper und startet danach Docker Compose mit dem bestehenden Backend-Dockerfile.

Alternativ:

```powershell
cd My-App_Backend
.\mvnw.cmd package
cd ..
docker compose -f compose.local.yaml up -d --build backend traefik
```

Backend direkt: **http://localhost:8081**. API über Traefik: **http://localhost/api/homepage-config**. `http://localhost` leitet zum nativen Frontend auf Port 5173 weiter; die `/api`-Routen gehen zum Backend.

Die Dev-Konfiguration `My-App_Backend/src/main/resources/application-dev.properties` muss lokal vorhanden sein und verweist auf die bestehende externe MongoDB. Sie bleibt von Git ausgeschlossen. Die Anwendung verwendet wieder ihr bisheriges Start- und Fehlerverhalten; ein erfolgreicher Start ohne Datenbank wird nicht garantiert.

```powershell
docker compose -f compose.local.yaml logs -f backend traefik
docker compose -f compose.local.yaml stop backend traefik
```

Die alte lokale MongoDB wird nicht mehr verwendet. Ihr eventuell vorhandenes Docker-Volume wurde nicht gelöscht. Die bisherigen Compose-Dateien in den Unterordnern bleiben unverändert; die neue lokale Compose-Datei enthält keine festen Rechnerpfade oder Zugangsdaten.

## VS Code und Prüfungen

Den gesamten Projektordner öffnen und die empfohlenen Extensions installieren. Java verwendet JDK 17, Maven den vorhandenen Wrapper. Die bisherigen TypeScript-Konfigurationen bleiben erhalten; Vite-Typen und der veraltete Web-Vitals-Aufruf wurden korrigiert.

```powershell
npm run typecheck
npm --prefix My-App_Frontend run build
```

Backend-Prüfung im Backend-Ordner: `mvnw.cmd verify`. Java-Debugging startet wie bisher das Dev-Profil; Chrome-Debugging startet das native Frontend. Ein bereits laufender Vite-Server muss vor einem zweiten Start beendet werden.

Die vorhandenen GitHub-Actions-Workflows und Produktions-Dockerfiles wurden nicht geändert. Details: [Backend](My-App_Backend/README.md), [Frontend](My-App_Frontend/README.md).

Lokale Vorschau mit echten NAS-Projekten und Bildern: [NAS-VORSCHAU.md](NAS-VORSCHAU.md). Verbindung mit `scripts/start-nas-preview.ps1` starten, anschliessend das Frontend wie gewohnt verwenden. Das bisherige lokale Backend darf dabei Port 8081 nicht gleichzeitig belegen.
