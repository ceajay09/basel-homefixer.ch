# Fünf Umsetzungsprompts für den Frontend-MVP

Die Prompts nacheinander im selben Projektchat verwenden. Jeden Block vollständig senden. Nach Prompt 1 den sichtbaren Stil kurz ansehen; notwendige Stilanpassungen dort klären, bevor weitere Seiten folgen. Spätere Prompts arbeiten mit den schon vorhandenen Änderungen. Der SMTP-/Backend-Auftrag folgt separat, sobald der Mailzugang bereitsteht.

Diese Datei enthält vorbereitete Arbeitsaufträge. Ihre Erstellung führt die Umsetzung noch nicht aus.

## Prompt 1 – Designbasis und Dienstleistungen als Musterseite

```text
Setze Schritt 1 unseres Frontend-MVP im Projekt basel-homefixer.ch um. Lies design/Kundenprojekt_PJA/UMSETZUNGSPLAN.md und KUNDENFREIGABEN.md. Designreferenzen sind die Dateien unter design/Kundenprojekt_PJA/Kundenpaket; beginne mit 03-dienstleistungen.html und verwende 01-startseite2.html für die gemeinsame Designrichtung.

Erhalte React, TypeScript, Material UI, React Router, i18next und den aktiven Layout-Einstieg src/home/Layout.tsx. Übertrage Grün-/Gold-Palette, Systemschrift, Breite, Abstände, Rundungen, Karten, Header und Footer in die bestehenden Komponenten und ein zentrales Theme. Baue nur wenige tatsächlich wiederverwendete Bausteine. Vereinheitliche den Stil, statt die CSS-Dateien vollständig zu kopieren. Bestehende Sprachumschaltung und URLs erhalten.

Setze Services.tsx als vollständige Musterseite mit den sechs Leistungen, Ablauf und FAQ um. Mehr erfahren und Beratungsbuttons führen nach /contactUs#contact-form; eine einfache Dienstleistungsvorwahl darf vorbereitet werden. Das Formular selbst wird erst in Prompt 4 umgesetzt. In diesem Zwischenstand darf der Anker deshalb noch auf die bestehende Kontaktseite führen; keinen funktionslosen Sendeweg bauen. Behalte den bestehenden Airbnb-Link und beschränke die neue öffentliche Navigation auf die vereinbarten MVP-Ziele.

Pflege neue sichtbare Texte in den vorhandenen DE-/EN-Dateien. Kunden-HTML als Designreferenz verwenden, nicht einbetten. Die anderen Seiten bekommen zunächst nur die gemeinsame Grundgestaltung, keinen umfassenden Inhaltsumbau.

Nur Frontend ändern. Backend, Datenbanken, SMTP, Workflows, NAS und demo.cesarjaquiery.ch nicht ändern. Bestehende lokale Änderungen und Kundenoriginale erhalten. Nicht committen, pushen oder deployen.

Prüfe den Ausgangsstand und anschliessend TypeScript sowie den Frontend-Build. Sieh die Musterseite nach Möglichkeit auch mobil und am Desktop an. Berichte geänderte Bereiche, Tests und konkrete Grenzen. Starte nicht selbständig mit Prompt 2.
```

Ergebnis: Ein gemeinsamer Designstil und eine Musterseite zur kurzen Sichtung.

## Prompt 2 – Startseite und Projektbereich

```text
Setze jetzt die Startseite des Frontend-MVP um. Nutze den vorhandenen Stand aus Prompt 1 und den aktuellen design/Kundenprojekt_PJA/UMSETZUNGSPLAN.md. Die verbindliche Referenz ist design/Kundenprojekt_PJA/Kundenpaket/01-startseite2.html.

Übertrage Einstieg, Nutzenargumente, Leistungsübersicht, Projekte und Kontaktaufrufe in die bestehende Home.tsx. Verwende das zentrale Theme und die bereits eingeführten Bausteine. Bilder aus den mitgelieferten Assets übernehmen, sinnvoll dimensionieren und zuschneiden; Originale erhalten. Wenn dieselben Leistungen auf Startseite und Dienstleistungsseite erscheinen, Texte und Service-Schlüssel konsistent halten.

Projekte erhält eine echte Abschnitts-ID. Links von der Startseite und von anderen Seiten müssen den Abschnitt korrekt erreichen, auch nach einem Routenwechsel. Kostenlose Erstberatung und Leistungsaktionen führen zur Kontaktseite bzw. zum vorbereiteten Formularziel. Für Leistungen bei Bedarf /contactUs?service=<service-schluessel>#contact-form nutzen.

Den Blogbereich im ersten Draft auslassen. Für die neue Startseite keine Homepage-, Blog- oder Instagram-Daten vom Backend laden und keine Einbettung voraussetzen. Vorhandenen Integrationscode und Backend-Daten nicht pauschal löschen. Die sichtbare Startseite soll mit den statischen freigegebenen Inhalten ohne laufendes Backend funktionieren.

DE/EN, Mobilansicht und die vorhandene React-/Material-UI-Basis erhalten. Nur Frontend ändern; keine Backend-, Workflow-, NAS- oder Demo-Änderungen. Vorhandene lokale Änderungen erhalten. Nicht committen, pushen oder deployen.

Prüfe die Startseite, Bildpfade, Navigation und Projektsprünge sowie TypeScript und Build. Berichte Ergebnis und Grenzen. Keine andere Seite umfassend neu gestalten und Prompt 3 nicht automatisch beginnen.
```

Ergebnis: Eine gestaltete Startseite mit Kundenbildern und funktionierenden Aktionszielen.

## Prompt 3 – Über uns / Teamseite

```text
Setze die Teamseite des Frontend-MVP auf dem bestehenden Stand um. Lies den aktuellen design/Kundenprojekt_PJA/UMSETZUNGSPLAN.md. Verwende ausschliesslich die freigegebene Referenz design/Kundenprojekt_PJA/Kundenpaket/02-ueber-uns2.html und die zugehörigen Bilder. Die beschädigte 3er-Datei nicht verwenden.

Passe die vorhandene AboutUs.tsx an: Teamvorstellung, gemeinsames Bild, Porträts von Pascal und Divine, Rollen, Kompetenzen, Vision/Werte und Kontaktaufruf. Nutze dieselbe Breite, Typografie, Abstandsfolge und Komponenten wie auf den bereits umgesetzten Seiten. Erfinde keine Qualifikationen, Zusagen oder Kontaktdaten. Fehlende Angaben als offene Inhalte berichten, ohne den restlichen Entwurf aufzuhalten.

DE-/EN-Texte in den bestehenden Übersetzungen pflegen, Bilder responsiv und mit sinnvollen Alt-Texten einsetzen. Beratungsbutton auf das gemeinsame Kontaktziel setzen. Keine neue Seitenarchitektur und keine neuen Backend-Abhängigkeiten.

Nur Frontend ändern. Bereits umgesetzte Seiten nicht neu erfinden; bestehende lokale Änderungen und Originale erhalten. Backend, Workflows, NAS und separates Demo-Projekt unverändert lassen. Nicht committen, pushen oder deployen.

Prüfe die Teamseite auf Desktop/Mobil sowie TypeScript und Build. Berichte das Ergebnis und noch notwendige Inhaltsbestätigungen. Prompt 4 nicht automatisch beginnen.
```

Ergebnis: Eine Teamseite, die sich in denselben Gestaltungsstil einfügt.

## Prompt 4 – Kontaktformular als Frontend, ohne Versand

```text
Setze jetzt die Kontaktseite und die Frontend-Vorbereitung des Kontaktformulars um. Lies design/Kundenprojekt_PJA/UMSETZUNGSPLAN.md. Referenz ist Kundenpaket/04-kontakt.html. Übernimm die Gestaltung in ContactUs.tsx mit den bisherigen MUI-Komponenten und dem gemeinsamen Theme.

Ergänze Kontaktinformationen und ein ausfüllbares Formular: Name, E-Mail, optionale Telefonnummer, Dienstleistungswahl, Nachricht und das für den späteren Versand vorgesehene Einwilligungsfeld. Verwende React-Zustand und sinnvolle Pflichtfeld-, Format- und Längenvalidierung mit DE-/EN-Rückmeldungen. Die Prüfung soll beispielsweise beim Verlassen eines Feldes nachvollziehbar sein, obwohl noch kein Versand möglich ist.

Lies den Service-Schlüssel aus der URL zur Vorwahl. Unbekannte Schlüssel führen zur neutralen Auswahl. Stelle sicher, dass alle bisherigen Beratungs-/Leistungsbuttons den Formularanker erreichen. Behalte Eingaben bei Fehlern und vermeide unerwartetes Zurücksetzen.

WICHTIG: Jetzt kein Backend und keinen Versand implementieren. Kein fetch-/SMTP-Aufruf, kein Mockserver, keine Speicherung auf einem Server und keine simulierte Erfolgsmeldung. Nachricht senden bleibt klar deaktiviert. Erkläre direkt daneben sichtbar, dass Onlineversand erst eingerichtet wird. E-Mail schreiben an baselhomefixer@gmail.com und der bestätigte Telefonlink bleiben als separate echte Kontaktaktionen verfügbar. Ein mailto-Link ist kein Direktversand.

Trenne Formulardaten und die spätere Übermittlung über einen kleinen ContactRequest-Typ und eine überschaubare Servicefunktion mit dem aktuellen Zustand unavailable. Dokumentiere kurz den vorgesehenen späteren API-Vertrag, beispielsweise POST /api/contact mit Feldern, Service-Schlüsseln und Fehlerzuständen. Baue keine unbenutzte HTTP-/Retry-Architektur. Empfänger und spätere Absender-/Mailzugangsdaten gehören nicht in den Client-Request oder Frontend-Secrets.

Keine Änderungen an Backend, Datenbank, SMTP, Workflows, NAS oder Demo-Projekt. Bestehende lokale Änderungen erhalten. Nicht committen, pushen oder deployen.

Prüfe Validierung, Vorwahl, Tastaturbedienung, sichtbaren Nicht-versendet-Zustand und die nutzbaren Kontaktlinks. Stelle sicher, dass keine Nachricht versandt wird. Prüfe TypeScript und Build und berichte, welche kleine Anschlussstelle später zu ergänzen ist. Prompt 5 nicht automatisch beginnen.
```

Ergebnis: Vollständige Kontaktoberfläche mit ehrlicher Versandgrenze und vorbereiteter Backend-Anschlussstelle.

## Prompt 5 – Frontend-MVP zusammenhängend prüfen und abschliessen

```text
Prüfe den bislang umgesetzten Frontend-MVP anhand von design/Kundenprojekt_PJA/UMSETZUNGSPLAN.md und behebe konkrete Fehler. Keine neue Funktion und keinen Backend-Umfang hinzufügen.

Prüfe Startseite, Über uns, Dienstleistungen und Kontakt zusammen: einheitlicher Header/Footer, Farben, Schrift, Breiten, Abstände, Bildausschnitte sowie Ansichten um 390, 768 und 1280 px. Prüfe DE/EN, mobile Navigation, Tastaturbedienung, Projektlinks auch über Routenwechsel hinweg, Beratungs-/Leistungsziele, Service-Vorwahl, Kontaktlinks und den bestehenden Airbnb-Link. In der neuen Navigation keine unvollständigen Blogziele oder fingierten Artikel anzeigen.

Prüfe, dass die vier MVP-Seiten ohne laufendes Backend funktionieren und keine ungeplanten API-Aufrufe auslösen. Beim Formular muss der fehlende Onlineversand erkennbar bleiben; kein aktiver Pseudoversand und keine Erfolgssimulation. Keine neuen SMTP-/Backend-Funktionen aufbauen, um diese Prüfung zu bestehen. Die kleine dokumentierte Anschlussstelle für später muss verständlich sein.

Korrigiere nur Befunde innerhalb des vereinbarten Frontend-Umfangs. Vorhandene fremde Änderungen erhalten. Keine allgemeine Codebereinigung, keine Frameworkmigration. Backend, Workflows, Datenbanken, NAS und separates Demo-Projekt unverändert lassen. Nicht committen, pushen oder deployen.

Führe abschliessend Typecheck und Produktionsbuild aus. Das bestehende npm-test-Script ist kein Funktionsnachweis. Falls visuelle Browserprüfung möglich ist, zeige aussagekräftige Screenshots; sonst dokumentiere die konkrete Einschränkung. Berichte kurz: fertiger Umfang, tatsächliche Prüfungen, noch fehlende Inhalte und die späteren Schritte für echten Formularversand. Der nächste Schritt ist die Kundensichtung dieses Frontend-Stands.
```

Ergebnis: Reviewbarer Frontend-MVP mit nachvollziehbarer Prüfung und kleinem Restpunkt für späteren Mailversand.

## Danach, separat

Erst wenn der Mailzugang verfügbar ist, einen eigenen Auftrag für Backend-Anbindung, Versand-Konfiguration und abgestimmten Testversand erstellen. Der Blog erhält später ebenfalls einen eigenen inhaltlichen und technischen Auftrag.
