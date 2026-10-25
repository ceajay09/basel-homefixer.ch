> **Für deinen bisherigen Portainer-Webeditor-Stack:** [compose.portainer.yaml](compose.portainer.yaml), [kurze Einfügeschritte](PORTAINER.md). Die ausführliche compose.mvp.yaml bleibt die optionale Betreiber-/Werkzeugvariante.

# NAS-Umstellung – kurze Checkliste

**Stand: nur vorbereitet, nichts ausgerollt.** Details, Befehle und Grenzen: [README.md](README.md).

## Festgelegt

- Produktive Datenbank: **basel_homefixer**. Vorschau bleibt separat.
- Du und Kunde: zwei eigene Website-Konten in basel_homefixer.account, unabhängig vom technischen MongoDB-Zugang. Anleitung: [ADMIN-ZUGANG.md](../../design/Kundenprojekt_PJA/ADMIN-ZUGANG.md).
- Öffentlicher Weg: bestehendes Cloudflare-HTTPS/Tunnel → Traefik → Frontend und /api auf derselben Domain.
- Neue Medien privat unter /volume1/docker/basel-homefixer/content/media, nur über die Medien-API.
- Übertragung: sechs Projekte, drei Blogartikel und sechs Bildquellen, alle zunächst Entwurf. Keine vollständige Vorschaukopie.
- Bestehende GitHub Actions unverändert. Für diese Umstellung separat bereinigte Release-Images erforderlich.

## Vor einer Freigabe abhaken

- [ ] Cloudflare-Service-Ziel und genaue Proxy-Peers bestätigen; /api und /admin nicht öffentlich cachen.
- [ ] Atlas-Nutzer mit readWrite nur auf basel_homefixer einrichten; eigenen Nutzer für isolierten Integrationstest verwenden.
- [ ] Private Umgebungsdateien, stabilen JWT-Schlüssel und eingeschränkte NAS-Dateirechte vorbereiten. Mailversand zunächst aus.
- [ ] Geprüfte Release-Images bauen und per Digest festhalten; kein automatischer Rückgriff auf :dev.
- [ ] Echten isolierten Container-/MongoDB-/Medientest mit compose.verify.yaml bestehen, einschliesslich Entwurfsschutz und Rücknahme.
- [ ] Inhalte/Bilder sichten; Übertragungspaket, Zielkollisionen und Dry-Run prüfen.
- [ ] Wartungsfenster und T0-Sicherung von basel_homefixer, Dateien, Konfiguration und alten Images; isolierte Wiederherstellung erproben.

## Erst nach ausdrücklicher Rolloutfreigabe

1. Alte öffentliche Schreib-/Lesewege im Wartungsfenster sperren; neuen Backendstand mit Entwurfsschutz bereitstellen.
2. Genehmigte Entwurfsdaten übertragen und zwei Website-Admins interaktiv anlegen. Keine Konten oder Entwürfe unter dem alten Backend anlegen.
3. Neues Frontend aktivieren, HTTPS, API, Bilder, Anmeldung und Entwurfssperren prüfen.
4. Projektveröffentlichung separat bestätigen. Blogentwürfe bleiben ohne eigene Freigabe unveröffentlicht.
5. Wartung aufheben. Gmail-Aktivierung und echter NAS-Testversand separat freigeben.

## Falls etwas fehlschlägt

Wartung beibehalten. Alte Images brauchen ihren passenden alten Stack und Konfiguration. Nach Datenänderungen ist ein blosser Image-Rücksprung wegen fehlendem alten Entwurfsschutz ungeeignet. Vollständige Rücknahme von DB und Dateien nur mit eigener Entscheidung; neuere Kundenänderungen vorher sichern und abgleichen.

## Nachgewiesen / noch offen

Nachgewiesen: lokale Vorlagen-/Policyprüfung, tatsächlicher NAS-Compose-Parser, bereinigtes Release-Archiv und Prüfsummen, Entwurfspaket und isolierter Zielcheck mit Doubles. Workflows unverändert.

Offen: Image-Build und echter Container-/MongoDB-/NAS-Test, Backup/Restore, Cloudflare-Ziel/Proxy-Bestätigung, neue technische DB-Rechte und zwei Konten. Kein Deployment, Import, Mailversand, produktiver Dateieingriff oder öffentliche Veröffentlichung wurde durchgeführt.

