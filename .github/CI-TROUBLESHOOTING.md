# CI-Fehler bei Dependabot (25. Oktober 2026)

Die regulären Frontend-/Backendläufe auf main, Commit 33527bb, waren erfolgreich. Die neuen Fehlermeldungen betrafen Dependabot:

1. Backend-CI auf dem reinen Vite-PR scheiterte bei Validate package token: PACKAGES_TOKEN fehlte. Dependabot kann normale Actions-Secrets nicht verwenden. Der Backendworkflow filtert nun auch Pull Requests nach Backend-/Builddateien. Maven verwendet bei fehlendem bisherigen PACKAGES_TOKEN den bereits vorhandenen kurzlebigen github.token mit der bestehenden packages:read-Berechtigung. Keine zusätzlichen langfristigen Secrets, kein pull_request_target und keine neuen Tokenrechte. Dependency-Submission läuft nur auf main-Push, nicht auf Bot-PRs.
2. Der Maven-Sicherheitsupdater meldete security_update_dependency_not_found für jackson-core. Die betroffene Anwendung löste 2.22.1 über den BOM auf. Jackson-BOM und direkte Databind/Core-Versionen sind nun einheitlich 2.22.4. Core ist ausdrücklich deklariert, damit Dependabot das Koordinatenziel findet. Keine automatische Schliessung/Unterdrückung von Sicherheitswarnungen.

## Noch zu beachten

Der kurzlebige GITHUB_TOKEN muss das gemeinsame Maven-Parent-Paket lesen dürfen. Seine bestehenden packages:read-Rechte allein garantieren keinen Zugriff auf ein Paket aus einem anderen Repository. Falls ein zukünftiger Maven-Bot-PR hier 401/403 erhält, den Paketzugriff separat prüfen. Ein zusätzliches langfristiges Dependabot-Secret wurde durch die automatische Freigabeprüfung abgelehnt und nicht hinzugefügt. Es wurde keine Paketberechtigung verändert und kein privater Token gelesen/kopiert.

Die Änderung liegt nur auf basel-homefixer und muss vor Wirkung auf neue Dependabot-PRs nach main übernommen werden. Alte fehlgeschlagene Runs werden dadurch nicht rückwirkend erfolgreich. Der laufende ECR-/NAS-Betrieb bleibt unverändert; kein automatisches Merge oder Deployment.

Offizielle Grundlagen:
- [Dependabot und Actions-Secrets](https://docs.github.com/en/code-security/reference/supply-chain-security/troubleshoot-dependabot/dependabot-on-actions)
- [Maven-Paketberechtigungen](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)

Lokal geprüft: Maven verify mit 74 erfolgreichen isolierten Tests, JAR-Build und vorhandene Deployment-/Workflow-Vertragsprüfung. Echte E-Mails und produktive Datenbanken wurden nicht verwendet. Der Zugriff eines zukünftigen Dependabot-Tokens auf das private Parent-Paket ist damit noch nicht nachgewiesen.
