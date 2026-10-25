# Umsetzung in sieben aufeinanderfolgenden Aufträgen

Stand: 24. Oktober 2026. Diese Folge ersetzt die frühere Frontend-only-Folge; Original unter archiv/20261019_UMSETZUNGSPROMPTS.md. Die frühere Designbasis und Startseite sind bereits umgesetzt. Massgeblich sind UMSETZUNGSPLAN.md, KUNDENFREIGABEN.md und die jeweils jüngste Benutzeranweisung.

Aktuelle Referenzen: design/Kundenpaket_20261024/Kundenpaket. Keine Kundenoriginale verändern. Bestehende lokale Änderungen erhalten. Nicht committen, pushen oder deployen. Keinen nächsten Auftrag automatisch beginnen. Neuere Kundenangaben verwenden, Annahmen berichten. Gemeinsame Architektur und Theme erhalten.

## Prompt 1 – Plan und Dienstleistungen (aktueller Auftrag)

Plan/Freigaben auf fünf Kernseiten, Blog und echten Mailversand aktualisieren. Services.tsx nach neuem 03-dienstleistungen.html mit acht Leistungen, sieben natürlich hohen Aufklappbereichen und separaten Beratungszielen erweitern. Schlüssel kitchen, energy, heating, smart-home, bathroom, consulting erhalten; windows-doors und electrical ergänzen. Startseite bei fünf ausgewählten Leistungen konsistent halten. Prozess/FAQ erhalten. Nur Planung und Frontend, kein Backend. DE/EN, Mobilansicht, Bedienung, TypeScript und Build prüfen.

## Prompt 2 – Projektseite und Startseitenverbindungen

Neue Projects.tsx und /projects nach 08-Projekte.html: sechs Projekte, lokale Filter, längere Beschreibungen, Bilder, Kennzahlen und Kontaktaufruf. Bestehendes Layout statt Vorlagenheader. Gaustüre.jpg-Verweis auf passendes Haustüre.jpg korrigieren, optimierte Kopien erzeugen. Projekt-IDs/Metadaten und mehrsprachige Kurz-/Langtexte gemeinsam für Startseite und Projektseite pflegen.

Neuere Dauern als angenommene Gesamtprojektdauer übernehmen: Küche 4–6 Monate, Balkon 2–4 Monate, Heizung 4–8 Monate. Alte Startseitenwerte abgleichen. Beschreibungen direkt anzeigen, ziellose Details-ansehen-Links weglassen; keine zusätzlichen Unterseiten. Navigation/Projekte ansehen auf /projects, Teaser ggf. auf Projektanker. /#projects erhalten. Nur Frontend. Bilder, Filter, Routensprünge, DE/EN, Tastatur, 390/768/1280 px, TypeScript und Build prüfen.

## Prompt 3 – Über uns / Team

AboutUs.tsx nach 02-ueber-uns2.html: gemeinsames Bild, Pascal/Divine-Porträts, Rollen, Kompetenzen, Vision/Werte und Kontaktaufruf. Gemeinsames Theme/Layout erhalten, Bilder optimieren und DE/EN pflegen. Keine Qualifikationen erfinden. Nur Frontend. Mobil/Desktop, Bilder, Kontaktziel, TypeScript und Build prüfen.

## Prompt 4 – Kontakt mit Backend und Gmail

Kontaktfrontend nach 04-kontakt.html, acht Service-Schlüssel, URL-Vorwahl und contact-form-Anker. Pflicht-/Format-/Längenprüfung, verständliche DE-/EN-Zustände, Eingaben bei Fehlern erhalten, Doppelversand verhindern, echte Kontaktlinks.

Dieser Auftrag erlaubt gezielte Backendarbeit: POST /api/contact, serverseitige Validierung, fester Absender/Empfänger baselhomefixer@gmail.com, Besucheradresse Reply-To, Honeypot/Versandbegrenzung, Spring-Mail mit Zeitlimits. Keine Anfragespeicherung, keine automatische Bestätigungsmail. Server-Umgebungsvariablen und geheimnisfreie Beispielkonfiguration. Gmail-App-Passwort bei Verfügbarkeit; fehlende Verfügbarkeit/OAuth-Aufwand berichten. Ohne Konfiguration unavailable; Erfolg nur nach SMTP-Annahme, keine Zustellgarantie.

Security/CORS gezielt prüfen, keine pauschalen Freigaben. Mit Testserver/Testdouble testen; keine echten E-Mails ohne gesonderte Freigabe. Kontaktbezogene Frontend-/Backend- und Dokumentationsarbeit erlaubt; keine Workflows, laufende NAS-Konfiguration, Datenbank oder Demo ändern. Passende Backend-Tests sowie Frontend-Typecheck/Build, Validierung/Reply-To/Fehler/Begrenzung testen. Offenen Gmail-/NAS-Echttest benennen.

## Prompt 5 – MongoDB-Blogmodell, Lese-API und Import

Bestehendes Blogmodell um Slug, DE-/EN-Titel/Vorschau/Markdown, Kategorien, Titelbildpfad/Alt-Texte, Status und Datum erweitern. MongoDB und Daten/IDs erhalten. Kleine feste Kategorienliste, Bilder /assets/blog; keine Bilddaten in MongoDB.

Öffentliche Liste mit begrenzter Seitengrösse, Datumssortierung/Kategorie, Artikel per Slug. Ausschliesslich published; auch Altendpunkte dürfen keine Entwürfe offenlegen. Altdaten ohne Status nicht automatisch veröffentlichen. Wiederholbarer Markdown-/Metadatenimport mit Prüfungen, Dry-Run und stabilen IDs. Keine produktiven Datenänderungen; Migration/Import dokumentieren. Keine Adminoberfläche, öffentlichen Schreibendpunkte oder Uploads. Entwurfsfilterung, Kategorien, Sortierung, Eindeutigkeit und Import in isolierter Umgebung testen. Gezielte Backendarbeit erlaubt; keine Workflow-/NAS-/Demo-Änderungen.

## Prompt 6 – Blogoberfläche und Artikelentwürfe

Blogübersicht /blog und Artikel /blog/:slug nach 07-aktuelle-beitraege.html und gemeinsamem Stil. Kategorien, Seitennavigation, Bilder, DE/EN, Lade-/Fehler-/Leerzustände. /latestPosts bewusst zuordnen/weiterleiten, Altintegration nicht pauschal löschen. Startseite ohne Blog-/Instagram-Abhängigkeit.

Markdown sicher darstellen: kein ungeprüftes HTML/unsichere Links. Optimierte Bilder public/assets/blog; neues Bild braucht Build/Deployment. Drei DE-/EN-Markdownentwürfe aus Kundeninfos zu Küche, Heizung und Raumthermostaten/Smart Home erstellen. Keine erfundenen Ergebnisse/Zitate/Einsparungen. Neuere Zeitwerte als Gesamtprojektdauer. Alles draft ohne fiktives Veröffentlichungsdatum; Altbeiträge weder als Kundenartikel veröffentlichen noch löschen.

Lokale Sichtung isoliert, keine öffentlichen Vorschauparameter/Entwurfsendpunkte. Navigation erst bei fertiger Oberfläche ergänzen; bei null veröffentlichten Artikeln ehrliche Leermeldung. Übersicht/Artikel/Kategorien/Bilder/URLs/Mobil/DE-/EN/Tastatur prüfen, Typecheck/Build und passende API-Tests. Keine öffentlichen Veröffentlichungen, produktiven Statusänderungen, Workflows/NAS/Demo ändern.

## Prompt 7 – Gesamtabnahme und Betriebsanleitung

Fünf Kernseiten, Blogübersicht und Artikel zusammen prüfen: gemeinsamer Stil, DE/EN, 390/768/1280 px, Bilder, Navigation/Tastatur, Projektanker, acht Services/Vorwahl, Kontakt und Airbnb. Backend-Ausfälle müssen verständlich sein; statische Inhalte bleiben nutzbar. Kein vorgetäuschter Versand, keine API-Schleifen/Instagram-Abhängigkeit.

Kontakt mit Testserver/Testdouble und Blog/Import isoliert prüfen. Keine echten Mails oder produktiven Daten-/Statusänderungen. Neue Funktionsgrenzen prüfen: feste Empfänger, Reply-To, Validierung/Begrenzung, Geheimnisse, sichere Markdown-Darstellung und Entwurfsfilterung. Keine allgemeine Sicherheits-Neuentwicklung.

Betriebsanleitung: Mail-Variablen ohne Geheimnisse, Gmail-Voraussetzungen, später separat freizugebender NAS-Test, Markdown/Dry-Run/Import, Bild-Deployment, bewusste Artikelveröffentlichung, Sicherung/Rücknahme und fehlende Inhalte/Zugänge. Abschliessend Typecheck/Frontend-Build und passende Backend-Prüfungen; Platzhalter npm test ist kein Nachweis. Ergebnis, Annahmen, Kundensichtung und verbleibende Aktivierungsschritte berichten.

## Separater Betriebsschritt

Gmail-Konfiguration auf dem NAS, echter Testversand, produktive Datenimporte, Artikelveröffentlichung und Deployment benötigen einen ausdrücklichen Auftrag. Diese Folge führt sie nicht automatisch aus.
