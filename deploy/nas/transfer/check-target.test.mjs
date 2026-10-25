import fs from 'node:fs';import vm from 'node:vm';import assert from 'node:assert/strict';import path from 'node:path';import {fileURLToPath} from 'node:url';import {createRequire} from 'node:module';
const dir=path.dirname(fileURLToPath(import.meta.url));const source=fs.readFileSync(path.join(dir,'check-target.js'),'utf8');
const manifest={projects:[{id:'kitchen',slug:'kitchen',status:'draft',publishedAt:null}],blogs:[]};
function run(conflict){const output=[];let queries=0;const mongo=class{constructor(uri){assert.equal(uri,'fixture-only');}getDB(name){assert.equal(name,'basel_homefixer');return{getCollection(collection){assert.equal(collection,'projects');return{findOne(query,projection){queries++;assert.ok(query.$or);assert.deepEqual(Object.keys(projection).sort(),['_id','slug','status']);return conflict?{status:'draft'}:null;}};}};}};
 const context={require(name){assert.equal(name,'fs');return{readFileSync(){return JSON.stringify(manifest);}};},process:{env:{CONTENT_CHECK_MONGO_URI:'fixture-only',TRANSFER_MANIFEST:'fixture.json'}},Mongo:mongo,print:line=>output.push(line),quit(code){throw Object.assign(new Error('fixture exit'),{fixtureExit:true,code});}};
 let exit=0;try{vm.runInNewContext(source,context);}catch(e){if(!e.fixtureExit)throw e;exit=e.code;}
 assert.equal(queries,1);return{exit,output};}
assert.equal(run(false).exit,0);assert.equal(run(true).exit,2);console.log('PASS: target preflight uses only basel_homefixer read queries and blocks existing IDs/slugs; no DB connection.');
