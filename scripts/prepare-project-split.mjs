// Einmalige Vorbereitung. Überschreibt weder ein vorhandenes Ziel noch Kundendateien.
import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import { execFileSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';
const source = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const target = path.join(source, '.project-split', 'demo.cesarjaquiery.ch');
if (fs.existsSync(path.join(path.dirname(source), 'demo.cesarjaquiery.ch'))) throw new Error('Die eigenständige Demo existiert bereits. Vorbereitung wird nicht wiederholt.');
if (fs.existsSync(target)) throw new Error('Vorbereitetes Ziel existiert bereits; keine Dateien überschrieben.');
const read = (name) => fs.readFileSync(path.join(target,name),'utf8');
const write = (name, text) => { fs.mkdirSync(path.dirname(path.join(target,name)),{recursive:true}); fs.writeFileSync(path.join(target,name),text,'utf8'); };
const json = (name,value) => write(name,JSON.stringify(value,null,2)+'\n');
const replace = (name,from,to) => write(name,read(name).replaceAll(from,to));
const listed = execFileSync('git',['ls-files','-z','--cached','--others','--exclude-standard'],{cwd:source}).toString('utf8').split('\0').filter(Boolean);
const excluded = name => /(^|\/)(\.git|\.idea|node_modules|target|dist|build|design|\.project-split)(\/|$)/.test(name) || /\.pdf$|\.preview$|^scripts\/(create-design-vorlagen|prepare-project-split)\.mjs$|Type conflict|^My-App_Frontend\/pc\.login/.test(name) || /(^|\/)(\.env[^/]*|application-dev\.properties|environment-variables\.env)$/.test(name);
const files=[...new Set(listed)].filter(name=>!excluded(name)&&fs.statSync(path.join(source,name)).isFile());
const workflowHashes={};
for(const name of files){fs.mkdirSync(path.dirname(path.join(target,name)),{recursive:true});fs.copyFileSync(path.join(source,name),path.join(target,name));if(name.startsWith('.github/workflows/'))workflowHashes[name]=crypto.createHash('sha256').update(fs.readFileSync(path.join(source,name))).digest('hex');}

// Nur in der Demo andere Workflows/Ziele. Basel-Homefixer-Workflows bleiben bytegleich.
for(const [oldName,newName,oldRepo,newRepo] of [
 ['React-Build-basel-homefixer-Frontend.yml','React-Build-demo-cesarjaquiery-Frontend.yml','basel-homefixer-frontend','demo-website-frontend'],
 ['build-maven_basel-homefixer-Backend.yml','build-maven_demo-cesarjaquiery-Backend.yml','basel-homefixer-backend','demo-webiste-backend']
]){
 const oldFile='.github/workflows/'+oldName,newFile='.github/workflows/'+newName;
 write(newFile,read(oldFile).replaceAll(oldName,newName).replaceAll(oldRepo,newRepo));
 fs.unlinkSync(path.join(target,oldFile));
}
replace('.github/workflows/React-Build-demo-cesarjaquiery-Frontend.yml','REACT_APP_API_URL','VITE_API_URL');
for(const file of ['My-App_Backend/src/main/java/com/example/myproject/config/SecurityConfig.java','My-App_Backend/src/main/java/com/example/myproject/config/WebConfig.java','My-App_Backend/src/main/java/com/example/myproject/controller/RequestHandler.java']){
 replace(file,'https://basel-homefixer.ch','https://demo.cesarjaquiery.ch');
 replace(file,'localhost:5173','localhost:5174');
}
write('My-App_Backend/src/main/resources/application.properties',`# Eigene Demo-Datenbank. Keine automatische Verbindung zum Kundencluster.
spring.data.mongodb.uri=\${DEMO_MONGODB_URI}
spring.data.mongodb.database=\${DEMO_MONGODB_DATABASE:demo_website}
logging.level.org.springframework.web=\${SPRING_LOG_LEVEL:INFO}
logging.level.org.springframework.security=\${SPRING_LOG_LEVEL:INFO}
logging.level.org.springframework.data.mongodb=\${SPRING_LOG_LEVEL:INFO}
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=never
server.port=8081
app.content.homepage-config-path=\${HOMEPAGE_CONFIG_PATH:classpath:homepage.json}
`);
json('My-App_Backend/src/main/resources/homepage.json',{instagram:{title:'Instagram',postUrl:''}});
json('My-App_Backend/src/main/resources/META-INF/additional-spring-configuration-metadata.json',{properties:[]});
write('My-App_Backend/Dockerfile','FROM amazoncorretto:17-alpine3.16\nMAINTAINER c.jaquiery\nRUN apk --no-cache add curl\nCOPY target/my-app*.jar /my-app.jar\nENV SPRING_LOG_LEVEL=INFO\nEXPOSE 8081/tcp\nENTRYPOINT ["java", "-jar", "/my-app.jar"]\n');
replace('My-App_Frontend/Dockerfile','EXPOSE 80','EXPOSE 8082');
json('My-App_Backend/src/main/resources/mywebapp.blogpost.json',[{description:{de:'# Willkommen in der Demo\n\nDies ist ein neutraler Beispielbeitrag für die React-/Spring-Boot-Demo.',en:'# Welcome to the demo\n\nThis is a neutral sample post for the React/Spring Boot demo.'},_class:'com.example.myproject.model.Blogpost'}]);
write('My-App_Backend/.env.demo.example',`# Als .env.demo kopieren und mit einer separaten Demo-MongoDB ausfüllen.
# Keine Zugangsdaten oder Verbindungszeichenfolge des Kundenprojekts verwenden.
DEMO_MONGODB_URI=
DEMO_MONGODB_DATABASE=demo_website
SPRING_LOG_LEVEL=INFO
HOMEPAGE_CONFIG_PATH=classpath:homepage.json
`);
write('My-App_Backend/src/main/resources/application-dev.properties.example',`# Optional für nativen Java-Start: nach application-dev.properties kopieren.
# DEMO_MONGODB_URI separat als Umgebungsvariable setzen. .env.demo wird nur von Compose gelesen.
spring.data.mongodb.uri=\${DEMO_MONGODB_URI}
spring.data.mongodb.database=\${DEMO_MONGODB_DATABASE:demo_website}
`);
write('My-App_Frontend/.env.development.example','VITE_API_URL=http://localhost:8083\n');
write('My-App_Frontend/.env.production.example','# NAS: API und Frontend liegen unter derselben Domain.\nVITE_API_URL=\n');
replace('My-App_Frontend/vite.config.ts','port: 5173','port: 5174');
replace('docker/traefik.local.yaml','"5173"','"5174"');
replace('compose.local.yaml','demo-website-local','demo-cesarjaquiery-local');
replace('compose.local.yaml','127.0.0.1:8081:8081','127.0.0.1:8083:8081');
replace('compose.local.yaml','127.0.0.1:80:80','127.0.0.1:8084:80');
replace('compose.local.yaml','    environment:\n','    env_file:\n      - ./My-App_Backend/.env.demo\n    environment:\n');
// Bestehende Dateien können CRLF verwenden.
if(!read('compose.local.yaml').includes('env_file:')) replace('compose.local.yaml','    environment:\r\n','    env_file:\r\n      - ./My-App_Backend/.env.demo\r\n    environment:\r\n');
replace('compose.local.yaml','SPRING_PROFILES_ACTIVE: dev','SPRING_PROFILES_ACTIVE: default');
for(const file of ['start-frontend.bat','.vscode/launch.json']) replace(file,'localhost:5173','localhost:5174');
replace('start-frontend.bat','http://localhost via Traefik','http://localhost:8084 via Traefik');
replace('start-backend.bat','My-App_Backend\\src\\main\\resources\\application-dev.properties','My-App_Backend\\.env.demo');
replace('start-backend.bat','http://localhost:8081','http://localhost:8083');

// Die bestehenden Inhaltsgruppen bleiben erhalten, die Kundentexte werden ersetzt.
for(const lang of ['de','en']){
 const de=lang==='de';const t=JSON.parse(read(`My-App_Frontend/public/locales/${lang}/${lang}.json`));delete t._unused;
 t.layout={headerTitle:'demo.cesarjaquiery.ch',sections:{home:de?'Startseite':'Home',aboutUs:de?'Über die Demo':'About the demo',services:de?'Technik':'Technology',realEstateForRent:de?'Beispiele':'Examples',contactUs:de?'Kontakt':'Contact'},footer:{title:de?'Demo-Projekt':'Demo project',description:de?'Beispielwebsite mit React und Spring Boot.':'Sample website with React and Spring Boot.'},socialLinks:{github:'GitHub',linkedin:'Website'}};
 t.aboutUs={title:de?'Über die Demo':'About the demo',contactDetails:de?'Projektinformationen':'Project information',qualification:de?'Basis:':'Foundation:',qualificationDetail:'React, TypeScript, Material UI, Spring Boot',basedIn:de?'Website:':'Website:',basedInDetail:'demo.cesarjaquiery.ch',hello:de?'Eine wiederverwendbare Website-Basis.':'A reusable website foundation.',techJourney:de?'Aufbau':'Structure',techJourneyDetail:de?'Dieses neutrale Demo-Projekt zeigt das gemeinsame Layout, mehrere Seiten, Mehrsprachigkeit und eine getrennte Backend-Anbindung.':'This neutral demo demonstrates a shared layout, multiple pages, multiple languages and a separate backend.',keySkills:de?'Bausteine':'Building blocks',keySkillsDetail:'React, Material UI, React Router, i18next, Spring Boot, MongoDB',continuousLearning:de?'Weiterentwicklung':'Development',continuousLearningDetail:de?'Inhalte und Gestaltung können pro Website angepasst werden.':'Content and styling can be adapted for each website.',careerGoals:de?'Ziel':'Goal',careerGoalsDetail:de?'Eine einfache und einheitliche Grundlage für weitere Projekte.':'A simple and consistent foundation for future projects.',interests:de?'Schwerpunkte':'Focus',interestList:de?'Frontend, Backend, Mehrsprachigkeit, Container':'Frontend, Backend, Languages, Containers',projects:de?'Beispiele':'Examples',bachelorThesis:de?'Frontend-Beispiel':'Frontend example',bachelorThesisDetail:de?'Responsives Layout mit wiederverwendbaren Komponenten.':'Responsive layout with reusable components.',bachelorThesisTasks:de?'Navigation, Karten, Übersetzungen':'Navigation, Cards, Translations',practicalProject:de?'Backend-Beispiel':'Backend example',practicalProjectDetail:de?'Eine separate Demo-Datenbank ist vorbereitet und muss noch konfiguriert werden.':'A separate demo database is prepared and still needs configuration.',practicalProjectTasks:'REST API, MongoDB, Docker',moreDetails:de?'Weitere Informationen':'More information'};
 t.blog={mainFeaturedPost:{title:de?'Demo-Beiträge':'Demo posts',linkText:de?'Mehr erfahren':'Learn more',description:de?'Beispielinhalte der Demo.':'Demo sample content.',imageText:de?'Beispielbild':'Sample image'},featuredPost1:{title:de?'Ein Beispielprojekt':'A sample project',date:de?'Demo':'Demo',description:de?'Hier kann ein eigener Beitrag stehen.':'Your own post can go here.',imageLabel:de?'Beispiel':'Example'},featuredPost2:{title:de?'Ein weiteres Beispiel':'Another example',date:'Demo',description:de?'Weitere Beispielinhalte.':'More sample content.',imageLabel:de?'Beispiel':'Example'},sidebar:{title:'Website-Demo',description:de?'React und Spring Boot als wiederverwendbare Projektbasis.':'React and Spring Boot as a reusable project foundation.'},leftBox:{title:de?'Beispielbilder':'Sample images'},middleBox:{title:de?'Externer Inhalt':'External content'},rightBox:{topText:de?'Technik':'Technology',bottomText:de?'Kontakt':'Contact'},introText:de?'Willkommen auf demo.cesarjaquiery.ch – einer neutralen Website-Vorlage.':'Welcome to demo.cesarjaquiery.ch – a neutral website template.',outroText:de?'Ein gemeinsames Layout, mehrere Seiten und zwei Sprachen.':'A shared layout, multiple pages and two languages.'};
 const neutral = value => typeof value==='string'?value.replaceAll('Basel Home Fixer','die Demo').replaceAll('basel-homefixer','demo.cesarjaquiery.ch'):Array.isArray(value)?value.map(neutral):Object.fromEntries(Object.entries(value).map(([k,v])=>[k,neutral(v)]));
 t.services=neutral(t.services);t.services.introduction.text=de?'Diese Seite erklärt den technischen Aufbau der Demo-Website.':'This page explains the technical structure of the demo website.';
 t.services.awsInfrastructure.title=de?'Betrieb auf Synology':'Hosting on Synology';t.services.awsInfrastructure.text=de?'Für den Betrieb auf einem Synology NAS steht eine separate Compose-Vorlage bereit. Container-Images werden über GitHub Actions nach Amazon ECR übertragen.':'A separate Compose template is provided for Synology NAS hosting. GitHub Actions publish container images to Amazon ECR.';
 t.realEstateForRent={title:de?'Beispielseite':'Example page',purpose:{title:de?'Zweck':'Purpose',text:de?'Diese Seite zeigt ausklappbare Inhaltsbereiche der Demo.':'This page demonstrates expandable demo content.'},contactInformation:{title:de?'Projekt':'Project',owner:de?'Name:':'Name:',ownerName:'Website-Demo',email:'Website:',emailAddress:'demo.cesarjaquiery.ch'},disclaimer:{title:de?'Hinweis':'Notice',text:de?'Dies ist eine Demo mit Beispielinhalten.':'This is a demo with sample content.'}};
 t.featuredPost={linkText:de?'Mehr erfahren':'Learn more'};
 json(`My-App_Frontend/public/locales/${lang}/${lang}.json`,t);
}
const layout='My-App_Frontend/src/home/Layout.tsx';
replace(layout,"title: 'Contact Us'","title: t('layout.sections.contactUs')");
write(layout,read(layout).replace(/url: 'https:\/\/www\.airbnb\.ch\/[^']*'/g,"url: '/realEstateForRent'"));
for(const file of ['My-App_Frontend/src/home/Layout.tsx','My-App_Frontend/src/Layout.tsx']){
 write(file,read(file).replace(/https:\/\/linkedin\.com\/in\/[^'"\s]*/g,'https://cesarjaquiery.ch'));
 if(file.endsWith('/src/Layout.tsx'))write(file,read(file).replace(/https:\/\/www\.airbnb\.ch\/[^']*/g,'/realEstateForRent'));
}
const homeFile='My-App_Frontend/src/home/Home.tsx';replace(homeFile,'href="#services"','href="/services"');replace(homeFile,'href="#contact"','href="/contactUs"');replace(homeFile,'No Instagram post configured.','Demo: no external content configured.');
write('My-App_Frontend/src/home/AboutUs.tsx',`import { Box, Container, Grid, Paper, Typography, Accordion, AccordionSummary, AccordionDetails } from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { useTranslation } from 'react-i18next';
import Layout from './Layout';

export default function AboutUs() {
 const { t } = useTranslation();
 return <Layout><Container maxWidth="lg"><Typography variant="h3" align="center" sx={{pt:2,mb:3}}>{t('aboutUs.title')}</Typography><Grid container spacing={3}>
 <Grid size={{xs:12,md:5}}><Paper elevation={4} sx={{p:2,mb:2}}><Box component="img" src="/assets/demo-profile.svg" alt="Demo" sx={{width:'100%'}}/></Paper><Paper elevation={4} sx={{p:2}}><Typography variant="h6">{t('aboutUs.contactDetails')}</Typography><Typography>{t('aboutUs.qualificationDetail')}</Typography><Typography>demo.cesarjaquiery.ch</Typography></Paper></Grid>
 <Grid size={{xs:12,md:7}}><Paper elevation={4} sx={{p:2}}><Typography variant="h6" sx={{mb:2}}>{t('aboutUs.hello')}</Typography>{['techJourney','keySkills','continuousLearning','careerGoals'].map(key=><Accordion key={key}><AccordionSummary expandIcon={<ExpandMoreIcon/>}>{t('aboutUs.'+key)}</AccordionSummary><AccordionDetails><Typography>{t('aboutUs.'+key+'Detail')}</Typography></AccordionDetails></Accordion>)}</Paper></Grid>
 </Grid></Container></Layout>;
}
`);
write('My-App_Frontend/public/assets/demo-profile.svg','<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 400"><rect width="600" height="400" rx="16" fill="#eef2f6"/><rect x="80" y="80" width="440" height="220" rx="12" fill="white" stroke="#1976d2" stroke-width="3"/><text x="300" y="180" text-anchor="middle" font-family="Arial" font-size="32" fill="#1976d2">Website-Demo</text><text x="300" y="230" text-anchor="middle" font-family="Arial" font-size="20" fill="#455a64">React + Spring Boot</text></svg>');
write('My-App_Frontend/src/home/ContactUs.tsx',`import { Container, Paper, Typography, Link } from '@mui/material';
import { useTranslation } from 'react-i18next';
import Layout from './Layout';
export default function ContactUs(){const {t,i18n}=useTranslation();const de=i18n.language.startsWith('de');return <Layout><Container maxWidth="lg"><Typography variant="h3" align="center" sx={{pt:2,mb:3}}>{t('layout.sections.contactUs')}</Typography><Paper elevation={4} sx={{p:3}}><Typography>{de?'Diese Demo besitzt kein Kontaktformular und versendet keine Nachrichten.':'This demo has no contact form and does not send messages.'}</Typography><Link href="https://cesarjaquiery.ch">cesarjaquiery.ch</Link></Paper></Container></Layout>;}
`);
write('My-App_Frontend/src/home/Footer.tsx',`import { Box, Container, Typography, Link } from '@mui/material';
interface FooterProps { description: string; title: string; }
export default function Footer({title,description}:FooterProps){return <Box component="footer" sx={{py:6}}><Container maxWidth="lg" sx={{textAlign:'center'}}><Typography variant="h6">{title}</Typography><Typography color="text.secondary">{description}</Typography><Link href="https://cesarjaquiery.ch">cesarjaquiery.ch</Link><Typography variant="body2" color="text.secondary">demo.cesarjaquiery.ch</Typography></Container></Box>;}
`);
replace('My-App_Frontend/index.html','<title>basel-homefixer</title>','<title>demo.cesarjaquiery.ch</title>');replace('My-App_Frontend/index.html','href="/favicon.png"','href="/favicon.ico"');replace('My-App_Frontend/index.html','Web site created using Vite','Neutral React and Spring Boot demo website');
replace('My-App_Frontend/index.html','<!-- <meta name="robots" content="noindex,nofollow" /> -->','<meta name="robots" content="noindex,nofollow" />');
const manifest=JSON.parse(read('My-App_Frontend/public/manifest.webmanifest'));manifest.name='demo.cesarjaquiery.ch';manifest.short_name='Website-Demo';json('My-App_Frontend/public/manifest.webmanifest',manifest);
const npm=JSON.parse(read('My-App_Frontend/package.json'));npm.name='demo-cesarjaquiery-frontend';if(npm.dependencies['frontend-app']==='file:')delete npm.dependencies['frontend-app'];json('My-App_Frontend/package.json',npm);
const lock=JSON.parse(read('My-App_Frontend/package-lock.json'));lock.name=npm.name;if(lock.packages?.['']){lock.packages[''].name=npm.name;if(lock.packages[''].dependencies)delete lock.packages[''].dependencies['frontend-app'];}if(lock.packages)delete lock.packages['node_modules/frontend-app'];json('My-App_Frontend/package-lock.json',lock);
const rootPackage=JSON.parse(read('package.json'));rootPackage.name='demo.cesarjaquiery.ch';json('package.json',rootPackage);
write('.gitignore',read('.gitignore')+'\n# Projekttrennung / lokale Zugangsdaten\n.project-split/\n.env.demo\nstack.env\n.env.nas\n!**/.env.*.example\n');

write('deployment/synology/compose.yaml',`# Separate Demo-Installation. Basel-Homefixer-Stack bleibt unverändert.
name: demo-cesarjaquiery-nas
services:
  backend:
    image: 471112833916.dkr.ecr.eu-central-1.amazonaws.com/demo-webiste-backend:dev
    env_file:
      - stack.env
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.demo-cesarjaquiery-backend.rule=Host(\u0060demo.cesarjaquiery.ch\u0060) && PathPrefix(\u0060/api/\u0060)"
      - "traefik.http.routers.demo-cesarjaquiery-backend.priority=100"
      - "traefik.http.services.demo-cesarjaquiery-backend.loadbalancer.server.port=8081"
      - "traefik.http.routers.demo-cesarjaquiery-backend.entrypoints=web"
    networks: [backend]
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:8081/actuator/health || exit 1"]
      interval: 45s
      timeout: 20s
      retries: 5
  frontend:
    image: 471112833916.dkr.ecr.eu-central-1.amazonaws.com/demo-website-frontend:dev
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.demo-cesarjaquiery-frontend.rule=Host(\u0060demo.cesarjaquiery.ch\u0060)"
      - "traefik.http.routers.demo-cesarjaquiery-frontend.priority=10"
      - "traefik.http.services.demo-cesarjaquiery-frontend.loadbalancer.server.port=8082"
      - "traefik.http.routers.demo-cesarjaquiery-frontend.entrypoints=web"
    networks: [frontend]
    healthcheck:
      test: ["CMD-SHELL", "wget -q --spider http://localhost:8082/ || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3
  traefik:
    image: traefik:v3.6
    command:
      - --providers.docker=true
      - --providers.docker.exposedbydefault=false
      - --entrypoints.web.address=:8095
      - --entrypoints.web.forwardedHeaders.trustedIPs=10.0.0.0/8,172.16.0.0/12,192.168.0.0/16
      - --ping=true
      - --log.level=ERROR
    ports:
      # Vorgeschlagener neuer Host-Port: Verfügbarkeit auf dem NAS noch prüfen.
      - "192.168.178.38:8096:8095"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock:ro
    networks: [frontend, backend]
    healthcheck:
      test: ["CMD", "traefik", "healthcheck", "--ping"]
      interval: 45s
      timeout: 20s
      retries: 5
networks:
  frontend:
    driver: bridge
  backend:
    driver: bridge
`);
write('deployment/synology/stack.env.example',read('My-App_Backend/.env.demo.example'));
write('deployment/synology/README.md',`# Demo auf Synology vorbereiten

Diese Vorlage ist nicht auf dem NAS installiert. Sie verwendet die vorhandenen ECR-Repositories demo-webiste-backend (Schreibweise absichtlich übernommen) und demo-website-frontend im bisherigen Konto und eu-central-1.

1. Einen eigenen Projektordner anlegen, z. B. /volume1/docker/demo-cesarjaquiery. compose.yaml dorthin kopieren.
2. stack.env.example als stack.env ablegen. Eigene Demo-MongoDB erstellen und DEMO_MONGODB_URI sowie DEMO_MONGODB_DATABASE=demo_website eintragen. Keinen Kundenbenutzer/keine Kundendatenbank verwenden. Bis dahin ist das Backend ausdrücklich nicht betriebsbereit.
3. ECR-Anmeldung auf dem NAS für den bisherigen Registry-Host sicherstellen. Die Vorlage enthält keine AWS-Zugangsdaten. Neue Images müssen zuerst vom Demo-GitHub-Repository gebaut worden sein.
4. Verfügbarkeit von 192.168.178.38:8096 prüfen. Der Host-Port ist ein Vorschlag; Basel Homefixer verwendet weiterhin 8095. Compose-Netze sind durch den eigenen Projektnamen getrennt.
5. Im Synology Reverse Proxy die Quelle HTTPS demo.cesarjaquiery.ch:443 mit passendem Zertifikat auf HTTP 192.168.178.38:8096 leiten. Host-Header erhalten. DNS auf denselben NAS-Zugang wie vorgesehen konfigurieren. HTTPS endet dabei am Synology Reverse Proxy, daher kein zweiter 8443-Port im Demo-Stack.
6. Nach vollständiger DB-/Proxy-Konfiguration: docker compose -f compose.yaml config --quiet, dann docker compose -f compose.yaml pull und docker compose -f compose.yaml up -d --force-recreate ausführen. Nicht auf dem laufenden Basel-Homefixer-Projekt ausführen.

Die Backend-API wird über /api/ an Port 8081, das Frontend an Port 8082 geroutet. Der Demo-Stack bindet bewusst keinen Basel-Homefixer-Inhaltsordner ein; homepage.json kommt standardmässig neutral aus dem JAR. Für eigene Inhalte kann HOMEPAGE_CONFIG_PATH später auf einen eigenen gemounteten Pfad gesetzt werden.

GitHub Actions schiebt weiterhin lediglich Images nach ECR. Ein automatisches Update auf dem NAS ist in diesen Workflows nicht eingerichtet. Bei unverändertem Tag dev müssen neue Images explizit gepullt und Container neu erzeugt werden, sofern keine separate bestehende Automatisierung dafür zuständig ist.

Die nicht standardmässige forward-headers-Middleware aus dem übergebenen Stack wurde nicht kopiert; vertrauenswürdige Forwarded-Header werden wie bisher am Traefik-EntryPoint konfiguriert. Am ursprünglichen Stack wurde nichts geändert.
`);
write('My-App_Backend/README.md',`# Demo-Backend

Spring Boot, Java 17, gleicher Maven-Parent ch.cesarjaquiery:shared-backend-parent:1.0.0. Der Parent bleibt unverändert und benötigt lokalen Maven-/GitHub-Packages-Zugriff.

MongoDB ist separat vorbereitet, aber nicht verbunden. .env.demo.example nach .env.demo kopieren und eine eigene Demo-Verbindungszeichenfolge eintragen. compose.local.yaml lädt diese Datei. Für nativen Java-Start DEMO_MONGODB_URI und optional DEMO_MONGODB_DATABASE als Umgebungsvariablen setzen; Spring liest .env.demo nicht selbst. application-dev.properties.example ist eine optionale Vorlage, keine Kundenkonfiguration.

DB_USER/DB_PASS/DB_INSTANCE des ursprünglichen Projekts werden von dieser Demo-Anwendung nicht verwendet. Der feste Atlas-Cluster aus dem Kundenprojekt wurde nicht übernommen. Das Backend kann vor Konfiguration der neuen MongoDB fehlschlagen; dieser Zustand ist beabsichtigt. Keine Tests verbinden sich während der Projekttrennung mit einer Datenbank.

Lokaler Backend-Port: 8083 (Container intern 8081). Einstieg: start-backend.bat im Hauptordner. Für den manuellen Start ohne dev-Profil kann nach Setzen der Variablen mvnw.cmd spring-boot:run verwendet werden.

mywebapp.blogpost.json ist ein neutrales Importbeispiel, kein automatischer Datenbankimport. Persönliche PDFs und Kundeninhalte wurden nicht übernommen.
`);
write('README.md',`# demo.cesarjaquiery.ch

Neutrale Kopie der bisherigen React-/TypeScript-/Material-UI-/Spring-Boot-Basis. Eigenes Repository: https://github.com/ceajay09/demo.cesarjaquiery.ch (remote Einrichtung noch zu bestätigen).

## Lokal

Node 22 verwenden. npm --prefix My-App_Frontend ci, danach npm run start oder start-frontend.bat. Frontend: http://localhost:5174. .env.development.example bei Bedarf als My-App_Frontend/.env.development kopieren, damit die API lokal auf http://localhost:8083 zeigt.

Die Demo kann unabhängig vom Backend angezeigt werden; API-Aufrufe benötigen die noch einzurichtende eigene MongoDB. Für das Backend My-App_Backend/.env.demo.example nach .env.demo kopieren und ausfüllen, Java 17 und den bisherigen Maven-Parent bereitstellen, dann start-backend.bat starten. Backend: http://localhost:8083. Optionaler lokaler Traefik: http://localhost:8084. Andere Ports als Basel Homefixer ermöglichen parallelen Betrieb.

Prüfen: npm run typecheck und npm --prefix My-App_Frontend run build. Das npm-test-Script ist weiterhin kein echter Testlauf.

## GitHub Actions und AWS

Die bestehenden Build-/Push-Abläufe sind in der Demo kopiert, auf eigene Workflow-Namen/Path-Filter und ECR-Ziele angepasst. Region eu-central-1, Tag dev:
- Backend: demo-webiste-backend (genau dieser vorhandene Name)
- Frontend: demo-website-frontend

Im neuen GitHub-Repository AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY und PACKAGES_TOKEN sowie für Dependabot DEPENDABOT_GITHUB_PACKAGES_TOKEN bereitstellen. Berechtigungen müssen die Demo-ECR-Ziele und den gemeinsamen Maven-Parent abdecken. Die Basler Workflows wurden bei der Trennung nicht geändert. Erst nach Prüfung der Demo-Ziele pushen; die Trigger auf main bleiben bestehen.

## NAS

Vorbereitung unter deployment/synology/. Dort stehen Compose-Vorlage, stack.env.example und Schritte für einen eigenen Synology-Reverse-Proxy auf demo.cesarjaquiery.ch. Bestehender Basel-Homefixer-Stack, DNS, Zertifikate, NAS und Datenbanken wurden nicht verändert.

## Abgrenzung

Keine Kundenentwürfe, Kundendokumente, persönlichen PDFs, bestehenden Zugangsdaten, IDE-Metadaten oder ursprüngliche Git-Datenbank mitkopiert. Bestehende Seitenstruktur und Technologien bleiben erhalten. Keine Kunden-Designumsetzung enthalten. Die Demo enthält keine echte Kontaktfunktion und ist mit noindex gekennzeichnet.
`);
fs.mkdirSync(path.join(source,'.project-split'),{recursive:true});
fs.writeFileSync(path.join(source,'.project-split','workflow-original-hashes.json'),JSON.stringify(workflowHashes,null,2)+'\n');
console.log(JSON.stringify({prepared:target,copiedSourceFiles:files.length,originalWorkflows:workflowHashes},null,2));
