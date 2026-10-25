# Website-Adminzugänge für dich und den Kunden

[README](../../README.md) · [Kundenanleitung](KUNDENANLEITUNG-VERWALTUNG.md) · [Dokumentationsübersicht](../../DOKUMENTATION.md)

**Stand 25. Oktober 2026:** Laut Betreiber sind beide Website-Zugänge eingerichtet und funktionieren. Die laufende Website verwendet `basel_homefixer`. Die folgenden Einrichtungsschritte sind für zusätzliche Konten oder Wiederherstellung gedacht. Bestehende Konten nicht erneut mit create-admin anlegen. `DEMO` und `basel_homefixer_preview` bleiben getrennt.

## Zwei verschiedene Arten von Zugang

| Zugang | Wer verwendet ihn? | Zweck |
| --- | --- | --- |
| Technischer MongoDB-Zugang | Nur Backend/Einrichtungswerkzeug | Verbindung zu Atlas, im Regelbetrieb auf basel_homefixer begrenzt |
| Website-Admin für dich | Du im Browser unter /admin | Projekte, Blog und Bilder verwalten |
| Website-Admin für den Kunden | Kunde im Browser unter /admin | Eigene Anmeldung für dieselbe Inhaltsverwaltung |

Die beiden Website-Konten liegen getrennt in `basel_homefixer.account`: jeweils E-Mail, BCrypt-Passwort-Hash, Verwaltungsrecht und Authentifizierungsrevision. Das normale Passwort wird nicht im Klartext gespeichert. Der Kunde braucht keinen MongoDB-Zugang. Eure Website-Passwörter sind unabhängig vom MongoDB-Passwort und voneinander.

Eine Kontoanlage erteilt nicht automatisch eine Projekt-/Artikelveröffentlichung. Beide Admins haben derzeit dieselben Verwaltungsrechte; eine feinere Rollenaufteilung und Benutzerverwaltung im Browser sind nicht implementiert. Öffentliche Registrierung bleibt deaktiviert.

## Einrichtung weiterer Zugänge über die optionale Werkzeugvorlage

Die vorbereitete NAS-Vorlage enthält `admin-setup` als Werkzeugprofil. Es wird nicht beim normalen Start ausgeführt. Nach Einrichtung des auf basel_homefixer begrenzten technischen Zugangs und Bereitstellung des geprüften neuen Images:

1. Deine E-Mail für den Entwickler-Admin festlegen. Als Kunden-E-Mail ist `baselhomefixer@gmail.com` naheliegend; bei Bedarf eine andere ausdrücklich vereinbarte Adresse verwenden.
2. Für jede Adresse zuerst ohne `--apply` prüfen. Das ist ausschliesslich ein Dry-Run.
3. Dann bewusst mit `--apply --confirm-database basel_homefixer` anlegen. Das Werkzeug fragt das neue Website-Passwort zweimal verdeckt im interaktiven Terminal ab.

Aus dem privaten Operationsordner auf dem NAS, mit den dort gesicherten Konfigurationsdateien der optionalen Werkzeugvorlage compose.mvp.yaml (nicht den Portainer-Stack ein zweites Mal starten). OPS_RELEASE_DIR muss dort auf den Ordner mit application-production.properties zeigen, NAS_CONTENT_ROOT auf denselben privaten Mount wie in Portainer. Für diesen interaktiven Werkzeuglauf reicht compose run; kein compose up:

```sh
# Dry-Run, noch kein Konto. Namen/Dateipfade gemäss deploy/nas/README.md.
docker compose --env-file release.env --env-file private.env -f compose.mvp.yaml \
  run --rm --no-deps admin-setup --mode create-admin \
  --email DEINE_EMAIL --database basel_homefixer

# Nach Freigabe: dein eigenes Konto, Passwort verdeckt im Terminal.
docker compose --env-file release.env --env-file private.env -f compose.mvp.yaml \
  run --rm --no-deps admin-setup --mode create-admin \
  --email DEINE_EMAIL --database basel_homefixer --apply --confirm-database basel_homefixer

# Zweites, getrenntes Konto für den Kunden; eigenes Passwort.
docker compose --env-file release.env --env-file private.env -f compose.mvp.yaml \
  run --rm --no-deps admin-setup --mode create-admin \
  --email baselhomefixer@gmail.com --database basel_homefixer --apply --confirm-database basel_homefixer
```

Ein echtes interaktives Terminal verwenden, keine Passwortargumente, Ausgabeumleitung oder Passwörter im Chat. Auf dem NAS ist der ermittelte Docker-Pfad `/usr/local/bin/docker`, falls `docker` nicht im PATH liegt. Mindestlänge zwölf Zeichen, höchstens 72 UTF-8-Bytes. Kunde sollte sein Passwort selbst festlegen; andernfalls die vertrauliche Übergabe und den anschliessenden Passwortreset ausdrücklich vereinbaren. Es gibt noch keine Passwort-ändern-Oberfläche oder öffentliche Passwort-vergessen-Funktion.

Falls dieselbe E-Mail bereits existiert, bricht die Neuanlage ab. Kein Altbestand wird überschrieben oder automatisch hochgestuft. Der dokumentierte Reset in AUTH.md ändert nur Passwort/Revision, nicht die Rechte; für ein vorhandenes normales Konto ist deshalb eine gesonderte geprüfte Lösung nötig.

## Alternative im eigenen Windows-Terminal

Der vorhandene lokale Helfer bleibt verfügbar. Hier ist kein Datenbank-Read-Host mehr nötig:

```powershell
Set-Location C:\Users\cesarjaquiery\vscode\basel-homefixer.ch\My-App_Backend
$adminEmail = Read-Host 'Deine E-Mail für den Website-Admin'
.\setup-admin.ps1 -Email $adminEmail -Database basel_homefixer
```

Für einen später ausdrücklich freigegebenen Apply den technischen MongoDB-Zugang verdeckt in die Prozessumgebung setzen:

```powershell
$mongoAccess = Read-Host 'Technischer MongoDB-Zugang (verdeckt)' -AsSecureString
$env:AUTH_SETUP_MONGO_URI = [Net.NetworkCredential]::new('', $mongoAccess).Password
try {
    .\setup-admin.ps1 -Email $adminEmail -Database basel_homefixer -Apply -ConfirmDatabase basel_homefixer
    .\setup-admin.ps1 -Email baselhomefixer@gmail.com -Database basel_homefixer -Apply -ConfirmDatabase basel_homefixer
} finally {
    Remove-Item Env:AUTH_SETUP_MONGO_URI -ErrorAction SilentlyContinue
    Remove-Variable mongoAccess -ErrorAction SilentlyContinue
}
```

Beide Varianten schreiben in dieselbe Collection und sind Alternativen; nicht doppelt ausführen. Das reguläre Backend muss basel_homefixer und einen stabilen serverseitigen AUTH_JWT_SECRET_BASE64 verwenden. Danach meldet ihr euch jeweils mit eurer Website-E-Mail und eurem eigenen Passwort unter `/admin` an. Nach Neuladen ist eine erneute Anmeldung nötig.

Die ursprüngliche Vorbereitung hat keine Konten angelegt. Die anschliessende Einrichtung beider Konten wurde inzwischen vom Betreiber bestätigt. Die reine NAS-Lesevorschau unterstützt weiterhin keine Anmeldung. Betriebsreihenfolge, Sicherung und Rücknahme stehen in `deploy/nas/README.md`.

## Passwort vergessen / Zugang wiederherstellen

Vorhandenes Werkzeug im Backend: `setup-admin.ps1 -Email DEINE_EMAIL -Database basel_homefixer -Mode reset-password` zuerst ohne Apply prüfen; danach bewusst `-Apply -ConfirmDatabase basel_homefixer` ergänzen. AUTH_SETUP_MONGO_URI wie oben verdeckt setzen und danach entfernen. Keine Passwortargumente. Der Reset erhält die ID und Rechte, erzeugt neuen Hash/Revision und sperrt damit alte Tokens. Er macht kein normales Konto zum Admin. Fehlt das Konto ganz, create-admin statt Reset verwenden. Fehlt die Berechtigung bei einem vorhandenen Konto, nicht direkt in MongoDB improvisieren, sondern den konkreten Fall separat prüfen. Details und tatsächliche Schalternamen: [AUTH.md](../../My-App_Backend/AUTH.md).

