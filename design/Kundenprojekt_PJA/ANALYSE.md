# Prüfung der Kundenentwürfe

Stand: 19. Oktober 2026. Grundlage: der Ordner `Kundenpaket` in diesem Verzeichnis und der vorhandene React-/Material-UI-/Spring-Boot-Code.

## Einschätzung

Mit einer ersten Designumsetzung kann begonnen werden: Farben, Typografie, Abstände, Karten, Seitenabschnitte, Bildauswahl und die allgemeine Wirkung sind hinreichend beschrieben. Die Rückgabe ist deutlich mehr als eine unveränderte Vorlage. Dienstleistungen, Teamvorstellung, Kontakt und Blog wurden fachlich neu gestaltet; Bilder sind mitgeliefert.

Die scheinbar defekten vier Seiten sind zu unterscheiden: Die alte Übersicht verlinkt vier nicht mehr vorhandene Dateinamen. Zusätzlich gibt es zwei tatsächlich abgebrochene HTML-Dokumente. Startseite, Über uns und Vermietung enthalten daneben vollständige Entwürfe. Sie sollten deshalb nicht allein aufgrund der defekten Übersicht als irrelevant eingestuft werden.

Für eine vollständige Funktionsumsetzung fehlen einige Entscheidungen: Welche Entwurfsversion gilt? Welche Seiten sollen erscheinen? Was sollen die Aktionsbuttons tun? Soll ein echtes Kontaktformular Nachrichten versenden? Sind die Blog-Beispiele und Leistungsversprechen freigegebene Inhalte?

Die fehlende Ausfüllung von `WUENSCHE.md` ist dafür kein eigenständiger Blocker. Die entscheidenden Fragen können mit einer kurzen Antwort geklärt werden.

## 1. Ursache der nicht erreichbaren Seiten

`Kundenpaket/index.html` verwendet weiterhin die ursprünglichen Dateinamen in Seitenlinks, Auswahlfeld und Vorschau. Folgende Ziele fehlen:

| Erwarteter Dateiname | Tatsächlich zurückgesendet |
| --- | --- |
| `01-startseite.html` | `01-startseite2.html` und `01-startseite (AI history lost).html` |
| `02-ueber-uns.html` | `02-ueber-uns2.html`, eine ältere Variante und `02-ueber-uns3 (corrupt).html` |
| `05-vermietung.html` | `05-vermietung (optional -- Link airbnb).html` |
| `06-tech-insights.html` | `06-tech-insights (optional -- CI updated).html` |

Damit sind die vier vom Entwickler genannten Probleme in der Übersicht erklärbar. Umbenannte Dateien sind grundsätzlich kein Hinweis darauf, dass die Seite nicht gewünscht ist. Die Zusätze „optional“ sind allerdings ein Anlass, den Umfang zu bestätigen.

Für die Prüfung gibt es neben diesem Bericht `PRUEFSTART.html`. Diese zusätzliche Linkübersicht öffnet die tatsächlich vorhandenen Dateinamen. Sie repariert keine Navigation innerhalb der Kundenentwürfe. Die Originaldateien wurden nicht verändert.

## 2. Bewertung je Seite

### Startseite

Die beiden vorhandenen Startseiten enthalten ein vollständiges HTML-Dokument mit eingebettetem CSS, einem Inhaltsbereich und JavaScript für das mobile Menü. Alle referenzierten Bilddateien sind vorhanden. Die 2er-Datei ist als Arbeitsgrundlage geeignet, ihre Freigabe ist aber noch zu bestätigen.

Inhalt: Einstiegsbereich mit zwei Aktionsbuttons, fünf Nutzenargumente, fünf Dienstleistungen, drei Projektbeispiele mit Bildern und Kennzahlen, Kontaktaufruf, zwei angekündigte Blog-Beiträge und Fusszeile. Die ehemals zentrale Instagram-Einbettung ist nicht mehr im Startseitenentwurf enthalten; ein Instagram-Link steht noch in der Fusszeile.

Die ältere und die 2er-Variante haben weitgehend denselben Inhalt. Geändert wurden insbesondere CSS und eine Textformatierung. Der Zusatz „AI history lost“ beweist keine Beschädigung der Datei.

Die Navigation verwendet `#startseite`, `#dienstleistungen`, `#projekte`, `#blog` und `#kontakt`, aber entsprechende HTML-IDs fehlen. `data-design-id` ist kein Ersatz für eine Sprungmarken-ID. Die Buttons „Kostenlose Erstberatung buchen“ und „Projekte ansehen“ haben keine Aktion. Es liegt somit eine verwertbare Designreferenz vor, keine fertig funktionierende Seite.

### Über uns

`02-ueber-uns2.html` enthält eine vollständige Teamseite mit Pascal Jaquiéry und Divine Mon, Teamfoto, zwei Porträts, Kompetenzen, Vision, Werten und Kontaktaufruf. Alle drei eingebundenen Fotos (`Portrait.jpg`, `Pascal.jpg`, `Divine.jpg`) liegen vor.

Gegenüber der älteren Variante wurden unter anderem die Porträt-Platzhalter durch Fotos ersetzt und der Einleitungstext zu „Projektmanagement, Real-Estate, Renovationen und Unternehmertum“ geändert. Die 2er-Datei ist deshalb ein plausibler Kandidat für die Umsetzung, aber nicht automatisch der bestätigte finale Stand.

`02-ueber-uns3 (corrupt).html` ist tatsächlich unbrauchbar: nur rund 1,5 KB, Abbruch im CSS bei `body {`, kein Inhaltsbereich und kein geschlossenes Style-Element. Das vorhandene `</html>` am Ende macht die Datei nicht vollständig. Fehlende Inhalte lassen sich daraus nicht verlässlich rekonstruieren.

Menülinks auf `01-startseite.html` und `02-ueber-uns.html` führen ins Leere. Weitere Sprungmarken und der Beratungsbutton sind nicht funktional.

### Dienstleistungen

Vollständiger, brauchbarer Entwurf. Der bisherige technische Website-Text wurde durch sechs konkrete Leistungsbereiche ersetzt: Küche, energetische Renovation, Heizung, Smart Home, Badezimmer und Projektmanagement/Consulting. Dazu kommen ein vierstufiger Ablauf und FAQ.

Design und Aufbau sind umsetzbar. Die fünf „Mehr erfahren“-Buttons und die Beratungsbuttons haben noch keine definierte Aktion. Menülinks und mehrere Sprungmarken sind defekt.

Vor Veröffentlichung sollte der Kunde die tatsächlich angebotenen Leistungen und Aussagen bestätigen: kostenlose Erstberatung, Zeitangaben, Versicherung aller Partner, Einhaltung von SIA/Vorschriften und „2–5 Jahre Gewährleistung“. Diese Angaben sind im Entwurf enthalten; die Prüfung bestätigt nicht deren sachliche oder rechtliche Richtigkeit.

### Kontakt

Vollständiger, brauchbarer Entwurf mit E-Mail, Telefonnummer, Standort, Erreichbarkeit und Formular. Das Formular hat Name, E-Mail, optionale Telefonnummer, Dienstleistungswahl, Nachricht und eine verpflichtende Einwilligungs-Checkbox.

Wichtig: Der JavaScript-Code verhindert das normale Absenden, schreibt Daten nur in die Konsole und zeigt trotzdem „Ihre Nachricht wurde empfangen“ an. Es gibt keinen Versand an einen Server. Die Darstellung darf daher nicht als funktionierender Nachrichtenkanal übernommen werden.

Im untersuchten vorhandenen Backend wurde kein Kontaktversand-Endpunkt gefunden. Ein echter Versand wäre zusätzliche Funktionalität. Benötigt werden zumindest die Entscheidung „Formular oder nur E-Mail/Telefon“ sowie beim Formular das Empfängerpostfach. Die technische Versandlösung kann anschliessend der Entwickler festlegen.

Die Aussage „Antwort innerhalb von 24 Stunden“ und die genannten Zeiten Montag bis Freitag 8–18 Uhr sollten bestätigt werden. Der Datenschutzlink in der Fusszeile zeigt mit `#privacy` auf die Formular-Checkbox, nicht auf eine Datenschutzerklärung.

### Vermietung / lokale Seite

Die umbenannte Datei ist ein vollständiges HTML-Dokument, nicht offensichtlich abgebrochen. Der alte Übersichtslink und der eigene Menülink verwenden aber den nicht vorhandenen Namen `05-vermietung.html`.

Der Inhalt beschreibt inzwischen eine umfassende Immobilienverwaltung mit drei Wohnungsangeboten, konkreten Monatsmieten, Vermarktung, Mieterauswahl, Verwaltung, Gebühren von 8–10 % und FAQ. Das ist ein deutlich grösserer Umfang als ein Airbnb-Link.

Der Dateiname enthält zugleich „optional -- Link airbnb“. Daraus lässt sich keine verbindliche Absicht ableiten. Empfohlene Zwischenannahme: bestehendes externes Airbnb-Ziel beibehalten, bis eine lokale Seite ausdrücklich bestätigt ist. Die angegebenen Wohnungen, Preise, Gebühren und insbesondere die pauschale FAQ-Aussage zur Haftung nicht ohne inhaltliche Bestätigung übernehmen.

### Tech Insights

Die Datei ist tatsächlich unvollständig: Abbruch im CSS bei `.brand { text`, kein geschlossenes Style-Element und kein Body mit Seiteninhalt. Die Datei enthält eine Farb-/Layoutanpassung, aber keine verwertbare inhaltliche Seitenvorlage.

Eine Rekonstruktion würde neue Inhalte erfordern. Die Seite kann aus der ersten Umsetzung herausgehalten werden. Zu klären ist nur, ob sie überhaupt gewünscht ist; falls ja, benötigt sie einen vollständigen Entwurf oder ein kurzes Inhaltsbriefing.

### Aktuelle Beiträge / Blog

Vollständiger Entwurf mit sechs Artikelkarten, Suche und Kategorien. Der lokale JavaScript-Code enthält eine Filterlogik für Titel, Kurztext und Schlagwörter. Die Artikel selbst fehlen: „Artikel lesen“ zeigt auf nicht vorhandene Ziele `#article-1` bis `#article-6`. Im Menü wird ausserdem `07-blog.html` verlinkt, obwohl die Datei `07-aktuelle-beitraege.html` heisst.

Es sind vollständige Titel und Kurztexte, aber keine dazugehörigen Langtexte mitgeliefert. Nicht voraussetzen, dass sechs veröffentlichungsfertige Beiträge vorliegen. Auf der Startseite stehen wiederum zwei andere Beiträge auf „Bald verfügbar“.

Der bestehende Blog-Datentyp enthält `id` und mehrsprachige `description`, aber keine eigenen Felder für Titel, Kategorie, Datum, Schlagwörter oder Vorschaubild. Die Listenansicht lässt sich gestalten; bei gewünschter Pflege, Suche und Artikeldetails muss entschieden werden, ob vorhandene Beiträge verwendet oder neue Artikel angelegt werden. Eine neue Verwaltungsoberfläche ist im Entwurf nicht beauftragt und nicht zwingend nötig.

Zusätzlich ist `/blog` in der aktuellen React-Anwendung noch ein Alias der Startseite, während die eigene Beitragsseite `/latestPosts` heisst. Die spätere Route muss bewusst zugeordnet werden.

## 3. Was bereits klar ist

- Gemeinsame Palette: Dunkelgrün `#2d5a3d`, helleres Grün `#3d7a4d`, Gold `#d4a574`, helle Hintergründe und dunkle Schrift.
- Systemschrift statt einer zusätzlichen externen Schrift; Karten, Rundungen, Schatten und grosszügige Abstände.
- Kernseiten verwenden meist 1.200 px maximale Breite, abgestufte Schriftgrössen und responsive Regeln für 768/480 px. Der Blog weicht bei Kopfbereich, Breite und Breakpoints ab. Ein gemeinsamer Kopf-/Fussbereich sollte bei der Umsetzung vereinheitlicht werden.
- Unternehmensdarstellung mit Pascal und Divine; echte Bilddateien für Team und Projekte vorhanden.
- Neu gestaltete Leistungsübersicht statt der bisherigen Technikbeschreibung.
- Deutsch als vorhandene Entwurfssprache, DE/EN als beabsichtigte Oberfläche. Funktionsfähige englische Entwurfsinhalte liegen nicht bei.

Das sind ausreichende Vorgaben für ein erstes React-Layout. Kleinere Unstimmigkeiten bei Abstand, Bildoptimierung oder dem gleichen Header auf allen Seiten kann der Entwickler lösen, ohne jede Einzelheit beim Kunden abzufragen.

## 4. Was noch zu entscheiden ist

### Vor Festlegung des Implementierungsumfangs

1. **Dateiversionen und Seitenumfang:** Sind Startseite2 und Über-uns2 massgeblich? Startseite und Über uns nicht allein wegen der defekten Übersicht verwerfen. Dienstleistungen/Kontakt/Blog bestätigen; Tech Insights weglassen oder später? Vermietung als Airbnb-Link oder eigene Seite?
2. **Navigation und Aktionsziele:** Ist „Projekte“ ein Abschnitt auf der Startseite oder eine eigene Seite? Führt „Erstberatung buchen“ zum Kontaktbereich, zu einer E-Mail oder zu einer Terminbuchung? Was bedeutet „Mehr erfahren“: Detailtext aufklappen, eigene Detailseite oder Kontakt? Es liegt keine Buchungslösung bei.
3. **Kontaktformular:** Echter Nachrichtenversand gewünscht oder reichen Kontaktlinks? Falls Versand: an welche Adresse?
4. **Blog:** Karten nur als Gestaltungsmuster, angekündigte Beiträge oder echte Artikel? Bestehende Beiträge weiterverwenden? Neue Langtexte fehlen. Wer liefert bzw. pflegt sie?

### Spätestens vor Veröffentlichung

- Texte, Projektkosten und -dauern, Teamrollen und Qualifikationen, Garantie-/Versicherungszusagen und kostenlose Angebote bestätigen.
- Kontaktdaten, Erreichbarkeit und tatsächliche Social-Media-Ziele bestätigen. Die Vorlagen verwenden vielfach Platzhalteranker statt echter Instagram-/Website-Links.
- Freigegebene Inhalte für Datenschutz/Impressum bereitstellen. Die verlinkten Inhalte fehlen im Paket; keine rechtliche Bewertung in dieser Prüfung.
- Umgang mit Englisch: Übersetzung der neuen deutschen Texte durch Entwickler/KI mit anschliessender Kundensichtung oder vom Kunden gelieferte Fassung.
- Soll die bisherige Instagram-Einbettung entfallen und nur ein Link bleiben? In der neuen Startseite wird sie nicht mehr gezeigt.

## 5. Empfohlenes Vorgehen

1. Kurze Rückfragen aus `RUECKFRAGEN-KUNDE.txt` klären. Das Ausfüllen des gesamten Wünsche-Dokuments ist nicht nötig.
2. Parallel kann die Umsetzung gemeinsamer Gestaltungswerte und wiederverwendbarer Seitenbausteine beginnen. Als vorläufige Referenzen die 2er-Startseite, Dienstleistungen, Kontakt und die 2er-Teamseite verwenden, bis abweichende Freigabe eintrifft.
3. Zuerst Kernseiten als React-Vorschau aufbauen. Kontaktformular sichtbar als noch nicht angebunden behandeln, solange Versand nicht umgesetzt ist. Neue Blog-Datentypen und Vermietungsfunktionen erst nach der Umfangsentscheidung ergänzen.
4. Navigation, Buttons, Artikelziele und Sprache in der echten Website korrekt anbinden; Prototyp-Verhalten nicht blind übernehmen.
5. Nach Bestätigung verbleibende Funktionen und Inhalte ergänzen und gemeinsam auf Desktop/Mobil prüfen. Veröffentlichen erst mit bestätigtem Inhalt und funktionierenden gewählten Kontaktwegen.

## Prüfumfang und Grenzen

Durchgeführt: statische Prüfung aller zehn nummerierten Kunden-HTML-Dateien, Dokumentstruktur, vorhandene Bild- und Dateiziele, Sprungmarken, JavaScript-Syntax und tatsächlicher Script-Inhalt, Variantenvergleich sowie Abgleich mit den vorhandenen React-Routen, dem Blogmodell und Backend-Controller. Die Rohdaten stehen in `dateipruefung.json`.

Keine visuelle Browserabnahme, kein echter Nachrichtenversand und keine Prüfung externer Angebote/Leistungsangaben. Ein vollständiges HTML-Dokument bedeutet nicht automatisch ein fehlerfreies Browserlayout. Die technische Ursache der vier nicht erreichbaren Übersichtsziele und der beiden abgebrochenen Dokumente ist unabhängig davon im Dateibestand nachgewiesen.

Die Kundenoriginale und der Anwendungscode wurden für diese Analyse nicht geändert.
