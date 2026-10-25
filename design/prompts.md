Führe die folgenden **sieben Prompts nacheinander im selben Projektchat** aus. Jeder endet mit einem prüfbaren Zwischenstand. Den Gmail-Zugang kannst du parallel zu den ersten Schritten beschaffen.

**Prompt 1 – Plan aktualisieren und Dienstleistungen erweitern**

```text
Aktualisiere den Umsetzungsplan im Projekt basel-homefixer.ch und setze anschließend die erweiterten Dienstleistungen um.

Lies zuerst den aktuellen Code, die vorhandenen Projektanweisungen sowie:
- design/Kundenprojekt_PJA/UMSETZUNGSPLAN.md
- design/Kundenprojekt_PJA/KUNDENFREIGABEN.md
- design/Kundenpaket_20261024/ANALYSE.md

Aktuelle Kundenreferenz ist design/Kundenpaket_20261024/Kundenpaket. Aktualisiere die Plan- und Freigabedokumentation nachvollziehbar: fünf Kernseiten inklusive eigener Projektseite; zusätzlich Blog und echter Kontaktversand als neuer MVP-Umfang. Dokumentiere die folgende Reihenfolge, ohne spätere Schritte bereits umzusetzen.

Übernimm 03-dienstleistungen.html in die bestehende Services.tsx:
- acht Leistungen;
- sieben aufklappbare Detailbereiche;
- Projektmanagement/Consulting mit direktem Beratungsziel;
- bestehender allgemeiner Ablauf und FAQ.

Mehr erfahren öffnet jetzt die jeweiligen Leistungsdetails. Ergänze bei Bedarf einen getrennten Beratungslink zu /contactUs?service=<schluessel>#contact-form.

Behalte kitchen, energy, heating, smart-home, bathroom und consulting bei. Ergänze windows-doors und electrical. energy bleibt energetische Renovation. Verwende den gemeinsamen Servicekatalog und die vorhandenen DE-/EN-Dateien. Halte die bestehende Startseitenübersicht konsistent; dort dürfen weiterhin fünf ausgewählte Leistungen erscheinen.

Verwende MUI Accordion oder Collapse mit automatischer Höhe und zugänglicher Bedienung. Übernimm nicht die fehlerhafte 2000-Pixel-Höhenbegrenzung der Kundenvorlage.

React, TypeScript, Material UI, React Router, i18next, das zentrale Theme und src/home/Layout.tsx erhalten. Gemeinsamen Header/Footer weiterverwenden; keine neue Seitenarchitektur.

Neuere Kundenangaben haben Vorrang. Nötige Annahmen dokumentieren und im Ergebnis nennen. Fachliche Zusagen nicht eigenständig erweitern.

Bestehende lokale Änderungen und Kundenoriginale erhalten. In diesem Schritt außer der Planungsdokumentation nur Frontend ändern. Keine Backend-, Datenbank-, Workflow-, NAS- oder Demo-Änderungen. Nicht committen, pushen oder deployen.

Prüfe TypeScript, Frontend-Build, DE/EN sowie Aufklappbereiche und Mobilansicht. Berichte Änderungen, Prüfungen und Grenzen. Den nächsten Prompt nicht automatisch beginnen.
```

**Prompt 2 – Projektseite und Verbindung zur Startseite**

```text
Setze im bestehenden Projekt basel-homefixer.ch die eigene Projektseite um. Lies den aktualisierten Umsetzungsplan und design/Kundenpaket_20261024/ANALYSE.md. Referenz ist design/Kundenpaket_20261024/Kundenpaket/08-Projekte.html.

Erstelle Projects.tsx mit Route /projects:
- sechs gelieferte Projekte;
- Bilder, längere Beschreibungen, Orte, Zeitangaben, Schlagwörter und Kennzahlen;
- Kategorienfilter mit lokalem React-Zustand;
- gemeinsamer Kontaktaufruf.

Verwende das vorhandene Theme, Layout, Header, Footer und wiederverwendete Bausteine. Übernimm weder den abweichenden Header noch dessen Scroll-Versteckverhalten.

Die gelieferten ausführlichen Beschreibungen stehen direkt auf der Projektseite. Entferne die ziellosen Details-ansehen-Aktionen; keine zusätzlichen Projekt-Unterseiten, Dialoge oder Inhalte erfinden.

Verwende assets/Haustüre.jpg anstelle des fehlerhaften Gaustüre.jpg-Verweises, sofern das Bildmotiv passt. Erzeuge optimierte Bildkopien mit sinnvollen Ausschnitten; Kundenoriginale erhalten.

Pflege Projekt-IDs und Bildmetadaten zentral und Kurz-/Langtexte in den vorhandenen DE-/EN-Dateien. Gemeinsam verwendete Kennzahlen nicht unabhängig für Startseite und Projektseite pflegen.

Die neueren Zeitangaben gelten:
- Küche: 4–6 Monate
- Balkon: 2–4 Monate
- Heizung: 4–8 Monate
Beschrifte sie entsprechend unserer Annahme als Gesamtprojektdauer. Gleiche die Startseite damit ab. Andere Angaben nur ändern, wenn der neuere Kundenstand dies begründet.

Startseite als Übersicht mit drei Projekten erhalten. Navigationspunkt Projekte und Projekte ansehen führen zu /projects. Ergänze passende Verweise von den Teasern auf /projects bzw. konkrete Projektanker. Bestehendes /#projects bleibt weiterhin erreichbar. Ankersprünge müssen auch nach Routenwechseln funktionieren.

Nur die hierfür notwendigen Frontend-Bereiche ändern. Kein umfassender Umbau anderer Seiten, keine Backend-Abhängigkeit. Bestehende lokale Änderungen, Kundenoriginale, Workflows, NAS und separates Demo-Projekt erhalten. Nicht committen, pushen oder deployen.

Prüfe Bilder, Filter, Navigation, Projektanker, DE/EN, Tastaturbedienung und Ansichten um 390, 768 und 1280 px sowie TypeScript und Build. Berichte Ergebnis und Annahmen. Den nächsten Prompt nicht automatisch beginnen.
```

**Prompt 3 – Über uns / Teamseite**

```text
Setze die Teamseite des MVP im Projekt basel-homefixer.ch um. Lies den aktuellen Umsetzungsplan. Verbindliche Referenz ist design/Kundenpaket_20261024/Kundenpaket/02-ueber-uns2.html.

Übertrage Teamvorstellung, gemeinsames Bild, Porträts von Pascal und Divine, Rollen, Kompetenzen, Vision/Werte und Kontaktaufruf in die bestehende AboutUs.tsx.

Nutze das gemeinsame Theme, Layout, Header/Footer und die vorhandenen Bausteine. Halte Typografie, Breite und Abstände konsistent mit den bereits umgesetzten Seiten. Keine eigenen ThemeProvider, die den gemeinsamen Stil überschreiben.

Verwende die gelieferten Bilder als optimierte Kopien mit passenden Ausschnitten und Alt-Texten. Kundenoriginale erhalten. Pflege sichtbare Inhalte in den bestehenden DE-/EN-Dateien.

Erfinde keine Qualifikationen, Erfahrungen oder Zusagen. Neuere Kundenangaben haben Vorrang. Unklare Angaben sinnvoll behandeln und als Annahmen berichten. Beratungsaktionen führen zu /contactUs#contact-form.

Nur Frontend ändern. Keine Backend-, Datenbank-, Workflow-, NAS- oder Demo-Änderungen. Bestehende lokale Änderungen erhalten. Nicht committen, pushen oder deployen.

Prüfe Desktop-/Mobilansicht, Bildpfade, DE/EN, Kontaktziel sowie TypeScript und Build. Berichte Ergebnis und offene Inhalte. Den nächsten Prompt nicht automatisch beginnen.
```

**Prompt 4 – Kontaktformular mit Backend und Gmail-Versand**

```text
Setze das Kontaktformular im Projekt basel-homefixer.ch vollständig um, einschließlich Versand über das bestehende Spring-Boot-Backend. Dieser Auftrag erweitert ausdrücklich die frühere Frontend-only-Grenze um den Kontaktversand.

Lies den aktuellen Plan und den bestehenden Backend-/Security-Code. Designreferenz ist design/Kundenpaket_20261024/Kundenpaket/04-kontakt.html.

Frontend:
- Kontaktinformationen und gemeinsamer Stil;
- Name, E-Mail, optionale Telefonnummer, Dienstleistungswahl, Nachricht und Einwilligung;
- acht Service-Schlüssel aus dem gemeinsamen Katalog;
- Vorwahl über ?service=<schluessel>, unbekannte Werte neutral behandeln;
- erreichbarer Anker contact-form;
- verständliche DE-/EN-Validierung und Lade-, Fehler- und Erfolgszustände;
- Eingaben bei Fehlern erhalten und doppeltes Absenden verhindern;
- E-Mail und Telefon als zusätzliche Kontaktaktionen.

Backend:
- kleiner typisierter POST-Endpunkt /api/contact;
- serverseitige Pflichtfeld-, Format-, Längen- und Service-Prüfung;
- feste serverseitige Empfängeradresse baselhomefixer@gmail.com;
- Versand von derselben Gmail-Adresse;
- Besucheradresse ausschließlich als Reply-To;
- keine vom Client frei wählbaren Empfänger oder Absender;
- Spring-Mail-Anbindung mit begrenzten Verbindungs-/Lese-/Schreibzeitlimits;
- einfacher Honeypot und serverseitige Versandbegrenzung;
- keine automatische Bestätigungsmail an Besucher;
- keine Speicherung der Nachrichten in MongoDB für diesen MVP.

Übernimm keine simulierte Erfolgsmeldung aus dem Kunden-HTML. Erfolg erst anzeigen, nachdem der Mailserver die Nachricht zum Versand angenommen hat. Keine garantierte Zustellung behaupten.

SMTP-Konfiguration ausschließlich über serverseitige Umgebungsvariablen. Keine Zugangsdaten in Frontend, Repository, Logs oder Berichten. Bereite eine Beispielkonfiguration ohne Geheimnisse vor. Gmail-SMTP mit App-Passwort verwenden, sofern das Kundenkonto dies erlaubt; bei fehlender Verfügbarkeit die konkrete Grenze berichten und OAuth nicht stillschweigend zusätzlich bauen.

Fehlen Zugangsdaten, vervollständige und teste die Implementierung trotzdem mit einem lokalen SMTP-Testserver oder geeignetem Testdouble. Ohne konfigurierte Versandmöglichkeit muss der öffentliche Sendeweg klar als nicht verfügbar erscheinen. Keine echten E-Mails ohne gesonderte Freigabe versenden.

Prüfe die vorhandene Security-/CORS-Konfiguration gezielt für diesen Endpunkt und die lokale Entwicklung. Keine pauschale Freigabe zusätzlicher APIs oder umfangreiche Authentifizierungs-Neugestaltung.

Kontaktbezogene Frontend-/Backend-Änderungen und Konfigurationsdokumentation sind erlaubt. Keine Workflows, laufende NAS-Konfiguration, Datenbank oder Demo ändern. Bestehende lokale Änderungen erhalten. Nicht committen, pushen oder deployen.

Teste Validierung, Service-Vorwahl, Mailinhalt/Reply-To, Fehlerfälle, Versandbegrenzung und deaktivierten Zustand ohne Konfiguration. Führe passende Backend-Tests sowie Frontend-Typecheck und Build aus. Berichte klar, was technisch geprüft wurde und was noch den echten Gmail-/NAS-Test benötigt. Den nächsten Prompt nicht automatisch beginnen.
```

**Prompt 5 – MongoDB-Blogmodell, Lese-API und Import**

```text
Erweitere den bestehenden MongoDB-Blog im Projekt basel-homefixer.ch. Dieser Auftrag erlaubt gezielte Backend-Änderungen für den Blog. MongoDB und vorhandene Daten bleiben erhalten.

Prüfe Blogpost, BlogRepository, BlogService, vorhandene Endpunkte und die mitgelieferte Daten-Datei. Erweitere das Modell passend zum bestehenden mehrsprachigen Aufbau um:
- stabile ID und eindeutigen Slug;
- DE-/EN-Titel und Vorschautext;
- bestehenden mehrsprachigen Markdown-Inhalt;
- sprachunabhängige Kategorie-Schlüssel;
- Titelbild-Pfad und mehrsprachigen Alternativtext;
- Status draft/published;
- Veröffentlichungsdatum.

Kategorien zunächst als kleine feste, zentral definierte Liste mit DE-/EN-Bezeichnungen. Kein Kategorien-CMS und keine Bilddaten in MongoDB. Bilder werden zunächst über /assets/blog/... referenziert.

Erhalte bestehende Datensätze und deren IDs. Alte Beiträge ohne Veröffentlichungsstatus dürfen nicht automatisch öffentlich werden. Sichere spätere Änderungen an der Datenbank durch einen dokumentierten, überprüfbaren Import-/Migrationsweg ab; in diesem Schritt keine produktive Datenbank verändern.

Öffentliche Lese-API:
- Liste ausschließlich veröffentlichter Beiträge mit Vorschauinformationen;
- serverseitig begrenzte Seitengröße, definierte Sortierung nach Veröffentlichungsdatum und optionaler Kategorie;
- Artikelabruf per Slug;
- Entwürfe dürfen weder über Listen noch direkte Artikelabrufe öffentlich erreichbar sein.
Bestehende Endpunkte kompatibel erhalten oder gezielt anpassen; auch darüber dürfen keine Entwürfe offengelegt werden. Einen kleinen Blog-Cache nicht unnötig übernehmen, falls er korrekte Filterung oder Aktualisierung erschwert.

Erstelle einen einfachen Entwickler-Importweg für Markdown-Dateien mit dokumentiertem Metadatenblock. Er soll Titel, Kategorien, Übersetzungen, Slugs, Bildpfade und Pflichtfelder prüfen, standardmäßig einen Dry-Run unterstützen und anhand stabiler IDs wiederholbar aktualisieren. Bestehende Veröffentlichungen nicht unbeabsichtigt überschreiben oder löschen. Datenbankzugang nur über lokale/serverseitige Konfiguration.

Keine Adminoberfläche, öffentlichen Schreibendpunkte, Uploadfunktion, neue Authentifizierungsarchitektur oder externes CMS bauen.

Teste insbesondere Entwurfsfilterung, direkten Artikelzugriff, Kategorien, Sortierung, Eindeutigkeit und wiederholbaren Import. Verwende eine isolierte Testdatenbank oder geeignete Repository-Tests, keine produktiven Datenänderungen. Berichte Modell, API, Importablauf und Kompatibilitätsgrenzen.

Bestehende lokale Änderungen erhalten. Keine Workflow-, NAS- oder Demo-Änderungen. Nicht committen, pushen oder deployen. Den nächsten Prompt nicht automatisch beginnen.
```

**Prompt 6 – Blogoberfläche und drei Artikelentwürfe**

```text
Setze auf dem bestehenden MongoDB-Blogmodell die Blogoberfläche im Projekt basel-homefixer.ch um.

Verwende die Kundenvorlage design/Kundenpaket_20261024/Kundenpaket/07-aktuelle-beitraege.html als Gestaltungsreferenz und das bestehende gemeinsame Theme/Layout. Erstelle bzw. vervollständige:
- Blogübersicht unter /blog;
- Artikelansicht unter /blog/:slug;
- Vorschaukarten mit Bild, Titel, Kurztext, Kategorie und Veröffentlichungsdatum;
- Kategorienfilter und einfache Seitennavigation passend zur API;
- DE/EN sowie Lade-, Fehler-, Leer- und Nicht-gefunden-Zustände.

Ordne die bestehenden Routen /blog und /latestPosts bewusst zu. /latestPosts kann auf /blog weiterleiten. Alten Integrationscode nicht pauschal löschen. Die Startseite bleibt ohne zusätzliche Blog-API-Aufrufe oder Instagram-Abhängigkeit. Die übrigen Kernseiten müssen bei ausgefallenem Backend weiterhin funktionieren.

Verwende den vorhandenen Markdown-Ansatz, sichere die Darstellung aber für Artikelinhalte ab: kein ungeprüftes eingebettetes HTML, keine unsicheren Linkprotokolle oder ausführbaren Inhalte. Titelbild und weitere Markdown-Bilder müssen responsiv sein und sinnvolle Alt-Texte haben.

Lege optimierte Bildkopien unter public/assets/blog ab. Dokumentiere: Artikeltextänderungen laufen über den Import nach MongoDB; neue Bilddateien erfordern vorerst einen Frontend-Build und ein Deployment. Keine Uploadfunktion ergänzen.

Erstelle drei DE-/EN-Artikelentwürfe als Markdown-Importdateien aus den gelieferten Kundeninformationen:
1. Küchenmodernisierung und Umstellung von Gas auf Elektrizität.
2. Heizungsmodernisierung im Mehrfamilienhaus.
3. Raumthermostate/Bodenheizung und Smart Home.

Nutze passende Kundenbilder und Informationen aus dem neuesten Paket. Keine erfundenen Projektergebnisse, Kundenzitate, Einsparungen oder fachlichen Garantien. Die neueren Projektdauern gelten und werden gemäß unserer Annahme als Gesamtprojektdauer bezeichnet.

Die vorhandenen persönlichen Website-/Hosting-Beiträge nicht als Kundenartikel veröffentlichen oder löschen. Neue Artikel bleiben draft; kein erfundenes Veröffentlichungsdatum setzen.

Biete eine dokumentierte lokale Sichtungsmöglichkeit für die Entwürfe, ohne öffentliche Entwurfsendpunkte oder einen ungeschützten Vorschauparameter einzuführen. Nutze dafür eine isolierte lokale Testumgebung oder Testdaten; keine produktiven Statusänderungen.

Ergänze den Blog in der gemeinsamen Navigation, sofern die Oberfläche fertig ist. Ohne veröffentlichte Beiträge eine ehrliche Leermeldung anzeigen, keine scheinbar veröffentlichten Entwürfe.

Prüfe Übersicht, Artikeldarstellung, Kategorien, Bilder, direkte URLs, DE/EN, Mobilansicht und Tastaturbedienung. Führe Typecheck und Build sowie passende API-/Integrationstests aus. Berichte insbesondere, wie der Kunde Entwürfe sichten und später freigeben kann.

Bestehende lokale Änderungen und Kundenoriginale erhalten. Keine Workflows, laufende NAS-Konfiguration oder Demo ändern. Nicht committen, pushen, deployen oder Artikel öffentlich veröffentlichen. Den nächsten Prompt nicht automatisch beginnen.
```

**Prompt 7 – Gesamtabnahme und Vorbereitung für den Betrieb**

```text
Prüfe den erweiterten MVP des Projekts basel-homefixer.ch zusammenhängend und behebe konkrete Fehler innerhalb des vereinbarten Umfangs.

Prüfe Startseite, Dienstleistungen, Projekte, Über uns, Kontakt, Blogübersicht und Artikelansicht:
- gemeinsamer Stil, Header/Footer, Breiten und Abstände;
- DE/EN und Ansichten um 390, 768 und 1280 px;
- Bildpfade und Ausschnitte;
- mobile Navigation und Tastaturbedienung;
- Projektlinks und Ankersprünge nach Routenwechseln;
- acht Service-Schlüssel und Kontaktvorwahl;
- Airbnb-Ziel;
- Blogkategorien, Artikel-URLs und Entwurfsfilterung.

Prüfe Backend-Ausfälle: Die statischen Kernseiten bleiben darstellbar, Blogfehler werden verständlich angezeigt und das Kontaktformular behauptet keinen erfolgreichen Versand. Keine stillen API-Dauerschleifen oder Instagram-Abhängigkeiten.

Prüfe den Kontaktversand mit Testdouble/lokalem SMTP-Testserver sowie Blog und Import in isolierter Umgebung. Keine echten E-Mails senden und keine produktiven MongoDB-Daten oder Veröffentlichungsstatus ändern.

Prüfe relevante Sicherheitsgrenzen der neuen Funktionen: feste Mailempfänger, Reply-To, Validierung, Versandbegrenzung, Geheimnisse außerhalb des Repositorys, sichere Markdown-Darstellung und keine öffentlich lesbaren Entwürfe. Keine allgemeine Sicherheits-Neuentwicklung des Projekts.

Erstelle eine kurze Betriebsanleitung mit:
- benötigten Mail-Umgebungsvariablen ohne Geheimnisse;
- Gmail-App-Passwort-Voraussetzungen und Alternative bei fehlender Verfügbarkeit;
- Schritten für einen später ausdrücklich freizugebenden Testversand vom NAS;
- Blogpflege über Markdown und Dry-Run/Import;
- Vorgehen für Bilddateien und deren Deployment;
- bewusster Freigabe von Artikeln;
- Sicherung/Rücknahme vor Datenimporten;
- noch fehlenden Inhalten und Zugangsdaten.

Führe abschließend Frontend-Typecheck und Produktionsbuild sowie passende Backend-Tests/Build-Prüfungen aus. Dokumentiere tatsächliche Prüfergebnisse und konkrete Einschränkungen. Das vorhandene npm-test-Platzhalterscript zählt nicht als Funktionsnachweis.

Bestehende lokale Änderungen erhalten. Keine automatischen Commits, Pushes, Deployments, NAS-Änderungen oder produktiven Datenimporte. Separates Demo-Projekt unverändert lassen.

Berichte abschließend:
1. Was für den MVP fertig und geprüft ist.
2. Welche Annahmen übernommen wurden.
3. Welche Kundensichtung noch nötig ist.
4. Welche konkreten Schritte für Gmail-Aktivierung, Artikelveröffentlichung und Deployment verbleiben.

Keine weiteren Arbeitspakete automatisch beginnen.
```

Die spätere Aktivierung besteht dann aus wenigen ausdrücklich beauftragten Schritten: **Gmail-Zugang konfigurieren, vom NAS aus testen, Artikel freigeben und den geprüften Stand deployen.**


Ich würde die restliche Umsetzung in **sechs Prompts** aufteilen. So werden zuerst die Schreibzugriffe abgesichert, danach Verwaltung und Blog umgesetzt. Den bisherigen Abschluss-Prompt habe ich an die NAS-Medienbasis und die spätere Kundenpflege angepasst.

### Prompt 1 – Anmeldung und Verwaltungsberechtigungen absichern

```text
Sichere im Projekt basel-homefixer.ch die Anmeldung als Grundlage für die spätere Inhaltsverwaltung ab. Noch keine Verwaltungsoberfläche oder Inhalts-Schreibendpunkte bauen.

Lies Projektanweisungen, Umsetzungsplan, NAS-VORSCHAU.md, My-App_Backend/CONTENT.md sowie Account-, Login-, Token- und Security-Code.

Prüfe die vorhandene Lösung und verwende ihre sinnvollen Bestandteile weiter. Ziel ist ein kleiner, konsistenter Authentifizierungsweg für die geschützte Verwaltung, kein externer Identity-Provider und keine allgemeine Sicherheits-Neuentwicklung.

Setze um:
- sichere Passwortspeicherung und Prüfung mit dem vorhandenen PasswordEncoder;
- konsistente Token-Ausstellung und serverseitige Prüfung;
- Signaturschlüssel ausschließlich über serverseitige Konfiguration; keine zufälligen Schlüssel pro Neustart;
- begrenzte Token-Gültigkeit;
- serverseitig vergebene Verwaltungsberechtigung;
- keine Verwaltungsrechte durch öffentliche Registrierung oder Client-Angaben;
- gezielte Begrenzung von Anmeldeversuchen;
- verständliche Fehler ohne Preisgabe von Passwörtern, Tokens oder unnötigen Kontodaten.

Schütze den künftigen Namespace /api/admin/** ausdrücklich vor der bestehenden pauschalen /api/**-Freigabe. Öffentliche Inhalts-Lesezugriffe und Kontaktversand müssen weiter funktionieren. Schütze bzw. begrenze bestehende Konto-Endpunkte, soweit sie die neue Anmeldung oder vertrauliche Kontodaten betreffen.

Bestehende Klartextkonten nicht stillschweigend verändern oder zu Administratoren machen. Bereite einen überprüfbaren lokalen Weg zur Einrichtung des ersten Verwaltungszugangs und einen dokumentierten Umgang mit Altpasswörtern vor. Keine produktiven Konten oder Datenbanken ändern. Keine fest eingebauten Standardzugänge.

Dokumentiere Token-Speicherung, Ablauf und Abmeldung für die kommende Verwaltungsoberfläche. Falls Abmeldung nur das lokale Token entfernt, diese Grenze ausdrücklich benennen.

Teste mit isolierten Konten insbesondere:
- korrekte/falsche Anmeldung;
- Passwort-Hashes;
- fehlende, manipulierte und abgelaufene Tokens;
- fehlende Verwaltungsberechtigung;
- öffentliche Registrierung ohne Rechteausweitung;
- Anmeldebegrenzung;
- weiterhin funktionierende öffentliche Lese- und Kontakt-Endpunkte.

Führe passende Backend-Tests einschließlich echter HTTP-/Security-Prüfungen aus. Dokumentiere Konfiguration ohne Geheimnisse, Einrichtung des ersten Zugangs und verbleibende Grenzen.

Bestehende Änderungen erhalten. Keine produktiven Daten-, NAS-, Workflow- oder Demo-Änderungen. Nicht committen, pushen oder deployen. Den nächsten Auftrag nicht automatisch beginnen.
```

### Prompt 2 – Geschützte Inhaltsverwaltung und Bildupload im Backend

```text
Erweitere das Backend von basel-homefixer.ch um geschützte Verwaltungsfunktionen für Projekte, Blog und Medien. Verwende die zuvor abgesicherte Anmeldung.

Lies den aktuellen Plan, CONTENT.md, NAS-VORSCHAU.md sowie die bestehenden Inhaltsmodelle, öffentlichen APIs, Import- und Medienverarbeitungsdienste.

Setze ausschließlich unter ausdrücklich geschützten Verwaltungsrouten um:
- Projekte und Blogbeiträge auflisten, anlegen und bearbeiten;
- Entwürfe speichern;
- Inhalte bewusst veröffentlichen und zurückziehen;
- Titelbilder und weitere Medien zuordnen;
- Auswahl/Reihenfolge der drei Startseitenprojekte bearbeiten;
- berechtigte Entwurfsvorschau einschließlich ihrer Bilder.

Verwende die vorhandenen Modelle, stabilen IDs, Slugs, DE-/EN-Felder, Kategorien und Kennzahlen. Keine zweite Inhaltsstruktur neben der öffentlichen API aufbauen. IDs bei Änderungen erhalten und Slug-Konflikte serverseitig verhindern. Gleichzeitige Bearbeitung darf neuere Änderungen nicht stillschweigend überschreiben.

Neue Inhalte beginnen als draft. Veröffentlichung erfordert eine eigene ausdrückliche Aktion und vollständige gültige Pflichtfelder. Veröffentlichungsdatum sinnvoll serverseitig setzen bzw. erhalten; nicht bei jedem Speichern erneuern. Zurückziehen muss öffentliche Inhalts- und Bildzugriffe zuverlässig sperren.

Bildupload:
- vorhandene MediaStorage-Verarbeitung direkt wiederverwenden;
- Dateien ausschließlich im konfigurierten privaten Medienordner speichern;
- Größen-, Pixel-, Format-, Pfad- und Variantenprüfung erhalten;
- EXIF-Orientierung für übliche Smartphone-Fotos korrekt berücksichtigen;
- Alttexte und Medienmetadaten unterstützen;
- keine frei wählbaren Speicherpfade und keine SVG-/HTML-Auslieferung;
- Uploadgröße und gleichzeitige Verarbeitung begrenzen.

Nicht mehr verwendete Dateien nicht automatisch löschen. Referenzierte Medien dürfen weder aus Projekten noch aus Blogbeiträgen oder Entwürfen entfernt werden. Für den MVP ist keine allgemeine Lösch-/Papierkorbfunktion erforderlich.

Öffentliche APIs bleiben rein lesend. Entwurfsvorschauen sind ausschließlich nach serverseitiger Berechtigungsprüfung erreichbar, ohne öffentlichen Vorschauparameter oder frei zugängliche Vorschautokens. Vorschauantworten nicht öffentlich cachen.

Der Import bleibt ein Übertragungswerkzeug. Die spätere Kundenpflege muss direkt über die Verwaltungsfunktionen ohne Importdateien oder Frontend-Deployment möglich sein.

Teste Authentifizierung und Rechte für jede Schreib-/Vorschauaktion, Veröffentlichung/Zurückziehen, Pflichtfelder, Slug-Konflikte, Bearbeitungskonflikte, Medienzugriffe und Uploadfehler. Verwende ausschließlich isolierte Datenbanken und temporäre Medienordner.

Dokumentiere API-Verträge und benötigte Konfiguration. Führe passende Backend-Tests aus.

Bestehende Änderungen und Kundenoriginale erhalten. Keine produktiven Daten-, NAS-, Workflow- oder Demo-Änderungen. Nicht committen, pushen oder deployen. Keine Verwaltungsoberfläche in diesem Schritt bauen und den nächsten Auftrag nicht automatisch beginnen.
```

### Prompt 3 – Einfache Verwaltungsoberfläche für den Kunden

```text
Baue im bestehenden React-/TypeScript-/MUI-Frontend von basel-homefixer.ch eine geschützte Verwaltungsoberfläche für die vorhandenen Verwaltungs-APIs.

Lies den aktuellen Plan und die Dokumentation der Anmeldung, Inhaltsverwaltung und Medienbasis. Bestehendes Theme, Router und i18next weiterverwenden; keine neue Frontend-Architektur und keinen zusätzlichen Editor-Dienst einführen.

Ergänze einen klar getrennten Verwaltungsbereich mit:
- Anmeldung und Abmeldung;
- Listen für Projekte und Blogbeiträge;
- deutlich sichtbarem Entwurfs-/Veröffentlichungsstatus;
- Formularen zum Anlegen und Bearbeiten;
- DE-/EN-Inhalten und den festen Kategorien;
- Bildauswahl, Bildupload und Alternativtexten;
- geschützter Vorschau;
- getrennten Aktionen Speichern, Veröffentlichen und Veröffentlichung zurückziehen.

Projektformulare unterstützen die vorhandenen Orte, Projektzeitpunkte, Schlagwörter, Zeitspannen/Einheiten und optionalen Kennzahlen. Fehlende Zahlen nicht mit null oder erfundenen Standardwerten auffüllen. Auswahl und Reihenfolge der drei Startseitenteaser einfach bearbeitbar machen.

Für Blogtexte einen einfachen Markdown-Editor mit verständlicher Hilfestellung und Vorschau verwenden. Kein umfangreiches WYSIWYG-System bauen. Die sichere Markdown-Darstellung gemeinsam mit der späteren öffentlichen Artikelansicht nutzbar vorbereiten.

Veröffentlichen muss eine bewusste Aktion sein. Zeige verständlich, welche Pflichtfelder noch fehlen. Eingaben bei API-Fehlern erhalten, doppelte Aktionen verhindern und vor dem Verlassen ungespeicherter Änderungen warnen. Bearbeitungskonflikte verständlich behandeln.

Setze das zuvor dokumentierte Token-/Sitzungskonzept um. Keine Zugangsdaten oder Tokens in URLs oder Logs. Frontend-Routenschutz ersetzt niemals die Backend-Berechtigungsprüfung. Bei abgelaufener Anmeldung einen klaren Weg zur erneuten Anmeldung anbieten.

Die Vorschau muss Entwurfsbilder über geschützte Zugriffe laden können. Keine Zugangstokens als Bild-URL-Parameter verwenden. Gemeinsam verwendete öffentliche und geschützte Darstellung möglichst wiederverwenden, ohne das öffentliche Projektseitendesign umfassend zu ändern.

Prüfe den vollständigen Ablauf mit isolierter API/Testumgebung:
Anmelden → Entwurf anlegen → Bild hochladen → bearbeiten → Vorschau → veröffentlichen → öffentlich prüfen → zurückziehen.

Prüfe DE/EN, Tastaturbedienung, Fehlerzustände und Mobil-/Desktopansicht. Führe Frontend-Typecheck, Build und passende Backend-/Integrationstests aus. Das npm-test-Platzhalterscript zählt nicht als Funktionsnachweis.

Erstelle eine kurze Kundenanleitung ohne unnötige technische Details.

Bestehende Änderungen erhalten. Keine produktiven Daten-, NAS-, Workflow- oder Demo-Änderungen. Nicht committen, pushen oder deployen. Den nächsten Auftrag nicht automatisch beginnen.
```

### Prompt 4 – Öffentliche Blogoberfläche und drei Artikelentwürfe

```text
Setze die öffentliche Blogoberfläche von basel-homefixer.ch auf der vorhandenen MongoDB-/Medienbasis um.

Lies den aktuellen Plan, CONTENT.md und die Verwaltungs-/Vorschaudokumentation. Designreferenz ist design/Kundenpaket_20261024/Kundenpaket/07-aktuelle-beitraege.html. Verwende das gemeinsame Theme, Layout, Header und Footer.

Erstelle:
- Blogübersicht /blog;
- Artikelansicht /blog/:slug;
- Vorschaukarten mit Titelbild, Titel, Kurztext, Kategorie und Veröffentlichungsdatum;
- Kategorienfilter und Seitennavigation passend zur vorhandenen API;
- DE/EN sowie verständliche Lade-, Fehler-, Leer- und Nicht-gefunden-Zustände.

Ordne die alten Routen bewusst zu: /blog verwendet die neue Übersicht, /latestPosts leitet nach /blog weiter. Alten Integrationscode nicht pauschal löschen. Ergänze den Blog in der Navigation, sobald die Oberfläche vollständig funktioniert.

Verwende die vorhandene gemeinsame Medienauslieferung. Keine Blogbilder unter public/assets/blog als neue Pflegevoraussetzung einführen. Titelbilder und weitere Artikelbilder müssen ohne Frontend-Deployment austauschbar sein.

Sichere die Markdown-Darstellung:
- kein ungeprüftes eingebettetes HTML;
- keine ausführbaren Inhalte oder unsicheren Linkprotokolle;
- kontrollierte Bildquellen und gültige Medienreferenzen;
- responsive Bilder mit sinnvollen Alttexten;
- dieselben Sicherheitsregeln in öffentlicher Ansicht und geschützter Vorschau.

Erstelle aus den neuesten Kundeninformationen drei DE-/EN-Artikelentwürfe:
1. Küchenmodernisierung und Umstellung von Gas auf Elektrizität.
2. Heizungsmodernisierung im Mehrfamilienhaus.
3. Raumthermostate/Bodenheizung und Smart Home.

Nutze passende Kundenbilder und vorhandene Fakten. Keine erfundenen Ergebnisse, Zitate, Einsparungen oder Garantien. Die neueren Monatsangaben bleiben als angenommene Gesamtprojektdauer bezeichnet. Englische Texte zur Kundensichtung kennzeichnen.

Bereite die Entwürfe im vorhandenen überprüfbaren Importformat vor; Markdown bleibt das Format des Artikelinhalts. Alle drei bleiben draft ohne erfundenes Veröffentlichungsdatum. Import und Sichtung ausschließlich in isolierter Umgebung. Persönliche Altbeiträge nicht löschen oder als Kundenartikel veröffentlichen.

Nutze zur Sichtung die geschützte Verwaltung/Vorschau. Öffentliche Listen und direkte Artikel-/Bildzugriffe dürfen keine Entwürfe zeigen. Ohne veröffentlichte Beiträge eine ehrliche Leermeldung anzeigen.

Startseite weiterhin ohne zusätzliche Blog-API-Aufrufe oder Instagram-Abhängigkeit. Die statischen Kernbereiche müssen bei Backend-Ausfall funktionieren.

Prüfe Übersicht, Artikel, Kategorien, Pagination, direkte URLs, Markdown-Sicherheit, Medien, DE/EN, Tastaturbedienung und 390/768/1280 px. Führe Typecheck, Build und passende API-/Integrationstests aus.

Bestehende Änderungen und Kundenoriginale erhalten. Keine produktiven Daten-, NAS-, Workflow- oder Demo-Änderungen. Nicht committen, pushen, deployen oder Artikel öffentlich veröffentlichen. Den nächsten Auftrag nicht automatisch beginnen.
```

### Prompt 5 – Regulären NAS-Betrieb und öffentliche Anbindung vorbereiten

```text
Bereite den regulären NAS-Betrieb des erweiterten MVP von basel-homefixer.ch konkret und überprüfbar vor. Noch nichts öffentlich ausrollen.

Lies NAS-VORSCHAU.md, CONTENT.md, Kontakt- und Verwaltungsdokumentation sowie vorhandene Docker-/Deployment-Konfiguration. Falls für die Planung nötig, darfst du den aktuellen NAS-Zustand ausschließlich lesend prüfen. Keine Geheimnisse ausgeben.

Erstelle passende Konfigurationsvorlagen und eine kurze Umstellungsanleitung für:
- aktuelle Frontend-/Backend-Anwendung im regulären Website-Stack;
- öffentliche HTTPS-Auslieferung von Website und /api/ unter derselben Domain;
- privaten dauerhaften Medienmount;
- bewusst ausgewählte produktive MongoDB-Datenbank;
- serverseitige Authentifizierungs- und SMTP-Konfiguration;
- Einrichtung des ersten Verwaltungszugangs;
- Sicherung, überprüfbare Datenübertragung und Rücknahme.

Der lokale SSH-Proxy und der separate Vorschau-Launcher sind keine Produktionsabhängigkeit. Keine Vorschau-Zugangsdaten, Entwicklungsdateien oder lokalen Geheimnisse in Images übernehmen.

Die private Vorschau-Datenbank nicht stillschweigend zur Produktionsdatenbank machen oder vollständig kopieren. Bereite eine gesichtete Übertragung der gewünschten Projekte/Medien vor. Blogentwürfe müssen Entwürfe bleiben; Veröffentlichung bleibt eine eigene ausdrückliche Entscheidung.

MongoDB-Zugriff für den regulären Betrieb auf die benötigte Datenbank begrenzen. Medienordner nicht zusätzlich über Synology Web Station oder andere statische Freigaben öffentlich machen. Bildzugriff weiterhin über die geprüfte Medien-API.

Vorhandene GitHub Actions unverändert lassen. Benenne konkrete nötige Abweichungen, falls der bestehende Build-/Deploymentweg nicht ausreicht, statt Workflows nebenbei umzubauen.

Dokumentiere:
- welche bestehende Konfiguration angepasst werden müsste;
- benötigte Umgebungsvariablen ohne Geheimnisse;
- Voraussetzungen und Reihenfolge des Rollouts;
- Prüfungen nach dem Rollout;
- Rückkehr zum vorherigen Anwendungsstand;
- Grenzen einer Rücknahme nach zwischenzeitlichen Inhaltsänderungen;
- welche Schritte ausdrücklich noch nicht ausgeführt wurden.

Validiere die vorbereiteten Konfigurationen soweit lokal möglich. Keine laufenden NAS-/Compose-Änderungen, produktiven Datenimporte, echten E-Mails oder öffentlichen Veröffentlichungen durchführen. Kein Commit, Push oder Deployment. Separates Demo-Projekt erhalten. Den nächsten Auftrag nicht automatisch beginnen.
```

### Prompt 6 – Zusammenhängende MVP-Abnahme und Betriebsanleitung

```text
Prüfe den erweiterten MVP von basel-homefixer.ch zusammenhängend und behebe konkrete Fehler innerhalb des vereinbarten Umfangs.

Lies den aktuellen Umsetzungsplan sowie die Dokumentation zu Kontakt, Inhalten/Medien, Verwaltung, NAS-Vorschau und vorbereitetem regulärem Betrieb.

Prüfe Startseite, Dienstleistungen, Projekte, Über uns, Kontakt, Blogübersicht und Artikelansicht:
- gemeinsamer Stil, Header/Footer, Breiten und Abstände;
- DE/EN und Ansichten um 390, 768 und 1280 px;
- Bildpfade, responsive Varianten und Ausschnitte;
- mobile Navigation und Tastaturbedienung;
- Projektlinks und asynchrone Ankersprünge nach Routenwechseln;
- acht Service-Schlüssel und Kontaktvorwahl;
- Airbnb-Ziel;
- Blogkategorien, Pagination, Artikel-URLs und Entwurfsfilterung.

Prüfe zusätzlich die Kundenverwaltung vollständig:
Anmeldung → Entwurf erstellen → Bilder hochladen → DE/EN bearbeiten → Vorschau → bewusst veröffentlichen → öffentliche Darstellung prüfen → zurückziehen.

Verwende dafür ausschließlich isolierte Konten, Datenbanken und Medienordner. Keine produktiven Konten, Daten oder Veröffentlichungsstatus verändern.

Prüfe Backend-Ausfälle: Statische Kernbereiche bleiben darstellbar, dynamische Bereiche zeigen verständliche Fehler und Wiederholungsmöglichkeiten. Das Kontaktformular behauptet keinen erfolgreichen Versand. Keine stillen API-Dauerschleifen oder Instagram-Abhängigkeiten.

Prüfe Kontaktversand mit Testdouble/lokalem SMTP-Testserver sowie Import, Inhaltsverwaltung und Medien mit isolierten Integrationstests. Keine echten E-Mails senden.

Prüfe gezielt die Sicherheitsgrenzen der neuen Funktionen:
- Passwort-Hashes, Token-Prüfung und Verwaltungsberechtigungen;
- keine Rechteausweitung über Registrierung oder Client-Felder;
- keine ungeschützten Schreib-, Upload- oder Vorschauzugriffe;
- Entwürfe auch über direkte Artikel-, Projekt- und Bild-URLs gesperrt;
- sichere Markdown-Darstellung;
- Upload-, Format-, Größen-, Pixel- und Pfadprüfung;
- feste Mailempfänger, Reply-To, Validierung und Versandbegrenzung;
- keine Geheimnisse in Repository, Images, Frontend oder Logs.

Keine allgemeine Sicherheits-Neuentwicklung außerhalb dieses Umfangs beginnen. Andere Befunde konkret dokumentieren.

Erstelle bzw. vereinheitliche eine kurze Betriebsanleitung mit:
- benötigten Authentifizierungs-, Medien-, Datenbank- und Mailvariablen ohne Geheimnisse;
- Einrichtung und Wiederherstellung des Verwaltungszugangs;
- Kundenpflege von Projekten, Startseitenauswahl, Blog und Bildern;
- Entwurfsvorschau, Veröffentlichung und Zurückziehen;
- Import/Dry-Run als Übertragungswerkzeug;
- gemeinsamer Sicherung und Wiederherstellung von MongoDB und Medien;
- Gmail-App-Passwort-Voraussetzungen und Grenze bei fehlender Verfügbarkeit;
- Schritten für einen später ausdrücklich freizugebenden NAS-Testversand;
- vorbereitetem öffentlichen Rollout und Rücknahme;
- noch fehlenden Inhalten, Kundensichtungen und Zugangsdaten.

Die überholte Anleitung „neue Blogbilder benötigen einen Frontend-Build“ nicht übernehmen. Inhalte und Bilder werden über Verwaltung und gemeinsame Medienbasis gepflegt. Codeänderungen benötigen weiterhin einen Anwendungs-Build/Rollout.

Führe abschließend Frontend-Typecheck und Produktionsbuild sowie passende Backend-Tests/Build-Prüfungen aus. Dokumentiere tatsächliche Prüfergebnisse und Einschränkungen. Das npm-test-Platzhalterscript zählt nicht als Funktionsnachweis.

Bestehende lokale Änderungen und Kundenoriginale erhalten. Keine automatischen Commits, Pushes, Deployments, laufenden NAS-Änderungen, produktiven Datenimporte oder öffentlichen Veröffentlichungen. Separates Demo-Projekt unverändert lassen.

Berichte abschließend:
1. Was für den MVP fertig und geprüft ist.
2. Welche Annahmen übernommen wurden.
3. Welche Kundensichtung noch nötig ist.
4. Was für Gmail-Aktivierung, ersten Verwaltungszugang, Artikelveröffentlichung und öffentlichen Rollout konkret verbleibt.
5. Welche Prüfungen isoliert erfolgt sind und welche erst nach einem ausdrücklich beauftragten Rollout möglich sind.

Keine weiteren Arbeitspakete automatisch beginnen.
```

Die **tatsächliche öffentliche Inbetriebnahme** bleibt danach ein eigener kurzer Auftrag auf Grundlage der geprüften Rollout-Anleitung.