import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const out = path.join(root, 'design', 'Kundenpaket');
if (fs.existsSync(out)) throw new Error('Kundenpaket existiert bereits. Zum Schutz bearbeiteter Entwürfe wird es nicht überschrieben.');
fs.mkdirSync(path.join(out, 'assets'), { recursive: true });
const t = JSON.parse(fs.readFileSync(path.join(root, 'My-App_Frontend/public/locales/de/de.json'), 'utf8'));
const esc = value => String(value).replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;');
const p = value => `<p>${esc(value).replaceAll('\n\n', '</p><p>')}</p>`;
const write = (name, content) => fs.writeFileSync(path.join(out, name), content, 'utf8');

const css = `/* DESIGNWERTE: Farben, Schrift, Breite und Abstände hier ändern. Alle Werte sind CSS, keine MUI-Abstandseinheiten. */
:root{--farbe-akzent:#1976d2;--farbe-text:#202124;--farbe-nebentext:#5f6368;--farbe-hintergrund:#fff;--farbe-box:#fff;--farbe-flaeche:#f2f3f5;--farbe-linie:#dadce0;--schrift:Arial,Helvetica,sans-serif;--schrift-basis:16px;--seitenbreite:1152px;--abstand:24px;--box-innenabstand:16px;--rundung:6px;--schatten:0 3px 10px #00000020}
*{box-sizing:border-box}body{margin:0;background:var(--farbe-hintergrund);color:var(--farbe-text);font:var(--schrift-basis)/1.6 var(--schrift)}
a{color:var(--farbe-akzent);text-underline-offset:3px}img{display:block;max-width:100%;height:auto}h1,h2,h3,p{margin-top:0}h1{font-size:clamp(2rem,5vw,3rem);font-weight:400;line-height:1.2;text-align:center;margin-bottom:32px}h2{font-size:1.3rem;font-weight:500;line-height:1.35}h3{font-size:1.05rem;line-height:1.4}p:last-child{margin-bottom:0}
.container{width:min(var(--seitenbreite),calc(100% - 40px));margin-inline:auto}.brand-row{display:flex;align-items:center;gap:16px;min-height:78px;border-bottom:1px solid var(--farbe-linie)}.brand{flex:1;text-align:center;color:inherit;text-decoration:none;font-size:1.5rem}.language{font-size:.85rem;color:var(--farbe-nebentext)}.navigation{display:flex;justify-content:space-between;gap:18px;padding:14px 0;flex-wrap:wrap}.navigation a{color:inherit;text-decoration:none;font-size:.9rem}.navigation a[aria-current=page]{color:var(--farbe-akzent);text-decoration:underline}.navigation a:hover{text-decoration:underline}main{padding-top:24px;padding-bottom:40px}
.box{background:var(--farbe-box);padding:var(--box-innenabstand);border-radius:var(--rundung);box-shadow:var(--schatten);min-width:0}.stack>*+*{margin-top:var(--abstand)}.center{text-align:center}.muted{color:var(--farbe-nebentext)}.grid-three{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:var(--abstand)}.grid-about{display:grid;grid-template-columns:5fr 7fr;gap:var(--abstand)}.grid-content{display:grid;grid-template-columns:2fr 1fr;gap:40px}.square{aspect-ratio:1;display:flex;flex-direction:column}.image-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:8px;min-height:0;flex:1}.image-grid img{width:100%;height:100%;object-fit:cover;border-radius:var(--rundung)}.action-stack{display:flex;flex-direction:column;gap:8px;flex:1}.action{display:flex;align-items:center;justify-content:center;flex:1;background:var(--farbe-flaeche);border-radius:var(--rundung);color:inherit;text-decoration:none;font-size:1.2rem;padding:16px}.action:hover{filter:brightness(.96)}.placeholder{display:grid;place-items:center;text-align:center;padding:20px;border:1px dashed #a7abb3;border-radius:var(--rundung);background:var(--farbe-flaeche);color:var(--farbe-nebentext);flex:1;min-height:150px}.placeholder small{display:block}.portrait{width:100%;aspect-ratio:4/5;object-fit:cover}.button{display:inline-block;border:1px solid var(--farbe-akzent);border-radius:var(--rundung);padding:10px 16px;color:var(--farbe-akzent);text-decoration:none;font:inherit;cursor:pointer;background:transparent}.button:hover{background:var(--farbe-flaeche)}details{border:1px solid var(--farbe-linie);border-radius:var(--rundung);background:var(--farbe-box);margin-bottom:12px}summary{cursor:pointer;padding:16px;font-weight:500}details>div{padding:0 16px 16px}dl{margin:0}dt{font-weight:600}dd{margin:0 0 14px;overflow-wrap:anywhere}.diagram{width:60%;margin:24px auto}.technical{max-width:936px;margin:auto;padding:32px}.technical>h2{text-align:center;font-size:1.75rem}.contact-box{border:2px solid var(--farbe-text)}.site-footer{text-align:center;padding:40px 0;border-top:1px solid var(--farbe-linie)}.footer-links{display:flex;justify-content:center;gap:24px;flex-wrap:wrap}.site-footer small{display:block;color:var(--farbe-nebentext);margin-top:24px}
/* Diese Hinweise gehören zum Vorlagenpaket, nicht zur späteren Website. */
.template-note{background:#eef5ff;color:#243f64;border-bottom:1px solid #cfdef3;padding:10px 20px;font:13px/1.5 Arial,sans-serif}.template-note a{color:inherit}.content-note{border-left:3px solid #bd7b00;background:#fff7e5;padding:12px 16px;font-size:.9rem;margin-bottom:24px}.content-note strong{display:block}.optional{font-size:.85rem;color:var(--farbe-nebentext)}:focus-visible{outline:3px solid var(--farbe-akzent);outline-offset:4px}li{margin-bottom:6px}
/* MOBIL: Reihenfolge entspricht dem HTML. Unter 900px stehen die Hauptspalten untereinander. */
@media(max-width:899px){.grid-three,.grid-about,.grid-content{grid-template-columns:1fr}.square{aspect-ratio:auto;min-height:300px}.image-grid img{aspect-ratio:1;height:auto}.action-stack{min-height:260px}.portrait{max-height:480px;object-fit:contain}.diagram{width:100%}.technical{padding:20px}.brand-row{min-height:68px}.brand{text-align:left;font-size:1.25rem}.navigation{justify-content:flex-start;gap:12px 20px}.container{width:calc(100% - 32px)}}
@media print{.template-note{display:none}.box{box-shadow:none;border:1px solid #ddd}details{break-inside:avoid}}
`;

const pages = [
  { file:'01-startseite.html', title:'Startseite', id:'home', route:' / und /blog', source:'Home.tsx', status:'Bestehende Seitenstruktur; dynamische Inhalte als Platzhalter.' },
  { file:'02-ueber-uns.html', title:'Über uns', id:'about', route:'/aboutUs', source:'AboutUs.tsx', status:'Bestehende Seitenstruktur und deutsche Texte; fehlendes Porträt ersetzt.' },
  { file:'03-dienstleistungen.html', title:'Dienstleistungen', id:'services', route:'/services', source:'Services.tsx', status:'Enthält derzeit technische Website-Informationen. Fachliche Inhalte bitte festlegen.' },
  { file:'04-kontakt.html', title:'Kontakt', id:'contact', route:'/contactUs', source:'ContactUs.tsx', status:'Bisherige Platzhalterseite, für die Vorlage ins Deutsche übertragen.' },
  { file:'05-vermietung.html', title:'Vermietung / lokale Seite', id:'rental', route:'/realEstateForRent', source:'RealEstateForRent.tsx', status:'Lokale Route enthält derzeit Kontaktinformationen; Menü der echten Website führt zu Airbnb.' },
  { file:'06-tech-insights.html', title:'Tech Insights (optional)', id:'insights', route:'/techInsights', source:'TechInsights.tsx', status:'Vorhandene Platzhalterroute, derzeit nicht im Hauptmenü.' },
  { file:'07-aktuelle-beitraege.html', title:'Aktuelle Beiträge (optional)', id:'posts', route:'/latestPosts', source:'LatestPosts.tsx', status:'Vorhandene Platzhalterroute, derzeit nicht im Hauptmenü.' }
];
const navPages = [pages[0],pages[3],pages[1],pages[2],pages[4]];
const sharedHeader = active => `<!-- BEREICH: shared.header | React: Header.tsx + Layout.tsx -->
<header id="shared-header" class="container" data-design-id="shared.header"><div class="brand-row"><a class="brand" href="01-startseite.html">${esc(t.layout.headerTitle)}</a><span class="language">DE / EN · Vorschau DE</span></div>
<nav class="navigation" aria-label="Hauptnavigation">${navPages.map(page=>`<a href="${page.file}"${page.id===active?' aria-current="page"':''}>${page.id==='rental'?'Immobilienvermietung':page.title}</a>`).join('')}</nav></header>`;
const footer = `<!-- BEREICH: shared.footer | React: Footer.tsx -->
<footer id="shared-footer" class="site-footer container" data-design-id="shared.footer"><h2>${esc(t.layout.footer.title)}</h2><p>${esc(t.layout.footer.description)}</p><div class="footer-links"><span>GitHub</span><span>E-Mail: [Adresse ergänzen]</span><span>LinkedIn</span></div><small>Fusszeile: Links, Kontaktdaten und Copyright bitte festlegen.</small></footer>`;
const note = (heading,text) => `<aside class="content-note" role="note"><strong>${esc(heading)}</strong>${esc(text)}</aside>`;
const box = (id,title,body,extra='') => `<!-- BEREICH: ${id} -->\n<section id="${id.replaceAll('.','-')}" data-design-id="${id}" class="box ${extra}">${title?`<h2>${esc(title)}</h2>`:''}${body}</section>`;
const accordion = (id,title,body) => `<details id="${id.replaceAll('.','-')}" data-design-id="${id}"><summary>${esc(title)}</summary><div>${body}</div></details>`;
const img = (src,alt,cls='') => `<img src="assets/${src}" alt="${esc(alt)}"${cls?` class="${cls}"`:''}>`;

const home = `<div class="stack">
${box('home.intro','',p(t.blog.introText),'center')}
<!-- DESKTOP: drei gleich breite Boxen. MOBIL: Arbeit, Instagram, Aktionen untereinander. -->
<div class="grid-three" id="home-main-grid" data-design-id="home.main-grid">
${box('home.featured-work',t.blog.leftBox.title,`<div class="image-grid">${[1,2,3,4].map(n=>img('projekt-platzhalter.svg',`Projektbild ${n} – bitte durch eigenes Foto ersetzen`)).join('')}</div>`,'square center')}
${box('home.instagram',t.blog.middleBox.title,'<div class="placeholder"><div>Instagram-Beitrag<small>Platzhalter für die bestehende Einbettung.<br>Hier Grösse und Position gestalten.</small></div></div>','square center')}
${box('home.actions','',`<div class="action-stack"><a class="action" href="03-dienstleistungen.html">${esc(t.blog.rightBox.topText)}</a><a class="action" href="04-kontakt.html">${esc(t.blog.rightBox.bottomText)}</a></div>`,'square')}
</div>
${box('home.benefits','',p(t.blog.outroText),'center')}
<div class="grid-content" data-design-id="home.lower-grid">
<section id="home-posts" data-design-id="home.posts"><h2>${esc(t.blog.mainFeaturedPost.title)}</h2><hr>${box('home.post-example','Beispiel für einen Blog-Beitrag',p('Hier erscheinen Beiträge aus der bestehenden Website. Dieser Beispieltext dient nur dazu, Schrift, Abstände und Darstellung zu gestalten.'))}</section>
<aside id="home-sidebar" data-design-id="home.sidebar">${box('home.sidebar-profile',t.blog.sidebar.title,p(t.blog.sidebar.description))}<h2 style="margin-top:24px">Social</h2><p>GitHub<br>LinkedIn</p></aside>
</div></div>`;

const about = `<h1>${esc(t.aboutUs.title)}</h1><div class="grid-about"><div class="stack">
${box('about.portrait','',img('portraet-platzhalter.svg','Porträt fehlt im Projekt – eigenes Foto einsetzen','portrait'))}
${box('about.contact',t.aboutUs.contactDetails,`<dl><dt>${esc(t.aboutUs.qualification)}</dt><dd>${esc(t.aboutUs.qualificationDetail)}</dd><dt>${esc(t.aboutUs.age)}</dt><dd>[Alter bestätigen: Code 28, Textdatei 35]</dd><dt>${esc(t.aboutUs.phoneNumber)}</dt><dd><details><summary>Nummer anzeigen</summary><div>[Telefonnummer ergänzen]</div></details></dd><dt>${esc(t.aboutUs.email)}</dt><dd>[E-Mail ergänzen]</dd><dt>${esc(t.aboutUs.basedIn)}</dt><dd>${esc(t.aboutUs.basedInDetail)}</dd></dl>`)}
${box('about.interests',t.aboutUs.interests,`<ul>${t.aboutUs.interestList.split(', ').map(x=>`<li>${esc(x)}</li>`).join('')}</ul>`)}
</div><div class="stack">
${box('about.biography',t.aboutUs.hello,[['journey','techJourney'],['skills','keySkills'],['learning','continuousLearning'],['goals','careerGoals']].map(([id,key])=>accordion(`about.${id}`,t.aboutUs[key],p(t.aboutUs[key+'Detail']))).join(''))}
${box('about.projects',t.aboutUs.projects,[['kitchen','bachelorThesis'],['balcony','practicalProject']].map(([id,key])=>`<article data-design-id="about.project-${id}"><h3>${esc(t.aboutUs[key])}</h3>${p(t.aboutUs[key+'Detail'])}<ul>${t.aboutUs[key+'Tasks'].split(', ').map(x=>`<li>${esc(x)}</li>`).join('')}</ul></article>`).join(''))}
</div></div>`;

const serviceKeys = ['versionControlAndCiCd','awsInfrastructure','frontendDevelopment','backendDevelopment','dockerAndContainerization','continuousMonitoringAndLoadBalancing','repositoryAndCodeStructure','summary'];
const services = `${note('Inhalt noch festlegen','Die bestehende Route heisst Dienstleistungen, beschreibt aber den technischen Aufbau der Website. Du darfst diese Bereiche durch deine tatsächlichen Dienstleistungen ersetzen. Leistungen, Preise und Zusagen bitte selbst bestätigen.')}<h1>${esc(t.services.title)}</h1>
${box('services.content',t.services.introduction.title,p(t.services.introduction.text)+`<h2>${esc(t.services.technologiesAndToolsUsed.title)}</h2><a href="assets/My-App-Overview.drawio.svg">${img('My-App-Overview.drawio.svg',t.services.technologiesAndToolsUsed.overviewImageAlt,'diagram')}</a>`+serviceKeys.map(key=>`<section data-design-id="services.${key}"><h3>${esc(t.services[key].title)}</h3>${key==='awsInfrastructure'?`<a href="assets/My-App-diagrams.drawio.svg">${img('My-App-diagrams.drawio.svg',t.services.technologiesAndToolsUsed.awsDetailImageAlt,'diagram')}</a>`:''}${p(t.services[key].text)}${key==='dockerAndContainerization'?`<ul>${['backendService','frontendService','traefikService'].map(k=>`<li>${esc(t.services[key][k])}</li>`).join('')}</ul>`:''}${key==='repositoryAndCodeStructure'?`<ul>${['config','controllers','services','repositories','models'].map(k=>`<li><strong>${esc(k)}:</strong> ${esc(t.services[key][k])}</li>`).join('')}</ul>`:''}</section>`).join(''),'technical')}`;

const stub = (id,title,optional=false) => `${note(optional?'Optionale Seite – noch nicht im Menü':'Platzhalterseite – Inhalte ergänzen','Die bestehende Seite enthält noch allgemeine englische Vorlagentexte. Unten ist diese Struktur auf Deutsch dargestellt. Bitte die gewünschten Inhalte und bei optionalen Seiten auch „behalten oder weglassen“ angeben.')}<h1>${esc(title)}</h1><div class="stack">
${box(id+'.purpose','Zweck der Seite',p('[Bitte beschreiben, was Besucher auf dieser Seite finden sollen.]'),'contact-box')}
${box(id+'.information','Kontaktinformationen','<dl><dt>Inhaber</dt><dd>[Vorname Nachname]</dd><dt>E-Mail</dt><dd>[E-Mail-Adresse]</dd></dl>','contact-box')}
${box(id+'.disclaimer','Hinweis',p('Die Informationen auf dieser Website werden regelmässig überprüft. Vollständigkeit, Genauigkeit und Aktualität können nicht garantiert werden.'),'contact-box')}
</div>`;
const rental = `${note('Lokale Seite oder externer Link?','Das Menü der bestehenden Website führt direkt zu Airbnb. Die lokale Route /realEstateForRent zeigt dagegen die unten stehenden Kontaktinformationen. Bitte festlegen: Airbnb-Link behalten oder eine eigene Vermietungsseite gestalten. Diese Vorlage gestaltet ausschliesslich die lokale Seite.')}<h1>${esc(t.realEstateForRent.title)}</h1>`+
accordion('rental.purpose',t.realEstateForRent.purpose.title,p(t.realEstateForRent.purpose.text))+
accordion('rental.contact',t.realEstateForRent.contactInformation.title,`<dl><dt>${esc(t.realEstateForRent.contactInformation.owner)}</dt><dd>${esc(t.realEstateForRent.contactInformation.ownerName)}</dd><dt>${esc(t.realEstateForRent.contactInformation.email)}</dt><dd>${esc(t.realEstateForRent.contactInformation.emailAddress)}</dd></dl>`)+
accordion('rental.disclaimer',t.realEstateForRent.disclaimer.title,p(t.realEstateForRent.disclaimer.text));
const contents = [home,about,services,stub('contact','Kontakt'),rental,stub('insights','Tech Insights',true),stub('posts','Aktuelle Beiträge',true)];

pages.forEach((page,i)=>write(page.file,`<!doctype html>
<html lang="de"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><meta name="design-source" content="My-App_Frontend/src/home/${page.source}"><title>${page.title} · Designvorlage</title>
<!-- Selbstständige HTML-Vorlage. CSS ist absichtlich eingebettet, damit eine KI nur diese Datei benötigt. Bilder liegen relativ unter assets/. Keine externen Bibliotheken oder API-Aufrufe. -->
<style>${css}</style></head><body>
<aside class="template-note">Designvorlage · ${page.title} · <a href="index.html">Zur Übersicht</a> · <a href="ANLEITUNG.html">Anleitung</a><br>${esc(page.status)} Diese Hinweisleiste gehört nicht zum Website-Design.</aside>
${sharedHeader(page.id)}
<main class="container" id="${page.id}-main" data-design-id="${page.id}.main">${contents[i]}</main>
${footer}
</body></html>`));

for (const name of ['My-App-Overview.drawio.svg','My-App-diagrams.drawio.svg']) fs.copyFileSync(path.join(root,'My-App_Frontend/public/assets',name),path.join(out,'assets',name));
for (const [name,label,ratio] of [['projekt-platzhalter.svg','Dein Projektfoto','600 600'],['portraet-platzhalter.svg','Dein Porträt','600 750']]) {
  const [w,h]=ratio.split(' ').map(Number);
  write('assets/'+name,`<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${w} ${h}" width="${w}" height="${h}"><rect width="${w}" height="${h}" fill="#edf0f4"/><rect x="50" y="50" width="${w-100}" height="${h-100}" rx="10" fill="none" stroke="#b4becb" stroke-width="2" stroke-dasharray="8 8"/><path d="M140 ${h*.6} L260 ${h*.4} L350 ${h*.52} L440 ${h*.34}" fill="none" stroke="#9aaabe" stroke-width="8"/><text x="${w/2}" y="${h*.8}" text-anchor="middle" fill="#4f6278" font-family="Arial,sans-serif" font-size="28">${label}</text></svg>`);
}

const guideCss = `${css}\nbody{background:#f5f6f8}.guide{max-width:1040px;margin:40px auto;padding:0 24px}.guide h1{text-align:left;font-size:clamp(2rem,4vw,3rem);font-weight:600}.eyebrow{font-size:.75rem;letter-spacing:.12em;text-transform:uppercase;color:#52647a;font-weight:bold}.guide-card{background:#fff;border:1px solid #dde2e9;border-radius:12px;padding:24px;margin:20px 0}.guide-card h2{font-size:1.35rem}.step-number{display:inline-grid;place-items:center;background:#1976d2;color:white;border-radius:50%;width:32px;height:32px;margin-right:10px}.guide pre,.guide blockquote{white-space:pre-wrap;overflow-wrap:anywhere;background:#f3f5f8;padding:20px;border-radius:8px;margin:16px 0}.guide code{font-family:Consolas,monospace;font-size:.9em}.guide li{margin-bottom:10px}.page-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:16px}.page-card{display:block;padding:20px;border:1px solid #dde2e9;border-radius:10px;color:inherit;text-decoration:none;background:white}.page-card:hover{border-color:#1976d2}.page-card strong{display:block;font-size:1.1rem;margin-bottom:6px}.page-card small{display:block;color:#5f6368}.preview-controls{display:flex;gap:12px;flex-wrap:wrap;align-items:center;margin:16px 0}.preview-controls select{font:inherit;max-width:100%;padding:10px;border:1px solid #bbb;border-radius:6px}.preview-stage{overflow:auto;background:#e5e9ef;border-radius:10px;padding:12px}.preview-stage iframe{display:block;width:100%;height:740px;border:0;background:#fff;margin-inline:auto}.checklist{padding-left:22px}table{width:100%;border-collapse:collapse;font-size:.9rem}td,th{text-align:left;border-bottom:1px solid #ddd;padding:12px;vertical-align:top}button[aria-pressed=true]{background:#1976d2;color:#fff}.toplinks{display:flex;gap:20px;flex-wrap:wrap;margin-bottom:24px}@media(max-width:650px){.page-grid{grid-template-columns:1fr}.guide{padding:0 16px;margin-top:24px}.guide-card{padding:18px}.preview-stage iframe{height:600px}}`;
const shell = (title,content,script='') => `<!doctype html><html lang="de"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>${title} · Basel Home Fixer</title><style>${guideCss}</style></head><body><main class="guide">${content}</main>${script}</body></html>`;

const prompt = `Ich möchte das Design meiner Website überarbeiten. Im Anhang ist eine HTML-Designvorlage der betreffenden Seite.

Meine Wünsche:
- Stil / Wirkung: [z. B. freundlich, hochwertig, übersichtlich]
- Farben: [Wunsch oder „bitte vorschlagen“]
- Schrift: [Wunsch oder „bitte vorschlagen“]
- Anordnung / Bilder / Abstände: [meine Wünsche]
- Texte: [behalten oder gewünschte Änderungen]
- Auf dem Handy: [Wünsche oder „sinnvoll untereinander anordnen“]

Bitte bearbeite die angehängte HTML-Datei und gib mir die VOLLSTÄNDIGE geänderte Datei als herunterladbare .html-Datei zurück, keine einzelnen Codeausschnitte.
Wenn du keine Datei bereitstellen kannst, gib den vollständigen HTML-Code in genau einem Codeblock aus und erkläre kurz, wie ich ihn als .html speichere.

Vorgaben:
1. Die Datei muss sich ohne Installation direkt im Browser öffnen lassen. CSS bleibt in der HTML-Datei. Keine Frameworks, externen Schriften, CDNs, API-Aufrufe oder Build-Schritte hinzufügen.
2. Erhalte Dateinamen, lokale Navigationslinks und die Zuordnung über data-design-id nach Möglichkeit. Neue Bereiche dürfen eigene eindeutige IDs bekommen. Benenne entfernte oder neu hinzugefügte Bereiche in deiner Antwort.
3. Farben, Schrift, Abstände, Rundungen und Breiten möglichst über die vorhandenen CSS-Variablen am Dateianfang einstellen. Anordnung mit Grid oder Flexbox statt fester Bildschirmkoordinaten umsetzen.
4. Desktop und Handy berücksichtigen. Auf 390px Bildschirmbreite sollen Texte und Bilder lesbar sein und die Seite nicht seitlich überlaufen.
5. Vorhandene Bilder liegen im Unterordner assets/. Nutze relative Pfade wie assets/kueche.jpg. Liefere zusätzliche Bilddateien mit oder kennzeichne sie als noch zu ersetzende Platzhalter.
6. Texte, Preise, Qualifikationen und Kontaktangaben nur ändern oder ergänzen, wenn ich diese angebe. Fehlende Angaben bleiben deutlich als Platzhalter erkennbar.
7. Die Hinweisleiste mit „Designvorlage“ und inhaltliche Statushinweise sind nur Arbeitshilfen. Behalte sie erkennbar getrennt vom eigentlichen Website-Design. Instagram, Sprache und dynamische Funktionen bleiben Vorschau/Platzhalter.
8. Teile mir kurz mit, ob du gemeinsame Elemente wie Navigation, Fusszeile, Farben oder Schrift geändert hast. Ich entscheide, ob dies für alle Seiten gelten soll.

Die Vorlage ist eine Designreferenz. Der Entwickler überträgt sie später auf die bestehende React-/Material-UI-Website.
`;
write('KI-AUFTRAG.txt',prompt);

const instructions = `<nav class="toplinks"><a href="index.html">← Seitenübersicht</a><a href="KI-AUFTRAG.txt" download>KI-Auftrag herunterladen</a></nav><p class="eyebrow">Anleitung · ohne Programmierkenntnisse</p><h1>So gestaltest du deine Website.</h1><p>Du bearbeitest eine Vorschau mit deiner KI. Du musst keinen Code verstehen und nichts installieren. Dein Entwickler übernimmt anschliessend die Umsetzung in die echte Website.</p>
<section class="guide-card"><h2><span class="step-number">1</span>Paket öffnen</h2><ol><li>Die ZIP-Datei zuerst vollständig <strong>entpacken</strong> (Windows: Rechtsklick → „Alle extrahieren“). Nicht direkt innerhalb der ZIP arbeiten.</li><li>Im entpackten Ordner <strong>index.html doppelklicken</strong>. Die Übersicht öffnet sich im Browser.</li><li>Eine Seite auswählen und anschauen. Mit „Handy“ siehst du eine schmale Vorschau.</li><li>Den entpackten Ordner einmal als Sicherung kopieren, bevor du Änderungen speicherst.</li></ol></section>
<section class="guide-card"><h2><span class="step-number">2</span>Mit deiner KI gestalten</h2><ol><li>Starte einen Chat mit deiner KI und hänge die gewünschte HTML-Datei an, zum Beispiel <strong>01-startseite.html</strong>. Falls deine KI keine HTML-Anhänge unterstützt, öffne die Datei in einem Texteditor und kopiere den ganzen Inhalt in den Chat.</li><li>Öffne <strong>KI-AUFTRAG.txt</strong>, kopiere den Text in den Chat und ersetze die Angaben in eckigen Klammern durch deine Wünsche.</li><li>Hänge eigene Bilder zusätzlich an. Sage der KI deren Dateinamen, zum Beispiel <code>kueche.jpg</code>, und lege dieselben Dateien in <strong>assets</strong> ab.</li><li>Lass dir die vollständige geänderte HTML-Datei geben. Du kannst mehrere Vorschläge ausprobieren; arbeite für die nächste Runde mit der jeweils neuesten Datei.</li></ol><p><strong>Beispiel für einen Änderungswunsch:</strong></p><blockquote>Die Startseite soll wärmer und ruhiger wirken. Verwende einen hellen Hintergrund und dunkelgrüne Akzente. Die Projektbilder sollen grösser sein. Auf dem Handy: zuerst Projekte, dann Dienstleistungen und Kontakt, danach Instagram. Die Texte bitte beibehalten.</blockquote><p><a class="button" href="KI-AUFTRAG.txt" download>Vorbereiteten KI-Auftrag herunterladen</a></p></section>
<section class="guide-card"><h2><span class="step-number">3</span>Ergebnis speichern und prüfen</h2><ol><li>Speichere die neue Datei im <strong>entpackten Paketordner neben index.html</strong>, unter demselben Dateinamen. Deine Sicherung bleibt im anderen Ordner. Eine heruntergeladene Datei eventuell aus „Downloads“ hierher verschieben.</li><li>Falls die KI nur Code liefert: Editor/Notepad öffnen → vollständigen Code einfügen → „Speichern unter“ → Dateityp <strong>Alle Dateien</strong>, Codierung <strong>UTF-8</strong>, Dateiname etwa <strong>01-startseite.html</strong>. Die Datei darf nicht auf <code>.html.txt</code> enden.</li><li>Öffne die gespeicherte HTML-Datei im Browser. Nach weiteren Änderungen die Seite neu laden (Strg+R). Falls die Übersicht noch den alten Stand zeigt, auch sie neu laden.</li><li>Prüfe grosse und schmale Ansicht, Texte, Bildausschnitte und Navigation. Die Handyansicht in der Übersicht ist eine Vorschau, kein vollständiger Gerätetest.</li><li>Für weitere Änderungen sende deiner KI die <strong>neueste HTML-Datei</strong>. Beschreibe konkret, was du noch ändern möchtest.</li></ol><p><strong>Wichtig:</strong> Die Vorschau speichert beim Anschauen nichts. Die Buttons „Desktop“ und „Handy“ ändern nur die Anzeige. Speichern geschieht über die von deiner KI gelieferte Datei.</p></section>
<section class="guide-card"><h2><span class="step-number">4</span>Deinen Entwurf zurücksenden</h2><ol><li>Öffne <strong>WUENSCHE.md</strong> in einem Texteditor. Fülle nur die passenden Zeilen aus: Was ist dir wichtig, was ist flexibel, welche Angaben fehlen?</li><li>Gemeinsame Gestaltung zuerst an der Startseite entscheiden. Gib an, ob Farben, Schrift, Navigation und Fusszeile für <strong>alle Seiten</strong> gelten sollen. Du musst diese Änderungen nicht selbst in jede Datei kopieren.</li><li>Prüfe: Liegen alle verwendeten Bilder im Ordner <strong>assets</strong>? Sind die gewünschten HTML-Dateien aktuell? Optional Screenshots in <strong>screenshots</strong> ablegen.</li><li>Den <strong>gesamten Paketordner als ZIP</strong> komprimieren und deinem Entwickler senden. Bitte genau einen ausgewählten Stand schicken. Die ZIP kann beispielsweise <code>Website-Entwurf-2026-10-20.zip</code> heissen.</li></ol><p>Der Entwickler setzt den Entwurf um und gibt dir einen Vorschaulink zur echten Website. Dort bestätigst du die Umsetzung oder beschreibst Abweichungen.</p></section>
<section class="guide-card"><h2>Was liegt wo?</h2><pre>Website-Designvorlagen/  ← entpackter Paketordner
  index.html           ← hier starten: Seitenübersicht + Vorschau
  ANLEITUNG.html       ← diese Anleitung
  KI-AUFTRAG.txt       ← in deine KI kopieren
  WUENSCHE.md          ← kurze Notizen für den Entwickler
  01-startseite.html
  02-ueber-uns.html
  03-dienstleistungen.html
  04-kontakt.html
  05-vermietung.html
  06-tech-insights.html       ← optional
  07-aktuelle-beitraege.html  ← optional
  assets/              ← deine Bilder und Logos
  screenshots/         ← optional: Bildschirmfotos</pre><p>Der Ordnername darf anders heissen. Die Dateien und Unterordner bitte zusammenlassen. <strong>index.html und ANLEITUNG.html sind Arbeitshilfen; die nummerierten Dateien sind deine Website-Entwürfe.</strong></p></section>
<section class="guide-card"><h2>Was du wissen solltest</h2><ul><li>Die Vorlagen bilden den vorhandenen Aufbau vereinfacht nach. Sie sind keine exakten Screenshots der aktuellen Website.</li><li>Manche bestehende Seiten enthalten noch alte oder fehlende Inhalte. Gelbe Hinweise erklären das. Diese Hinweise gehören nicht zum späteren Design.</li><li>„Dienstleistungen“ zeigt derzeit technische Website-Informationen. Du kannst dort mit deiner KI deine tatsächlichen Leistungen darstellen; gib ihr die richtigen Inhalte.</li><li>Die Vermietung führt im echten Menü zu Airbnb. Entscheide, ob das so bleiben oder eine eigene Seite entstehen soll.</li><li>Tech Insights und Aktuelle Beiträge sind optional und derzeit nicht im Hauptmenü. Du kannst beide weglassen.</li><li>Instagram, Sprachwechsel, Kontaktdaten-Anzeige und Blog-Daten sind vereinfacht oder als Platzhalter dargestellt. Die Dateien versenden keine Nachrichten und verbinden sich mit keinem Backend.</li><li>Die Vorschau ist auf Deutsch. Schreibe bei Bedarf in WUENSCHE.md, was für Englisch zusätzlich gewünscht ist.</li><li>Wenn die KI eine völlig andere Seite erzeugt: Lade die letzte passende Datei erneut hoch und bitte um einzelne konkrete Änderungen.</li></ul></section>`;
write('ANLEITUNG.html',shell('Anleitung',instructions));

write('index.html',shell('Designpaket',`<p class="eyebrow">Basel Home Fixer · Designpaket</p><h1>Deine Website. Dein Entwurf.</h1><p>Wähle eine Seite, gestalte sie mit deiner KI und sende deinen ausgewählten Entwurf zurück. Alle Vorlagen funktionieren lokal, ohne Installation.</p><nav class="toplinks"><a class="button" href="ANLEITUNG.html">1. Anleitung lesen</a><a class="button" href="KI-AUFTRAG.txt" download>2. KI-Auftrag herunterladen</a><a class="button" href="WUENSCHE.md" download>3. Wünsche-Datei herunterladen</a></nav>
<section class="guide-card"><h2>Seiten zum Gestalten</h2><div class="page-grid">${pages.map(page=>`<a class="page-card" href="${page.file}"><strong>${page.title}</strong><small>${page.file}</small><small>${esc(page.status)}</small></a>`).join('')}</div></section>
<section class="guide-card"><h2>Desktop und Handy vergleichen</h2><p>Die Vorschau zeigt deine lokal gespeicherten Dateien. Hier wird nichts bearbeitet oder gespeichert.</p><div class="preview-controls"><label for="page-select">Seite</label><select id="page-select">${pages.map(page=>`<option value="${page.file}">${page.title}</option>`).join('')}</select><button class="button" id="desktop" type="button" aria-pressed="true">Desktop</button><button class="button" id="mobile" type="button" aria-pressed="false">Handy · 390px</button><a id="open-page" href="01-startseite.html" target="_blank" rel="noopener">Separat öffnen ↗</a></div><div class="preview-stage"><iframe id="preview" src="01-startseite.html" title="Vorschau der ausgewählten Website-Seite"></iframe></div><p class="optional" id="preview-status" aria-live="polite">Desktop: nutzt die verfügbare Breite. Für eine grosse Ansicht „Separat öffnen“ wählen.</p><noscript><p>Für die Vorschau-Umschaltung ist JavaScript nötig. Alle Seiten lassen sich über die Links oben auch ohne JavaScript öffnen.</p></noscript></section>
<section class="guide-card"><h2>Für die erste Runde reicht eine Seite.</h2><p>Beginne mit der Startseite. Entscheide dort über die allgemeine Wirkung, Farben und Schrift. Übertrage diesen Stil anschliessend mit deiner KI auf weitere Seiten oder bitte den Entwickler, die gemeinsamen Stile zu übernehmen.</p><p><strong>Zurücksendung:</strong> aktueller Paketordner als ZIP, mit deinen HTML-Dateien, Bildern und der ausgefüllten WUENSCHE.md.</p></section>`, `<script>
const select=document.getElementById('page-select'),frame=document.getElementById('preview'),openLink=document.getElementById('open-page');
select.addEventListener('change',()=>{frame.src=select.value;frame.title='Vorschau: '+select.selectedOptions[0].textContent;openLink.href=select.value;});
for(const id of ['desktop','mobile'])document.getElementById(id).addEventListener('click',()=>{const mobile=id==='mobile';frame.style.width=mobile?'390px':'100%';document.getElementById('desktop').setAttribute('aria-pressed',String(!mobile));document.getElementById('mobile').setAttribute('aria-pressed',String(mobile));document.getElementById('preview-status').textContent=mobile?'Handy: 390px breite Vorschau. Auf kleinen Bildschirmen den Vorschau-Bereich seitlich scrollen.':'Desktop: nutzt die verfügbare Breite. Für eine grosse Ansicht „Separat öffnen“ wählen.';});
</script>`));

write('HIER-STARTEN.txt',`DEIN WEBSITE-DESIGNPAKET\n\n1. ZIP vollständig entpacken.\n2. index.html doppelklicken.\n3. Dort „Anleitung lesen“ öffnen.\n\nDu brauchst keine Installation und keine Programmierkenntnisse.\nDie nummerierten HTML-Dateien bearbeitest du mit deiner KI.\nFür die Rücksendung den gesamten Ordner inklusive Bildern als ZIP senden.\n`);
write('WUENSCHE.md',`# Mein ausgewählter Website-Entwurf

Name:
Datum:
Stand: Entwurf / bereit zur Umsetzung (bitte auswählen)

## Gemeinsamer Stil
Massgebliche Datei für Farben, Schrift, Navigation und Fusszeile: 01-startseite.html
Dieser Stil soll für alle Seiten gelten: ja / nein / Ausnahmen:
Was mir am wichtigsten ist:
Was der Entwickler bei Bedarf anpassen darf:
Gewünschte Schrift (oder „ähnliche Schrift ist okay“):
Sprachen: Deutsch und Englisch beibehalten / Änderungswunsch:

${pages.map(page=>`## ${page.title}\nDatei: ${page.file}\nBearbeitet: nein / ja\nBehalten: ja / nein / noch offen\nWichtig am Design:\nVerhalten auf dem Handy:\nTexte, Bilder oder Angaben, die noch fehlen:\n`).join('\n')}
## Noch zu entscheiden
- Dienstleistungen: technische Inhalte behalten oder durch welche Leistungen ersetzen?
- Vermietung: externer Airbnb-Link oder eigene lokale Seite?
- Über uns: korrektes Alter, Telefonnummer, E-Mail und Porträt?
- Fusszeile: gewünschte Links, E-Mail und Copyright?
- Instagram: gewünschter Beitrag / Profil?

## Bilder
Dateiname im Ordner assets | auf welcher Seite / in welchem Bereich | gewünschter Ausschnitt
-

## Neue Funktionen oder Seiten
Hier aufschreiben, wenn etwas zusätzlich funktionieren soll (z. B. Kontaktformular).
Eine Gestaltung in HTML allein setzt diese Funktion noch nicht in der echten Website um.
-
`);
write('assets/BILDER-HIER-ABLEGEN.txt',`Hier eigene Bilder und Logos ablegen.\nEinfache Dateinamen verwenden, z. B. kueche.jpg oder logo.svg.\nDer HTML-Pfad dazu lautet assets/kueche.jpg.\nBeim KI-Chat dieselben Bilder anhängen und ihren Dateinamen angeben.\nDie mitgelieferten Platzhalter kannst du ersetzen.\nDie beiden drawio.svg-Dateien stammen aus der bisherigen technischen Informationsseite.\n`);
fs.mkdirSync(path.join(out,'screenshots'));
write('screenshots/OPTIONAL.txt','Hier kannst du Bildschirmfotos des ausgewählten Designs ablegen. Die HTML-Dateien bleiben die Hauptreferenz.\n');

const manifest = pages.map(page=>({file:page.file,route:page.route.trim(),source:'My-App_Frontend/src/home/'+page.source,status:page.status}));
fs.writeFileSync(path.join(root,'design','seiten-zuordnung.json'),JSON.stringify(manifest,null,2)+'\n');
fs.writeFileSync(path.join(root,'design','ENTWICKLER.md'),`# Designübergabe für dieses Projekt

## Versand
Nur Website-Designvorlagen.zip beziehungsweise den Inhalt von Kundenpaket an den Auftraggeber senden.
Das Paket enthält keine Backend-Konfiguration, Zugangsdaten oder privaten Dokumente.
Kundenpaket/index.html ist der Einstieg; ANLEITUNG.html und KI-AUFTRAG.txt führen durch die Bearbeitung.

## Umfang und Ausgangsbasis
Alle sieben öffentlichen Seitenkomponenten aus App.tsx sind abgedeckt. /blog ist ein Alias der Startseite.
Authentifizierungs-Komponenten sind nicht als Routen in App.tsx eingebunden und deshalb nicht Teil des Pakets.
Statische, vereinfachte Nachbildung anhand des vorhandenen JSX und der deutschen Übersetzungen, keine pixelgenaue Momentaufnahme.
Services.tsx zeigt technische Website-Informationen statt fachlicher Dienstleistungen. RealEstateForRent.tsx zeigt Kontaktinformationen; der aktuelle Menülink ist externes Airbnb.
Kontakt, Tech Insights und Latest Posts enthalten alte englische Platzhalter; die HTML-Vorlagen geben deren Struktur auf Deutsch wieder.
Texte unter _unused wurden nicht als freigegebene Inhalte übernommen. Blog-Daten und Instagram bleiben markierte Vorschau-Platzhalter.
Porträt fehlt in den vorhandenen öffentlichen Assets; Code/Übersetzung nennen widersprüchliches Alter. Vorlagen markieren fehlende Angaben.
Die Menüführung im Paket verbindet lokale Vorlagen. Der Vermietungslink ist ausdrücklich eine Vorschau der lokalen Route, kein stiller Wechsel der echten Navigation.
Die beiden vorhandenen öffentlichen Technik-SVGs werden mitgeliefert. Keine externen Fonts, Bilder oder Bibliotheken nötig.

## Umsetzung nach Rücksendung
1. ZIP in einen separaten Ordner entpacken; ausgewählten Stand und WUENSCHE.md lesen.
2. HTML im Browser visuell prüfen. Die tatsächlichen CSS-Regeln berücksichtigen, nicht nur :root oder Screenshots.
3. Gemeinsame Gestaltung nach home/Layout.tsx (createTheme), Header.tsx und Footer.tsx übertragen. Auf eigene ThemeProvider in Unterseiten achten.
4. Seiten anhand seiten-zuordnung.json und data-design-id zuordnen. Bereiche dürfen vom Kunden hinzugefügt oder entfernt worden sein.
5. Wiederholte Stile zentralisieren. CSS-Pixel nicht direkt als MUI-sx-Abstandszahlen übernehmen: sx p:2 bedeutet standardmässig 16px.
6. Übersetzungen in public/locales/de/de.json und en/en.json pflegen; Sprache und bestehende API-Funktionen erhalten, falls keine Änderung beauftragt ist.
7. Arbeitsleisten (.template-note), gelbe Statushinweise (.content-note), Übersichtsseite und Anleitung nicht in die Website übernehmen. Fehlende Kontaktdaten klären.
8. Neue Funktionen separat umsetzen. Ein dargestelltes Formular oder ein Link in der Vorlage beweist keine funktionierende Integration.
9. Typecheck und Frontend-Build ausführen; echte Seiten in Desktop- und Mobilansicht prüfen. Auftraggeber erhält Vorschaulink zur Abnahme.
10. Für eine spätere Runde eine aktuelle statische Referenz bereitstellen; bestätigte Stände getrennt aufbewahren.

## Prompt für die Coding-KI
Übertrage den freigegebenen Entwurf aus den zurückgesendeten HTML-Dateien auf das bestehende React-/Material-UI-Frontend. Beachte WUENSCHE.md und die Seitenzuordnung. Nutze die vorhandenen Komponenten und Übersetzungen; erhalte bestehende Funktionen. Übernimm die tatsächlichen Layoutregeln, Farben, Typografie, Bildausschnitte und Abstände responsiv. Übertrage Arbeitshilfen und Platzhalterhinweise nicht als Produktinhalt. Prüfe TypeScript, Build und die echte Darstellung auf Desktop und Mobilgeräten. Berichte verbleibende fehlende Angaben.

## Generator
scripts/create-design-vorlagen.mjs erzeugt das Ausgangspaket aus den deutschen Texten und vereinfachten Layouts. Er verweigert absichtlich das Überschreiben eines bestehenden Kundenpakets, damit Kundenentwürfe nicht verloren gehen. Zum Neugenerieren den bisherigen Ordner zunächst separat archivieren. Das ZIP muss nach Änderungen neu gepackt werden.
`,'utf8');
console.log(`Erstellt: ${out} (${pages.length} Seiten, Übersicht, Anleitung und Übergabedateien)`);
