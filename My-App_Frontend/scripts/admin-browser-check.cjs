/* Opt-in browser test. Point only to AdminBrowserFixture. Never use against production. */
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const { chromium } = require(process.env.PLAYWRIGHT_MODULE || 'playwright');
const sharp = require(process.env.SHARP_MODULE || 'sharp');
const base = process.env.ADMIN_UI_TEST_URL || 'http://127.0.0.1:5173';
const api = fs.readFileSync(process.env.ADMIN_FIXTURE_ENDPOINT || 'C:/Temp/basel-admin-browser-endpoint.txt', 'utf8').trim();
if (!/^http:\/\/127\.0\.0\.1:\d+$/.test(api) || !/^http:\/\/127\.0\.0\.1:\d+$/.test(base)) throw new Error('Local isolated test only');
(async () => {
 const browser = await chromium.launch({ channel: 'msedge', headless: true });
 const context = await browser.newContext({ viewport: { width: 1280, height: 900 } });
 const page = await context.newPage(); const errors = []; page.on('pageerror', e => errors.push(e.message));
 const suffix = Date.now().toString(36), projectSlug = 'isolated-browser-project-' + suffix, blogSlug = 'isolated-blog-post-' + suffix;
 let token = ''; page.on('response', async r => { if (new URL(r.url()).pathname === '/api/login' && r.status() === 200) { token = (await r.json()).token; } });
 const button = (name) => page.getByRole('button', { name, exact: true });
 const fill = (label, text) => page.getByRole('textbox', { name: label, exact: true }).fill(text);
 const waitText = async text => page.getByText(text, { exact: false }).filter({ visible: true }).first().waitFor();
 async function login() { await fill('E-Mail', 'browser@example.invalid'); await page.locator('input[type=password]').fill('Isolated-browser-only-123'); await button('Anmelden').click(); await button('Abmelden').waitFor(); }
 async function clean() { assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth + 1), true, 'horizontal overflow'); }
 async function languageFields(lang, title) {
  await button(lang).last().click(); await fill('Titel ' + lang, title); await fill('Kurztext ' + lang, 'Summary ' + lang); await fill('Beschreibung / Artikeltext ' + lang, 'Description ' + lang);
  await fill('Ort ' + lang, 'Basel'); await fill('Ausgangslage ' + lang, 'Challenge ' + lang); await fill('Ergebnis ' + lang, 'Result ' + lang);
 }
 try {
  await page.goto(base + '/admin'); await button('DE').first().click();
  await fill('E-Mail', 'browser@example.invalid'); await page.locator('input[type=password]').fill('wrong'); await button('Anmelden').click(); await waitText('Die Anmeldung ist fehlgeschlagen.');
  await login(); console.log('Signed in'); await button('Entwurf anlegen').click(); await fill('URL-Name', projectSlug);
  await languageFields('DE', 'Browser Testprojekt'); await languageFields('EN', 'Browser Test Project');
  await page.getByLabel('Küchen', { exact: true }).check(); await fill('Projektzeitpunkte', '2026-10');
  const png = await sharp({ create: { width: 80, height: 50, channels: 3, background: '#28764a' } }).png().toBuffer();
  await page.getByLabel('Bilddatei auswählen', { exact: true }).setInputFiles({ name: 'phone.png', mimeType: 'image/png', buffer: png });
  await fill('Alternativtext zum neuen Bild DE', 'Küche Testbild'); await fill('Alternativtext zum neuen Bild EN', 'Kitchen test image'); await button('Bild hochladen').click();
  await page.getByRole('img', { name: 'Kitchen test image', exact: true }).first().waitFor();
  const create = page.waitForResponse(r => new URL(r.url()).pathname === '/api/admin/projects' && r.request().method() === 'POST');
  await button('Entwurf speichern').click(); const created = await (await create).json(); console.log('Project draft saved'); const projectId = created.id; assert.ok(projectId);
  await button('Geschützte Vorschau').click(); await page.getByRole('dialog').getByText('Browser Test Project', { exact: true }).waitFor();
  await page.getByRole('dialog').locator('img').first().waitFor(); assert.equal(await page.getByRole('dialog').locator('img').first().evaluate(img => img.currentSrc.startsWith('blob:')), true);
  await button('Schliessen').click();
  await button('Veröffentlichen').click(); await button('Ja, ausführen').click(); await button('Veröffentlichung zurückziehen').waitFor();
  console.log('Project published'); let response = await context.request.get(api + '/api/projects/by-id/' + projectId + '?lang=de'); assert.equal(response.status(), 200);
  const publicProject = await response.json(); response = await context.request.get(api + publicProject.image.largeUrl); assert.equal(response.status(), 200);
  const publicPage = await context.newPage(); await publicPage.goto(base + '/projects#' + projectId); await publicPage.getByText('Browser Test Project', { exact: true }).waitFor(); await publicPage.close();
  await button('Veröffentlichung zurückziehen').click(); await button('Ja, ausführen').click(); await button('Veröffentlichen').waitFor();
  assert.equal((await context.request.get(api + '/api/projects/by-id/' + projectId + '?lang=de')).status(), 404);
  assert.equal((await context.request.get(api + publicProject.image.largeUrl)).status(), 404);
  console.log('Project withdrawn'); await fill('Titel EN', 'Unsaved retained');
  page.once('dialog', d => d.dismiss()); await button('Zur Liste').click(); assert.equal(await page.getByRole('textbox', { name: 'Titel EN', exact: true }).inputValue(), 'Unsaved retained');
  // Real optimistic conflict: update the fixture from a second client.
  response = await context.request.get(api + '/api/admin/projects/' + projectId, { headers: { Authorization: 'Bearer ' + token } });
  const current = await response.json(); response = await context.request.put(api + '/api/admin/projects/' + projectId, { headers: { Authorization: 'Bearer ' + token, 'If-Match': '"' + current.version + '"' }, data: current }); assert.equal(response.status(), 200);
  await button('Entwurf speichern').click(); await waitText('Dieser Stand wurde zwischenzeitlich geändert.'); assert.equal(await page.getByRole('textbox', { name: 'Titel EN', exact: true }).inputValue(), 'Unsaved retained');
  // Expired/invalid authentication response, with input preservation through real sign-in.
  await page.route('**/api/admin/projects/' + projectId, route => route.request().method() === 'PUT' ? route.fulfill({ status: 401, contentType: 'application/json', body: '{"code":"unauthorized"}' }) : route.continue());
  await button('Entwurf speichern').click(); await waitText('Deine Anmeldung ist abgelaufen.'); await page.unroute('**/api/admin/projects/' + projectId); await login();
  assert.equal(await page.getByRole('textbox', { name: 'Titel EN', exact: true }).inputValue(), 'Unsaved retained');
  // Data-router browser navigation guard.
  let navigationWarning = false; page.once('dialog', async d => { navigationWarning = true; await d.dismiss(); });
  await page.reload({ timeout: 3000 }).catch(() => {}); assert.equal(navigationWarning, true); assert.equal(await page.getByRole('textbox', { name: 'Titel EN', exact: true }).inputValue(), 'Unsaved retained');
  page.once('dialog', d => d.accept()); await button('Zur Liste').click();
  await page.getByRole('navigation').getByRole('button', { name: 'Startseitenteaser', exact: true }).click(); await button('Auswählen').first().click(); await button('Entwurf speichern').click();
  await page.getByRole('navigation').getByRole('button', { name: 'Blogbeiträge', exact: true }).click(); await button('Entwurf anlegen').click(); await fill('URL-Name', blogSlug);
  for (const lang of ['DE', 'EN']) { await button(lang).last().click(); await fill('Titel ' + lang, 'Blog ' + lang); await fill('Kurztext ' + lang, 'Short ' + lang); await fill('Beschreibung / Artikeltext ' + lang, '## Heading\n\n**Text**\n\n<script>alert(1)</script>\n\n[Unsafe](javascript:alert)'); }
  await page.getByLabel('Renovation', { exact: true }).check(); await button('Als Titelbild verwenden').first().click();
  const createBlog = page.waitForResponse(r => new URL(r.url()).pathname === '/api/admin/blog' && r.request().method() === 'POST'); await button('Entwurf speichern').click(); const blogId = (await (await createBlog).json()).id;
  assert.equal(await page.locator('script').filter({ hasText: 'alert(1)' }).count(), 0); assert.equal(await page.locator('a[href^="javascript:"]').count(), 0);
  await button('Geschützte Vorschau').click(); await page.getByRole('dialog').getByRole('heading', { name: 'Heading', exact: true }).waitFor(); await button('Schliessen').click();
  await button('Veröffentlichen').click(); await button('Ja, ausführen').click(); await button('Veröffentlichung zurückziehen').waitFor();
  assert.equal((await context.request.get(api + '/api/blog/' + blogSlug + '?lang=de')).status(), 200);
  await button('Veröffentlichung zurückziehen').click(); await button('Ja, ausführen').click(); await button('Veröffentlichen').waitFor(); assert.equal((await context.request.get(api + '/api/blog/' + blogSlug + '?lang=de')).status(), 404);
  for (const width of [390, 768, 1280]) { await page.setViewportSize({ width, height: 900 }); await clean(); await page.screenshot({ path: path.join(process.env.TEMP || 'C:/Temp', 'basel-admin-' + width + '.png'), fullPage: true }); }
  await page.getByRole('toolbar').getByRole('button', { name: 'EN', exact: true }).click(); await button('Save draft').waitFor(); await clean();
  await page.keyboard.press('Tab'); assert.ok(await page.evaluate(() => document.activeElement !== document.body));
  assert.equal(await page.evaluate(() => Object.keys(sessionStorage).length), 0);
  assert.equal(await page.evaluate(() => Object.keys(localStorage).some(k => /token|password|auth/i.test(k))), false);
  await button('Sign out').click(); await button('Sign in').waitFor(); await page.reload(); await button('DE').first().waitFor();
  assert.deepEqual(errors, []);
  console.log('PASS: real login, project/blog lifecycle, authenticated images, public visibility/withdrawal, conflict and expiry preservation, dirty navigation guard, selection, DE/EN, 390/768/1280, keyboard and memory-only token.');
 } catch (e) { await page.screenshot({ path: path.join(process.env.TEMP || 'C:/Temp', 'basel-admin-failure.png'), fullPage: true }); console.error(String(e)); process.exitCode = 1; }
 finally { await browser.close(); }
})();
