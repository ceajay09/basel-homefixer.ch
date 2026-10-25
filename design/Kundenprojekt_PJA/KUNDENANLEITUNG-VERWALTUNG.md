# Deine Website selbst pflegen

[Verwaltung öffnen](https://basel-homefixer.ch/admin) · [Website ansehen](https://basel-homefixer.ch) · [Zurück zum README](../../README.md)

## Was kannst du ändern?

| Bereich | Deine Möglichkeiten |
| --- | --- |
| Projekte | Neue Referenzarbeiten erstellen, Texte und Bilder ändern, Kategorien und Kennzahlen pflegen |
| Startseitenteaser | Bis zu drei Projekte auswählen und ihre Reihenfolge ändern |
| Blogbeiträge | Ratgeber schreiben, Bilder einfügen und Artikel freigeben |
| Bilder | JPEG/PNG hochladen, wiederverwenden und Bildbeschreibungen pflegen |
| Veröffentlichung | Entwürfe ansehen, veröffentlichen und zurückziehen |

**Projekte** beschreiben konkrete Arbeiten, etwa einen Küchenumbau. **Blogartikel** erklären ein Thema, etwa die Planung einer Küchenmodernisierung. Du musst nicht zu jedem Projekt einen Artikel schreiben.

Texte der allgemeinen Seiten, Design, Dienstleistungen, Team, Kontaktangaben und Benutzerkonten passt der Entwickler an. Es gibt keine allgemeine Seitenbearbeitung in der Verwaltung.

## Anmelden

1. Öffne [basel-homefixer.ch/admin](https://basel-homefixer.ch/admin).
2. Melde dich mit deiner Website-E-Mail und deinem persönlichen Passwort an.
3. Wähle den gewünschten Bereich. Du brauchst keinen MongoDB- oder NAS-Zugang.

Beide eingerichteten Konten besitzen dieselben Verwaltungsrechte. Über DE/EN oben änderst du die Sprache der Bedienung. Im Bearbeitungsformular wählst du separat die Sprache des Inhalts.

Melde dich nach der Arbeit ab. Nach einem Neuladen ist eine neue Anmeldung nötig. Die Anmeldung läuft ausserdem nach begrenzter Zeit ab; dann erscheint die Möglichkeit zur erneuten Anmeldung. Es gibt noch keine Funktion „Passwort vergessen“ oder „Passwort ändern“ im Browser. Wende dich dafür an den Betreiber.

## Projekte pflegen

### Neues Projekt

1. Öffne **Projekte → Entwurf anlegen**.
2. Vergib einen eindeutigen URL-Namen, beispielsweise `kueche-basel`. Verwende Kleinbuchstaben, Zahlen und Bindestriche. Nach der Veröffentlichung möglichst unverändert lassen.
3. Fülle Titel, Kurztext und ausführliche Beschreibung in **DE und EN** aus. Ergänze Ausgangslage und Ergebnis, besonders für Startseitenteaser.
4. Wähle passende Kategorien. Ergänze Ort, Projektmonate und Schlagwörter nur anhand tatsächlicher Angaben.
5. Lade ein Bild hoch oder wähle ein vorhandenes Titelbild. Ergänze die Bildbeschreibungen in beiden Sprachen.
6. Wähle **Entwurf speichern**. Prüfe danach die Vorschau und veröffentliche erst nach deiner Freigabe.

Projektmonate werden beispielsweise als `2026-04, 2026-10` eingegeben. Die Dauer meint die **Gesamtprojektdauer**, nicht nur die Arbeitszeit vor Ort. Entweder Einheit und beide Grenzen angeben, etwa 4 bis 6 Monate, oder „Keine Angabe“ wählen. Unbekannte Kennzahlen leer lassen; keine erfundenen Werte oder Platzhalter eintragen.

### Bestehendes Projekt ändern

Öffne das Projekt in der Liste. Ist es veröffentlicht, musst du zunächst **Veröffentlichung zurückziehen** bestätigen. Während der Bearbeitung ist es öffentlich nicht sichtbar. Danach ändern, speichern, beide Sprachen prüfen und erneut veröffentlichen. Es gibt derzeit keinen parallel bearbeitbaren Entwurf eines weiterhin veröffentlichten Projekts.

## Bilder verwalten

1. Öffne den Bilderbereich im Bearbeitungsformular oder den Bereich **Bilder**.
2. Wähle eine **JPEG- oder PNG-Datei**. HEIC-Fotos vom Smartphone vorher als JPEG exportieren. SVG-Dateien werden nicht unterstützt.
3. Beschreibe das Motiv kurz in DE und EN, etwa „Neue Hauseingangstür nach der Renovation“. Diese Alternativtexte helfen, wenn das Bild nicht sichtbar ist.
4. Lade das Bild hoch und wähle bei Bedarf **Als Titelbild verwenden**. Prüfe die Auswahl im Formular und speichere den Beitrag.

Die Website erstellt passende Bildgrössen automatisch. Bei zu grossen Dateien das Foto verkleinern und erneut versuchen. Verwende Bilder, für deren Veröffentlichung du die Rechte hast, und prüfe erkennbare Personen oder private Angaben.

Vorhandene Bilder kannst du für mehrere Inhalte wiederverwenden. Titelbild-Alternativtexte lassen sich im Beitrag separat anpassen. Das Entfernen einer Zuordnung löscht die Datei nicht; eine allgemeine Löschfunktion ist nicht vorgesehen.

Ein Upload allein veröffentlicht kein Bild. Wird dasselbe Bild bereits in einem anderen veröffentlichten Inhalt verwendet, bleibt es darüber öffentlich sichtbar.

## Startseitenteaser

1. Öffne **Startseitenteaser**.
2. Entferne bei Bedarf eine bisherige Auswahl und füge die gewünschten Projekte hinzu: höchstens drei.
3. Ordne sie mit **Hoch/Runter** und speichere.
4. Prüfe die öffentliche Startseite.

Ein ausgewählter Entwurf erscheint erst nach seiner Veröffentlichung. Das Entfernen aus der Startseitenauswahl entfernt das Projekt nicht von der Projektseite. Für einen guten Teaser brauchen beide Sprachversionen auch Ausgangslage, Ergebnis und ein Titelbild.

## Blog pflegen

Öffne **Blogbeiträge → Entwurf anlegen**. Ergänze URL-Namen, Titel, Kurztext, Kategorie, Titelbild und Artikeltext in DE und EN. Speichern, Vorschau und Freigabe funktionieren wie bei Projekten.

Die drei vorbereiteten Artikel zu Küche, Heizung sowie Raumthermostaten/Smart Home sind zunächst Entwürfe. Prüfe Fakten, Bilder und Übersetzungen vor der Veröffentlichung. Hinweise auf einen englischen Übersetzungsentwurf erst nach sprachlicher Prüfung entfernen. Solange kein Artikel veröffentlicht ist, zeigt die öffentliche Blogseite eine Leermeldung.

### Text einfach formatieren

Der Artikeltext verwendet Markdown. Beispiele:

```markdown
## Eine Zwischenüberschrift

Ein Absatz mit **wichtigen Worten**.

- Erster Punkt
- Zweiter Punkt

[Weitere Informationen](https://beispiel.ch)
```

Kein HTML einfügen. Für ein Bild im Artikel das gewünschte Bild **als zusätzliches Bild zuordnen** und dann **Bild in Text einfügen** wählen. Ersetze den eingefügten Beschreibungstext durch eine passende Bildbeschreibung. Wiederhole dies bei Bedarf im englischen Text. Bilder über die Bildauswahl verwenden, statt NAS-Pfade oder fremde Bildlinks einzufügen.

## Vorschau und Veröffentlichung

| Aktion | Was passiert? |
| --- | --- |
| Entwurf speichern | Änderungen werden gespeichert, der Entwurf bleibt öffentlich unsichtbar |
| Textvorschau | Zeigt aktuelle Texteingaben, auch vor dem Speichern |
| Geschützte Vorschau | Zeigt den zuletzt gespeicherten Stand mit Bildern, nur für angemeldete Verwalter |
| Veröffentlichen | Macht einen vollständigen Inhalt nach ausdrücklicher Bestätigung öffentlich sichtbar |
| Veröffentlichung zurückziehen | Nimmt den Inhalt aus der öffentlichen Darstellung; er bleibt zur Bearbeitung erhalten |

**Deine Prüfreihenfolge:** Speichern → DE-Vorschau → EN-Vorschau → Texte und Bilder prüfen → Veröffentlichen bestätigen → öffentliche Seite kontrollieren.

Bei fehlenden Pflichtfeldern zeigt das Formular Hinweise. Speichere zunächst alle Änderungen und ergänze die fehlenden Angaben, wenn die Vorschau oder Veröffentlichung noch nicht auswählbar ist. Der öffentliche Blogartikel erhält seine Adresse aus dem URL-Namen; Projekte erscheinen vollständig auf der Projektseite.

## Wenn etwas nicht klappt

| Situation | Was du tun kannst |
| --- | --- |
| Anmeldung abgelaufen | Im offenen Tab erneut anmelden; nicht vorschnell neu laden |
| Speichern fehlgeschlagen | Eingaben geöffnet lassen, Verbindung prüfen und erneut versuchen |
| Jemand hat denselben Inhalt geändert | Eigene Texte sichern, aktuellen Eintrag neu öffnen und Änderungen bewusst zusammenführen |
| Veröffentlichung nicht möglich | Pflichtfelder, beide Sprachen, Titelbild und gespeicherten Stand prüfen |
| Projekt nicht auf der Startseite | Veröffentlichung und Auswahl unter Startseitenteaser prüfen |
| Bild abgelehnt | JPEG/PNG verwenden und bei Bedarf verkleinern; bei wiederholtem Fehler Betreiber kontaktieren |
| Passwort vergessen | Betreiber kontaktieren; kein neues Konto über eine Registrierung anlegen |

Speichere regelmässig. Nach Schliessen oder Neuladen gehen ungespeicherte Eingaben verloren. Teile Fehlermeldung und betroffenen Inhalt mit dem Betreiber, aber keine Passwörter.

## Was weiterhin der Betreiber erledigt

Benutzerkonten und Passwortreset, technische Fehler, Datensicherung, Mailkonfiguration, allgemeine Seiten- und Designänderungen sowie neue Softwareversionen. Projekte, Blogtexte und Bilder pflegst du direkt hier; dafür ist kein neues Deployment nötig.
