# basel-homefixer.ch

Website und Inhaltsverwaltung für Basel Homefixer.

**Aktueller Stand · 25. Oktober 2026:** Laut Betreiber läuft die Website auf dem NAS, beide Verwaltungszugänge sind eingerichtet und die Projekte veröffentlicht. Sechs Projekte, drei Blogentwürfe und sechs Bilder wurden in die separate Datenbank `basel_homefixer` übernommen. SMTP ist laut Betreiber aktiviert; ein echter Testversand wurde durch diese Arbeiten nicht nachgewiesen.

## Für dich als Kunde

**[Verwaltung öffnen](https://basel-homefixer.ch/admin)** · **[Website ansehen](https://basel-homefixer.ch)** · **[Kundenanleitung lesen](design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md)**

| Was möchtest du tun? | Direkt zur Anleitung |
| --- | --- |
| Anmelden oder abmelden | [Zugang](design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md#anmelden) |
| Ein Projekt ergänzen oder ändern | [Projekte pflegen](design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md#projekte-pflegen) |
| Bilder hochladen und auswählen | [Bilder verwalten](design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md#bilder-verwalten) |
| Drei Projekte auf der Startseite auswählen | [Startseitenteaser](design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md#startseitenteaser) |
| Einen Ratgeber schreiben | [Blog pflegen](design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md#blog-pflegen) |
| Einen Entwurf prüfen und freigeben | [Vorschau und Veröffentlichung](design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md#vorschau-und-veröffentlichung) |
| Eine Fehlermeldung verstehen | [Hilfe](design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md#wenn-etwas-nicht-klappt) |

**Merken:** Speichern → Vorschau prüfen → bewusst veröffentlichen. Änderungen an Projekten, Blogtexten und Bildern benötigen keinen neuen Website-Build. Beide Konten haben dieselben Verwaltungsrechte. Zugangsdaten vertraulich behandeln.

Projekte zeigen konkrete Referenzarbeiten. Der Blog ist für Ratgeber, Tipps und Erklärungen gedacht. Ein neuer Referenzauftrag braucht keinen zusätzlichen Blogartikel. Allgemeine Seiten wie Dienstleistungen, Team und Kontaktdaten werden weiterhin durch den Entwickler angepasst.

## Für den Betreiber und Entwickler

| Thema | Dokumentation |
| --- | --- |
| Alle Anleitungen mit Einordnung | [Dokumentationsübersicht](DOKUMENTATION.md) |
| Konten anlegen oder Passwort zurücksetzen | [Adminzugänge](design/Kundenprojekt_PJA/ADMIN-ZUGANG.md) |
| Laufender Betrieb, Sicherung, Rollout und Rücknahme | [NAS-Betrieb](deploy/nas/README.md), [aktuelle Kurzcheckliste](deploy/nas/KURZANLEITUNG.md) |
| Portainer-Stack und Umgebungsvariablen | [Portainer](deploy/nas/PORTAINER.md), [Compose-Vorlage](deploy/nas/compose.portainer.yaml) |
| Images und GitHub Actions | [Pipeline](deploy/nas/PIPELINE.md), [CI-Fehlerhilfe](.github/CI-TROUBLESHOOTING.md) |
| Datenbank, Inhalte und Medien | [Datenbank](deploy/nas/DATENBANK.md), [Inhaltsbasis](My-App_Backend/CONTENT.md) |
| Sicherheit, Verwaltung und E-Mail-Konfiguration | [Anmeldung](My-App_Backend/AUTH.md), [Verwaltungs-API](My-App_Backend/ADMIN.md), [Kontaktversand](My-App_Backend/CONTACT.md) |
| Lokale Entwicklung | [Lokaler Start](LOKALER-START.md) |
| Planung und durchgeführte Prüfungen | [Umsetzungsplan](design/Kundenprojekt_PJA/UMSETZUNGSPLAN.md), [Prüfbericht](design/Kundenprojekt_PJA/MVP-ABNAHME.md) |

### Noch prüfen

- Echten Kontaktversand und Antwort an die Besucheradresse bewusst testen; SMTP-Aktivierung allein beweist keine Zustellung.
- Texte, Bilder/Bildrechte und beide Sprachversionen vom Kunden sichten lassen. Die drei Blogtexte bleiben bis zur Freigabe Entwürfe.
- Gemeinsame Sicherung und Wiederherstellung von Datenbank und Medien sowie Proxy-Vertrauen und Cache-Regeln betrieblich prüfen.

Produktive Datenbank: `basel_homefixer`. `basel_homefixer_preview` bleibt die getrennte Vorschau, `DEMO` gehört zur Demo-Website. Zugangsdaten gehören ausschliesslich in private Serverkonfiguration.
