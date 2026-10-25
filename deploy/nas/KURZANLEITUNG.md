# NAS-Betrieb – kurze Checkliste

[README](../../README.md) · [Kundenanleitung](../../design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md) · [Ausführliche Betriebsanleitung](README.md)

## Aktueller Stand · 25. Oktober 2026

- Website läuft laut Betreiber im regulären Portainer-Stack; beide Website-Adminzugänge sind eingerichtet und die Projekte veröffentlicht.
- Datenbank: `basel_homefixer`. Demo und Vorschau bleiben separat.
- Übernommen: sechs Projekte, drei Blogentwürfe und sechs Medien. Blogveröffentlichung bleibt eine eigene Entscheidung.
- Aktueller NAS-Content-Mount: `/volume1/webdata/basel-homefixer/data/content` → `/data/content`. Bilder liegen unter `media/` und werden über die Medien-API ausgeliefert. Keine zusätzliche öffentliche statische Freigabe einrichten; tatsächliche Web-Station-Freigaben prüfen.
- Externe Konfigurationsdatei: `/volume1/docker/basel-homefixer/ops/application-production.properties`.
- Der Medien-Unterordner ist für Backend UID/GID `10001:10001` eingerichtet. Bei Pfadänderungen Rechte erneut prüfen.
- SMTP ist laut Betreiber aktiviert; ein echter Testversand ist damit noch nicht nachgewiesen.

## Noch erledigen oder nachweisen

- [ ] Beide Sprachversionen, Bilder/Bildrechte und veröffentlichte Inhalte durch den Kunden prüfen.
- [ ] Blogentwürfe gesondert sichten und nur bewusst veröffentlichen.
- [ ] Kontaktversand nach ausdrücklicher Freigabe vom NAS testen; Empfang und Reply-To prüfen.
- [ ] Tatsächliche Proxy-Kette und Cache-Regeln für `/api` und `/admin` prüfen.
- [ ] Datenbank und Medien zusammen sichern; Wiederherstellung isoliert erproben. Private Konfiguration und stabilen Signaturschlüssel mit sichern.
- [ ] Sicherstellen, dass der Medienordner nicht durch Web Station oder andere statische Freigaben erreichbar ist.

## Spätere Softwareupdates

1. Geprüften Code nach dem vereinbarten Git-Ablauf übernehmen. Die Pipeline veröffentlicht Images bei Push auf `main`.
2. In Portainer die `:dev`-Images neu ziehen und den Stack aktualisieren. Ein neues ECR-Image allein ersetzt keinen laufenden Container.
3. Frontend/Backend-Health, Anmeldung, Projekte, Bilder, Entwurfsschutz und Kontaktstatus prüfen.
4. Datenbank-/Medienstand vor einer Rücknahme sichern. Nach Inhaltsänderungen nicht blind alte Daten zurückspielen.

Inhaltspflege erfolgt direkt in der Verwaltung; dafür sind weder Import noch neue Images erforderlich. Import bleibt ein Übertragungswerkzeug. Technische Details und Rücknahme: [Betriebsanleitung](README.md), [Pipeline](PIPELINE.md), [Adminzugänge](../../design/Kundenprojekt_PJA/ADMIN-ZUGANG.md).
