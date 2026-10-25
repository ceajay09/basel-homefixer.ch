# Analyse des neuen Kundenstands vom 24. Oktober 2026

Grundlage: Kundenpaket_20261024/Kundenpaket, bisherige Kundenoriginale unter Kundenprojekt_PJA/Kundenpaket, die bestehenden Freigaben und der lokale React-/TypeScript-/Material-UI-Stand nach Prompt 1 und 2.

## Ergebnis

Die Ergänzung erfüllt die besprochene Richtung: Startseite als Übersicht beibehalten, Dienstleistungen vertiefen und eine eigene Projektseite ergänzen. Es ist kein grundlegender Architekturumbau nötig. Die neuen Inhalte lassen sich in das zentrale Theme, Layout.tsx, PageSection und ContactCta übernehmen. Backend und Datenbank werden dafür nicht benötigt.

Mit der technischen Umsetzung kann begonnen werden. Zwei inhaltliche Entscheidungen sollten parallel geklärt werden: widersprüchliche Projektdauern und die Bedeutung der sechs Links „Details ansehen“. Das muss nicht die Arbeit an Dienstleistungen, Team und Kontaktvorbereitung aufhalten. Keine stillschweigende Freigabe widersprüchlicher Angaben.

Diese Analyse ändert weder Anwendungscode noch die bisherigen Freigabe-/Plandateien. Die folgenden Empfehlungen sind ein Vorschlag für deren nächste Aktualisierung.

## 1. Tatsächliche Änderungen gegenüber dem ersten Kundenpaket

| Datei / Bereich | Befund |
| --- | --- |
| 01-startseite2.html | Bytegleich. Weiterhin fünf Leistungen und drei Projekte. Keine neuen Detailverlinkungen eingebaut. |
| 02-ueber-uns2.html | Bytegleich. Bestehende Freigabe und geplante Teamumsetzung bleiben anwendbar. |
| 03-dienstleistungen.html | Erweitert von sechs auf acht Leistungen; sieben aufklappbare Detailbereiche mit konkreten Arbeitsschritten. |
| 04-kontakt.html | Bytegleich. Noch keine Auswahl für die beiden neuen Leistungen. Versand weiterhin nur vorgetäuschte Erfolgsmeldung in der HTML-Vorlage, kein Backend. |
| 08-Projekte.html | Neu: sechs Projekte, Kategorienfilter, Kennzahlen, Kontaktaufruf. |
| Neue Bilder | Badezimmer.jpg, Bodenheizung.jpg, Elektro.jpg, Haustüre.jpg. |
| Bestehende Bilder | Unverändert; alle anderen Dateien im bisherigen assets-Ordner sind bytegleich. |
| index.html / Anleitung / WUENSCHE.md | Unverändert; neue Projektseite nicht in der alten Übersicht nachgeführt. WUENSCHE.md bleibt eine unausgefüllte Vorlage. |
| Blog und optionale Altdateien | Unverändert. Ihre Anwesenheit ist keine erneute Beauftragung. Frühere Ausschlüsse bleiben bestehen. |

Die nicht ausgefüllte WUENSCHE.md ist kein Blocker. Frühere Freigaben zu Sprachumschaltung, Airbnb, Blogaufschub und Frontend ohne Versand gelten weiter. Die alte beschädigte Über-uns-Datei bleibt unberücksichtigt.

## 2. Dienstleistungen: fachlich deutlich vollständiger

Neue Reihenfolge: Küche, Badezimmer, Heizung, Fenster & Türen, Elektroplanung und Installation, Smart Home, energetische Renovation, Projektmanagement/Consulting.

Die ersten sieben Leistungen besitzen jetzt ausklappbare Arbeitsschritte; Projektmanagement/Consulting führt weiterhin zur kostenlosen Beratung. Die Details beschreiben Bestandsaufnahme, Planung, Offerten, Koordination, Ausführung und Übergabe, je nach Leistung unterschiedlich ausführlich.

Wichtig: Die Vorlage ändert das bisher freigegebene Verhalten. „Mehr erfahren“ öffnet jetzt Informationen auf der Dienstleistungsseite. Es ist dort kein Kontaktlink mehr. Als bewusste Weiterentwicklung übernehmen; nicht den alten Prompt 1 unverändert wiederholen. Ein eigener, eindeutig beschrifteter Beratungslink kann ergänzend beim Detailbereich stehen.

Technische Übertragung:

- Bestehende Services.tsx erweitern, keine acht eigenen Leistungsseiten bauen.
- MUI Accordion oder Collapse mit automatischer Höhe, aria-expanded, zugeordneten Inhalts-IDs und übersetzten Schaltflächen verwenden.
- Bestehende Service-Schlüssel kitchen, energy, heating, smart-home, bathroom und consulting beibehalten.
- Zwei neue Schlüssel vorschlagen: windows-doors und electrical. energy bleibt energetische Renovation und darf nicht für Elektroinstallation umgewidmet werden.
- Gemeinsamen Katalog in serviceCatalog.ts um Details erweitern; Texte in den vorhandenen DE-/EN-Dateien pflegen.
- Die Kontaktvorwahl und das spätere Formular auf acht Leistungen erweitern. Der unveränderte Kontaktentwurf ist diesbezüglich überholt.
- Die Startseite darf weiterhin eine Auswahl von fünf Leistungen zeigen; ein Übersichtsausschnitt muss nicht den gesamten Katalog wiederholen. Titel, Schlüssel und sachliche Aussagen müssen übereinstimmen.

Die neue Dienstleistungsseite verwendet weiterhin Emojis und keine Leistungsfotos. Badezimmer.jpg ist neu geliefert, wird in den geprüften aktiven HTML-Referenzen aber noch nicht eingebunden. Daraus keinen zusätzlichen Bildbereich ableiten.

## 3. Projekte: neue eigene Seite ist ausreichend beschrieben

Sechs Projekte mit längeren Texten, Bild, Ort, Zeitangabe, Schlagwörtern und je zwei Kennzahlen:

1. Küchenmodernisierung – Gas zu Elektrizität.
2. Balkonverglasung.
3. Heizsystemmodernisierung über 18 Wohnungen.
4. Elektrorenovation und Smart Home, 80 m², CHF 15k.
5. Energiesparende Hauseingangs-Tür.
6. Raumthermostate/Bodenheizung und Smart Home, 15 Thermostate, CHF 1.6k.

Die Filter funktionieren in der Vorlage: Alle 6, Küchen 1, Elektrik 1, Fenster 1, Balkon 1, Heizung 2. Ein Projekt hat jeweils genau eine Filterkategorie; mehrere sichtbare Schlagwörter bedeuten aktuell keine Mehrfachzuordnung. Für sechs Einträge genügt lokaler React-Zustand, kein Server und keine Suchinfrastruktur.

Die Kennzahlen „20+ Projekte“, „40+ zufriedene Kunden“ und „16 Jahre Erfahrung“ sind neue bzw. erneut verwendete Kundenaussagen. Sie können als Entwurfsinhalte übernommen werden; vor Veröffentlichung sollte der Kunde deren Richtigkeit bestätigen.

### Ungeklärte Aktion: Details ansehen

Alle sechs Links haben lediglich href="#". Es fehlen weitere Detailseiten, Dialoginhalte oder zusätzliche Bildgalerien als eigenes Ziel.

Empfehlung für den MVP: Die gelieferten längeren Projekttexte direkt auf /projects zeigen und diese sechs Blindlinks weglassen. Die Seite ist bereits ausführlicher als die Startseite. Wenn der Kunde weitere Detailansichten möchte, müssen deren Inhalt und Umfang konkret ergänzt werden. Keine sechs zusätzlichen Seiten oder fiktiven Projektgeschichten erzeugen.

### Widerspruch bei den Projektdauern

| Projekt | Unveränderte Startseite und aktuelles Frontend | Neue Projektseite |
| --- | --- | --- |
| Küche | 8–10 Wochen | 4–6 Monate |
| Balkon | 4–6 Wochen | 2–4 Monate |
| Heizsystem | 6–8 Wochen | 4–8 Monate |

Möglicherweise ist einmal die Ausführungszeit und einmal der gesamte Zeitraum mit Planung/Lieferung gemeint. Das ist eine mögliche Erklärung, keine bestätigte Tatsache. Den Kunden bitten, die Bedeutung festzulegen. Anschliessend dieselben Daten auf beiden Seiten verwenden oder beide Zeitarten klar beschriften.

Die bisherigen Kosten von CHF 37.000, CHF 12.000 und CHF 60.000 stehen weiterhin auf der Startseite, werden auf der neuen Projektseite bei diesen drei Projekten aber nicht erneut genannt. Das ist keine direkte widersprüchliche Angabe und auch keine eindeutige Aufforderung, sie zu entfernen.

## 4. Browserbefunde und Fehler der Kundenoriginale

Geprüft wurden die geänderte Dienstleistungsseite und die neue Projektseite lokal in Microsoft Edge über Playwright, bei 390, 768 und 1280 Pixeln. Zusätzlich wurden Filter und alle sieben Aufklappbereiche bedient. Die Dateien selbst wurden nicht repariert.

### Bilder und Dateinamen

- Auf der Projektseite laden fünf von sechs Projektbildern.
- Der Türbereich referenziert assets/Gaustüre.jpg; vorhanden ist assets/Haustüre.jpg. Naheliegender Tippfehler, ohne neue Kundenrunde technisch korrigierbar; Bildmotiv bei Übernahme sichten.
- Mehrere Links zeigen weiterhin auf nicht vorhandene 01-startseite.html, 02-ueber-uns.html und 07-blog.html.
- Die Navigation verwendet 08-projekte.html, die Datei heisst 08-Projekte.html. Unter Windows meist unauffällig, auf einem System mit Beachtung der Gross-/Kleinschreibung problematisch. In React ohnehin auf /projects abbilden.
- Footer- und Social-Links enthalten weiterhin Platzhalter. Keine funktionierenden Ziele vortäuschen.

### Abgeschnittene Dienstleistungsdetails auf dem Handy

Die CSS-Grenze max-height: 2000px schneidet bei 390 px fünf von sieben Detailbereichen ab. Nach Ablauf der Animation gemessene Inhaltshöhen:

| Leistung | Benötigte Höhe, ungefähr |
| --- | --- |
| Küche | 1654 px, vollständig |
| Badezimmer | 2329 px, abgeschnitten |
| Heizung | 2263 px, abgeschnitten |
| Fenster & Türen | 2346 px, abgeschnitten |
| Elektro | 2715 px, abgeschnitten |
| Smart Home | 2546 px, abgeschnitten |
| Energie | 1741 px, vollständig |

Bei der React-Umsetzung natürliche bzw. automatisch gemessene Höhe verwenden. Den willkürlichen Grenzwert nicht übernehmen. Das ist ein Vorlagenfehler, kein fehlender Kundeninhalt.

### Projektlayout auf Mobilgeräten

Bei 390 px ist der fixierte Header stark umgebrochen; es gibt dort keinen mobilen Menüknopf. Der Kopfbereich überdeckt den Einstieg, weil der reservierte obere Abstand nicht zur tatsächlichen Headerhöhe passt. Die Sprachangabe „DE / EN“ ist auf dieser Seite nur Text.

Lösung: bestehenden gemeinsamen React-Header samt mobilem Menü und funktionierender Sprachumschaltung beibehalten. Keinen zweiten Header und kein neues Scroll-Versteckverhalten allein für Projekte einführen.

Bei den geprüften drei Breiten trat kein horizontaler Seitenüberlauf auf. Filter und Aufklappaktionen verursachten keine JavaScript-Ausnahmen. Das schliesst die oben nachgewiesenen Layout-/Inhaltsfehler nicht aus.

## 5. Vorschlag für einen gemeinsamen roten Faden

- Ein einziges Layout.tsx mit vorhandenem Theme und gemeinsamem Header/Footer bleibt massgeblich. Abweichende Arial-Schrift, Radien, Breiten und Headertechnik der neuen Projektvorlage an diesen Stil angleichen.
- Startseite: bestehende Übersicht und drei Projektteaser beibehalten. Keine erneute Komplettgestaltung.
- Dienstleistungen: acht Leistungen mit kurzen Einstiegen und den neuen aufklappbaren Details; separater Beratungseinstieg.
- Projekte: neue Route /projects mit sechs Projekten, Filtern und den gelieferten längeren Texten.
- Navigationspunkt „Projekte“ künftig zu /projects. Das bisherige /#projects als weiterhin gültigen Startseitenanker erhalten.
- Vorschlag: „Projekte ansehen“ und ein gemeinsamer Link unter den Startseitenteasern führen zu /projects; einzelne Teaser bei Bedarf zu /projects#kitchen usw. Es sind keine zusätzlichen Projekt-Unterseiten notwendig.
- Projektkennzahlen und Inhalte zentral pflegen: stabile Projekt-IDs und Bildmetadaten in einer kleinen Datei, Kurz-/Langtexte in den vorhandenen Sprachdateien. So müssen Kosten und Dauer nicht unabhängig an zwei Stellen geändert werden.
- Service- und Projektkategorien unterscheiden: eine Balkonreferenz erzwingt keine zusätzliche Dienstleistungskarte, und Smart-Home-Schlagwörter erzwingen keinen weiteren Filter.

## 6. Was noch beim Kunden zu klären ist

Für den nächsten Umsetzungsschritt genügen zwei gezielte Rückfragen:

1. Auf der Startseite stehen für Küche/Balkon/Heizung 8–10, 4–6 und 6–8 Wochen; auf der Projektseite 4–6, 2–4 und 4–8 Monate. Welche Werte sollen gelten? Sind das Ausführungszeit und Gesamtdauer inklusive Planung/Lieferung?
2. Soll „Details ansehen“ weitere Inhalte öffnen? Die sind bisher nicht vorhanden. Vorschlag: Für den MVP die längeren Beschreibungen direkt auf der Projektseite zeigen und die zusätzlichen Detailbuttons weglassen. Passt das?

Spätestens vor Veröffentlichung: neue Projektorte/-daten, Kennzahlen, Kosten und fachliche Aussagen durch den Kunden sichten lassen; korrekte Instagram-URL und Inhalte/Ziele für Datenschutz/Impressum bleiben offen. Fachliche Normen-/Garantie-/Versicherungsformulierungen nicht als geprüft darstellen. Eine erneute allgemeine Nachfrage nach dem gesamten Seitenumfang ist nicht nötig.

Keine erneute Nachfrage erforderlich zu: Startseite2 und Über-uns2, Airbnb, Blogaufschub, beiden Sprachen, Empfängerpostfach und ausschliesslich Frontend ohne Versand. Dazu liegen bereits Vorgaben vor.

## 7. Empfohlene nächste Umsetzungsschritte

1. Umsetzungsplan und Prompts auf fünf Kernseiten sowie das neue Referenzverzeichnis aktualisieren. Die bisherigen abgeschlossenen Prompts 1/2 nicht einfach erneut ausführen. Frühere Regeln „sechs Leistungen“, „Mehr erfahren = Kontakt“ und „keine eigene Projektseite“ sind durch den neuen Entwurf überholt bzw. zu ersetzen.
2. Dienstleistungen erweitern: acht Schlüssel, sieben Detailbereiche, gemeinsame DE-/EN-Inhalte, Kontaktvorwahl vorbereiten; Startseitenübersicht konsistent halten.
3. Projekte ergänzen: /projects, sechs statische Referenzen, Filter, optimierte Bildkopien und gemeinsame Projektinformationen für Startseite und Projektseite. Navigation und Aktionsziele anpassen. Widersprüchliche Zeitangaben vor finaler Übernahme klären.
4. Teamseite nach unveränderter, freigegebener 2er-Vorlage umsetzen.
5. Kontaktfrontend umsetzen, nun mit acht Leistungen; weiterhin ehrlich deaktivierter Versand sowie nutzbare E-Mail-/Telefonlinks.
6. Fünf Kernseiten gemeinsam auf DE/EN, Mobilansichten, Tastaturbedienung, Bildpfade und Backend-Unabhängigkeit prüfen.

Die neue Projektseite und die zusätzlichen Leistungsdetails sind normaler Frontend-Umfang auf der vorhandenen Basis. Dafür braucht es weder neue Bibliotheken noch Backend-/Datenbankschemata. Nicht Bestandteil dieses Analyseschritts: Anwendungscode ändern, Backend/SMTP, Workflows/NAS/Demo ändern, committen, pushen oder deployen.
