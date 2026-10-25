import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
import { createRequire } from 'node:module';
import { fileURLToPath } from 'node:url';
const dir=path.dirname(fileURLToPath(import.meta.url));const repo=path.resolve(dir,'../..');
const yaml=createRequire(path.join(repo,'My-App_Frontend/package.json'))('js-yaml');
const fixture={BACKEND_IMAGE:'471112833916.dkr.ecr.eu-central-1.amazonaws.com/basel-homefixer-backend@sha256:'+'0'.repeat(64),FRONTEND_IMAGE:'471112833916.dkr.ecr.eu-central-1.amazonaws.com/basel-homefixer-frontend@sha256:'+'0'.repeat(64),OPS_RELEASE_DIR:'/volume1/docker/basel-homefixer/ops/releases/validation-only',NAS_CONTENT_ROOT:'/volume1/docker/basel-homefixer/content',MONGODB_URI:'mongodb://fixture:fixture@127.0.0.1:27017/basel_homefixer?authSource=admin',AUTH_JWT_SECRET_BASE64:'AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=',EDGE_TRUSTED_IPS:'192.168.178.38/32',VERIFY_MONGODB_URI:'mongodb://fixture:fixture@127.0.0.1:27017/basel_homefixer_releasecheck?authSource=admin',VERIFY_AUTH_JWT_SECRET_BASE64:'BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=',VERIFY_CONTENT_ROOT:'/volume1/docker/basel-homefixer-releasecheck/content'};
function resolve(value){
 if(typeof value==='string')return value.replace(/\$\{([A-Z_][A-Z0-9_]*)(?:(:\?|:-)([^}]*))?\}/g,(_,key,mode,fallback)=>{if(fixture[key])return fixture[key];if(mode===':-')return fallback;throw Error('Missing fixture variable '+key);});
 if(Array.isArray(value))return value.map(resolve);
 if(value&&typeof value==='object')return Object.fromEntries(Object.entries(value).map(([key,item])=>[key,resolve(item)]));
 return value;
}
function render(name){return resolve(yaml.load(fs.readFileSync(path.join(dir,name),'utf8')));}
const compose=render('compose.mvp.yaml');const verify=render('compose.verify.yaml');const portainer=render('compose.portainer.yaml');
assert.deepEqual(Object.keys(portainer.services),['backend','frontend','traefik']);
assert.deepEqual(portainer.services.backend.env_file,['stack.env']);
assert.equal(portainer.services.backend.environment.SPRING_DATA_MONGODB_DATABASE,'basel_homefixer');
assert.equal(portainer.services.backend.ports,undefined);
assert.equal(portainer.services.frontend.environment,undefined);
assert.ok(portainer.services.backend.volumes[0].source.startsWith('/volume1/docker/basel-homefixer/'));
assert.equal(portainer.services.backend.volumes[0].bind.create_host_path,false);
assert.equal(portainer.services.traefik.ports.length,2);
assert.ok(!portainer.services.traefik.command.some(v=>v.includes('10.0.0.0/8')));
assert.ok(!JSON.stringify([compose,verify]).includes('${'), 'Every test variable must be resolved');
assert.equal(compose.name,'basel-homefixer_portainer');assert.equal(compose.services.backend.environment.SPRING_DATA_MONGODB_DATABASE,'basel_homefixer');
assert.equal(compose.services.backend.ports,undefined);assert.equal(compose.services.frontend.ports,undefined);
assert.equal(compose.services.frontend.environment,undefined);assert.equal(compose.services.backend.user,'10001:10001');
assert.ok(compose.services.backend.volumes.every(v=>!v.source.startsWith('/volume1/webdata/')));
assert.equal(compose.services.backend.environment.AUTH_PUBLIC_REGISTRATION_ENABLED,'false');
assert.equal(compose.services.backend.environment.CONTACT_MAIL_ENABLED,'false');
for(const service of ['admin-setup','content-import']){assert.deepEqual(compose.services[service].profiles,['tools']);assert.equal(compose.services[service].labels['traefik.enable'],'false');assert.ok(!compose.services[service].command.includes('--apply'));}
assert.equal(verify.services.backend.environment.SPRING_DATA_MONGODB_DATABASE,'basel_homefixer_releasecheck');assert.equal(verify.services.backend.environment.CONTACT_MAIL_ENABLED,'false');assert.equal(verify.services.frontend.labels['traefik.enable'],'false');assert.ok(verify.services.frontend.ports[0].startsWith('127.0.0.1:'));
const transfer=JSON.parse(fs.readFileSync(path.join(dir,'transfer/review.json'),'utf8'));assert.equal(transfer.targetDatabase,'basel_homefixer');assert.equal(transfer.initialStatus,'draft');assert.equal(transfer.publishApproved,false);assert.equal(transfer.accountsFromPreview,false);
assert.ok(!Object.keys(compose.services.backend.environment).some(k=>/AWS_|DB_PASS|DB_USER|DB_INSTANCE/.test(k)));

// The simple CI path and the optional isolated path must serve the same site.
assert.equal(fs.readFileSync(path.join(repo,'My-App_Frontend/nginx.conf'),'utf8').trim(),fs.readFileSync(path.join(dir,'nginx.production.conf'),'utf8').trim());
for(const [name,job] of [['build-maven_basel-homefixer-Backend.yml','build-jar'],['React-Build-basel-homefixer-Frontend.yml','build-react']]){
 const workflow=yaml.load(fs.readFileSync(path.join(repo,'.github/workflows',name),'utf8'));
 const publish=workflow.jobs['push-to-aws'];
 assert.equal(publish.if,"github.event_name == 'push' && github.ref == 'refs/heads/main'");
 assert.equal(publish.needs,job);
 assert.deepEqual(workflow.on.pull_request.types,['opened','synchronize','reopened']);
 const run=publish.steps.find(s=>s.name==='Build, tag, and push docker image to Amazon ECR').run;
 assert.ok(run.includes('COMMIT_TAG="sha-$GITHUB_SHA"')&&run.includes('docker push "$IMAGE:$COMMIT_TAG"')&&run.includes('docker push "$IMAGE:$IMAGE_TAG"'));
 if(job==='build-jar'){
  const artifact=workflow.jobs[job].steps.find(s=>s.name==='Archive Backend Jar');
  assert.equal(artifact.with.path.trim(),'My-App_Backend/target/release/app.jar');
  assert.equal(publish.steps.find(s=>s.name==='Download jar from build-jar job').with.path,'My-App_Backend/target/release');
 }
}
if(process.argv.includes('--portainer-stdin')){delete portainer.services.backend.env_file;process.stdout.write(yaml.dump(portainer,{noRefs:true,lineWidth:-1}));}
else if(process.argv.includes('--compose-stdin'))process.stdout.write(yaml.dump(compose,{noRefs:true,lineWidth:-1}));
else if(process.argv.includes('--verify-stdin'))process.stdout.write(yaml.dump(verify,{noRefs:true,lineWidth:-1}));
else console.log('PASS: production/staging YAML and deployment safety contracts; test substitutions only. No real credentials, Docker, DB or NAS operations.');

