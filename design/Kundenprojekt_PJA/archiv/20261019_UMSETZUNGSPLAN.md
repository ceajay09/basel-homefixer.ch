# Frontend-MVP: Umsetzungsplan Basel Homefixer

Stand: 19. Oktober 2026. Aktualisierte Vorgabe des Auftraggebers: vorerst ausschliesslich Frontend. Dieser Plan ersetzt die bisherige Einplanung eines möglichen Mailversands im ersten Durchgang. Es wurde noch kein Anwendungscode umgesetzt.

## 1. Ziel und festgelegter Umfang

- Vier Kernseiten: Startseite, Über uns, Dienstleistungen und Kontakt.
- Freigegebene Referenzen unter design/Kundenprojekt_PJA/Kundenpaket: 01-startseite2.html, 02-ueber-uns2.html, 03-dienstleistungen.html und 04-kontakt.html.
- Design, Bilder, Mobilansicht und ein gemeinsamer Gestaltungsstil haben Vorrang.
- Kostenlose Erstberatung und Mehr erfahren führen zum Kontaktformular. Die Dienstleistung kann dabei vorausgewählt werden.
- Projekte führt zum Projektabschnitt auf der Startseite; keine eigene Projektseite.
- Vermietung bleibt der bestehende externe Airbnb-Link.
- Das Kontaktformular wird vollständig als Frontend vorbereitet. Echter Versand folgt später, sobald der Mailzugang verfügbar ist.
- Geplantes Empfängerpostfach: baselhomefixer@gmail.com. Laut Auftraggeber soll dieses Konto später auch für den Versand verwendet werden; Zugriff und Versandkonfiguration liegen noch nicht vor.
- Blog und Artikeldetails bleiben für einen späteren Durchgang zurückgestellt.

Historische Analysen und Fragen sind keine zusätzlichen Arbeitsaufträge. Für diesen Durchgang sind dieser Plan, KUNDENFREIGABEN.md und UMSETZUNGSPROMPTS.md massgeblich.

## 2. Klare Grenze: Frontend ohne Backend-Abhängigkeit

In diesem Durchgang werden kein Mail-Endpunkt, SMTP-Zugang, Spring-Boot-Service, Datenbankumbau oder Backend-Mockserver gebaut. Keine Änderungen an Backend, GitHub Actions, ECR, NAS oder am separaten Demo-Projekt. Keine automatischen Commits, Pushes oder Deployments im Rahmen der Umsetzungsprompts.

Die vier MVP-Seiten müssen ohne laufendes Backend dargestellt und bedient werden können. Statische Inhalte und Bilder stammen aus den freigegebenen Vorlagen. Die neue Startseite ruft für ihren Inhalt weder die bestehende Homepage-Konfiguration noch Blogdaten oder Instagram-Einbettungen ab. Bereits vorhandener Integrationscode und Daten bleiben im Projekt erhalten; sie werden nicht allein für diesen Frontend-Draft entfernt.

Ein Formular ohne Versand ist eine Vorbereitung, keine fertige Kontaktfunktion. Der Entwurf zeigt deshalb klar, dass Onlineversand noch nicht verfügbar ist. Tatsächlich nutzbare E-Mail- und Telefonlinks bleiben zusätzlich sichtbar. Ein mailto-Link öffnet das Mailprogramm des Besuchers; er wird nicht als Direktversand dargestellt.

## 3. Prioritäten

| Priorität | Umfang | Ergebnis |
| --- | --- | --- |
| P0 – sehr hoch | Gemeinsames Design und Startseite | Freigegebene Bildsprache, Grün-/Gold-Palette, Typografie, Abstände und Seitenhierarchie sind erkennbar. |
| P0 – hoch | Dienstleistungen und Teamseite | Vollständige, zusammenhängend gestaltete Seiten auf derselben Komponentenbasis. |
| P0 – hoch | Mobilansicht, DE/EN, Navigation | Lesbare Darstellung, echte Klickziele und keine sichtbaren Übersetzungsschlüssel. |
| P1 – im Frontend-Durchgang | Kontaktseite und Formularvorbereitung | Felder, Validierung, Dienstleistungsvorwahl und klarer Nicht-versendet-Zustand; echte Kontaktlinks. |
| P1 – Abschluss | Browserprüfung, Typecheck, Build | Zusammenhängender, backendunabhängiger und reviewbarer Frontend-MVP. |
| Später | Mailzugang, Backend und echter Versand | Bestehende Formularoberfläche durch eine kleine Versand-Anbindung aktivieren. |
| Später / niedrig | Blog, Artikelinhalte und Detailansichten | Separater Umfang nach Freigabe der vollständigen Inhalte. |

Der erste gemeinsame Designstand wird nach Prompt 1 gesichtet. Dadurch werden Stilanpassungen vor der Übertragung auf alle Seiten geklärt.

## 4. Bestehende Architektur weiterverwenden

```text
App.tsx / bestehende URL
  -> home/<Seite>.tsx
    -> home/Layout.tsx
      -> gemeinsames Material-UI-Theme
      -> vorhandener Header mit Sprache und Navigation
      -> Seiteninhalt aus MUI-Komponenten
      -> gemeinsamer Footer
```

React, TypeScript, Vite, Material UI, React Router und i18next bleiben bestehen. Kunden-HTML nicht per iframe oder HTML-Injektion in die laufende Website übernehmen. Kein neues Framework, CMS oder allgemeiner Seitenrenderer.

Farben und wiederkehrende Komponentenstile im zentralen Theme bündeln. Kleine gemeinsame Bausteine wie PageSection oder ContactCta nur dort einführen, wo sie mehrfach helfen. Texte in den bestehenden DE-/EN-Dateien pflegen; Bilder nach public/assets übernehmen, die Kundenoriginale erhalten. Grössen und Abstände sinnvoll in das MUI-System übersetzen, statt CSS-Zahlen blind zu kopieren.

Eigene Standard-ThemeProvider in angepassten Unterseiten nicht das gemeinsame Theme überschreiben lassen. Verschachtelte Container und zusätzliche main-Elemente nur in den ohnehin betroffenen Bereichen vereinfachen. Der aktive Layout-Einstieg bleibt home/Layout.tsx. Keine allgemeine Bereinigung unbeteiligter Dateien.

Einheitliche Navigation, Breite und Breakpoints haben Vorrang vor einer pixelgenauen Nachbildung der untereinander abweichenden HTML-Dateien. Die gewünschte Wirkung, Hierarchie und Bildanordnung erhalten.

## 5. Kontaktformular: heute vorbereiten, später anbinden

### Frontend-Umfang

- Name und E-Mail als Pflichtfelder, Telefon optional, Dienstleistungsauswahl, Nachricht und das zum späteren Versand vorgesehene Einwilligungsfeld.
- Sinnvolle Pflichtfeld-/Format-/Längenprüfung sowie verständliche DE-/EN-Rückmeldungen. Ein finaler Link zu den Informationsinhalten braucht vor Veröffentlichung ein echtes Ziel.
- Vorwahl über einen begrenzten Service-Schlüssel, beispielsweise /contactUs?service=kitchen#contact-form; unbekannte Werte führen zur neutralen Auswahl.
- Formularzustand in React. Eingaben bleiben erhalten; weder das Ausfüllen noch ein Klick löst eine Nachricht oder Speicherung auf einem Server aus.
- Sichtbarer Hinweis wie „Der Onlineversand wird noch eingerichtet. Bitte kontaktieren Sie uns per E-Mail oder Telefon.“
- „Nachricht senden“ bis zur späteren Anbindung klar deaktivieren und den Grund direkt daneben erklären. Keine fingierte Erfolgsnachricht, kein stiller Versandversuch und kein lediglich protokollierter Pseudoversand.
- E-Mail schreiben und Telefon als separate echte Kontaktaktionen, nicht als vorgetäuschte Formularübermittlung.

### Kleine Anschlussstelle für später

Formulardaten und Versand trennen: ein typisiertes ContactRequest mit den Feldern und eine kleine Frontend-Servicefunktion, beispielsweise submitContactRequest. Der aktuelle Zustand ist ausdrücklich unavailable. Ein hypothetischer Erfolg wird nicht simuliert. Die Formularoberfläche ist damit nicht an eine bestimmte SMTP-Technik gebunden.

Die spätere Backend-Phase ergänzt den echten POST-Aufruf und aktiviert den Sendeweg. Erst dann werden auch tatsächliche Sende-, Fehler- und Bestätigungszustände im Browser aktiviert und mit dem Backend geprüft. Noch keinen unbenutzten vollständigen HTTP-Client mit Retry-/Queue-Architektur auf Vorrat bauen.

Den vorgesehenen Schnittstellenvertrag kurz neben der Frontend-Anbindung dokumentieren: möglicher Endpunkt POST /api/contact; Namens-, E-Mail-, Telefon-, Service-, Nachrichten- und Einwilligungsfelder; zulässige Service-Schlüssel; erwartete Validierungs-/Fehlerantworten. Der konkrete Vertrag kann bei der Backend-Umsetzung angepasst werden. Empfänger, Absender und Mailzugang gehören in die spätere serverseitige Konfiguration, nicht in Formulardaten oder Frontend-Secrets.

## 6. Durchführung in fünf Prompts

Die vollständig vorbereiteten Aufträge stehen in UMSETZUNGSPROMPTS.md. Sie werden nacheinander im gleichen Projektchat ausgeführt, nicht parallel. Jeder Prompt endet mit einem prüfbaren Zwischenstand; der nächste baut auf dem tatsächlich vorhandenen Code auf.

1. **Designbasis plus Dienstleistungen als Musterseite:** gemeinsame Stile und wiederkehrende Bausteine festlegen. Kurze Sichtung vor der nächsten Seite.
2. **Startseite:** freigegebene 2er-Vorlage übertragen, Projektbereich und Klickziele umsetzen, ohne Backend-Aufrufe.
3. **Über uns:** bestätigte Teamseite und Fotos in denselben Stil übertragen.
4. **Kontakt und Formularvorbereitung:** Oberfläche, Validierung, Vorwahl und Anschlussstelle erstellen, ausdrücklich ohne Versand.
5. **Frontend-Abnahme:** vier Seiten zusammen prüfen, Mobil-/Sprach-/Navigationsfehler beheben und Backend-Unabhängigkeit kontrollieren.

Nicht nach jedem Prompt das gesamte Projekt umbauen oder eine vollständige Abnahme wiederholen. Angemessene TypeScript-/Build-Prüfungen nach tatsächlichen Codeänderungen; visuelle Sichtung der betroffenen Seiten. Die umfassende Prüfung erfolgt zum Abschluss. Bereits vorhandene fremde Änderungen erhalten, keine automatischen Commits oder Deployments.

## 7. Abnahmekriterien dieses Frontend-MVP

- [ ] Vier Kernseiten folgen den bestätigten Referenzen und einem gemeinsamen Stil.
- [ ] Bilder sind vorhanden, sinnvoll zugeschnitten und in angemessener Grösse eingebunden.
- [ ] Mobilansicht und DE/EN sind benutzbar; englische Entwurfstexte als noch zu sichtende Übersetzung kennzeichnen.
- [ ] Navigation, Projekte, Beratungs-/Leistungsbuttons und Airbnb-Ziel funktionieren.
- [ ] Kontaktformular kann ausgefüllt werden und zeigt sinnvolle Validierung und Vorwahl.
- [ ] Der fehlende Onlineversand wird sichtbar erklärt; keine Nachricht wird scheinbar oder tatsächlich verschickt.
- [ ] E-Mail und Telefon sind zusätzlich erreichbar.
- [ ] Kleine typisierte Anschlussstelle und dokumentierter späterer API-Vertrag vorhanden.
- [ ] Die MVP-Seiten sind ohne Backend nutzbar und verursachen keine ungeplanten API-Aufrufe.
- [ ] Keine unfertigen Artikel oder toten Platzhalterlinks in der neuen Navigation.
- [ ] Typecheck und Frontend-Build erfolgreich; Browserprüfung mit etwa 390, 768 und 1280 px und Tastaturbedienung durchgeführt oder eine konkrete Testgrenze dokumentiert.
- [ ] Backend, Workflows, NAS, Datenbank und separates Demo-Projekt unverändert.

Dies ist zunächst ein Frontend-MVP zur Sichtung. Die spätere öffentliche Veröffentlichung mit aktivem Formular erfordert die Versand-Anbindung und die noch nötigen Inhaltsfreigaben. Die aktuelle Freigabe des Auftraggebers umfasst ausdrücklich die Frontend-Vorbereitung ohne Backend.
