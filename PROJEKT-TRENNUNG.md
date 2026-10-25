# Trennung Basel Homefixer / Demo

Stand: 19. Oktober 2026.

## Aktueller geprüfter Stand

- Die neutrale Demo liegt unter `C:\Users\cesarjaquiery\vscode\demo.cesarjaquiery.ch`. Das eigene private GitHub-Repository `ceajay09/demo.cesarjaquiery.ch` ist angelegt und der initiale Commit `0e80c3c` wurde auf main gepusht. `[skip ci]` im initialen Commit verhindert einen Build-/ECR-Push vor Einrichtung der noch fehlenden Secrets.
- TypeScript-Prüfung und Frontend-Produktionsbuild der Demo sind erfolgreich. Dafür wurde die vorhandene Abhängigkeitsinstallation des Ausgangsprojekts vorübergehend verknüpft. Die Verknüpfung ist entfernt; ein eigener späterer `npm ci` in der Demo ist erforderlich. Eine separate Neuinstallation wurde wegen langer Installationsdauer abgebrochen. Die beiden Compose-Vorlagen sind ohne echte env-Dateien syntaktisch validiert. MongoDB und der NAS-Betrieb wurden nicht getestet.
- Beide ursprünglichen Basel-Homefixer-Workflows sind per SHA-256-Vergleich unverändert.
- Die Umbenennung des Originalordners ist noch offen: Windows verweigert sie wegen eines geöffneten Prozesses. Das Kundenprojekt liegt weiterhin unter `C:\Users\cesarjaquiery\vscode\Demo-Website`. Geöffnete Codex-/Terminal-/Editor-Prozesse wurden nicht beendet. Nach Schliessen des Projekts den Ordner in `basel-homefixer.ch` umbenennen und neu öffnen.
- Das bisherige GitHub-Repository wurde erfolgreich in `ceajay09/basel-homefixer.ch` umbenannt. Der lokale origin-Verweis des Kundenprojekts zeigt bereits auf die neue Adresse. Die Repository-Verwaltung wurde über die angemeldete Browser-Sitzung abgeschlossen; beide Repository-Seiten sind geöffnet. Lokale uncommittete Kundenänderungen wurden nicht gepusht.
- ECR, NAS, Reverse Proxy, DNS, Zertifikate, Kundendatenbank und Kundeninhalte wurden nicht verändert.

## Ziel

- Kundenprojekt: lokaler Ordner basel-homefixer.ch; vorgesehenes GitHub-Repository ceajay09/basel-homefixer.ch.
- Neutrale Kopie: separater lokaler Ordner demo.cesarjaquiery.ch; vorgesehenes GitHub-Repository ceajay09/demo.cesarjaquiery.ch.
- Kundenentwürfe, persönliche Dokumente und vorhandene Zugangsdaten bleiben beim Kundenprojekt.

## Basel Homefixer

Die beiden bestehenden GitHub-Actions-Dateien bleiben inhaltlich unverändert, einschliesslich main-Trigger, Image-Tag dev, Region und ECR-Zielen basel-homefixer-backend und basel-homefixer-frontend.

Der bestehende Synology-Stack bleibt unverändert. Dort verwendet Traefik den Host basel-homefixer.ch, den Host-Port 8095 und das Kunden-Inhaltsverzeichnis /volume1/webdata/basel-homefixer/data/content.

Die GitHub-Umbenennung und die Aktualisierung von origin sind abgeschlossen. Der lokale Ordner trägt wegen der Windows-Dateisperre noch den alten Namen. Der bereits ausgeführte Remote-Befehl lautet:

```powershell
git remote set-url origin https://github.com/ceajay09/basel-homefixer.ch.git
```

Nicht die vorhandenen ECR-Repositories oder den gemeinsamen Maven-Parent umbenennen.

## Demo

Die neutrale Kopie verwendet bewusst die bereits vorhandenen ECR-Namen:
- Backend: demo-webiste-backend (Schreibweise exakt wie vom Benutzer angegeben)
- Frontend: demo-website-frontend
- Registry: 471112833916.dkr.ecr.eu-central-1.amazonaws.com
- Region: eu-central-1; Image-Tag: dev

Workflow-Dateinamen, Path-Filter und Image-Ziele sind in der Demo angepasst. Der Frontend-Build verwendet dort den passenden Variablennamen VITE_API_URL. Der Ablauf Build → Artefakt → Docker → ECR bleibt gleich.

Die separate Git-Historie der Demo wurde ohne Kopie des ursprünglichen .git angelegt. Das neue private GitHub-Repository ist erstellt; main ist mit dem lokalen initialen Commit synchronisiert. Secrets und die spätere Laufzeitkonfiguration stehen noch aus.

Keine Secrets aus dem ursprünglichen Repository wurden ausgelesen oder kopiert. Im Demo-Repository bereitstellen:
- Actions: AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, PACKAGES_TOKEN.
- Dependabot, falls weiter genutzt: DEPENDABOT_GITHUB_PACKAGES_TOKEN.
- IAM-Berechtigungen für die Demo-ECR-Ziele prüfen. Der Tag dev muss wie im bisherigen Ablauf überschreibbar sein.

## MongoDB

Die Demo benötigt DEMO_MONGODB_URI und verwendet DEMO_MONGODB_DATABASE=demo_website. Es gibt keinen Rückfall auf die Kunden-Verbindungszeichenfolge oder den bisherigen Atlas-Host. Ein leerer URI ist keine funktionierende Konfiguration. Eine separate Demo-Verbindung muss später eingerichtet werden.

Vorlagen befinden sich in der Demo unter My-App_Backend/.env.demo.example und deployment/synology/stack.env.example. Zugangsdaten gehören nur in die ignorierten tatsächlichen Dateien bzw. in die NAS-Umgebung.

## Lokaler Parallelbetrieb

| | Basel Homefixer, bisherige Ports | Demo |
| --- | --- | --- |
| Vite | 5173 | 5174 |
| Backend, Host | 8081 | 8083 |
| Lokaler Traefik, Host | 80 | 8084 |

Die Ports der laufenden Kundeninstallation wurden nicht geändert.

## Synology-Demo vorbereiten

Die Demo enthält deployment/synology/compose.yaml und README.md. Die Vorlage verwendet eigene Router, eigene Compose-Netze und den vorgeschlagenen NAS-Host-Port 8096. Portverfügbarkeit muss auf dem NAS geprüft werden. Synology Reverse Proxy soll HTTPS demo.cesarjaquiery.ch auf HTTP 192.168.178.38:8096 weiterleiten und den Host-Header erhalten. DNS und Zertifikat sind separat einzurichten.

Der Kunden-Inhaltsordner wird nicht eingebunden. Für die Demo wird standardmässig das neutrale homepage.json aus dem JAR verwendet.

GitHub Actions schiebt Images nach ECR. Ein automatischer Pull/Neustart auf dem NAS ist in den Workflows nicht enthalten. Bestehende separate NAS-Automatisierung wurde nicht geprüft oder geändert. Ohne solche Automatisierung müssen pull und anschliessende Neuerzeugung der Demo-Container bewusst im eigenen Demo-Stack ausgeführt werden.

## Noch nicht durchgeführt

Keine Änderung auf ECR, NAS, DNS oder an einer MongoDB. Lokal den Kundenprojektordner nach Schliessen der blockierenden Prozesse noch umbenennen. Secrets des Demo-Repositories, separate Datenbank und späterer erster Deployment-Test bleiben erforderlich.
