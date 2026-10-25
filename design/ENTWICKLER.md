# Designübergabe für dieses Projekt

## Versand
Nur Website-Designvorlagen.zip beziehungsweise den Inhalt von Kundenpaket an den Auftraggeber senden.
Das Paket enthält keine Backend-Konfiguration, Zugangsdaten oder privaten Dokumente.
Kundenpaket/index.html ist der Einstieg; ANLEITUNG.html und KI-AUFTRAG.txt führen durch die Bearbeitung.

## Umfang und Ausgangsbasis
Alle sieben öffentlichen Seitenkomponenten aus App.tsx sind abgedeckt. /blog ist ein Alias der Startseite.
Authentifizierungs-Komponenten sind nicht als Routen in App.tsx eingebunden und deshalb nicht Teil des Pakets.
Statische, vereinfachte Nachbildung anhand des vorhandenen JSX und der deutschen Übersetzungen, keine pixelgenaue Momentaufnahme.
Services.tsx zeigt technische Website-Informationen statt fachlicher Dienstleistungen. RealEstateForRent.tsx zeigt Kontaktinformationen; der aktuelle Menülink ist externes Airbnb.
Kontakt, Tech Insights und Latest Posts enthalten alte englische Platzhalter; die HTML-Vorlagen geben deren Struktur auf Deutsch wieder.
Texte unter _unused wurden nicht als freigegebene Inhalte übernommen. Blog-Daten und Instagram bleiben markierte Vorschau-Platzhalter.
Porträt fehlt in den vorhandenen öffentlichen Assets; Code/Übersetzung nennen widersprüchliches Alter. Vorlagen markieren fehlende Angaben.
Die Menüführung im Paket verbindet lokale Vorlagen. Der Vermietungslink ist ausdrücklich eine Vorschau der lokalen Route, kein stiller Wechsel der echten Navigation.
Die beiden vorhandenen öffentlichen Technik-SVGs werden mitgeliefert. Keine externen Fonts, Bilder oder Bibliotheken nötig.

## Umsetzung nach Rücksendung
1. ZIP in einen separaten Ordner entpacken; ausgewählten Stand und WUENSCHE.md lesen.
2. HTML im Browser visuell prüfen. Die tatsächlichen CSS-Regeln berücksichtigen, nicht nur :root oder Screenshots.
3. Gemeinsame Gestaltung nach home/Layout.tsx (createTheme), Header.tsx und Footer.tsx übertragen. Auf eigene ThemeProvider in Unterseiten achten.
4. Seiten anhand seiten-zuordnung.json und data-design-id zuordnen. Bereiche dürfen vom Kunden hinzugefügt oder entfernt worden sein.
5. Wiederholte Stile zentralisieren. CSS-Pixel nicht direkt als MUI-sx-Abstandszahlen übernehmen: sx p:2 bedeutet standardmässig 16px.
6. Übersetzungen in public/locales/de/de.json und en/en.json pflegen; Sprache und bestehende API-Funktionen erhalten, falls keine Änderung beauftragt ist.
7. Arbeitsleisten (.template-note), gelbe Statushinweise (.content-note), Übersichtsseite und Anleitung nicht in die Website übernehmen. Fehlende Kontaktdaten klären.
8. Neue Funktionen separat umsetzen. Ein dargestelltes Formular oder ein Link in der Vorlage beweist keine funktionierende Integration.
9. Typecheck und Frontend-Build ausführen; echte Seiten in Desktop- und Mobilansicht prüfen. Auftraggeber erhält Vorschaulink zur Abnahme.
10. Für eine spätere Runde eine aktuelle statische Referenz bereitstellen; bestätigte Stände getrennt aufbewahren.

## Prompt für die Coding-KI
Übertrage den freigegebenen Entwurf aus den zurückgesendeten HTML-Dateien auf das bestehende React-/Material-UI-Frontend. Beachte WUENSCHE.md und die Seitenzuordnung. Nutze die vorhandenen Komponenten und Übersetzungen; erhalte bestehende Funktionen. Übernimm die tatsächlichen Layoutregeln, Farben, Typografie, Bildausschnitte und Abstände responsiv. Übertrage Arbeitshilfen und Platzhalterhinweise nicht als Produktinhalt. Prüfe TypeScript, Build und die echte Darstellung auf Desktop und Mobilgeräten. Berichte verbleibende fehlende Angaben.

## Generator
scripts/create-design-vorlagen.mjs erzeugt das Ausgangspaket aus den deutschen Texten und vereinfachten Layouts. Er verweigert absichtlich das Überschreiben eines bestehenden Kundenpakets, damit Kundenentwürfe nicht verloren gehen. Zum Neugenerieren den bisherigen Ordner zunächst separat archivieren. Das ZIP muss nach Änderungen neu gepackt werden.

## Prüfung des Ausgangspakets
Neun HTML-Dateien inklusive Übersicht und Anleitung sowie 82 lokale Verweise wurden auf vorhandene Ziele geprüft. Keine externen HTML-Ressourcen, doppelte HTML-IDs oder UTF-8-Ersatzzeichen gefunden. JavaScript-Syntax der Vorschau-Steuerung und die Zuordnung der sieben Seiten zu vorhandenen React-Dateien geprüft. ZIP-Inhalt kontrolliert.
Eine visuelle Browserprüfung konnte in dieser Umgebung nicht abgeschlossen werden: Der Browser erreicht den lokalen Server nicht und erlaubt keine file:-URLs. Desktop- und Mobilansicht vor der ersten Weitergabe bei Bedarf lokal über index.html ansehen.
