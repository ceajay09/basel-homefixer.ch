/* Read-only preflight. mongosh --nodb --quiet --file check-target.js. No credentials in argv. */
try {
  const fs = require('fs');
  const manifest = JSON.parse(fs.readFileSync(process.env.TRANSFER_MANIFEST, 'utf8'));
  if (!process.env.CONTENT_CHECK_MONGO_URI || [...manifest.projects, ...manifest.blogs].some(c => c.status !== 'draft' || c.publishedAt !== null)) throw new Error('Invalid explicit target or manifest');
  const database = new Mongo(process.env.CONTENT_CHECK_MONGO_URI).getDB('basel_homefixer');
  const matches = [];
  for (const [collection, contents] of [['projects', manifest.projects], ['blogpost', manifest.blogs]]) {
    for (const item of contents) {
      const existing = database.getCollection(collection).findOne({ $or: [{ _id: item.id }, { slug: item.slug }] }, { _id: 1, slug: 1, status: 1 });
      if (existing) matches.push({ collection, selectedId: item.id, existingStatus: existing.status || 'legacy' });
    }
  }
  print(JSON.stringify({ database: 'basel_homefixer', selectedProjects: manifest.projects.length, selectedBlogs: manifest.blogs.length, existingMatches: matches }));
  if (matches.length) { print('REVIEW_REQUIRED: existing IDs/slugs; do not overwrite or publish automatically.'); quit(2); }
  print('READ_ONLY_PREFLIGHT_OK: no selected ID/slug conflicts. Apply still repeats full content/media preflight.');
} catch (failure) {
  if (failure && failure.fixtureExit) throw failure;
  print('READ_ONLY_PREFLIGHT_FAILED: check private connection configuration and explicit manifest; no raw error details printed.'); quit(3);
}
