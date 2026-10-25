# Drei Blogentwürfe – Quelle und Sichtung

Die sechs Dateien `blog-*.de.md` / `blog-*.en.md` enthalten den Markdown-Artikelinhalt. `articles.json` enthält Titel, Kurztexte, stabile IDs/Slugs, Kategorien und Bildzuordnung. Das daraus erzeugte `../blogs.json` verwendet das bereits vorhandene Importformat und wird nicht unabhängig von diesen Quellen gepflegt.

Nach einer Änderung an diesen einmaligen Transferquellen:

```powershell
node .\content-import\blog-drafts\build-manifest.mjs
.\content-import\import-content.ps1 -Manifest "$PWD/content-import/blogs.json" `
  -MediaRoot "$env:TEMP/basel-blog-review-media"
```

Aus `My-App_Backend` ausführen. Der zweite Befehl ist standardmässig ein Dry-Run: keine Datenbankverbindung, keine Dateien und keine Veröffentlichung. Originalbilder stammen unverändert aus dem neuesten Kundenpaket: Küche.jpg, Moderne_Heizung.jpg und Bodenheizung.jpg. Die Namen werden im bestehenden Medienimport auf serverseitige IDs abgebildet; keine Bilddateien im Frontend-Build als Pflegevoraussetzung.

Alle drei Inhalte bleiben `draft`, `publishedAt=null`. Englisch ist ausdrücklich als Übersetzungsentwurf zur Kundensichtung markiert. Diesen Hinweis erst nach sprachlicher Freigabe bewusst in der Verwaltung entfernen. Die Beiträge beschreiben vorhandene Kundenfakten, keine neuen Einsparungs-, Ergebnis- oder Kompatibilitätszusagen. 4–6 Monate Küche und 4–8 Monate Heizung sind als angenommene Gesamtprojektdauern bezeichnet. Für Thermostate keine Dauer ergänzt.

## Geschützte lokale Sichtung ohne echte Datenbank

`AdminBrowserFixture` im Backend-Testquellbaum kann die Entwürfe über denselben Import in isolierte Repository-Doubles und einen temporären privaten Medienordner übernehmen. Keine MongoDB-, NAS- oder SMTP-Verbindung. Nach `test-compile` die Test-Klassenpfadvorbereitung aus ADMIN.md verwenden. Die drei Programmargumente sind:

1. Private temporäre Ausgabedatei für die Loopback-Backendadresse.
2. `content-import/blogs.json`
3. `../design/Kundenpaket_20261024/Kundenpaket/assets`

Separate lokale Vite-Vorschau auf dem erlaubten Port 5173 mit ADMIN_DEV_API_TARGET und CONTENT_DEV_API_TARGET auf die ausgegebene Adresse, VITE_API_URL leer. Anmeldung ausschliesslich mit dem in der Test-Fixture definierten isolierten Testkonto; keine echten Zugangsdaten dafür einsetzen. Unter `/admin` → Blogbeiträge alle drei Entwürfe öffnen und DE/EN in der geschützten Vorschau sichten. Sie bleiben öffentlich 404; ein leerer öffentlicher Blog ist der korrekte Ausgangsstand.

`My-App_Frontend/scripts/blog-browser-check.cjs` automatisiert die geschützte Sichtung und prüft die öffentliche Oberfläche ausschliesslich mit separaten synthetischen lokalen Testartikeln. Umgebungsvariablen: BLOG_FIXTURE_ENDPOINT (Ausgabedatei), BLOG_UI_TEST_URL (Standard http://127.0.0.1:5173), bei Bedarf PLAYWRIGHT_MODULE/SHARP_MODULE für installierte lokale Testwerkzeuge. Je Durchlauf eine frische Fixture starten; synthetische Testinhalte werden nur im Speicher gehalten. Die echten Kundenentwürfe werden nicht veröffentlicht. Fixture und Vite nach Sichtung beenden.

## Späterer Betrieb

Erst nach gesonderter Freigabe, Sicherung und geprüftem Datenbankziel dürfen die Entwürfe auf eine dauerhafte Instanz übertragen werden. Bestehende persönliche Artikel werden nicht gelöscht, überschrieben oder zu Kundenartikeln umgewidmet. Inhalte mit Verwaltungs-Version lassen sich nicht durch einen späteren abweichenden Import überschreiben. Nach der einmaligen Übertragung läuft die Pflege über `/admin`; weder Markdown-Importdateien noch Frontend-Deployment sind für Text-/Bildänderungen nötig.

Im vorliegenden Auftrag kein echter MongoDB-Apply, keine NAS-Änderung und keine öffentliche Artikelveröffentlichung.
