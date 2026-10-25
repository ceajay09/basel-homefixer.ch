// Keep the required build-jar check present even for frontend-only PRs.
const fs = require('node:fs');
async function main() {
  let relevant = true;
  if (process.env.GITHUB_EVENT_NAME === 'pull_request') {
    const event = JSON.parse(fs.readFileSync(process.env.GITHUB_EVENT_PATH, 'utf8'));
    const exact = new Set(['.github/workflows/build-maven_basel-homefixer-Backend.yml', '.github/scripts/backend-changes.cjs', 'deploy/nas/PrepareJar.java', 'deploy/nas/ValidateRelease.java']);
    const matches = name => name.startsWith('My-App_Backend/') || name.startsWith('design/Kundenpaket_20261024/Kundenpaket/assets/') || exact.has(name);
    relevant = false;
    for (let page = 1; ; page++) {
      const url = `${process.env.GITHUB_API_URL || 'https://api.github.com'}/repos/${process.env.GITHUB_REPOSITORY}/pulls/${event.number}/files?per_page=100&page=${page}`;
      const headers = { Accept: 'application/vnd.github+json', 'User-Agent': 'basel-backend-ci' };
      if (process.env.GITHUB_TOKEN) headers.Authorization = `Bearer ${process.env.GITHUB_TOKEN}`;
      const response = await fetch(url, { headers, signal: AbortSignal.timeout(30000) });
      if (!response.ok) throw new Error(`Could not inspect PR files (HTTP ${response.status})`);
      const files = await response.json();
      relevant ||= files.some(file => matches(file.filename) || file.previous_filename && matches(file.previous_filename));
      if (relevant || files.length < 100) break;
    }
  }
  fs.appendFileSync(process.env.GITHUB_OUTPUT, `backend=${relevant}\n`);
  console.log(relevant ? 'Backend changes: run full checks.' : 'Frontend-only PR: backend steps unnecessary; check succeeds.');
}
main().catch(error => { console.error(error.message); process.exitCode = 1; });
