# Einfacher CI-Weg und Push

Die bestehenden zwei GitHub Actions und ECR-Repositories bleiben erhalten. Der frühere Hinweis „Workflows unverändert“ ist durch den ausdrücklichen Folgeauftrag zur CI-Anpassung überholt.

- Pull Requests nach main: Tests/Build und Buildartefakte, kein ECR-Push.
- Push auf main: Tests/Build, Dockerprüfungen, danach ECR-Push.
- Push nur auf basel-homefixer: keine Ausführung dieser beiden Image-Pipelines. Für neue AWS-Images später bewusst nach main übernehmen.
- Keine automatische Aktualisierung auf dem NAS.

## Einfacher Ablauf

Backend: Maven verify → bereinigte Kopie target/release/app.jar → Archivprüfung → Docker-Build → Java-/Boot-Archivprobe → ECR.

Frontend: Typecheck → neun echte Tests → Deployment-Vertragsprüfung → Vite-Build mit relativer /api → Docker-Build → Nginx-/HTTP-/SPA-/Admin-Cache-/API-404-Prüfung → ECR. npm-test-Platzhalter wird nicht verwendet.

Docker-Buildkontexte haben Positivlisten: Backend nur Dockerfile und validiertes Release-JAR; Frontend nur Dockerfile, nginx.conf und dist. Private Konfiguration und Entwicklungsdateien werden nicht als Docker-Kontext übertragen. Das normale lokale Maven-JAR bleibt ein Entwicklungsartefakt und wird nicht direkt verteilt.

## Tags

Pro erfolgreichem Image-Build werden dev und sha-<vollständige Commit-ID> geschrieben. Beispiel:

```text
471112833916.dkr.ecr.eu-central-1.amazonaws.com/basel-homefixer-backend:sha-<Commit-ID>
471112833916.dkr.ecr.eu-central-1.amazonaws.com/basel-homefixer-frontend:sha-<Commit-ID>
```

Die tatsächliche Referenz steht in der Actions-Zusammenfassung. In Portainer BACKEND_IMAGE / FRONTEND_IMAGE auf die passenden geprüften Commit-Tags setzen. Bei reinen Frontend-/Backendänderungen den jeweils tatsächlich gebauten Stand wählen; nicht automatisch identische Tags für beide voraussetzen.

Manuelles Kopieren von Digests oder Setzen von Basisimage-Buildargumenten ist im normalen CI-Weg nicht nötig. Ein erneut gebauter identischer Commit kann denselben SHA-Tag überschreiben; für technisch exaktes Pinning bleibt der Digest optional. dev bleibt der bewegliche bisherige Komforttag.

## Konfiguration / Rollout

GitHub verwendet weiterhin vorhandene PACKAGES_TOKEN und AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY. Keine MongoDB-, Gmail- oder JWT-Produktionswerte in der Pipeline. Diese nur privat in Portainer hinterlegen. Technischer MongoDB-Nutzer laut Betreiber: baselhomefixer, Ziel-DB basel_homefixer.

Erster regulärer NAS-Rollout bleibt separat: externe application-production.properties, privater Medienmount und UID-/GID-Rechte, neue DB-Konfiguration, stabiler JWT-Schlüssel, Backup, isolierter MongoDB-/Medientest und bewusste Inhaltsfreigabe. Frühere :dev-Images passen nicht automatisch zur neuen Compose-Vorlage.

Für lokale Docker-Builds zuerst Maven verify, dann PrepareJar.java und ValidateRelease.java wie in den Workflows ausführen. Das Root-Backend-Dockerfile erwartet target/release/app.jar. PrepareJar verlangt ein neues Ziel; vorhandene Release-Ausgaben nicht blind überschreiben. Optionale getrennte Release-Kontexte bleiben über prepare-release.ps1 verfügbar. Alte lokale Docker-Starthelfer erstellen diese Release-Kopie noch nicht automatisch; sie sind kein neuer Produktionsstartweg.


## Nachweis vor Push: neue Produktionsimages lokal geprüft

Beide tatsächlichen CI-Dockerfiles lokal erfolgreich gebaut. Backend: UID 10001, schreibgeschütztes Dateisystem, exaktes validiertes Release-JAR, Boot-Archivwerkzeug und curl geprüft. Zusätzlich vollständiger Backendstart mit externer Produktionskonfiguration gegen frische echte MongoDB 7 in einem internen temporären Docker-Netz erfolgreich: Health UP, leere öffentliche Projekt-/Blog-APIs, Admin ohne Token 401, Kontaktversand deaktiviert. Keine Hostports, produktiven Zugangsdaten oder externe E-Mail-Verbindungen. Testcontainer/Netzwerk entfernt. Dies ersetzt nicht den noch offenen NAS-/Atlas-Rechte-/Backup-/vollständigen Verwaltungs-Integrationstest.

Frontend: UID 101, read-only und tmpfs, Nginx-Syntax, direkte SPA-Routen /projects, /admin und /blog/..., admin Cache-Control no-store und kein HTML-Fallback unter /api geprüft. Temporärer Loopback-Testcontainer danach entfernt.

CI-Umstellung ist ausdrücklich freigegeben und umgesetzt: dev sowie sha-Commit-Tags, ECR nur bei Push auf main. Aktueller Pushauftrag umfasst ausschliesslich basel-homefixer; keine Zusammenführung nach main, keine Imageveröffentlichung durch diesen Branch-Push und kein NAS-Deployment. Frühere Angaben „Docker lokal nicht verfügbar / Workflows unverändert“ beschreiben den damaligen Stand.
