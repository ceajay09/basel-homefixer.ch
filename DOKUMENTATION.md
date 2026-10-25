# Dokumentationsübersicht

[Zurück zum README](README.md)

## Kundenpflege

Die **[Kundenanleitung](design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md)** erklärt alle verfügbaren Pflegefunktionen ohne NAS-, Datenbank- oder Programmierkenntnisse. Sie ist der Einstieg für die tägliche Arbeit.

## Betrieb und Entwicklung

| Dokument | Verwenden für |
| --- | --- |
| [Adminzugänge](design/Kundenprojekt_PJA/ADMIN-ZUGANG.md) | Einrichtung und Wiederherstellung der Website-Konten; nur Betreiber |
| [NAS-Kurzcheckliste](deploy/nas/KURZANLEITUNG.md) | Aktueller Betriebsstand und nächste Aufgaben |
| [NAS-Betriebsanleitung](deploy/nas/README.md) | Ausführliche Übertragung, Sicherung, Wiederherstellung und Rücknahme |
| [Portainer](deploy/nas/PORTAINER.md) | Stack, Mounts und private Umgebungsvariablen |
| [Pipeline](deploy/nas/PIPELINE.md) | Builds, ECR-Images und manuelle Stack-Aktualisierung |
| [Datenbank](deploy/nas/DATENBANK.md) | Produktionsziel und Abgrenzung zu Vorschau/Demo |
| [Inhalte und Medien](My-App_Backend/CONTENT.md) | Datenmodell und Import als einmaliges Übertragungswerkzeug |
| [Authentifizierung](My-App_Backend/AUTH.md) | Passwortprüfung, Token und serverseitige Rechte |
| [Verwaltungs-API](My-App_Backend/ADMIN.md) | Geschützte Bearbeitung, Upload und Veröffentlichung |
| [Kontaktversand](My-App_Backend/CONTACT.md) | Gmail-Konfiguration, Test und Versandgrenzen |
| [Lokaler Start](LOKALER-START.md) | Entwicklung auf dem eigenen Rechner |
| [CI-Fehlerhilfe](.github/CI-TROUBLESHOOTING.md) | Bekannte GitHub-Actions-Probleme |

## Planung und historische Nachweise

[Umsetzungsplan](design/Kundenprojekt_PJA/UMSETZUNGSPLAN.md), [Kundenfreigaben](design/Kundenprojekt_PJA/KUNDENFREIGABEN.md) und [MVP-Prüfbericht](design/Kundenprojekt_PJA/MVP-ABNAHME.md) dokumentieren Entscheidungen und tatsächlich ausgeführte Prüfungen. Datierte Prüfungen sind keine Garantie für jeden späteren Produktionsstand.

[NAS-Vorschau](NAS-VORSCHAU.md) beschreibt die frühere isolierte Lesevorschau. Sie ist keine Startanleitung für die aktuelle öffentliche Website. Kundenvorlagen unter `design/` bleiben Designreferenzen, keine Anleitung zur laufenden Inhaltspflege.

Bei älteren Aussagen wie „nur vorbereitet“ gilt für den aktuellen Betriebsstatus das [README](README.md). Technische Vorlagen immer mit der tatsächlich laufenden Konfiguration abgleichen.
