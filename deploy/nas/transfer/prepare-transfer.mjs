import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import { fileURLToPath } from 'node:url';
const dir=path.dirname(fileURLToPath(import.meta.url));const repo=path.resolve(dir,'../../..');
const out=process.argv[2];if(!out||fs.existsSync(out))throw Error('Supply a NEW output directory; no overwrite/delete');
const review=JSON.parse(fs.readFileSync(path.join(dir,'review.json'),'utf8'));
if(review.targetDatabase!=='basel_homefixer'||review.initialStatus!=='draft'||review.publishApproved!==false||review.accountsFromPreview!==false)throw Error('Review policy changed; inspect before use');
const selected={version:1,projects:[],blogs:[],media:{}};
for(const [type,source] of [['projects','projects.json'],['blogs','blogs.json']]){
 const input=JSON.parse(fs.readFileSync(path.join(repo,'My-App_Backend/content-import',source),'utf8'));
 for(const id of review[type]){const c=input[type].find(c=>c.id===id);if(!c)throw Error('Unknown selected content ID');
  if(c.status!=='draft'||c.publishedAt!==null)throw Error('Only source drafts without publication dates');
  selected[type].push(c);for(const alias of [c.coverImageId,...c.mediaIds]){if(!input.media[alias])throw Error('Missing media');selected.media[alias]=input.media[alias];}
 }
}
fs.mkdirSync(path.join(out,'assets'),{recursive:true});
const root=fs.realpathSync(path.join(repo,'design/Kundenpaket_20261024/Kundenpaket/assets'));const checks=[];
for(const source of Object.values(selected.media)){
 if(path.basename(source.filename)!==source.filename)throw Error('Non-flat source path');const original=fs.realpathSync(path.join(root,source.filename));if(path.dirname(original)!==root)throw Error('Source escapes customer assets');
 const bytes=fs.readFileSync(original);fs.writeFileSync(path.join(out,'assets',source.filename),bytes,{flag:'wx'});checks.push({filename:source.filename,sha256:crypto.createHash('sha256').update(bytes).digest('hex')});
}
fs.writeFileSync(path.join(out,'reviewed.json'),JSON.stringify(selected,null,2)+'\n');
checks.push({filename:'reviewed.json',sha256:crypto.createHash('sha256').update(fs.readFileSync(path.join(out,'reviewed.json'))).digest('hex')});
fs.writeFileSync(path.join(out,'sha256.json'),JSON.stringify(checks,null,2)+'\n');
console.log('Prepared '+selected.projects.length+' projects, '+selected.blogs.length+' blogs and '+Object.keys(selected.media).length+' selected media sources. All draft; no DB connection.');
