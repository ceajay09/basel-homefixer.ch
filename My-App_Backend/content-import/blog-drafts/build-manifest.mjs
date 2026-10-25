// Local, reviewable manifest generation only. Does not connect to MongoDB or write media.
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
const dir = path.dirname(fileURLToPath(import.meta.url));
const references = JSON.parse(fs.readFileSync(path.join(dir, '../projects.json'), 'utf8'));
const records = JSON.parse(fs.readFileSync(path.join(dir, 'articles.json'), 'utf8'));
const media = {}; const blogs = [];
for (const { id, slug, categories, media: source, title, excerpt } of records) {
  if (!/^[a-z0-9-]+$/.test(id) || !references.media[source]) throw new Error('Invalid draft metadata');
  const description = Object.fromEntries(['de', 'en'].map(lang => [lang, fs.readFileSync(path.join(dir, id + '.' + lang + '.md'), 'utf8')]));
  media[source] = references.media[source];
  blogs.push({ id, slug, title, excerpt, description, categories, coverImageId: source, coverAlt: references.media[source].alt, mediaIds: [], status: 'draft', publishedAt: null });
}
fs.writeFileSync(path.join(dir, '../blogs.json'), JSON.stringify({ version: 1, projects: [], blogs, media }, null, 2) + '\n');
console.log('Prepared ' + blogs.length + ' bilingual drafts. No database connection, media writes or publication.');
