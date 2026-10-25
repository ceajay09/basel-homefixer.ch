> **Aktuelles Betriebsziel:** neue Datenbank basel_homefixer, noch nicht angelegt. DEMO gehört zur Demo-Website und bleibt unverändert; preview bleibt Test. Frühere DEMO-Angaben unten sind historischer Istzustand, keine aktuelle Zielvorgabe. Siehe [Datenbankentscheidung](../deploy/nas/DATENBANK.md) und [Portainer](../deploy/nas/PORTAINER.md).

# Gemeinsame Inhalte und Medien (24. Oktober 2026)

## Architektur und aktueller Umfang

MongoDB behält die vorhandene Collection `blogpost` und ihre IDs. Neue Collections sind `projects` und `media`. Projekte und Blog erben gemeinsame Inhaltsfelder; Bilddateien liegen ausschliesslich in einem privaten, dauerhaften Dateisystemordner. Geschützte Verwaltungs-, Upload- und Vorschau-Endpunkte sowie eine Frontend-Verwaltung unter /admin sind inzwischen lokal implementiert; aktuelle Verträge und Betriebsgrenzen in [ADMIN.md](ADMIN.md). Öffentliche APIs bleiben rein lesend. Die folgenden ursprünglichen Abschnitte und datierten Nachträge dokumentieren die Entwicklung.

`ContentFields` enthält ID, Slug, DE-/EN-Titel, Vorschautext, Beschreibung/Markdown, Kategorie-Schlüssel, Titelbild-ID, alternative Texte pro Inhalt, weitere Medien-IDs, Status und Veröffentlichungszeitpunkt. Blogbeschreibungen bleiben mehrsprachiger Markdown. Projektbeschreibungen sind die bisherigen vollständig sichtbaren Texte. Projektdaten ergänzen Ort, Monat/Jahr, Schlagwörter, Gesamtprojektdauer bzw. Wochen, optionale Kennzahlen und `homeOrder`.

`homeOrder` wählt und sortiert veröffentlichte Startseitenprojekte; die API liefert höchstens drei. Fehlende optionale Werte werden im Frontend ausgelassen. Gemeinsame Zahlen in Texten werden mit `{{area}}`, `{{apartments}}`, `{{thermostats}}`, `{{investment}}` aus denselben Projektfeldern aufgelöst. Die Marketingangaben 20+/40+/16 bleiben davon unabhängig.

Die sechs stabilen IDs/Anker bleiben `kitchen`, `balcony`, `heating`, `electrical`, `entrance-door`, `thermostats`. Küche 4–6, Balkon 2–4 und Heizung 4–8 Monate bleiben als angenommene **Gesamtprojektdauer** bezeichnet. Eingangstür bleibt sechs Wochen.

## Öffentliche API

| GET-Pfad | Zweck |
| --- | --- |
| `/api/projects?lang=de&page=0&size=12&category=heating` | Projektliste |
| `/api/projects/featured?lang=de` | Höchstens drei ausgewählte Startseitenprojekte |
| `/api/projects/by-id/kitchen?lang=de` | Veröffentlichtes Projekt für bestehende Anker, keine neue Frontend-Unterseite |
| `/api/blog?lang=de&page=0&size=12&category=energy` | Blog-Vorschauen ohne vollständigen Markdown |
| `/api/blog/<slug>?lang=de` | Vollständiger veröffentlichter Artikel |
| `/api/content/categories?type=projects&lang=de` | Zentrale lokalisierte Kategorien; alternativ `type=blog` |
| `/api/media/<server-uuid>/small` bzw. `/large` | Geprüfte JPEG-Bildvarianten |

`lang` ist `de` oder `en`. Listen sind auf 1–24 Einträge pro Seite begrenzt, Seiten auf 0–10000. Standardsortierung: Veröffentlichungsdatum absteigend, ID aufsteigend; Startseite: `homeOrder`, ID. Unbekannte Kategorien/ungültige Parameter ergeben 400. Unveröffentlichte direkte Zugriffe ergeben 404.

Öffentlich bedeutet ausschliesslich `status=published` UND gesetztes `publishedAt` in der Vergangenheit/Gegenwart. Fehlender Status und zukünftige Daten bleiben verborgen. Bestehende `/api/getBlogposts` und `/api/blogpost/{id}` verwenden dieselbe Freigabe; die alte Liste liefert maximal 24 Beiträge. Der bisherige Blog-Cache wurde entfernt. Die alte Collection, Inhalte und IDs werden nicht automatisch migriert oder veröffentlicht.

Bildauslieferung prüft bei jedem Abruf, ob mindestens ein aktuell veröffentlichtes Projekt oder ein veröffentlichter Blogbeitrag die Medien-ID als Titelbild oder in `mediaIds` referenziert. Ein gemeinsam verwendetes Bild ist öffentlich, solange eine solche öffentliche Referenz existiert. Ein allein in Entwürfen verwendetes Bild bleibt 404, auch bei bekannter URL. `Cache-Control: no-store` und `nosniff` sind gesetzt; ein bereits heruntergeladenes Bild kann technisch nicht zurückgerufen werden. Keine zusätzliche statische Freigabe des Medienordners im NAS/Webserver einrichten.

## Dateisystem und spätere NAS-Anbindung

Serverkonfiguration: `CONTENT_MEDIA_ROOT`, Standard `/data/content/media`. Der bereits vorgesehene private Mount nach `/data/content` ist grundsätzlich geeignet; `homepage.json` bleibt daneben unverändert. Der Betreiber muss prüfen, dass der Hostordner nicht über Synology Web Station, Dateifreigaben oder einen weiteren Webserver öffentlich ausgeliefert wird. Ein öffentliches Verzeichnis unter `webdata` wäre ohne Zugriffsschutz ungeeignet; gegebenenfalls einen anderen privaten Hostordner mounten. In diesem Auftrag wurde keine Compose-/NAS-Konfiguration geändert.

Der Backend-Benutzer braucht Leserechte; der explizite Import und spätere Upload brauchen Schreibrechte. MongoDB und Medienordner zusammen sichern und gemeinsam wiederherstellen. Dateien liegen unter `<root>/<server-uuid>/small.jpg` und `large.jpg`; MongoDB speichert ID, SHA-256, DE-/EN-Alttexte und Variantenabmessungen/Prüfsummen. Die IDs werden serverseitig reproduzierbar aus den Quellbytes generiert. Keine Client-Dateipfade werden verwendet.

`MediaStorage.prepare` ist die gemeinsame Verarbeitung für Import und spätere geschützte Uploads. Erlaubt sind tatsächlich dekodierbare JPEG/PNG, maximal 15 MiB und 25 Millionen Pixel. Prüfung erfolgt vor voller Dekodierung. Ausgaben sind neu kodierte JPEG ohne ursprüngliche Metadaten, maximal 480/1280 Pixel an der längsten Kante, ohne Hochskalieren. SVG/HTML, Pfadwechsel, Symlinks und fremde Varianten werden abgewiesen. Originale werden nie verändert. EXIF-Orientierung wird inzwischen einschliesslich gespiegelter Varianten berücksichtigt und isoliert getestet; Details im Verwaltungsnachtrag und ADMIN.md.

Kein Löschvorgang ist implementiert. `ContentStore.anyReference` berücksichtigt auch Entwürfe als Anschlussstelle für spätere sichere Löschungen. Ein Fehler nach Dateierzeugung kann unreferenzierte private Dateien hinterlassen; es gibt keine atomare Transaktion über MongoDB und Dateisystem und keine automatische Bereinigung.

## Überprüfbarer Import

Vorbereitete Daten: `content-import/projects.json`, Version 1, alle sechs Projekte mit DE-/EN-Texten und bisherigen Fakten. Alle sind **draft**, `publishedAt=null`; der erste Import veröffentlicht nichts. `media` ordnet lokale Aliasnamen den unveränderten Kundendateien und DE-/EN-Alttexten zu. `coverImageId` und `mediaIds` enthalten im Manifest diese Aliasnamen und werden bei der Prüfung in serverseitige IDs übersetzt. Die drei bisherigen Startseitenprojekte haben `homeOrder` 1/2/3.

Der Import ist ein Übertragungswerkzeug, keine Voraussetzung für die spätere Kundenpflege. Es wird weder beim Backendstart noch beim Build automatisch importiert. Keine Zugangsdaten im Manifest. Aus dem Backendordner in PowerShell, Java 17 erforderlich:

```powershell
.\content-import\import-content.ps1 -MediaRoot C:\Temp\basel-media-test
```

Das ist der Standard-Dry-Run: prüft Metadaten, Kategorien, IDs/Slugs, Bildquellen und Pflichtfelder, erzeugt Varianten nur im Arbeitsspeicher, schreibt weder Dateien noch Daten und öffnet keine Datenbankverbindung. Er prüft deshalb keine Konflikte mit einer bestehenden Datenbank.

Erst nach gesondertem Auftrag, Sicherung und Auswahl einer isolierten Testdatenbank wäre die tatsächliche Übertragung möglich:

```powershell
# CONTENT_IMPORT_MONGO_URI vorher ausschliesslich lokal/serverseitig setzen.
.\content-import\import-content.ps1 -MediaRoot C:\Temp\basel-media-test `
  -Apply -Database basel_content_test -ConfirmDatabase basel_content_test
```

`-Manifest` und `-SourceRoot` erlauben andere geprüfte lokale Daten. `-UpdateDrafts` erlaubt ausschliesslich ausdrücklich geänderte Entwürfe; bestehende Veröffentlichungen und Legacy-Beiträge ohne Status werden nicht überschrieben. Identische wiederholte Importe aktualisieren nichts. Ein Slug darf nur derselben stabilen ID gehören. `-AllowPublish` ist eine zusätzliche ausdrückliche Schranke für bereits bewusst als veröffentlicht vorbereitete neue Datensätze und allein keine Freigabe, dies produktiv auszuführen. Bestehende Entwürfe werden damit nicht automatisch veröffentlicht; deren spätere Statusänderung gehört zur geschützten Verwaltung.

Vor ersten Schreibzugriffen prüft der Import den gesamten Datensatz gegen bestehende IDs/Slugs und Medien. Beim expliziten Apply werden eindeutige partielle Slug-Indizes für Zeichenketten pro Inhaltstyp angelegt; Legacy-Dokumente ohne Slug bleiben erhalten. Bereits bestehende doppelte Slugs führen zu einem Fehler statt einer stillen Reparatur. Importe seriell ausführen; bei Abbruch Sicherung und Zustand prüfen. Keine Dateien, Dokumente oder alten Blogbeiträge werden automatisch gelöscht. Eine Migration alter Blogbeiträge erfordert zuerst eine gesichtete Zuordnung zu den neuen Pflichtfeldern und eine eigene Freigabe; IDs bleiben dabei bestehen.

## Frontend und lokale Entwicklung

Projektkarten, Filter, Kontaktaufruf, DE/EN und bestehende URLs bleiben erhalten. Projektinhalte kommen ausschliesslich aus der API, nicht aus einer zweiten statischen Laufzeitkopie. `projectCatalog.ts` enthält nur Präsentationstypen, Formatierung und unabhängige Marketingangaben. Die alten Bilddateien werden aus Bestandsschutz nicht gelöscht, aber nicht mehr als Projekt-Datenquelle verwendet.

Startseite lädt nur die drei ausgewählten Projekte dynamisch. Dienstleistungen und weitere statische Bereiche bleiben bei Ausfall benutzbar. Lade-, Leer-, Fehler- und Wiederholungszustände sind vorgesehen. Nach zehn Sekunden wird ein hängender API-Abruf beendet. Projektanker warten auf Daten, laden bei Bedarf ein Projekt ausserhalb der ersten Seite nach und setzen Scrollposition/Fokus. `/#projects` bleibt gültig.

Vite leitet ausschliesslich die neuen Inhalts-/Medienpfade an `CONTENT_DEV_API_TARGET` weiter (Standard `http://127.0.0.1:8081`); bestehender Kontaktproxy bleibt. Alternativ bestehendes `VITE_API_URL` verwenden. Produktion nutzt die vorhandene `/api/`-Weiterleitung, benötigt aber bereitgestelltes Backend, MongoDB, Medienmount und separat freigegebene Daten. Ohne diese Einrichtung zeigt die lokale Website einen Fehler bzw. bei nur importierten Entwürfen einen Leerzustand. Keine statischen Daten als verdeckten Ersatz veröffentlichen.

## Nächster Auftrag: geschützte Kundenverwaltung

Wiederverwendbar sind Kontomodell, Spring Security, MongoDB, Inhaltsfelder und die gekapselte Medienverarbeitung. Die bestehende Anmeldung ist vor Schreibzugriffen jedoch zu vervollständigen:

- `AccountService` speichert und vergleicht Passwörter derzeit im Klartext, trotz vorhandenem BCrypt-Bean: sichere Hashes, Bestandsmigration/Passwortwechsel erforderlich.
- `TokenService` erzeugt einen zufälligen Schlüssel pro Prozess; `TokenUtil` und Tokenausstellung müssen auf einen konsistenten, serverseitig konfigurierten Schlüssel und überprüfte Ablauf-/Abmeldebehandlung gebracht werden.
- Der aktuelle Security-Filter verwendet HTTP Basic und bindet keinen vollständigen Bearer-Authentifizierungsweg für die ausgegebenen Login-Tokens ein. `/api/**` ist im Bestand pauschal freigegeben. Neue Lese-Namespaces sperren andere Methoden; `/api/admin/**` ist jetzt ausdrücklich gesperrt.
- Öffentliche Registrierung ist kein Freigabeprozess für Administratoren. Redakteurs-/Adminrechte müssen serverseitig vergeben und für jede Verwaltungsaktion geprüft werden. Bestehenden Accountabruf auf Datenminimierung prüfen.
- Cookie-/Tokenentscheidung, CSRF-Schutz, Loginbegrenzung und Uploadbegrenzung für geschützte Schreibzugriffe festlegen und gezielt testen.

Der nächste Auftrag baut danach Verwaltung für Projekte/Blog, sicheren Upload über `MediaStorage`, Bildauswahl und Alttexte, Entwurfsvorschau nur mit Berechtigung sowie bewusstes Veröffentlichen/Zurückziehen. Unique-Slug-Indizes auch für Verwaltung sicherstellen; Medienlöschung muss beide Inhaltstypen inklusive Entwürfen prüfen. Keine öffentlichen Preview-Tokens oder Schreibendpunkte unter der alten pauschalen Freigabe ergänzen. Eine neue öffentliche Blogoberfläche und Artikelentwürfe bleiben ein separater Auftrag.

## Prüfungen und Grenzen dieses Schritts

31 Backend-Tests erfolgreich: bestehender Kontaktversand, Inhalts-/Legacy-Entwurfsfilter, Bildfreigabe/Zurückziehen, erlaubte Methoden/CORS, Kategorien, Sortierung/Paginierung, Importkonflikte/Wiederholung, tatsächliches Bildformat, Grössen-/Pixellimits und Pfadprüfung. MongoTemplate-Abfragen und isolierte In-Memory-Importziele wurden getestet; keine reale/produktive MongoDB verwendet. Tatsächliche Indexerstellung und NAS-Dateirechte bleiben im isolierten Integrationstest vor Betrieb zu prüfen.

TypeScript und Frontend-Produktionsbuild erfolgreich; bestehende Bundlewarnung >500 kB bleibt. Browserprüfungen in lokalem Edge mit isoliert simulierten API-Antworten: DE/EN bei 390/768/1280 px, Bilder, Kategorien, kein horizontaler Überlauf, Startseitenauswahl, Dauern, asynchrone Anker, Leer-/Fehlerzustände und Wiederholen. Echter Dry-Run mit den sechs Kundenoriginalen erfolgreich, ohne Dateien oder DB-Verbindung. Keine produktiven Daten verändert, Inhalte veröffentlicht, echten E-Mails versendet oder NAS-/Workflow-/Demo-Konfiguration geändert.

## Echte NAS-Vorschau (24. Oktober 2026)

Eine separat beauftragte lokale Vorschau mit sechs Projekten und zwölf Bildvarianten läuft jetzt: private Inhalts-API/Medien auf dem NAS und isolierte Atlas-Datenbank `basel_homefixer_preview`. Keine produktive Datenbank, öffentlichen Website-Container oder bestehenden NAS-Compose-Dateien geändert. Start, Verbindung ohne erlaubte SSH-Portweiterleitung, Ablage und Betriebsgrenzen: [NAS-VORSCHAU.md](../NAS-VORSCHAU.md).

## Authentifizierungsnachtrag (24. Oktober 2026)

Die zuvor benannten Authentifizierungslücken wurden im lokalen Quellcode gezielt bearbeitet: Hashes, ein konfigurierter Signaturschlüssel, Bearer-Prüfung, serverseitige Rollen und abgesicherte Kontoantworten. Siehe [AUTH.md](AUTH.md) für Einrichtung und Betriebsgrenzen. Die laufende NAS-Vorschau wurde dabei nicht aktualisiert. Geschützte Inhalts-Schreibendpunkte, Upload und Verwaltungsoberfläche bleiben offen; kein produktiver Zugang oder Altpasswortbestand wurde verändert.

## Nachtrag 25. Oktober 2026: geschützte Verwaltungs-API

Die Backend-Verwaltung ist implementiert: Projekte/Blog als Entwürfe anlegen und versioniert bearbeiten, ausdrücklich veröffentlichen/zurückziehen, Startseitenauswahl, geschützte Vorschau und Bildupload. API-Verträge, If-Match/ETag, Konfiguration und Grenzen: [ADMIN.md](ADMIN.md). Die frühere Aussage über fehlende Schreibendpunkte ist damit überholt; die Verwaltungsoberfläche bleibt offen. Die laufende NAS-Lesevorschau wurde nicht geändert.

MediaStorage berücksichtigt jetzt EXIF-Orientierung und begrenzt die längste Kante auf 480/1280 Pixel. Alte Bild-IDs bleiben lesbar; neue Verarbeitung erzeugt eigene IDs, ohne alte Dateien zu ersetzen. Gemeinsame Startseitenauswahl ist jetzt versioniert in content_settings; homeOrder gilt nur vor der ersten expliziten Auswahländerung als Rückfall. Abweichende Importänderungen an bereits verwalteten/versionierten Inhalten werden abgewiesen.

## Nachtrag 25. Oktober 2026: öffentliche Blogoberfläche

`/blog` verwendet die neue Übersicht, `/blog/:slug` die Artikelansicht. `/latestPosts` leitet mit replace nach `/blog` weiter. Blog in gemeinsamer Navigation ergänzt; Artikel markieren Blog statt Startseite aktiv. Der alte Integrationscode bleibt als Bestand erhalten, wird für diese Routen aber nicht verwendet. Die Startseite hat weiterhin keine Blog-/Instagram-Abhängigkeit.

API-Paginierung sechs Beiträge pro Seite, URL-Zustand über category/page, DE/EN-Kategorien und Inhalte, Lade-/Fehler-/Leer-/404-Zustände sowie Wiederholen/Filterreset. Die Kategorien stammen aus der vorhandenen zentralen Liste. Keine erfundenen Daten oder veröffentlicht erscheinenden Entwürfe als Fallback. Suchleiste und Beispieldaten der HTML-Referenz wurden nicht als funktionslose Features übernommen; eine API-Suche war nicht beauftragt.

Die öffentliche Artikelantwort enthält jetzt zusätzlich mediaIds für zugeordnete weitere Bilder. Listen enthalten weiterhin keinen vollständigen Artikeltext. Der gemeinsame SafeMarkdown-Baustein verlangt in öffentlicher Ansicht und geschützter Vorschau eine dem Inhalt zugeordnete Medien-ID und einen nicht leeren Alttext. Raw-HTML wird als Text behandelt; Links sind auf sichere Protokolle/lokale Ziele begrenzt, Bilder auf die kontrollierte Medien-API. Keine zusätzlichen SVG-/HTML-/Fremdbildquellen. Titel- und Artikelbilder bleiben dynamisch, ohne Frontend-Build austauschbar. Bei älteren API-Antworten ohne mediaIds werden nicht zugeordnete Inlinebilder sicherheitshalber ausgelassen.

Drei Artikelentwürfe mit Originalfakten/-bildern im vorhandenen Importformat vorbereitet: content-import/blogs.json, erzeugt aus sechs DE-/EN-Markdown-Dateien und Metadaten unter content-import/blog-drafts. Dry-Run, Quellen, Annahmen und private Sichtung siehe dortige README.md. Alle bleiben draft ohne Veröffentlichungsdatum. Englische Texte tragen einen expliziten Hinweis zur Kundensichtung. Keine persönlichen Altbeiträge geändert, kein produktiver Import oder öffentliche Veröffentlichung.

Abschlussprüfung Blog am 25. Oktober 2026: TypeScript und Produktionsbuild erfolgreich; neun gezielte Frontend-Tests erfolgreich. Maven verify/JAR-Build: 74 Tests, keine Fehler/Fehlschläge/übersprungenen Tests. Echter Dry-Run mit allen drei Kundenbildern erfolgreich; zusätzlicher isolierter Importtest prüft Wiederholbarkeit, private Entwürfe und fehlendes Veröffentlichungsdatum. Reproduzierbarer Edge-/Playwright-Browsercheck erfolgreich: alle drei echten Entwürfe ausschliesslich in geschützter Verwaltung/Vorschau gesichtet, deren öffentliche Artikel und Bilder vor/nach den Tests 404. Sieben separate synthetische lokale Beiträge prüfen öffentliche Karten, Kategorie/Pagination, Artikel, direkte URLs, /latestPosts-Weiterleitung, deklarierte Inlinebilder, HTML-/Link-Sicherheit, serverseitige Ablehnung externer Bilder, DE/EN, 390/768/1280 px, mobile Navigation/Tastatur, Wiederholen und statische Startseite bei API-Ausfall ohne Blog-/Instagram-API-Aufrufe. Screenshot-Sichtprüfung Mobil/Desktop durchgeführt. Synthetische Daten liegen nur in der flüchtigen lokalen Test-Fixture, keine echten Kundenentwürfe veröffentlicht.

Bestehende Bundlewarnung >500 kB und veraltete Browserslist-Daten bleiben. Kein realer MongoDB-Apply, kein NAS-Test oder Deployment dieses Blogschritts. Mail-Einzeltest ist separat in CONTACT.md dokumentiert; der angenommene Versand bestätigt keinen Postfacheingang. Admin-Einrichtung nur Dry-Run/Anleitung, kein produktiver Zugang angelegt.

## Regulärer NAS-Betrieb – Vorbereitung 25. Oktober 2026

Verbindlicher vorbereiteter Betriebsweg: `../deploy/nas/README.md`, inklusive Compose-/Docker-/Nginx-Vorlagen, gesonderter isolierter Testkonfiguration, Backup/Rücknahme und geprüftem Übertragungspaket. Der tatsächliche reguläre DB-Name wurde lesend als **DEMO** bestätigt. `basel_homefixer_preview` ist keine implizite Produktionsquelle. Medienziel privat `/volume1/docker/basel-homefixer/content/media`, weiterhin nur über die geprüfte API. Keine NAS-Änderung oder Datenübertragung erfolgt.

Die bestehende CI bleibt unverändert und verwendet weiter alte Dockerfiles/:dev. Für den Regelbetrieb sind separat bereinigte Build-Kontexte vorbereitet; der lokale Packaging-Nachweis ersetzt keinen echten Image-/MongoDB-/NAS-Test. Entwürfe und Admin-Konten erst nach Abschaltung des alten öffentlichen Backends in DEMO übertragen; alter Code darf sie nicht öffentlich lesen. Alle neun vorbereiteten Kundeninhalte bleiben draft. Einrichtung der zwei unabhängigen Website-Admins: `../design/Kundenprojekt_PJA/ADMIN-ZUGANG.md`.


## Aktuelle Entscheidung des Betreibers: getrennte reguläre Datenbank

Die frühere Empfehlung, die beobachtete DEMO-Datenbank weiterzuverwenden, ist aufgehoben. DEMO gehört zur Demo-Website und bleibt unberührt. Neues vorgesehenes Ziel ist basel_homefixer; noch nicht angelegt. basel_homefixer_preview bleibt die isolierte Test-/Vorschauumgebung. Cluster und Datenbank unterscheiden: Der endgültige Cluster wird bewusst durch den Betreiber gewählt, kein Demo-exklusiver Cluster wird automatisch übernommen. Details: deploy/nas/DATENBANK.md und Projekt-README. Blog bleibt auf ausdrücklichen Wunsch sichtbar; Projekte sind Referenzfälle, Blog ist redaktioneller Ratgeber. Keine produktive Änderung durch diese Dokumentationskorrektur.

