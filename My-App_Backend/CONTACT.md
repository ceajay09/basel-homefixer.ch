# Kontaktversand

Stand: 24. Oktober 2026. Bestehendes Spring-Boot-Backend; kein zusätzlicher Dienst und keine MongoDB-Speicherung der Anfragen.

## Konfiguration und Aktivierung

contact.env.example enthält ausschliesslich leere/unverfängliche Beispielwerte. Echte Werte ausschliesslich als Server-Umgebungsvariablen setzen, nicht ins Repository, Frontend oder Logs übernehmen. Die aktuelle NAS-/Compose-Konfiguration wurde nicht geändert.

- CONTACT_MAIL_ENABLED muss ausdrücklich true sein.
- CONTACT_SMTP_PASSWORD ist das separat für diese Anwendung erstellte Gmail-App-Passwort, nicht das normale Google-Passwort. Zwei-Faktor-Authentifizierung und eine beim Kundenkonto verfügbare App-Passwort-Funktion sind Voraussetzung. Falls diese fehlt, ist OAuth ein eigener zusätzlicher Implementierungsschritt.
- CONTACT_SMTP_HOST standardmässig smtp.gmail.com, PORT 587 mit verpflichtendem STARTTLS. SSL=true und Port 465 sind alternativ möglich. Authentifizierung und Serveridentitätsprüfung bleiben aktiv.
- Absender, SMTP-Benutzer und Empfänger sind serverseitig fest baselhomefixer@gmail.com. Die Besucheradresse wird ausschliesslich als Reply-To gesetzt.
- Verbindung, Lesen und Schreiben sind jeweils auf 5 Sekunden begrenzt. Es gibt keine Hintergrundwarteschlange und keine automatischen Wiederholungen oder Besucher-Bestätigungsmails.

Ohne aktivierte/vollständige Konfiguration meldet der Status available=false, POST antwortet mit 503 und versendet nichts. Konfigurationsverfügbarkeit ist kein SMTP-Verbindungstest und keine Zustellgarantie. Der globale Mail-Health-Check ist deaktiviert, damit eine noch nicht konfigurierte Kontaktfunktion den vorhandenen Container-Healthcheck nicht fehlschlagen lässt. Erst ein ausdrücklich freigegebener Echttest prüft Gmail und den Eingang im Postfach.

## Schutz und Reverse Proxy

Honeypot website muss leer bleiben. Serverseitige Bean Validation begrenzt Name auf 2–100, E-Mail auf 254, Telefon auf 40 und Nachricht auf 10–5000 Zeichen, verlangt Einwilligung und prüft die acht Service-Schlüssel bzw. die neutrale Auswahl. Kein frei wählbarer Empfänger/Absender. Inhalt ist Klartext.

Begrenzung pro Backendprozess: höchstens 3 gültige Versandversuche je Adresse in 15 Minuten sowie 20 global in einer Stunde. Fehlgeschlagene SMTP-Versuche zählen mit. Zustand ist nur im Speicher, nach Neustart zurückgesetzt und nicht zwischen mehreren Backendinstanzen geteilt. Für den aktuellen Einzelcontainer-MVP gedacht.

Standardmässig zählt die Socket-IP. Hinter Traefik teilen Besucher sonst dessen Adresse und damit ein Limit. Vor öffentlicher Aktivierung CONTACT_TRUSTED_PROXIES auf die exakten tatsächlichen Proxy-Adressen setzen und sicherstellen, dass der Backendport nicht öffentlich am Proxy vorbei erreichbar ist. Die Anwendung wertet X-Forwarded-For nur bei diesen Socket-Peers aus und durchläuft die Kette von rechts bis zur ersten nicht vertrauenswürdigen Adresse; beliebige vom Client gesetzte linke Werte werden nicht bevorzugt. Keine pauschalen privaten Netze oder Wildcards hinterlegen.

CORS wurde ausschliesslich für /api/contact und /api/contact/status ergänzt: Produktionsdomain sowie localhost/127.0.0.1 auf Ports 5173 und 5180. Fremde Origins sind nicht freigegeben. Die bereits vorhandene allgemeine API-Autorisierung wurde nicht erweitert oder umgebaut. CORS ersetzt keinen Spamschutz.

## API-Vertrag

GET /api/contact/status liefert { available: boolean }, ohne Geheimnisse und ohne Cache.

POST /api/contact mit JSON: name, email, phone (optional), service (leer oder kitchen/bathroom/heating/windows-doors/electrical/smart-home/energy/consulting), message, consent=true und website leer. Unbekannte vom Client mitgeschickte Empfänger-/Absenderfelder steuern den Versand nicht.

Antworten enthalten code und ggf. fieldErrors mit Feldnamen und dem Wert invalid, niemals eingegebene Nachrichtentexte oder SMTP-Zugangsdaten:
- 200 accepted: synchroner Versandaufruf hat nach SMTP-Annahme abgeschlossen. Kein Nachweis des Eingangs im Empfängerpostfach.
- 400 invalid_request: Feldprüfung, ungültiger Service, Honeypot oder ungültiges JSON.
- 413 invalid_request: JSON-Nutzlast über 32 KiB, vor der Deserialisierung begrenzt (auch ohne Content-Length).
- 429 rate_limited mit Retry-After.
- 503 unavailable: nicht aktiviert/vollständig konfiguriert.
- 502 send_failed: SMTP-Aufruf fehlgeschlagen. Bei Verbindungsabbruch kann der genaue Zustellstatus unklar sein.

Name/E-Mail/Telefon/Nachricht werden serverseitig getrimmt. Mehrfachklicks während eines laufenden Requests werden im Frontend verhindert; kein dauerhafter serverseitiger Idempotenzspeicher. Bei Browser-Timeout wird bewusst ein unklarer Versandstatus gemeldet und nicht automatisch wiederholt.

## Lokale Entwicklung und Tests

Vite leitet ausschliesslich /api/contact an http://127.0.0.1:8081 weiter. CONTACT_DEV_API_TARGET kann den lokalen Entwicklungs-Backendhost ändern; Vite danach neu starten. Alternativ verwendet der vorhandene VITE_API_URL-Wert die erlaubte Backend-Origin. Kein Live-Backend mit produktiver MongoDB-Konfiguration nur für diesen Test starten.

Backend: im Ordner My-App_Backend .\mvnw.cmd test ausführen. MVC-/Security-Tests arbeiten mit einem Mail-Testdouble, Rate-Limiter-Tests mit steuerbarer Zeit. ContactSmtpTest betreibt einen Loopback-SMTP-Sink ohne externe Zustellung und prüft echte JavaMail-Verarbeitung inklusive UTF-8. Kein Gmail-Zugang und keine Datenbank erforderlich. Das lokale SMTP-Testdouble schaltet nur im Test TLS/Auth aus; der produktive Sender behält beides bei.

Frontend: npm run typecheck, npm run build; Browserprüfung kann Status/POST-Antworten gezielt abfangen. Solche Testantworten sind keine Produktivfunktion und ersetzen keinen Echttest.

## Vor öffentlichem Betrieb offen

Kunde richtet verfügbares Gmail-App-Passwort ein; Konfiguration lokal auf dem NAS setzen und Proxy-Adressen prüfen. Danach separat freigegebenen Testversand vom NAS durchführen und Posteingang kontrollieren. Keine aktuellen Workflows/NAS-Einstellungen wurden dafür geändert. Die in der Vorlage genannten Antwortzeit von 24 Stunden und Erreichbarkeit Mo–Fr 8–18 Uhr sowie die finale Datenschutzinformation bedürfen Kundensichtung. Die Einwilligung behauptet keine bereits gelesene, nicht vorhandene Datenschutzerklärung und enthält keinen toten Link.

## Ausgeführte Prüfung

14 Backendtests erfolgreich: MVC/Validierung, acht Services und neutrale Auswahl, feste Empfänger/Reply-To, fehlende Konfiguration, SMTP-Fehler, CORS, Forwarded-IP-Behandlung, Nutzlastbegrenzung, Limitfenster und lokaler UTF-8-SMTP-Austausch. Browserprüfung in DE/EN bei 390/768/1280 px mit ausschliesslich abgefangenen Testantworten: Vorwahl, Validierung, Erhalt der Eingaben, Fehler-/429-/503-Zustände, Mehrfachklickschutz und SMTP-Annahmezustand. Echte Gmail-/NAS-Verbindung und Zustellung sind damit ausdrücklich noch nicht geprüft.

## Lokaler Gmail-Test (25. Oktober 2026)

Der Benutzer hat einen einzelnen echten Testversand ausdrücklich freigegeben. Das vorhandene SMTP-Passwort wurde ohne Wertausgabe aus contact.env.example in `%LOCALAPPDATA%/basel-homefixer/contact.env` verschoben. Der private Ordner ist ausschliesslich für das aktuelle Windows-Konto freigegeben; Beispieldatei wieder ohne Passwort. Keine NAS-/Container-Konfiguration geändert.

Die private Konfiguration enthielt SSL=true mit Port 587. Das wurde lokal auf SSL=false mit verpflichtendem STARTTLS korrigiert. Die reine Konfigurationsprüfung hat zuvor keinen Versand versucht. Danach genau ein Versand über den bestehenden ContactService/JavaMailSender an und von baselhomefixer@gmail.com: SMTP_ACCEPTED. Betreff: Website-Anfrage: Allgemeine Anfrage; Name Technischer Funktionstest, Nachricht ausdrücklich als autorisierter Test ohne Kundenauftrag markiert. Gmail hat die Mail angenommen; Eingang/Postfach/Spamordner und Betrieb vom NAS sind noch nicht geprüft. Keine Wiederholung oder Bestätigungsmail versendet.

Der explizite Testhelfer ContactLiveSmtpCheck liegt ausschliesslich im Testquellbaum, wird nicht beim Testlauf oder Anwendungsstart ausgeführt und nicht im Anwendungs-JAR mitgeliefert. Er verlangt --send-once und private Prozess-Umgebungsvariablen, erlaubt nur smtp.gmail.com mit passender TLS-/Portkombination und gibt keine Zugangsdaten/rohen SMTP-Ausnahmen aus. Keine automatische Wiederholung. Das reguläre Kontaktformular ist dadurch nicht automatisch auf dem NAS aktiviert; dafür bleibt die serverseitige Umgebung einschliesslich CONTACT_MAIL_ENABLED=true bewusst einzurichten.

## Regulärer Betrieb: Aktivierung erst nach eigener Freigabe

Der lokale frühere SMTP-Einzeltest ist kein NAS-Test. In diesem Abnahmeschritt ausschliesslich Testdouble/Loopback-SMTP verwendet, keine echte Mail. Ein vorhandenes lokales Passwort wurde nicht in Portainer kopiert.

Google-App-Passwörter setzen aktivierte Bestätigung in zwei Schritten voraus. Die Option kann bei bestimmten Organisationskonten, ausschliesslichen Sicherheitsschlüsseln oder erweitertem Kontoschutz fehlen. Dann bleibt der Versand deaktiviert; OAuth ist nicht implementiert. Entweder verfügbaren SMTP-Zugang ausdrücklich vereinbaren oder OAuth als gesonderten Auftrag planen. Keine Umgehung der Kontorichtlinien. [Offizielle Google-Anleitung](https://support.google.com/accounts/answer/185833?hl=de).

Später genehmigter NAS-Test: private Portainer-Variablen setzen (smtp.gmail.com, 587, SSL=false/STARTTLS, App-Passwort), verifizierte Proxy-Kette ergänzen, CONTACT_MAIL_ENABLED bewusst auf true setzen und neuen Backendstand kontrollieren. Erst nach eigener Versandfreigabe genau eine klar als Test bezeichnete Anfrage auslösen, SMTP-Annahme und Empfängerpostfach/Spam prüfen; keine Zustellgarantie und kein automatischer Wiederholungsversand. Bei Fehlern zunächst wieder deaktivieren und ohne Ausgabe von Passwort/Mailinhalt untersuchen. Website-Konten und Gmail-App-Passwort sind unabhängig. Die vorherige 24-Stunden-/Erreichbarkeitszusage und Datenschutzinformation müssen weiterhin vom Kunden bestätigt werden.
