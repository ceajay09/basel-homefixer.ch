> **Aktueller Betrieb, 25. Oktober 2026:** Der Betreiber hat Website-Start, beide Adminzugänge und veröffentlichte Projekte bestätigt. Die ursprünglichen Vorbereitungsabschnitte unten dokumentieren den Rolloutweg. Aktueller Stand und tatsächliche Mountpfade: [Kurzcheckliste](KURZANLEITUNG.md). Für den täglichen Umgang: [Kundenanleitung](../../design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md). Die Compose-Datei ist eine Vorlage; insbesondere Content-Pfad und Image-Auswahl mit dem laufenden Stack abgleichen.

> **Aktueller CI-Stand:** Bestehende Workflows/Dockerfiles sind nun gezielt angepasst: bereinigtes JAR, Non-root/Nginx, Tests, dev plus sha-Commit-Tag. [Einfacher Ablauf](PIPELINE.md). Frühere Aussagen zu noch unveränderten Workflows und zwingend manueller Digest-Verwaltung sind historisch. Push nur auf basel-homefixer startet noch keine Image-Pipelines; diese veröffentlichen bei Push auf main.

# Portainer – möglichst nah am bisherigen Stack

**Vorbereitet, noch nicht deployen.** Voraussetzung: neue geprüfte Anwendungsimages, isolierter Integrationstest, Backup und ausdrückliche Rolloutfreigabe. Die alten :dev-Images sind kein Nachweis für den neuen Stand.

## Was du in Portainer einfügst

Kompletter Webeditor-Inhalt: [compose.portainer.yaml](compose.portainer.yaml). Wieder drei Dienste backend/frontend/traefik, stack.env und dieselben Ports. Die Namen der bestehenden Netzwerke bleiben erhalten. Im bisherigen Stack basel-homefixer_portainer verwenden; keinen zweiten öffentlichen Stack auf denselben Ports starten.

Portainer-Umgebungsvariablen gemäss [portainer-stack.env.example](portainer-stack.env.example) im privaten Environment-Bereich setzen bzw. aus einer privaten Datei laden. Auf Docker Standalone erzeugt Portainer daraus stack.env. Nicht mit Docker Swarm verwenden. Änderungen an Variablen über Portainer pflegen, nicht nur die erzeugte Datei von Hand ändern ([Portainer](https://docs.portainer.io/sts/user/docker/stacks/add)).

| Wert | Eintragen |
| --- | --- |
| BACKEND_IMAGE / FRONTEND_IMAGE | Vollständige ECR-Referenzen der geprüften neuen Images mit @sha256:… |
| MONGODB_URI | Privater Nutzer auf bewusst gewähltem Produktionscluster, readWrite nur basel_homefixer |
| AUTH_JWT_SECRET_BASE64 | Einmalig erzeugter stabiler 32-Byte-Schlüssel, Base64, privat sichern |
| EDGE_TRUSTED_IPS | Tatsächlich bestätigte lokale Cloudflare-Tunnel-Peer-Adresse(n), keine breiten Privatnetze |
| CONTACT_TRUSTED_PROXIES | Vollständige bestätigte Proxy-Kette; leer begrenzt Besucher ggf. gemeinsam |
| CONTACT_MAIL_ENABLED | Anfangs false |
| CONTACT_SMTP_PASSWORD | Gmail-App-Passwort privat, erst bei bewusster Aktivierung benötigt |

**Alte Variablen bereinigen:** keine alten spring.data.mongodb.*-Einträge, DB_USER/DB_PASS/DB_INSTANCE, SPRING_APPLICATION_JSON, SPRING_PROFILES_ACTIVE oder weiteren Spring-Config-Overrides übernehmen. Keine AWS-Schlüssel in den Appcontainer übernehmen; ECR-Anmeldung erfolgt als geschützte Portainer-/Host-Registry-Konfiguration. Die URI ist neu und bewusst auszuwählen; DEMO und Vorschau bleiben unberührt.

## Zwei NAS-Pfade vorbereiten

1. `/volume1/docker/basel-homefixer/content`: dauerhafter privater Inhaltsordner, darin media/. Backend UID/GID 10001:10001 muss schreiben können. Nicht durch Web Station oder statischen Proxy veröffentlichen.
2. `/volume1/docker/basel-homefixer/ops/application-production.properties`: Datei aus [dieser Vorlage](application-production.properties). Für Backend-UID lesbar, für fremde Nutzer gesperrt. Elternordner schützen. Kein SMTP-Passwort in diese Datei schreiben; Variablen kommen aus der privaten Umgebung.

Bind-Mounts verlangen existierende Quellen. Keine automatische Übernahme des alten Webdatenordners. Nicht nur neue Images in die alte Konfiguration einsetzen: die bereinigten Images erwarten diese externe Datei und Benutzerrechte.

## Bewusst beibehalten / verändert

- Ports 8095 und 8444 bleiben wie bisher; Backend/Frontend intern 8081/8082. Der vorhandene 8444→8443-Port erzeugt allein kein HTTPS, derzeit ist dort kein Router aktiv. Öffentliches HTTPS bleibt beim bestehenden Cloudflare-Tunnel; dessen Ziel/Cache-Regeln noch bestätigen.
- Traefik ist auf den beobachteten Digest fixiert, kein ungeprüftes latest-Update. Die beiden ungültigen/unnötigen alten Middleware-Labels entfallen.
- /api erhält höhere Routingpriorität; Headervertrauen wird auf bestätigte Peers beschränkt. Medien nur über /api, keine zusätzliche statische Dateifreigabe.
- Frontend-Healthcheck verwendet wget, weil das vorbereitete Nginx-Image dies unterstützt. Backend-Image enthält curl. Tatsächlichen Image-Start vor Produktion separat prüfen.
- Keine Admin-/Import-Werkzeuge starten automatisch. Der optionale CLI-Werkzeugweg liegt weiterhin in compose.mvp.yaml. Für ihn die gleichen Images/Schlüssel/Medien und das gleiche Datenbankziel verwenden; keine zweite Anwendung per up starten. Operator-Konfiguration ausserhalb Portainer separat geschützt sichern.

## Reihenfolge

Neue Datenbank/technischen Zugang wählen → Images und isolierten Test fertigstellen → Schreibpause und gemeinsame Sicherung → neuer Backendstand mit Entwurfsschutz → genehmigter Import und zwei Admin-Konten → neues Frontend prüfen → getrennte Inhaltsfreigabe → erst danach gesonderte Gmail-Aktivierung/Testfreigabe. Einzelbefehle, Zielcheck, Backup und Rücknahme: [ausführliche Betriebsanleitung](README.md).

## Rücknahme

Alten Stack, alte geschützte Umgebung und Image-Digests zusammen sichern. Nach neuen Entwürfen oder Konten kein blosser Rücksprung auf den alten Backendstand: er kennt die neuen Schutzregeln nicht. Neue Datenbank bleibt bei einer Rückkehr auf den alten Stack erhalten; es wird weder DEMO überschrieben noch eine DB automatisch zurückkopiert. Bei zwischenzeitlichen Kundenänderungen sichern und abgleichen; Details in [README.md](README.md#rücknahme).

