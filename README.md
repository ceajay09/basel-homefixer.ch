# basel-homefixer.ch – Einstieg und Initialisierung

**Stand 25. Oktober 2026:** MVP lokal implementiert; regulärer NAS-Betrieb vorbereitet, noch nicht ausgerollt. Keine produktiven Konten/Daten angelegt oder verändert. [Aktueller Prüfbericht](design/Kundenprojekt_PJA/MVP-ABNAHME.md).

## Die wichtigsten Entscheidungen

- **Datenbankziel: basel_homefixer.** Laut Betreiber bereits samt technischem Nutzer baselhomefixer angelegt; Verbindung noch nicht geprüft. DEMO gehört zur Demo-Website. basel_homefixer_preview war eine isolierte Vorschau mit Test-Veröffentlichungen und bleibt getrennt. Den Produktionscluster bewusst wählen; eine neue Datenbank trennt nicht automatisch die Clusterressourcen. [Details und Atlas-Schritte](deploy/nas/DATENBANK.md).
- **Zwei Website-Admins**, für dich und den Kunden, mit eigenen E-Mails/Passwörtern in basel_homefixer.account. MongoDB-Zugang ist nur technischer Serverzugang. [Einrichten / Wiederherstellen](design/Kundenprojekt_PJA/ADMIN-ZUGANG.md).
- **Projekte** dokumentieren konkrete Referenzarbeiten. **Blog** bietet Ratgeber und Erklärungen, auch ohne einzelnen Auftrag. Beide sind dynamisch pflegbar; der Blog bleibt auf Wunsch sichtbar. Artikel müssen keinen Inhalt aus Projekten wiederholen.
- Inhalte/Bilder werden über /admin gepflegt und benötigen keinen Frontend-Build. Codeänderungen benötigen weiterhin neue Images und Rollout. Keine statische öffentliche NAS-Bilderfreigabe.

## Was du zuerst tun musst

1. **MongoDB-Zugang nachführen:** Datenbank und Nutzer baselhomefixer sind laut Betreiber erstellt. In der neuen Portainer-Vorlage MONGODB_URI privat setzen; Host aus Atlas, URI-Datenbank basel_homefixer. Kein MongoDB-Passwort im Repository/Frontend/CI. [Genaue Stelle und URI-Muster](deploy/nas/DATENBANK.md#zugang-jetzt-hinterlegen). Verbindung/Rollen vor Rollout prüfen; keine Vorschaukopie.
2. **Release:** bestehende Workflows sind auf bereinigte Images angepasst: Tests/Build, Dockerprüfungen, dev- und Commit-Tags. Images entstehen nur bei Push auf main, nicht beim Push auf basel-homefixer. [Einfacher CI-Ablauf](deploy/nas/PIPELINE.md). Kein automatischer NAS-Rollout; isolierten MongoDB-/Medientest vor Regelbetrieb nachweisen.
3. **NAS:** privaten Medienordner und externe Konfigurationsdatei mit passenden Rechten vorbereiten, stabilen Signaturschlüssel privat sichern. Cloudflare-Ziel/Proxy-Kette und Cache-Regeln bestätigen.
4. **Portainer:** [Compose zum vollständigen Kopieren](deploy/nas/compose.portainer.yaml) und [kurze Einfügeanleitung / Variablen](deploy/nas/PORTAINER.md). Wieder drei Dienste wie bisher und stack.env. **Noch nicht Deploy drücken:** zuvor Backup, isolierten Test und Rollout ausdrücklich freigeben.
5. **Beim genehmigten Rollout:** neuen Backend-Entwurfsschutz bereitstellen, gesichtete Inhalte als Entwürfe übertragen, zwei Admins interaktiv anlegen, Frontend prüfen. Projekte/Artikel separat veröffentlichen.
6. **Gmail zuletzt:** verfügbares App-Passwort serverseitig eintragen, SMTP bewusst aktivieren und einen Testversand vom NAS gesondert freigeben. Keine automatische Testmail.

## Pflege und Betrieb

| Aufgabe | Anleitung |
| --- | --- |
| Kunde: Projekte/Blog/Bilder, Startseitenauswahl, Vorschau, Veröffentlichen/Zurückziehen | [Kurze Kundenanleitung](design/Kundenprojekt_PJA/KUNDENANLEITUNG-VERWALTUNG.md) |
| Erstzugang, vergessenes Passwort, Token/Abmeldung | [Adminzugang](design/Kundenprojekt_PJA/ADMIN-ZUGANG.md), [Authentifizierung](My-App_Backend/AUTH.md) |
| Datenmodell, Medien, Import/Dry-Run | [CONTENT.md](My-App_Backend/CONTENT.md) |
| Geschützte API, Uploadgrenzen, Bearbeitungskonflikte | [ADMIN.md](My-App_Backend/ADMIN.md) |
| SMTP-Variablen, App-Passwort, späterer Testversand | [CONTACT.md](My-App_Backend/CONTACT.md) |
| Gemeinsames DB-/Medienbackup, Wiederherstellung, Rollout und Rücknahme | [NAS-Betriebsanleitung](deploy/nas/README.md) |
| Lokales Entwickeln / private NAS-Lesevorschau | [Lokaler Start](LOKALER-START.md), [NAS-VORSCHAU.md](NAS-VORSCHAU.md) |
| Umfang und Freigaben | [Umsetzungsplan](design/Kundenprojekt_PJA/UMSETZUNGSPLAN.md) |

Offene Kundensichtung: Bilder/Ausschnitte, finale DE-/EN-Texte, drei Artikelentwürfe, Kontakt-/Antwortzeitangaben und Datenschutzinformation. Öffentliche Veröffentlichung bleibt eine eigene Entscheidung. Details und tatsächlich geprüfte Grenzen stehen im Prüfbericht.
