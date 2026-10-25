# Zusammenhängende MVP-Abnahme – 25. Oktober 2026

Ergebnis: Anwendung und vereinbarte Abläufe lokal geprüft; regulärer NAS-Rollout **nicht ausgeführt und noch nicht freigegeben**. Echter MongoDB-/NAS-Containertest und Wiederherstellungsnachweis stehen aus. Das ist keine pauschale Produktionsfreigabe.

## Aktueller Umfang und Korrekturen

- Blog bleibt ausdrücklich sichtbar. Projekte = konkrete Referenzfälle; Blog = redaktionelle Erklärungen/Ratgeber. Technisch sind beide dynamische Inhaltstypen mit gemeinsamer Medienbasis. Drei Kundenartikel bleiben Entwürfe, keine Veröffentlichung erfolgt.
- Neues reguläres DB-Ziel **basel_homefixer**, noch nicht angelegt. DEMO bleibt Demo, basel_homefixer_preview bleibt Test. Der Produktionscluster ist vom Betreiber bewusst auszuwählen; separate DB bedeutet keine eigene Hardware-/Ressourcentrennung.
- Ähnlich zum alten Stack vorbereitete Portainer-Vorlage: drei Dienste, stack.env, bekannte Ports/Netzwerknamen. Privater Mount, neues DB-Ziel, externe Konfiguration, geprüfte Image-Digests und begrenztes Proxyvertrauen ergänzen die notwendigen Betriebsgrenzen.
- Gefundenen Sprachfehler behoben: festes lng=en setzte DE bei vollständigem Neuladen zurück. Vorhandener i18next-Detector übernimmt jetzt gespeicherte DE/EN-Auswahl; Produktions-Debugausgaben deaktiviert. Persistenz gezielt im Browser bestätigt.
- Ein historischer kommentierter MongoDB-Zugangswert aus der ungenutzten lokalen Legacy-Compose-Datei entfernt. Aktive Konfiguration nicht geändert. Private Betreiberdateien im Gitignore ergänzt. Gültigkeit/Widerruf des alten Zugangswerts nicht geprüft; bei möglicher weiterer Nutzung kontrolliert ersetzen.
- Projekt-README als kurzer Einstieg neu gegliedert; bisherige lokale Startanleitung nach LOKALER-START.md erhalten. Historische DB-Empfehlungen deutlich als überholt markiert, Admin-/Backup-/Importvorlagen auf neues Ziel angepasst.

## Tatsächlich ausgeführte Prüfungen

| Prüfung | Nachweis / Ergebnis |
| --- | --- |
| Backend | Maven offline verify erfolgreich: **74 Tests**, 0 Fehler, 0 Fehlschläge, 0 übersprungen; JAR-Build erfolgreich |
| Frontend nach Sprachkorrektur | TypeScript --noEmit, **9 gezielte Vitest-Tests**, Produktionsbuild erfolgreich; npm-test-Platzhalter nicht verwendet |
| Alle sieben öffentlichen Seiten | Startseite, Dienstleistungen, Projekte, Über uns, Kontakt, Blogliste, Artikel: DE/EN bei 390/768/1280 px; ein main/H1, keine horizontale Überbreite, keine erkannten defekten Bilder oder sichtbaren Übersetzungsschlüssel |
| Bilder / visuelle Sichtung | Alle sechs dynamischen Projektbilder im gezielten Nachtest vollständig dekodiert; Desktop-Projektansicht, mobile Team-/Verwaltungsansicht visuell geprüft. Responsive Bildpfade, Varianten und Bildzuschnitte erhalten |
| Navigation / Bedienung | Mobile Navigation mit Tastatur öffnen/Link/Schliessen; sieben Leistungsdetails plus sechs FAQ, Tastatur-Aufklappen; acht Servicevorwahlen und neutraler unbekannter Wert; vorhandenes Airbnb-Ziel; Projekt-/Startseitenanker mit Fokus nach asynchronem Laden |
| Öffentliche dynamische Inhalte | Projektfilter mit tatsächlichem API-Ergebnis; drei Startseitenteaser; Blogfilter, Pagination, direkte Artikeladresse, latestPosts-Weiterleitung, leere Kategorie, zurückgezogener Artikel und sichere Markdown-Ausgabe |
| Verwaltung | Echter React-/HTTP-Ablauf: Login → Entwurf → DE/EN → Bild hochladen → speichern → geschützte Vorschau → bewusst veröffentlichen → öffentliche Artikel-/Bild-API 200 → zurückziehen → beide öffentlich 404. Vor Veröffentlichung war das neue nur diesem Entwurf zugeordnete Bild öffentlich 404 |
| Verwaltungsdarstellung | Formular in DE/EN bei 390/768/1280 px ohne Überbreite; mobile Ansicht visuell geprüft |
| Kontakt | Browserprüfung DE/EN bei drei Breiten, Pflichtfelder/ungültige E-Mail, alle Service-Schlüssel, Erhalt bei Fehlern, 429/503, Lade-/Annahmezustand, kein Doppelversand. Nur abgefangene Testantworten; Backend zusätzlich echter lokaler SMTP-Sink ohne externe Zustellung |
| Backendausfall | Statische Kernseiten weiter darstellbar; Projekte/Blog verständlicher Fehler mit Wiederholung; Kontakt deaktiviert; keine API-Dauerschleife/Startseiten-Blog- oder Instagram-Abhängigkeit; keine Browserausnahmen im erfolgreichen Gesamtlauf |
| Konfiguration | Lokale YAML-/Policychecks und Zielkonfliktcheck mit Doubles erfolgreich. Neue Portainer-Vorlage mit synthetischen Werten über stdin durch tatsächlichen NAS-Compose-Parser akzeptiert, ohne Datei-/Containeränderung |
| Release-Verpackung | Neues getrenntes Releasepaket aus final geprüften Artefakten, sieben nichtproduktive Ressourcen ausgeschlossen, leere Pflichtressource für bestehende PropertySource-Annotation, 305 JAR-Einträge/CRC/Boot-Loader geprüft; kein Anwendung-/Adminstart. Keine Docker-Images gebaut |
| Workflowbestand | SHA-256 beider bestehenden GitHub Actions unverändert |

Screenshots/temporäre Prüfartefakte lokal unter C:\Temp\basel-mvp-abnahme; Kontaktprüfung unter C:\Temp\basel-contact-20261024. Finale bereinigte Build-Kontexte: C:\Temp\basel-mvp-release-abnahme-20261025. Übertragungspaket mit korrigiertem Ziel: C:\Temp\basel-nas-transfer-corrected-20261025. Diese Temp-Dateien sind kein dauerhaftes Betriebsbackup.

## Isolation und Sicherheitsgrenzen

Backendtests verwenden echte eingebettete HTTP-/Security-Handler, isolierte Konten/Repository-Doubles und temporäre Medienordner; MongoDB-Query-/Mapping-/Indexverträge werden mit Doubles geprüft. Die Browser-Test-API schliesst MongoDB-Autokonfiguration aus. Neun vorbereitete Kundenentwürfe wurden ausschliesslich in den temporären In-Memory-Import geladen; synthetische Testartikel und Publikationsstatus nur dort erzeugt. Kein echter MongoDB-Apply.

Die 74 Tests decken insbesondere BCrypt/Altpasswortgrenzen, gültige/manipulierte/abgelaufene Tokens, aktuelle ADMIN-Berechtigung, Registrierung ohne Rechteausweitung, Loginbegrenzung, geschützte Schreib-/Vorschau-/Uploadwege, Slug-/Versionskonflikte, Entwürfe und direkte Medienzugriffe, sichere Medienzuordnungen, Format-/Grössen-/Pixel-/Pfadprüfung, EXIF und Verarbeitungsbegrenzung sowie feste Mailempfänger/Reply-To/Validierung/Rate-Limits ab. Frontend und Vorschau verwenden dieselbe sichere Markdown-Darstellung. Keine ungeprüften HTML-/JavaScript-Links im geprüften Artikel ausgeführt.

Zusätzliche Grenzen bleiben: Abmeldung entfernt das Token lokal; bis zum Ablauf kann es ohne serverseitige Revisionsänderung weiter gültig sein. Kein Refresh-Token und keine öffentliche Passwortreset-/Benutzerverwaltungsoberfläche. Beide Website-Admins haben dieselben Rechte. Veröffentlichte Inhalte müssen vor Bearbeitung zurückgezogen werden; keine parallel öffentliche alte Version. Gemeinsam von veröffentlichten Inhalten verwendete Bilder dürfen trotz einer anderen Entwurfszuordnung öffentlich bleiben. Keine allgemeine Löschfunktion.

Private lokale Entwicklungsdaten sind weiterhin von Git ausgeschlossen; das normale lokale Maven-JAR enthält Entwicklungsressourcen und darf nicht unverändert als Produktionsimage verteilt werden. Geprüft wurde die bereinigte Release-Kopie. Tatsächliche ECR-/Basisimages und deren Layer wurden mangels lokalem Docker nicht gebaut oder untersucht. Keine pauschale Aussage über unverändert alte Images oder vollständige Repository-Historie. Legacy-Routen ausserhalb der neuen Funktionen wurden nicht vollständig sicherheitsauditiert.

## Annahmen / Kundensichtung

Neuere Kundendaten bleiben massgeblich: Küche 4–6, Balkon 2–4, Heizung 4–8 Monate weiterhin als angenommene Gesamtprojektdauer. Keine neuen Qualifikationen, Einsparungen, Zitate oder Zusagen ergänzt. Marketingzahlen werden nicht aus sechs Referenzen berechnet. Englisch ist Übersetzungsentwurf zur Freigabe.

Kunde sichtet Bildauswahl/Ausschnitte, Rollen-/Leistungstexte, Projektfakten und die drei Artikelentwürfe. Antwortzeit/Erreichbarkeit und Datenschutz-/Einwilligungstext noch bestätigen. Blog ist sichtbar, aber noch ohne freigegebene Kundenartikel; ehrliche Leermeldung bleibt bis bewusster Veröffentlichung richtig.

## Was als Nächstes konkret fehlt

1. Betreiber wählt Produktionscluster und erstellt neue leere DB basel_homefixer samt darauf begrenztem technischem Nutzer. Keine DEMO-/Vorschaukopie. Details: deploy/nas/DATENBANK.md.
2. Geprüfte neue Images bauen/pinnen. Bestehende CI bleibt unverändert und baut noch alte Dockerfiles/:dev; notwendige spätere Abweichungen stehen in deploy/nas/README.md. Kein stilles Workflowupdate erfolgt.
3. Echter isolierter Container-/MongoDB-/NAS-Test mit eigener Test-DB, Nutzer, Medienordner und deaktiviertem SMTP. Reale Indizes, Schreibrechte, Speicherlimits, Nginx-Start und Upload unter Containerbedingungen prüfen. Das kann vor einem öffentlichen Rollout gesondert beauftragt werden.
4. Privaten Medien-/Operationsordner, stabilen JWT-Schlüssel, genaue Cloudflare-Ziel-/Proxykette und Cache-Ausnahmen vorbereiten. Gemeinsame DB-/Medien-/Konfigurationssicherung und isolierten Restore erproben.
5. Nach Rolloutfreigabe neuen Backendschutz vor Import/Kontoanlage bereitstellen; genehmigtes Paket mit sechs Projekten, drei Blogentwürfen und sechs Bildquellen als draft importieren. Zwei Admins interaktiv anlegen; kein Konto wurde hier erstellt. Eigene Entwickler-E-Mail und vertrauliche Passwortfestlegung noch nötig.
6. Projekte/Artikel in geschützter Vorschau sichten und getrennt freigeben. Blogartikel bleiben ohne eigene Entscheidung draft. Inhalte/Bilder danach über Verwaltung austauschen, ohne Frontend-Build.
7. Gmail-App-Passwort privat auf NAS hinterlegen, erst nach Freigabe aktivieren und genau einen freigegebenen Test senden; SMTP-Annahme und Posteingang getrennt prüfen. Fehlende App-Passwort-Verfügbarkeit bedeutet deaktivierten Versand; OAuth nicht implementiert.
8. Nach ausdrücklich beauftragtem Rollout erst öffentliche HTTPS-/HTTP-Weiterleitung, Cloudflare-Caches, tatsächliche Besucher-IP-Ratebegrenzung, beide realen Admins, öffentliche Bildauslieferung und realen Gmail-/NAS-Weg prüfen. Diese produktiven Nachweise wurden nicht vorweggenommen.

Buildhinweis: bestehende Hauptbundle-Warnung (ca. 747 kB unkomprimiert, 238 kB gzip) bleibt; keine zusätzliche Bundle-/Architekturumstellung. Vorhandene Browserslist-Daten melden veralteten Stand; keine Abhängigkeitsupdates nebenbei durchgeführt.

Keine produktiven Konten/Daten/Dateirechte/Container-/NAS-/Cloudflare-Konfiguration geändert; keine echte E-Mail, Veröffentlichung, Commits, Pushes oder Deployments. Separates Demo-Projekt unverändert. Nur separater lesender Compose-Parser-Aufruf auf dem NAS. Testserver werden nach Abschluss beendet; nächsten Auftrag nicht automatisch begonnen.


## Nachweis vor Push: neue Produktionsimages lokal geprüft

Beide tatsächlichen CI-Dockerfiles lokal erfolgreich gebaut. Backend: UID 10001, schreibgeschütztes Dateisystem, exaktes validiertes Release-JAR, Boot-Archivwerkzeug und curl geprüft. Zusätzlich vollständiger Backendstart mit externer Produktionskonfiguration gegen frische echte MongoDB 7 in einem internen temporären Docker-Netz erfolgreich: Health UP, leere öffentliche Projekt-/Blog-APIs, Admin ohne Token 401, Kontaktversand deaktiviert. Keine Hostports, produktiven Zugangsdaten oder externe E-Mail-Verbindungen. Testcontainer/Netzwerk entfernt. Dies ersetzt nicht den noch offenen NAS-/Atlas-Rechte-/Backup-/vollständigen Verwaltungs-Integrationstest.

Frontend: UID 101, read-only und tmpfs, Nginx-Syntax, direkte SPA-Routen /projects, /admin und /blog/..., admin Cache-Control no-store und kein HTML-Fallback unter /api geprüft. Temporärer Loopback-Testcontainer danach entfernt.

CI-Umstellung ist ausdrücklich freigegeben und umgesetzt: dev sowie sha-Commit-Tags, ECR nur bei Push auf main. Aktueller Pushauftrag umfasst ausschliesslich basel-homefixer; keine Zusammenführung nach main, keine Imageveröffentlichung durch diesen Branch-Push und kein NAS-Deployment. Frühere Angaben „Docker lokal nicht verfügbar / Workflows unverändert“ beschreiben den damaligen Stand.
