> **Aktueller Stand nach Betreiberkorrektur (25.10.2026):** Blog bleibt sichtbar. Neues reguläres Datenbankziel **basel_homefixer**, noch nicht angelegt; DEMO bleibt für Demo und basel_homefixer_preview bleibt Testumgebung. Frühere Nachträge zu DEMO beschreiben ausschliesslich den beobachteten Altbetrieb und sind als Ziele aufgehoben. Einstieg: Projekt-README, deploy/nas/DATENBANK.md und deploy/nas/PORTAINER.md. Keine produktive Umstellung erfolgt.
# MVP: Umsetzungsplan Basel Home Fixer

Stand: 24. Oktober 2026. Ersetzt den Frontend-only-Plan vom 19. Oktober; dieser und die damaligen Freigaben/Prompts sind unter archiv/20261019_* erhalten. Die früheren Designbasis-/Startseiten-Schritte wurden bereits umgesetzt und werden nicht erneut ausgeführt.

## 1. Referenzen und Vorrang

Aktuelles Kundenpaket: design/Kundenpaket_20261024/Kundenpaket. Bestandsanalyse: design/Kundenpaket_20261024/ANALYSE.md. Für die Umsetzung gelten die jüngsten Benutzeranweisungen, diese Planung und KUNDENFREIGABEN.md. Die Analyse dokumentiert einen früheren Kenntnisstand; ihre inzwischen überholten Rückfragen und Frontend-only-/Blogaufschub-Empfehlungen sind keine zusätzlichen Freigabeanforderungen.

Massgeblich sind 01-startseite2.html, 02-ueber-uns2.html, 03-dienstleistungen.html, 04-kontakt.html und 08-Projekte.html. 07-aktuelle-beitraege.html dient später als Blog-Gestaltungsreferenz. Beschädigte/ältere Varianten nicht verwenden. Kundenoriginale erhalten.

## 2. Ziel und Prioritäten

Fünf Kernseiten: Startseite, Dienstleistungen, Projekte, Über uns, Kontakt. Zusätzlich gehören ein kleiner vollständiger MongoDB-Blog und echter Kontaktversand zum erweiterten MVP. Vermietung bleibt ausschliesslich der bestehende Airbnb-Link. Tech Insights gehört nicht zum MVP.

| Priorität | Umfang |
| --- | --- |
| P0 | Einheitliche Gestaltung, vollständige Leistungen/Projekte/Team, DE/EN, Mobilansicht und echte Navigationsziele |
| P1 hoch | Kontaktformular mit serverseitigem Gmail-Versand, Validierung und Fehlerrückmeldungen |
| P1 danach | MongoDB-Blog: Kategorien, Bilder, Übersicht, Artikelansicht und drei prüfbare Entwürfe |
| Abschluss | Gemeinsame Funktions-/Browserprüfung und Betriebsanleitung |

## 3. Architektur und Gestaltung

React, TypeScript, Vite, Material UI, React Router und i18next erhalten. Aktiver Einstieg bleibt src/home/Layout.tsx mit zentralem Theme und gemeinsamem Header/Footer. Bestehende Bausteine wie PageSection und ContactCta weiterverwenden. Keine neue Seitenarchitektur, kein allgemeiner HTML-Renderer, kein neues CMS. Kunden-HTML als Referenz übertragen, nicht einbetten.

Bestehendes Spring-Boot-Backend und MongoDB später gezielt erweitern. Statische Kerninhalte dürfen weiterhin kein laufendes Backend benötigen; Blog und Kontakt zeigen verständliche Ausfallzustände. Startseite lädt keine Blog-/Homepage-Konfiguration oder Instagram-Einbettung nach. Bisherigen Integrationscode und Daten nicht pauschal löschen.

Neue Texte in den vorhandenen DE-/EN-Dateien pflegen. Englische Übersetzungen sind Entwickler-/KI-Entwürfe für die Kundensichtung. Optimierte Bildkopien unter public/assets, Originale unverändert. Gemeinsame Breite/Typografie/Abstände haben Vorrang vor abweichenden Einzelstilen der Vorlagen.

## 4. Dienstleistungen und Startseitenübersicht

Acht stabile Schlüssel in Kundenreihenfolge: kitchen, bathroom, heating, windows-doors, electrical, smart-home, energy, consulting. energy bleibt energetische Renovation; elektrische Installation ist electrical. Gemeinsamen serviceCatalog.ts verwenden.

Sieben Leistungen besitzen aufklappbare Detailabläufe. Mehr erfahren öffnet diese auf der Dienstleistungsseite; es ist dort kein Kontaktlink mehr. MUI Collapse/Accordion mit natürlicher Höhe, zugänglicher Tastaturbedienung, aria-expanded und zugeordneten IDs verwenden. Die 2000-Pixel-Grenze des HTMLs nicht übernehmen. Beratung ist eine getrennte Aktion zu /contactUs?service=<schluessel>#contact-form. Consulting führt direkt dorthin.

Startseite bleibt eine Übersicht mit den bisherigen fünf Leistungen kitchen, energy, heating, smart-home, consulting. Auswahl und Reihenfolge ausdrücklich festlegen, damit neue Katalogeinträge nicht automatisch dort erscheinen. Gleiche Titel, Schlüssel und Kurztexte aus den gemeinsamen Übersetzungen. Bestehenden allgemeinen Prozess und FAQ erhalten; fachliche Zusagen nicht erweitern.

## 5. Projekte und Annahmen aus dem neueren Stand

Eigene Seite /projects mit sechs statischen Projekten, Bildern, längeren Beschreibungen, Kennzahlen, Kategorienfiltern und Kontaktaufruf. Keine sechs zusätzlichen Projekt-Unterseiten. Die gelieferten Beschreibungen stehen direkt auf der Seite; ziellose Details-ansehen-Links entfallen.

Startseite mit drei Teasern erhalten. Im Projektschritt Navigation und Projekte-ansehen-Ziel auf /projects ausrichten; /#projects bleibt gültig. Einzelteaser dürfen Projektanker auf /projects erreichen. Inhalte/Kennzahlen über stabile IDs gemeinsam pflegen.

Neuere Dauern gelten: Küche 4–6 Monate, Balkon 2–4 Monate, Heizung 4–8 Monate. Annahme: Gesamtprojektdauer einschliesslich Planung/Lieferung. Diese Beschriftung ist eine Entwicklerannahme, keine zusätzliche Kundenbehauptung. Im Projektschritt Startseite entsprechend abgleichen, nicht vorher nebenbei ändern. Fehlverweis Gaustüre.jpg auf das vorhandene Haustüre.jpg korrigieren und Motiv sichten.

## 6. Kontakt mit echtem Gmail-Versand – eigener späterer Schritt

Name/E-Mail Pflicht, Telefon optional, acht Dienstleistungen, Nachricht und Einwilligung. Service-Vorwahl per URL, unbekannte Schlüssel neutral. Frontendzustände, verständliche DE-/EN-Validierung und nutzbare Telefon-/E-Mail-Links.

Kleiner POST /api/contact im bestehenden Backend. Serverseitige Pflichtfeld-/Format-/Längen-/Service-Prüfung, Honeypot und Versandbegrenzung. Empfänger und Absender fest serverseitig baselhomefixer@gmail.com; Besucheradresse nur Reply-To. Keine frei wählbaren Empfänger, keine automatische Bestätigungsmail und keine Speicherung der Anfragen in MongoDB im MVP.

SMTP ausschliesslich über Server-Umgebungsvariablen, niemals Geheimnisse in Git/Frontend/Logs. Gmail-SMTP mit App-Passwort vorsehen, sofern beim Kundenkonto verfügbar; andernfalls OAuth-Aufwand separat berichten. Begrenzte SMTP-Zeitlimits. Ohne konfigurierte Versandmöglichkeit klar unavailable anzeigen; Erfolg erst nach Annahme durch den Mailserver, keine garantierte Zustellung behaupten.

Fehlende Zugangsdaten blockieren nicht Implementierung und Tests mit SMTP-Testserver/Testdouble. Echter Testversand vom NAS erfordert gesonderte Freigabe und verfügbare Konfiguration. Security/CORS gezielt prüfen, keine pauschale Freigabe weiterer APIs.

## 7. Blog mit MongoDB – eigene spätere Schritte

Bestehende IDs und Daten erhalten. Modell ergänzen: Slug, DE-/EN-Titel/Vorschau/Markdown, Kategorie-Schlüssel, Titelbildpfad mit Alt-Texten, draft/published und Veröffentlichungsdatum. Alte Datensätze ohne Status nicht automatisch veröffentlichen.

Kleine feste Kategorienliste mit DE-/EN-Bezeichnungen; keine Kategorienverwaltung. Bilddateien zunächst public/assets/blog, MongoDB speichert Pfade. Neue Bilder brauchen Frontend-Build/Deployment; Texte laufen über einen geprüften Import. Kein Upload, GridFS oder separates CMS für diesen MVP.

Öffentliche API nur veröffentlichte Beiträge: begrenzte Listen mit Datumssortierung/Kategorie sowie Artikel per Slug. Auch alte Endpunkte dürfen keine Entwürfe offenlegen. Wiederholbarer Import aus Markdown mit Metadaten, Pflichtfeld-/Kategorie-/Bildpfadprüfung und Dry-Run. Keine Datenbankzugänge in Quelldateien. Produktive Datenimporte benötigen einen eigenen Auftrag und Sicherung.

Blogoberfläche /blog und /blog/:slug; vorhandenes /latestPosts bewusst weiterleiten/zuordnen. Sichere Markdown-Darstellung ohne ungeprüftes HTML oder unsichere Links. Lade-/Fehler-/Leerzustände, responsive Bilder, DE/EN. Drei neue Artikel aus den Kundeninformationen zu Küche, Heizung und Raumthermostaten/Smart Home zunächst draft. Persönliche Website-/Hosting-Beiträge nicht als Kundeninhalte veröffentlichen oder löschen. Keine erfundenen Ergebnisse/Zitate/Einsparungen. Lokale Entwurfssichtung isoliert, kein öffentlicher Vorschauparameter.

## 8. Verbindliche Reihenfolge und aktueller Status

Die neue Folge in UMSETZUNGSPROMPTS.md ersetzt die früheren fünf Frontend-only-Prompts. Jeder Auftrag endet separat; keinen nächsten Auftrag automatisch starten.

1. Plan/Freigaben aktualisieren und acht Dienstleistungen mit Details umsetzen – umgesetzt und geprüft am 24. Oktober 2026.
2. Eigene Projektseite und Verbindungen zur Startseite – offen.
3. Teamseite nach 02-ueber-uns2 – offen.
4. Kontaktfrontend plus Backend/Gmail-Versand – offen; Zugang parallel beschaffen.
5. MongoDB-Blogmodell, Lese-API und Import – offen.
6. Blogoberfläche und drei Artikelentwürfe – offen.
7. Gemeinsame Abnahme und Betriebsanleitung – offen.

Die Designbasis und Startseite aus der alten Folge bleiben Ausgangsstand. Neue Routen, Projektdauern, Kontaktbackend und Blog nicht im Dienstleistungsschritt vorwegnehmen.

## 9. Arbeitsgrenzen

Aktueller Schritt 1: nur Planungsdokumentation und Frontend. Keine Änderungen an Backend, MongoDB, SMTP, GitHub Actions/Workflows, ECR, NAS oder separatem demo.cesarjaquiery.ch. Spätere Schritte 4/5 erlauben gezielte Backend-Arbeit gemäss jeweiligem Auftrag; das ist keine Freigabe, diese jetzt auszuführen.

In allen Schritten vorhandene lokale Änderungen erhalten, keine automatischen Commits/Pushes/Deployments, echten E-Mails, produktiven Datenimporte oder Artikelveröffentlichungen. Aktivierung/Betrieb separat beauftragen. Keine fremden Änderungen bereinigen.

## 10. Abnahmekriterien und offene Betriebsfragen

- Fünf Kernseiten plus Blogübersicht/-artikel folgen einem gemeinsamen Stil.
- Acht Leistungen, sieben vollständig lesbare Detailabläufe, fünf Startseitenteaser und getrennte Kontaktaktionen.
- Projektkennzahlen konsistent; sechs Projekte mit Filtern und funktionierender Verlinkung.
- DE/EN, 390/768/1280 px, Bilder, Tastaturbedienung und Navigation geprüft.
- Kontaktvalidierung, Vorwahl, Mailinhalt/Reply-To, Versandfehler/Begrenzung und unavailable ohne Zugang getestet.
- MongoDB erhalten; Entwürfe über keine öffentliche Route/API erreichbar; Import prüfbar/wiederholbar.
- Drei Entwürfe lokal sichtbar, fachlich/übersetzerisch zur Kundensichtung; nichts stillschweigend veröffentlicht.
- TypeScript, Frontend-Build und in den entsprechenden Schritten Backend-Tests/Build geprüft; Grenzen berichtet.
- Betriebsanleitung zu Mailkonfiguration, Testfreigabe, Import/Sicherung, Medien und Veröffentlichung vorhanden.

Vor öffentlichem Betrieb verbleiben Mailzugang und freigegebener echter NAS-Test, Inhalts-/Bildfreigaben, Instagram-Ziel sowie echte Datenschutz-/Impressumsinhalte. Fachliche Zusagen aus Kundenreferenzen werden nicht als rechtlich oder technisch geprüft ausgegeben.

Startseiten-Verhalten im Dienstleistungsschritt: Mehr erfahren führt zu /services#service-<schluessel> und öffnet den passenden Detailbereich; Consulting bleibt ein direktes Beratungsziel.

## Prüfergebnis Schritt 1 (24. Oktober 2026)

- Acht Leistungen, sieben Aufklappbereiche mit 35 Schritten und 169 Punkten in DE/EN.
- Browserprüfung in lokalem Microsoft Edge: 390, 768 und 1280 px, alle Detailbereiche vollständig, kein horizontaler Überlauf, Enter/Leertaste, Zustand und Kontaktvorwahl-URLs geprüft.
- Startseite weiterhin fünf Leistungen in bisheriger Reihenfolge. Mehr-erfahren-Direktlinks öffnen den passenden Bereich automatisch und erhalten den Fokus; unbekannte Anker öffnen keinen Bereich.
- Keine API-Aufrufe oder Browser-Ausnahmen in der Dienstleistungs-/Startseitenprüfung.
- TypeScript und Produktionsbuild erfolgreich. Bestehende Bundlegrössenwarnung über 500 kB bleibt.
- 134 Backend-, Workflow- und aktuelle Kundenoriginal-Dateien per Prüfsumme unverändert. Keine anderen Arbeitspakete umgesetzt.
- Annahmen: englische Texte zur Kundensichtung übersetzt; Startseite bleibt eine bewusste Auswahl von fünf Leistungen. Projektdauern/Gesamtzeit sind für Schritt 2 dokumentiert und noch nicht im Frontend geändert. Kontaktformular/Versand folgen in Schritt 4.

## Nachtrag 24. Oktober 2026: dynamische Projekte und gemeinsame Medienbasis

Dieser ausdrücklich beauftragte Schritt ersetzt den statischen Projektansatz in Abschnitt 5 und die Build-gebundene Bildablage/Importplanung in Abschnitt 7. Projekte und Blog bleiben getrennte MongoDB-Inhaltstypen mit gemeinsamer privater Medienablage. Titelbilder und Varianten werden per serverseitiger ID ausgeliefert, ausschliesslich bei Referenz durch veröffentlichte Inhalte. Keine Bildbinärdaten in MongoDB, keine Bilder im Frontend-Build als zukünftige Pflegevoraussetzung.

Aktueller Stand: eigene Projektseite, Teamseite und Kontaktimplementierung sind vorhanden; echter Gmail-/NAS-Test und Betrieb bleiben separat. Jetzt umgesetzt: gemeinsame Inhaltsmodelle, öffentliche gefilterte Lese-APIs, lokale Medienverarbeitung, überprüfbarer Import mit sechs Entwürfen sowie dynamische Projektseite und drei dynamische Startseitenteaser. IDs/Anker, Gesamtprojektdauern, Gestaltung und Marketingkennzahlen bleiben erhalten. Alte Blogdaten/IDs werden nicht verändert oder automatisch öffentlich. Keine neue Blogoberfläche in diesem Schritt.

Die Auswahl/Reihenfolge der Startseitenprojekte liegt in `homeOrder`; gemeinsam verwendete Projektkennzahlen kommen aus demselben MongoDB-Dokument. Statische Kernbereiche funktionieren bei API-Ausfall weiter, der Projektbereich zeigt einen Fehler/Leerzustand ohne dauerhafte statische Ersatzquelle.

Nächste Aufträge, nicht automatisch beginnen:
1. Geschützte Verwaltung für Projekte und Blog, Upload, geschützte Entwurfsvorschau, bewusste Veröffentlichung. Vorher bestehende Authentifizierung absichern (Passworthashing, konsistente Tokens, Rollen und Schreibberechtigungen). Dieser Schritt allein ermöglicht noch keine eigenständige Kundenpflege.
2. Öffentliche Blogoberfläche und gesichtete Artikelentwürfe auf der gemeinsamen Grundlage.
3. Abnahme, isolierter MongoDB-/NAS-Integrationstest, Sicherung/Dateirechte, ausdrücklich freigegebener Import/Veröffentlichung und echter Gmail-Test.

Betrieb, Importbefehle, Modelle/API, Sicherheitslücken, Annahmen und Testergebnisse: `My-App_Backend/CONTENT.md`. Neue Arbeitsfreigabe umfasst diese Inhalte und gezielte Backend-/Frontend-Änderungen. Unverändert ausgeschlossen: produktive Datenänderungen, laufendes NAS/Compose, Workflows, separates Demo-Projekt, Commit/Push/Deployment und öffentliche Veröffentlichung. Die älteren schrittbezogenen Arbeitsgrenzen beschreiben den damaligen Stand.

## Nachtrag: lokale Vorschau mit echten NAS-Daten

Der anschliessende Einzelauftrag hat eine isolierte Vorschau freigegeben: sechs Projektbeiträge, gemeinsame Medienablage auf dem NAS und separate Atlas-Datenbank `basel_homefixer_preview`. Startseite und Projektseite nutzen diese Daten über einen lokalen, ausschliesslich lesenden SSH-Proxy. Die öffentliche Website bleibt auf ihrem bisherigen Stand. Details/Start in `NAS-VORSCHAU.md`; zusätzliche Blogartikel und Kundenverwaltung sind weiterhin offen. Bei einem echten HTTP-Integrationstest wurde die Fehlerweiterleitung korrigiert, damit gesperrte/nicht vorhandene Inhalte 404 statt 401 erhalten. Keine neuen Schreibendpunkte.

## Nachtrag 24. Oktober 2026: Authentifizierungsgrundlage

Backend-Anmeldung für die spätere Verwaltung umgesetzt: gemeinsame konfigurierte JWT-Ausstellung/-Prüfung, BCrypt, geschütztes `/api/admin/**`, aktuelle Rollen-/Revisionsprüfung, minimale eigene Kontoantworten und begrenzte Anmeldeversuche. Öffentliche Registrierung ist als dokumentierte Annahme standardmässig deaktiviert; optional nur normale Konten. Ohne Schlüssel Anmeldung nicht verfügbar, öffentliche Inhalte/Kontakt bleiben nutzbar.

Altkonten werden weder automatisch geändert noch hochgestuft. Ein expliziter lokaler Einrichtungs-/Reset-Weg mit Dry-Run ist vorbereitet; kein echter Verwaltungszugang angelegt. Einrichtung, Fehlercodes, Token-Speicherung/Ablauf, lokale Abmeldegrenze und Altpasswortverfahren in `My-App_Backend/AUTH.md`.

Nächster Schritt bleibt ausdrücklich ein eigener Auftrag: geschützte Inhaltsverwaltung im Backend, danach Verwaltungsoberfläche und Blog. Keine NAS-, produktiven Daten-, Workflow- oder Demo-Änderungen, kein Deployment in diesem Authentifizierungsschritt. Die zuvor laufende Inhaltsvorschau bleibt auf ihrem eigenen Runtime-Stand.

Prüfergebnis Authentifizierung: 57 Backend-Tests erfolgreich, darunter echte HTTP-Tests mit isolierten Konten; Maven-Verify/JAR-Build und Einrichtungs-Dry-Run erfolgreich. Kein echter Konto-/Index-Apply. Vor dem späteren produktiven Build die bestehende Übernahme von application-dev.properties in das JAR auflösen; Details in AUTH.md. Keine NAS- oder produktiven Datenänderungen.

## Nachtrag 25. Oktober 2026: Backend-Inhaltsverwaltung

Geschützte Verwaltungsfunktionen für Projekte, Blog und Medien umgesetzt. Gemeinsame bestehende Modelle, serverseitige Rechte, ETag/If-Match gegen stilles Überschreiben, explizite Veröffentlichung/Rücknahme, geschützte Entwurfsvorschau, Startseitenauswahl und wiederverwendete sichere Bildverarbeitung einschliesslich EXIF. API-Verträge und Betrieb: My-App_Backend/ADMIN.md.

Dokumentierte MVP-Annahme: keine parallele Live-/Entwurfskopie; vor Bearbeitung veröffentlichter Inhalte ausdrücklich zurückziehen. Neue Inhalte bleiben bis zur eigenen Veröffentlichung draft. Startseitenauswahl liegt gemeinsam versioniert in content_settings; bestehendes homeOrder bleibt nur Rückfall vor der ersten Auswahländerung.

Backend-Tests verwenden ausschliesslich isolierte Konten/Repository-Doubles und temporäre Medienordner. Echte MongoDB-Indexerstellung und NAS-Dateirechte bleiben im isolierten Integrationstest zu prüfen. Keine produktiven Daten, NAS-, Workflow-, Demo- oder Frontend-Änderungen. Kein Commit, Push oder Deployment. Nächster separater Auftrag: geschützte Verwaltungsoberfläche; öffentliche Blogoberfläche und spätere Betriebsfreigabe ebenfalls weiterhin separat.

Abschlussprüfung Verwaltungsbackend am 25. Oktober 2026: Maven verify und JAR-Build erfolgreich, 73 Tests ohne Fehler oder übersprungene Tests. API-/Konfigurationsdokumentation in My-App_Backend/ADMIN.md und admin.env.example vollständig ergänzt. Echte MongoDB-/NAS-Integration und Verwaltungsoberfläche bleiben offen.

## Nachtrag 25. Oktober 2026: Verwaltungsoberfläche

Geschützter Frontend-Bereich unter /admin auf vorhandener React-/TypeScript-/MUI-/i18next-Basis ergänzt. Umfasst Anmeldung/Abmeldung, Projekt-/Bloglisten, DE-/EN-Formulare, Kategorien, optionale Projektfakten, Medienauswahl/-upload und Alttexte, geschützte Vorschau, getrennte Speichern-/Publikationsaktionen und Startseitenauswahl. Eingaben bleiben bei Fehlern, Konflikten und erneuter Anmeldung erhalten; Navigation und Neuladen warnen bei ungespeicherten Änderungen. Token nur im Arbeitsspeicher, kein öffentlicher Vorschauparameter. Gemeinsame sichere Markdown-Darstellung für den späteren öffentlichen Blog vorbereitet.

Kundenanleitung: design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md. Technische Einrichtung und Testablauf: My-App_Backend/ADMIN.md. Unverändert offen: echte isolierte MongoDB-/NAS-Integration, Zugangseinrichtung, Kundensichtung, öffentliche Blogoberfläche und explizite Betriebsfreigabe. Keine produktiven Daten-, NAS-, Workflow- oder Demo-Änderungen; kein Deployment.


Abschlussprüfung Frontend-Verwaltung: Typecheck, Produktionsbuild und fünf gezielte Frontend-Tests erfolgreich; 73 Backend-Tests/Maven verify erfolgreich. Vollständiger lokaler Browserablauf mit echten HTTP-/Security-Handlern und isolierten Repository-Doubles erfolgreich, einschliesslich Projekt-/Blogveröffentlichung und Rücknahme, geschützter Bilder, Konflikten, erneuter Anmeldung, 503-/Netzwerkfehlern, DE/EN und 390/768/1280 px. Bestehende Bundlewarnung bleibt; Verwaltung separat geladen. Echter MongoDB-/NAS-Test nicht erfolgt. Lokalen Vite-Inhaltsproxy für Listen-Queryparameter korrigiert. Kundenanleitung und technische Test-/Betriebsgrenzen dokumentiert.

## Nachtrag 25. Oktober 2026: Blogoberfläche und Artikelentwürfe

Öffentliche Übersicht /blog und Artikel /blog/:slug auf gemeinsamer MongoDB-/Medienbasis umgesetzt; /latestPosts leitet weiter. Gemeinsames Layout/Header/Footer und Navigation, Kategorien und Seitennavigation, DE/EN sowie Lade-/Fehler-/Leer-/404-Zustände. Die gemeinsame Markdown-Darstellung in Verwaltung und öffentlichem Blog prüft jetzt zusätzlich die inhaltsspezifische Medienzuordnung; API-Detailantwort ergänzt mediaIds. Keine Blogbilder im Frontend-Build als Pflegevoraussetzung, keine zusätzlichen Startseiten-Blogaufrufe.

Drei neue Artikelentwürfe zu Küche, Heizung und Thermostaten aus Kundeninformationen vorbereitet: sechs Markdown-Dateien, Metadaten und reproduzierbares bestehendes JSON-Importformat. Alle draft ohne Veröffentlichungsdatum; Englisch als Übersetzungsentwurf markiert. Küche 4–6 und Heizung 4–8 Monate als angenommene Gesamtprojektdauer; keine Thermostatdauer erfunden. Import und Sichtung ausschliesslich isoliert, keine produktiven Daten oder persönlichen Altartikel verändert.

Gesondert autorisierter lokaler Gmail-Einzeltest erfolgreich: Mail vom SMTP-Server angenommen, Eingang und NAS-Betrieb nicht bestätigt. Passwort aus Projekt-Beispieldatei in private Windows-Konfiguration verschoben; STARTTLS zu Port 587 passend korrigiert. Keine NAS-/Containeränderung. Admin-Einrichtung über vorhandenen interaktiven Helfer dokumentiert und als Dry-Run geprüft; kein produktives Konto erstellt. Anleitung: design/Kundenprojekt_PJA/ADMIN-ZUGANG.md.

Abschlussprüfung dieses Schritts: Frontend-Typecheck und Produktionsbuild, neun gezielte Frontend-Tests sowie 74 Backend-Tests/Maven verify erfolgreich. Import-Dry-Run und isolierter wiederholbarer Transfer der drei Artikel mit Kundenbildern erfolgreich. Vollständiger lokaler Browsernachweis erfolgreich: private Kundenentwurfssichtung, öffentliche Entwurfs-/Bildsperre, synthetische öffentliche Artikel/Listen, Filter/Pagination, sichere gemeinsame Markdown-Darstellung, /latestPosts-Redirect, DE/EN, direkte URLs, 390/768/1280 px, Mobilnavigation/Tastatur, Fehler/Wiederholen und Startseite bei API-Ausfall. Screenshots visuell geprüft. Keine echte Datenbank, NAS-Konfiguration oder Workflows geändert; keine Kundenartikel veröffentlicht. Lokale Test-Fixture und Test-Vite nach Prüfung beendet. Nächster Auftrag wird nicht automatisch begonnen.

## Nachtrag 25. Oktober 2026: regulärer NAS-Betrieb vorbereitet

Die laufende Umgebung wurde ausschliesslich lesend geprüft. Produktive Inhaltsdatenbank ist bestätigt DEMO, private Vorschau separat basel_homefixer_preview. Der öffentliche Zugang läuft über den bestehenden Cloudflare-Tunnel und Traefik; die genaue Dashboard-Service-Zuordnung und Proxy-Peer-Kette sind noch zu bestätigen.

Konkrete Vorlagen und Umstellungsanleitung: deploy/nas/README.md. Enthalten sind getrennte bereinigte Release-Kontexte, unveränderliche Images, obligatorische externe Spring-Konfiguration, privater dauerhafter Medienordner, auf DEMO begrenzter technischer DB-Zugang, stabile Authentifizierung, zunächst deaktivierter Mailversand, zwei gesonderte Website-Admin-Konten und ein isolierter Vorabtest. Die bekannten GitHub Actions bleiben unverändert; notwendige spätere Anpassungen des bisherigen :dev-/Dockerfile-Buildweges sind benannt.

Gesichtete Übertragung vorbereitet: sechs Projekte und drei Blogartikel aus Repository-Importdaten, sechs Bildquellen, alle draft ohne Veröffentlichungsdatum. Keine Übernahme der kompletten Vorschau-DB oder ihrer Konten. Vor Import/Kontoanlage muss der alte öffentliche Backendstand abgeschaltet und der neue Entwurfsschutz geprüft sein. Import und Veröffentlichung sind getrennte Freigaben.

Validiert: lokale YAML-/Policyprüfungen, beide Compose-Dateien mit synthetischen Werten durch tatsächlichen NAS-Compose-Parser akzeptiert, separate JAR-/Build-Kontexte und Archivintegrität/Boot-Loader, Übertragungspaket und Zielkonfliktcheck mit Doubles. Keine Images gebaut oder Container gestartet; echter isolierter MongoDB-/NAS-Test und Wiederherstellungstest bleiben vor Rollout erforderlich. Keine produktiven Daten, Konten, Dateien, NAS-Konfiguration, Workflows oder Demo geändert; keine E-Mail und keine Veröffentlichung. Nächster Auftrag nicht automatisch begonnen.


## Aktuelle Entscheidung des Betreibers: getrennte reguläre Datenbank

Die frühere Empfehlung, die beobachtete DEMO-Datenbank weiterzuverwenden, ist aufgehoben. DEMO gehört zur Demo-Website und bleibt unberührt. Neues vorgesehenes Ziel ist basel_homefixer; noch nicht angelegt. basel_homefixer_preview bleibt die isolierte Test-/Vorschauumgebung. Cluster und Datenbank unterscheiden: Der endgültige Cluster wird bewusst durch den Betreiber gewählt, kein Demo-exklusiver Cluster wird automatisch übernommen. Details: deploy/nas/DATENBANK.md und Projekt-README. Blog bleibt auf ausdrücklichen Wunsch sichtbar; Projekte sind Referenzfälle, Blog ist redaktioneller Ratgeber. Keine produktive Änderung durch diese Dokumentationskorrektur.


Zusammenhängende Abschlussprüfung und aktueller Betriebsumfang: [MVP-ABNAHME.md](MVP-ABNAHME.md). Sprachpersistenz korrigiert; 74 Backend- und 9 Frontendtests sowie Build erfolgreich. Keine produktive Freigabe, echter isolierter MongoDB-/NAS-Test bleibt offen.


## Folgeauftrag: CI und Push

Bestehende Workflows gezielt angepasst: Tests, bereinigtes JAR, Docker-Kontexte mit Positivliste, Containerproben vor ECR-Push, dev-/sha-Tags. Ausschliesslich Push auf main veröffentlicht Images; ausdrücklicher aktueller Auftrag ist nur Commit/Push auf basel-homefixer, keine Übernahme nach main und kein NAS-Deployment. Details: deploy/nas/PIPELINE.md.

