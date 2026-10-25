# Geschützte Inhaltsverwaltung – Backend

Stand: 25. Oktober 2026. Lokale Implementierung; keine Einrichtung produktiver Konten, Datenimporte, Veröffentlichung oder NAS-Bereitstellung. Anmeldung und Schlüsselkonfiguration: [AUTH.md](AUTH.md). Gemeinsame Modelle und öffentlicher Lesezugriff: [CONTENT.md](CONTENT.md).

## Berechtigung und Bearbeitungsmodell

Alle folgenden Routen liegen unter `/api/admin/**`. Jede Anfrage benötigt ein gültiges Bearer-Token und die serverseitige Verwaltungsberechtigung. Normale Konten erhalten 403, fehlende/ungültige Tokens 401. Öffentliche Inhalts- und Medien-APIs bleiben rein lesend. Antworten der Verwaltung, einschliesslich Vorschau und Bilder, verwenden `Cache-Control: no-store`.

Es gibt dieselben MongoDB-Dokumente wie für die öffentliche API, keine parallele CMS-Struktur. Neue Inhalte beginnen als `draft`; Status, ID, Version und Veröffentlichungsdatum bestimmt der Server. Annahme für diesen MVP: Veröffentlichte Inhalte müssen vor einer Bearbeitung ausdrücklich zurückgezogen werden. Es gibt keine getrennte Arbeitskopie eines weiterhin öffentlich sichtbaren Artikels. Speichern veröffentlichter Inhalte liefert 409 `withdraw_before_editing`. Eine spätere Live-/Entwurfsversionierung wäre ein eigener Ausbau.

Einzelabrufe liefern einen `ETag`, beispielsweise `"3"`. Alle Änderungen ausser Neuanlage und Upload benötigen `If-Match` mit genau diesem Wert. Fehlender Header: 428; ungültiger Header: 400; veraltete Version: 409 `version_conflict`. Dokument-ID und Version werden gemeinsam in der atomaren MongoDB-Schreibbedingung geprüft. Nach einem Konflikt neu laden und Änderungen bewusst zusammenführen. Bestehende Dokumente ohne Version beginnen mit Version 0. IDs bleiben unverändert; Slug-Konflikte werden per Vorprüfung und eindeutigem partiellem Index pro Inhaltstyp verhindert. Bestehende doppelte Slugs werden nicht still repariert. Slug-Änderungen erzeugen keine Weiterleitung alter URLs.

## Inhaltsverträge

`{type}` steht ausschliesslich für `projects` oder `blog`.

| Methode / Route | Verhalten |
| --- | --- |
| GET `/api/admin/{type}` | Liste mit `status=draft`, `published` oder `legacy` optional; `page=0`, `size=20` (1–50). Antwort `{items,page,size,totalElements}`. |
| POST `/api/admin/{type}` | JSON mit bearbeitbaren Feldern; neuer Entwurf, 201, Location und ETag. Auch ein leerer Entwurf ist möglich. |
| GET `/api/admin/{type}/{id}` | Vollständiges Dokument und ETag. |
| PUT `/api/admin/{type}/{id}` | Vollständiger Ersatz der bearbeitbaren Felder eines Entwurfs, kein partielles PATCH; If-Match erforderlich. |
| POST `/api/admin/{type}/{id}/publish` | Ausdrückliche Veröffentlichung nach vollständiger Prüfung; If-Match erforderlich. |
| POST `/api/admin/{type}/{id}/withdraw` | Auf draft zurückziehen; If-Match erforderlich. |
| GET `/api/admin/{type}/{id}/preview?lang=de` | Berechtigte DE-/EN-Vorschau `{content,media,status,version}`. |
| GET `/api/admin/homepage-projects` | Geordnete Auswahl und ETag. |
| PUT `/api/admin/homepage-projects` | JSON `{ "projectIds": ["id-1", "id-2", "id-3"] }`, If-Match erforderlich. |

Bearbeitbare gemeinsame Felder: `slug`, `title`, `excerpt`, `description`, `categories`, `coverImageId`, `coverAlt`, `mediaIds`. Sprachfelder sind Objekte mit `de` und `en`; `description` enthält bei Blogbeiträgen Markdown. Projektfelder zusätzlich: `location`, `challenge`, `result`, `tags`, `dates`, `duration`, `investmentChf`, `areaM2`, `apartments`, `thermostats`, `doors`, `complexity`. Die Struktur entspricht den bestehenden Modellen, siehe `ContentEdit.java` und CONTENT.md. Ausgelassene optionale Fakten werden entfernt und nicht als erfundene Zahlen ergänzt. Serverfelder wie `status`, `publishedAt`, `version`, `updatedAt` und `id` können nicht durch den Client vergeben werden.

Entwürfe dürfen unvollständig sein; Format-, Längen-, Kategorie- und Referenzprüfungen gelten trotzdem. Veröffentlichung benötigt vollständige DE-/EN-Pflichttexte, gültige Kategorien, ein tatsächlich vorhandenes Titelbild samt Alternativtexten und bei Projekten die erforderlichen Zeitangaben. Ausgewählte Startseitenprojekte benötigen zusätzlich Challenge/Result in beiden Sprachen. Titel maximal 200, Vorschautext 1000, Beschreibung 50000 und Alttext 1000 Zeichen pro Sprache; höchstens fünf Kategorien und 50 zusätzliche Medienreferenzen. Fehlende/ungültige Pflichtangaben ergeben 422 mit einem Fehlercode. Bilder in Blog-Markdown müssen lokale, deklarierte Medienreferenzen mit nicht leerem Alttext verwenden. Markdown ist weiterhin untrusted input; die spätere Oberfläche muss HTML und unsichere Linkprotokolle bei der Darstellung sperren.

Die erste Veröffentlichung setzt serverseitig das Datum; Speichern und erneutes Veröffentlichen erhalten es. Ein vorgefundenes zukünftiges Datum wird bei bewusster Veröffentlichung auf jetzt gesetzt. Zurückziehen sperrt neue öffentliche Inhaltsabrufe unmittelbar. Bilder bleiben nur dann öffentlich, wenn ein anderer veröffentlichter Inhalt sie weiterhin referenziert. Bereits heruntergeladene Bilder können nicht aus Browsern zurückgerufen werden.

Die Startseitenauswahl liegt gemeinsam und versioniert im Dokument `content_settings/homepage-projects`. Null bis drei unterschiedliche vorhandene Projekte sind möglich; die Array-Reihenfolge gilt. Entwürfe dürfen ausgewählt werden, erscheinen aber erst nach gültiger Veröffentlichung öffentlich. Bis zum ersten Speichern gilt die vorhandene `homeOrder`-Auswahl als Rückfall. Projekt- und Auswahländerung sind getrennte atomare Vorgänge, keine gemeinsame Transaktion. Die öffentliche Abfrage prüft Veröffentlichung und notwendige Teasertexte zusätzlich. Allgemeine Marketingzahlen werden nicht aus der Projektauswahl berechnet.

## Medienverträge

| Methode / Route | Verhalten |
| --- | --- |
| GET `/api/admin/media` | Paginierte Metadaten, page/size wie oben. |
| POST `/api/admin/media` | Multipart mit Pflichtteil `file` und optionalem JSON-Teil `alt`, z.B. `{ "de": "Küche", "en": "Kitchen" }`; 201, Location und ETag. |
| GET `/api/admin/media/{id}` | Metadaten und ETag. |
| PUT `/api/admin/media/{id}/alt` | JSON `{ "alt": { "de": "…", "en": "…" } }`, If-Match erforderlich. |
| GET `/api/admin/media/{id}/small` bzw. `/large` | Geschütztes JPEG, auch für Entwürfe; keine öffentlichen Vorschautokens. |

Uploads und lokaler Import verwenden direkt dieselbe `MediaStorage.prepare`-Verarbeitung. Erlaubt sind tatsächlich dekodierbare JPEG/PNG bis 15 MiB und 25 Millionen Pixel. Client-Dateiname und MIME-Angabe bestimmen weder Dateiformat noch Speicherpfad. Keine SVG-, HTML-, HEIC- oder WebP-Unterstützung. EXIF-Orientierung einschliesslich gespiegelter Varianten wird für JPEG und PNG berücksichtigt. Ausgabe: neu kodierte JPEG ohne ursprüngliche EXIF-/GPS-Metadaten, transparente Bereiche weiss, längste Kante maximal 480/1280 Pixel, kein Hochskalieren.

Serverseitig erzeugte IDs bestimmen den privaten Dateipfad. Die verbesserte Verarbeitung verwendet eine neue interne ID-Version; bestehende Bild-IDs bleiben lesbar, alte Dateien werden nicht überschrieben. Identische Uploads verwenden vorhandene Medien wieder und überschreiben deren Alttexte nicht. Alttexte öffentlich referenzierter Medien dürfen nicht auf leere/einsprachige Werte reduziert werden. Titelbilder können eigene Alternativtexte besitzen; fehlen sie bei Veröffentlichung vollständig, werden die Medienalttexte übernommen.

Keine Löschroute und keine automatische Dateibereinigung. Das Entfernen einer Zuordnung löscht keine Datei oder Metadaten, auch nicht bei Referenzen durch andere Inhalte oder Entwürfe. Datenbank und Dateisystem sind nicht gemeinsam transaktional: Bei einem Datenbankfehler können private unreferenzierte Dateien verbleiben. Vor späterer Bereinigung sämtliche Referenzen und Sicherungen prüfen.

## Konfiguration und Betrieb

Zusätzlich zur bestehenden Auth-/MongoDB-Konfiguration, ausschliesslich serverseitig:

| Variable | Standard / Grenze |
| --- | --- |
| `CONTENT_MEDIA_ROOT` | `/data/content/media`; dauerhafter privater Ordner mit Lese-/Schreibrechten für den Backendbenutzer. |
| `ADMIN_MEDIA_MAX_BYTES` | `15728640`; erlaubter Bereich 1024 bis 15728640 Bytes. |
| `ADMIN_MEDIA_CONCURRENT_UPLOADS` | `1`; erlaubter Bereich 1 bis 4, pro Backendprozess. |

Beispiel ohne Geheimnisse: [admin.env.example](admin.env.example). Keine NAS-/Compose-Änderung durch diesen Auftrag. Der Medienordner darf nicht zusätzlich durch Web Station oder einen anderen Dateiserver öffentlich erreichbar sein. MongoDB und Medien gemeinsam sichern/wiederherstellen.

Multipart-Dateigrenze entspricht dem Uploadlimit, Anfragegrenze liegt 1 MiB darüber. Multipart-Zwischendateien liegen im privaten serverseitigen temporären Ordner. Die Bildverarbeitung ist per Semaphore begrenzt; bei Belegung 429 mit `Retry-After: 2`. Multipart-Annahme kann bereits vorher stattfinden. Bei 25 MP benötigt allein ein dekodiertes Bild ungefähr 100 MB; Standardparallelität 1 beibehalten und Container-/Proxy-Limits vor echtem Betrieb prüfen. Zu grosse Dateien: 413, ungültige Bilder: 422, Speicher-/Datenbankfehler: bereinigte 503-Antwort. Keine vertraulichen Pfade oder Datenbankdetails in API-Fehlern.

Die kommende Verwaltungsoberfläche verwendet Bearer-Header, keine URL-Tokens. Geschützte Bilder daher authentifiziert abrufen und z.B. über kurzlebige Blob-URLs anzeigen; ein gewöhnliches img-src setzt keinen Bearer-Header. Blob-URLs nach Verwendung freigeben. Vorschau verwendet dieselben öffentlichen Darstellungsobjekte, ersetzt Medienpfade aber durch geschützte Routen. Es gibt keinen öffentlichen Vorschauparameter. Tokenablauf und lokale Abmeldung siehe AUTH.md.

Der Import bleibt ein Transferwerkzeug. Bereits durch Verwaltung versionierte Dokumente werden bei abweichendem Import nicht überschrieben, auch nicht mit UpdateDrafts. Keine automatische Migration oder Änderung produktiver Daten. Kundenpflege kann anschliessend über diese API ohne Importdateien und Frontend-Deployment erfolgen; die Bedienoberfläche fehlt noch.

## Prüfungen und verbleibende Grenzen

Backend-Prüfungen verwenden isolierte Konten/In-Memory-Repositories, echte eingebettete HTTP-/Security-Server und temporäre Medienordner. Zusätzlich werden tatsächliche MongoDB-Abfragen, Mapping, Versionsbedingungen und Indexdefinitionen geprüft. Abgedeckt: Rechte für Verwaltungsrouten, Projekt-/Blogzyklus, Veröffentlichung und Rücknahme, Pflichtfelder, Slug-/Versionskonflikte, Auswahl, geschützte Bildvorschau, Uploadfehler/-grenzen, parallele Verarbeitung, EXIF-Orientierungen, Pfadgrenzen und Schutz vor Importüberschreibung. Kein produktiver Zugriff und kein tatsächlicher MongoDB-Apply. Echte Indexerstellung, Dateirechte und Containergrenzen bleiben vor Betrieb in isolierter MongoDB-/NAS-Integration zu prüfen.

Die laufende NAS-Lesevorschau enthält diese neuen Verwaltungsfunktionen noch nicht. Keine Verwaltungsoberfläche, zusätzliche öffentliche Blogoberfläche oder produktive Zugangseinrichtung in diesem Schritt. Vor einem späteren Deployment bleibt ausserdem der in AUTH.md dokumentierte Ausschluss der bestehenden application-dev.properties aus dem verteilten JAR erforderlich. Das lokale Entwicklungsartefakt nicht unverändert verteilen.

Abschlussprüfung am 25. Oktober 2026: `.\mvnw.cmd -o -B verify` erfolgreich, 73 Tests, keine Fehler/Fehlschläge/übersprungenen Tests; JAR-Build erfolgreich. Kein realer MongoDB-Apply und keine NAS-/Produktivprüfung. Entwicklungsartefakt wegen der oben genannten bestehenden Entwicklungsressource nicht zur unveränderten Verteilung freigegeben.

## Verwaltungsoberfläche (25. Oktober 2026)

Frontend unter `/admin` ergänzt: reine Arbeitsspeicher-Anmeldung, Ablauf/erneute Anmeldung, getrennte Projekt-/Bloglisten und zweisprachige Formulare, Medienauswahl/Upload/Alttexte, bewusste Publikationsaktionen, geschützte Vorschau und Startseitenauswahl. Lokale Abmeldung widerruft das Token nicht serverseitig (siehe AUTH.md). Kein Standardzugang und keine Kontoeinrichtung durch das Frontend.

Der vorhandene React Router verwendet jetzt einen zentralen `createBrowserRouter`/`RouterProvider` mit derselben App und denselben Seitenrouten. Das ermöglicht den eingebauten `useBlocker` für Browser-Zurück und Routenwechsel bei ungespeicherten Änderungen. Zusätzlich beforeunload für Neuladen/Schliessen; Browser bestimmen die Anzeige des Warntexts. Innerhalb der Verwaltung erfolgt die Auswahl in React-Zustand, ohne Geheimnisse in URLs. Kein neuer Editor-Dienst und keine neue öffentliche Blogoberfläche.

Vorschau-Bilder werden authentifiziert als Blob geladen, Blob-URLs beim Wechsel freigegeben. SafeMarkdown deaktiviert Raw-HTML-Verarbeitung, begrenzt Linkprotokolle und Bilder auf die lokale Medien-API; derselbe Baustein ist für die spätere öffentliche Artikelansicht nutzbar. Kennzahlen-Darstellung wird mit der öffentlichen Projektseite geteilt. Zusätzliche Projektmedien können zugeordnet werden; keine neue öffentliche Projektgalerie in diesem Schritt.

Lokale Entwicklung: Verwaltungsanfragen verwenden relative URLs und den Vite-Proxy `ADMIN_DEV_API_TARGET` (Standard http://127.0.0.1:8081). Dieser muss auf ein Backend mit aktueller Auth-/Verwaltungsimplementierung zeigen. Der bestehende reine NAS-Leseproxy genügt nicht. Kategorien verwenden die Inhalts-API; für isolierte Gesamttests auch CONTENT_DEV_API_TARGET auf das isolierte Backend setzen. In Produktion gilt wie bei der vorhandenen API `VITE_API_URL` oder derselbe Ursprung. Keine Zugangsdaten in VITE-Konfiguration. API, Medienmount und Rollen bleiben serverseitige Voraussetzungen.

Reproduzierbare Prüfungen:

- Frontend: `npm run typecheck`, `npx vitest run src/admin/admin.test.tsx`, `npm run build`.
- Backend: Maven verify; `AdminBrowserFixture` ausschliesslich im Testquellbaum, keine MongoDB-/SMTP-Verbindung, reale Handler/Security mit isolierten Repository-Doubles und temporärem Medienordner. Nicht im Anwendungs-JAR enthalten.
- Für den Browserlauf: test-compile und `dependency:build-classpath` mit Test-Scope, dann Fixture über target/test-classes + target/classes + Test-Klassenpfad starten. Einziger Programmparameter: private temporäre Ausgabedatei für die lokale Testadresse. Testzugang ausschliesslich in der Fixture-Datei, nie für echte Konten verwenden.
- Separate Vite-Instanz mit ADMIN_DEV_API_TARGET und CONTENT_DEV_API_TARGET auf diese lokale Testadresse, VITE_API_URL leer; Standard des Checks Port 5173. `scripts/admin-browser-check.cjs` erwartet ADMIN_FIXTURE_ENDPOINT (Standard C:/Temp/basel-admin-browser-endpoint.txt) und optional ADMIN_UI_TEST_URL. Playwright/Sharp müssen lokal vorhanden sein; alternativ über PLAYWRIGHT_MODULE/SHARP_MODULE auf installierte Module verweisen. Kein automatischer Download.
- Der Browsercheck veröffentlicht und widerruft ausschliesslich flüchtige lokale Fixture-Inhalte. Niemals mit einer echten Datenbank oder Produktionsadresse verwenden. Fixture und Test-Vite danach beenden. Screenshots liegen im privaten temporären Ordner.

Kundenanleitung: `design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md`. Noch ausstehend: echter isolierter MongoDB-/NAS-Integrationstest, operative Zugangseinrichtung, kundenseitige Abnahme und gesondert freizugebende Bereitstellung. Die laufende NAS-Lesevorschau wurde nicht aktualisiert.


Abschlussprüfung Verwaltungsoberfläche am 25. Oktober 2026: TypeScript und Produktionsbuild erfolgreich; fünf gezielte Vitest-Tests erfolgreich (Datenvertrag, Pflichtfelder, sichere Markdown-/Link-/Bilddarstellung). Maven verify samt JAR-Build: 73 Tests ohne Fehler/Fehlschläge/übersprungene Tests. Vollständiger Edge-/Playwright-Ablauf gegen die reale lokale HTTP-/Security-Implementierung mit isolierten Repository-Doubles erfolgreich: falsche/richtige Anmeldung, Projekt- und Blogentwurf, Upload, geschützte Blob-Bilder, Vorschau, Veröffentlichung, öffentliche API/Projektansicht, Rücknahme einschliesslich Bildsperre, echte Versionskonflikte durch zweiten Client, Eingabeerhalt nach injizierter 401-Antwort/erneuter echter Anmeldung, Verlassenswarnung, Startseitenauswahl, DE/EN und 390/768/1280 px ohne horizontalen Überlauf. Zusätzlich 503 und Transportfehler simuliert: Eingaben bleiben erhalten, keine Schreibanfrage erreichte den Server. Tastaturfokus und mobile/desktop Screenshots geprüft; mobilen Kopf anschliessend gezielt korrigiert und nachgeprüft. Kein echter 15-Minuten-Warteversuch und kein realer MongoDB-Apply. Bestehende Bundlewarnung über 500 kB bleibt; Adminbereich wird separat geladen.

## Öffentliche Artikel und gemeinsame Vorschau (25. Oktober 2026)

Die öffentliche Blogoberfläche ist im lokalen Frontend implementiert. Sie verwendet dieselbe SafeMarkdown-Darstellung wie der Verwaltungsbereich, einschliesslich deklarierter Medien-ID-Liste und Alternativtextprüfung. Die Artikelantwort der Lese-API ergänzt mediaIds. Detailtext/-bilder von Entwürfen bleiben ausschliesslich hinter den bestehenden Verwaltungsrouten erreichbar. Echte Kundentexte wurden im Browser nur geschützt gesichtet; öffentliche Tests verwenden ausschliesslich synthetische Daten in der lokalen Fixture.

Drei DE-/EN-Entwürfe: content-import/blog-drafts/README.md. Ersten dauerhaften Verwaltungszugang selbst einrichten: design/Kundenprojekt_PJA/ADMIN-ZUGANG.md; vorhandener Einrichtungs-Dry-Run erfolgreich. Keine produktiven Konten oder Datenbanken geändert, kein allgemeiner Testzugang aktiviert.
