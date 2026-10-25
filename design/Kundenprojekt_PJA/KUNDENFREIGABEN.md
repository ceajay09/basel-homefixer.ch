# Kundenfreigaben und aktuelle Vorgaben

Stand: 24. Oktober 2026. Aktualisiert anhand des neuen Kundenpakets und der anschliessenden Benutzeranweisungen. Die frühere Fassung vom 19. Oktober liegt unter archiv/20261019_KUNDENFREIGABEN.md. Unterscheidung: Kundeninhalte stammen aus den HTMLs; Umfang und Arbeitsfreigaben stammen aus dem Projektchat.

## Referenzen und Umfang

- Aktueller Referenzordner: design/Kundenpaket_20261024/Kundenpaket.
- 01-startseite2.html und 02-ueber-uns2.html bleiben massgeblich; beschädigte/ältere Versionen ignorieren.
- Fünf Kernseiten: Startseite, Dienstleistungen, Projekte, Über uns, Kontakt.
- Eigene Projektseite nach 08-Projekte.html zusätzlich zur Startseitenübersicht.
- Blog und echter Kontaktversand gehören jetzt ausdrücklich zum erweiterten MVP; frühere Zurückstellungen sind überholt.
- Vermietung bleibt der bestätigte Airbnb-Link. Tech Insights bleibt ausgeschlossen.
- React/TypeScript/MUI/Router/i18next, Layout.tsx und gemeinsames Theme erhalten; minimaler Umbau, gemeinsamer roter Faden.

## Dienstleistungen und Aktionen

- Acht Leistungen nach neuem 03-dienstleistungen.html, davon sieben mit aufklappbaren Abläufen.
- Mehr erfahren öffnet dort Details; Beratung führt getrennt zum Kontaktziel. Consulting direkt zum Kontakt.
- Schlüssel: kitchen, bathroom, heating, windows-doors, electrical, smart-home, energy, consulting. Bestehende Schlüssel nicht umdeuten.
- Erstberatung führt zu /contactUs#contact-form; Dienstleistungsberatung zu /contactUs?service=<schluessel>#contact-form.
- Startseite behält die bisherigen fünf ausgewählten Leistungen mit gemeinsamen Titeln/Schlüsseln/Texten.

## Neuere Angaben und ausdrücklich dokumentierte Annahmen

Benutzervorgabe: neuere Kundeninformationen verwenden; wenn das nicht sinnvoll möglich ist, Annahme treffen und im Ergebnis nennen. Die beiden früheren Rückfragen sind deshalb keine Voraussetzung für weiteres Arbeiten.

- Neuere Dauern Küche 4–6, Balkon 2–4, Heizung 4–8 Monate gelten. Annahme: als Gesamtprojektdauer ausweisen. Abgleich erfolgt im Projektschritt.
- Projekt-Langbeschreibungen direkt auf /projects; ziellose Details-ansehen-Buttons entfallen. Keine weiteren Detailseiten erfinden.
- Projektnavigation künftig /projects, vorhandenen Startseitenanker /#projects erhalten. Umstellung erst im Projektschritt.
- Neue Blogartikel zunächst Entwürfe; keine automatische öffentliche Freigabe durch diese Planung.
- Englisch wird auf Grundlage der deutschen Kundeninhalte übersetzt und zur Sichtung ausgewiesen.

## Kontaktversand im späteren Kontaktauftrag

- Backendversand vom Gmail-Konto an dasselbe Postfach baselhomefixer@gmail.com; Besucheradresse als Reply-To.
- Serverseitige Konfiguration, Validierung, Honeypot/Versandbegrenzung; keine Zugangsdaten in Repository oder Frontend.
- Keine automatische Besucher-Bestätigung und keine MongoDB-Speicherung der Kontaktanfragen im MVP.
- App-Passwort nur bei Kontoverfügbarkeit; Zugang und echter Versand vom NAS noch nicht geprüft.
- Ohne Konfiguration klar nicht verfügbar, keine vorgetäuschte Erfolgsmeldung. Echtversand erst separat freigeben; bis dahin Tests mit Testserver/Testdouble.

## Blog im späteren Blogauftrag

- MongoDB bleibt; bestehende Daten/IDs erhalten. Modell für Kategorien, Bildpfade, Slugs, DE/EN und draft/published erweitern.
- Öffentliche API nur veröffentlichte Inhalte. Alte Daten ohne Status nicht automatisch veröffentlichen.
- Bilder zunächst statische Dateien, Artikelpflege per geprüftem Markdown-Import mit Dry-Run; keine Adminoberfläche oder Uploadfunktion im MVP.
- Drei Kundenartikelentwürfe zu Küche, Heizung und Raumthermostaten/Smart Home; persönliche Altbeiträge nicht als Kundenartikel ausgeben.

## Aktuelle Arbeitsfreigabe und Grenzen

Der aktuell beauftragte Schritt umfasst Plan-/Freigabedokumentation und Frontend-Dienstleistungen. Backend, Datenbank, SMTP, Workflows, NAS und separates Demo-Projekt bleiben jetzt unverändert. Die Folgeaufträge werden einzeln gestartet. Vorhandene lokale Änderungen und Kundenoriginale erhalten. Keine Commits, Pushes, Deployments, produktiven Datenimporte, öffentlichen Artikelveröffentlichungen oder echten Testmails ohne gesonderten Auftrag.

Fachliche Behauptungen, Garantie-/Versicherungsangaben und Übersetzungen vor Veröffentlichung durch den Kunden sichten lassen. Fehlende Instagram-/Datenschutz-/Impressumsziele bleiben offene Inhalte und werden nicht erfunden.

Startseiten-Verhalten im Dienstleistungsschritt: Mehr erfahren führt zu /services#service-<schluessel> und öffnet den passenden Detailbereich; Consulting bleibt ein direktes Beratungsziel.

## Ergänzende Arbeitsfreigabe 24. Oktober 2026: gemeinsame Inhalte/Medien

Der neue Auftrag ersetzt die frühere Festlegung auf statische Projekte und Frontend-Bildpfade: Projekte und Blog verwenden MongoDB-Metadaten und einen gemeinsamen privaten, dauerhaften Medienordner. Öffentliche Bildauslieferung nur bei Bezug zu veröffentlichten Inhalten. Die sechs Projekt-IDs, Texte, Zahlen und Bilder werden als geprüfte Importdaten erhalten; sicherer Ausgangsstatus ist draft, keine automatische Veröffentlichung. Küche/Balkon/Heizung bleiben mit den neueren Monatsangaben als angenommene Gesamtprojektdauer beschriftet.

Projektseite und drei Startseitenteaser beziehen Inhalte nun dynamisch, Design und bestehende Anker bleiben. 20+/40+/16 werden nicht aus Referenzprojekten berechnet. Deutsch und Englisch aus dem bisherigen Stand werden in den Importdaten übernommen; die Übersetzungen bleiben zur Kundensichtung.

Freigegeben sind gemeinsame Modelle, Lese-API, lokale Medienverarbeitung/Import mit Dry-Run und dynamisches Projektfrontend. Verwaltungsoberfläche, Upload-Endpunkte, Entwurfsvorschau und bewusste Veröffentlichung folgen erst in einem eigenen Auftrag nach Absicherung der vorhandenen Authentifizierung. Keine produktive Datenübertragung, Veröffentlichung, NAS-/Workflow-/Demo-Änderung, Commit, Push oder Deployment. Einzelheiten und verbleibende Betriebsprüfungen in `My-App_Backend/CONTENT.md`.
