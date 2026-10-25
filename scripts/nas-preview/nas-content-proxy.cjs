// Local-only, read-only transport to the NAS preview; no database credentials are used here.
const http = require('node:http');
const { spawn } = require('node:child_process');
const [key, portText = '8081'] = process.argv.slice(2);
const port = Number(portText);
if (!key || !Number.isInteger(port) || port < 1024 || port > 65535) throw Error('Invalid proxy configuration');
const origins = new Set(['http://127.0.0.1:5180', 'http://localhost:5180', 'http://127.0.0.1:5173', 'http://localhost:5173']);
let active = 0;
const waiting = [];
function schedule(job) {
  if (active >= 6) { if (waiting.length >= 24) return false; waiting.push(job); }
  else { active++; job(); }
  return true;
}
function release() { active--; const next = waiting.shift(); if (next) { active++; next(); } }
function fail(response, status = 502) {
  if (response.headersSent) response.destroy();
  else { response.writeHead(status, { 'Content-Type': 'application/json', 'Cache-Control': 'no-store' }); response.end('{"error":"NAS preview unavailable"}'); }
}
const server = http.createServer((request, response) => {
  if (![`${'127.0.0.1'}:${port}`, `localhost:${port}`].includes(request.headers.host)) return fail(response, 403);
  const origin = request.headers.origin;
  if (origin && !origins.has(origin)) return fail(response, 403);
  if (origin) { response.setHeader('Access-Control-Allow-Origin', origin); response.setHeader('Vary', 'Origin'); }
  if (request.url === '/_nas-preview/health' && request.method === 'GET') {
    response.writeHead(200, { 'Content-Type': 'application/json' });
    return response.end('{"mode":"nas-content-preview","transport":"ssh-exec"}');
  }
  if (!/^\/api\/(projects|blog|media|content)(?:[/?]|$)/.test(request.url || '') || request.url.length > 4096) return fail(response, 404);
  if (request.method === 'OPTIONS') {
    if (!['GET', 'HEAD'].includes(request.headers['access-control-request-method'])) return fail(response, 405);
    response.writeHead(204, { 'Access-Control-Allow-Methods': 'GET, HEAD', 'Access-Control-Allow-Headers': 'Accept, Content-Type' });
    return response.end();
  }
  if (!['GET', 'HEAD'].includes(request.method)) return fail(response, 405);
  const accepted = schedule(() => {
    if (response.destroyed) { release(); return; }
    const payload = Buffer.from(JSON.stringify({ path: request.url, method: request.method })).toString('base64');
    const child = spawn('ssh.exe', ['-T', '-i', key, '-p', '22022', '-o', 'BatchMode=yes', '-o', 'StrictHostKeyChecking=yes', '-o', 'ConnectTimeout=10',
      'c.jaquiery@gundelinas.tailad725b.ts.net', 'python3 /volume1/docker/basel-homefixer-preview/request.py ' + payload], { windowsHide: true, stdio: ['ignore', 'pipe', 'ignore'] });
    let pending = Buffer.alloc(0), receivedHeaders = false, completed = false;
    const timer = setTimeout(() => child.kill(), 25000);
    response.on('close', () => { if (!completed) child.kill(); });
    child.stdout.on('data', chunk => {
      if (!receivedHeaders) {
        pending = Buffer.concat([pending, chunk]);
        const newline = pending.indexOf(10);
        if (newline < 0) { if (pending.length > 8192) child.kill(); return; }
        try {
          const metadata = JSON.parse(pending.subarray(0, newline).toString('utf8'));
          if (!Number.isInteger(metadata.status) || metadata.status < 100 || metadata.status > 599) throw Error('Invalid status');
          response.writeHead(metadata.status, metadata.headers);
          receivedHeaders = true;
          chunk = pending.subarray(newline + 1);
          pending = Buffer.alloc(0);
        } catch { child.kill(); return; }
      }
      if (!response.write(chunk)) { child.stdout.pause(); response.once('drain', () => child.stdout.resume()); }
    });
    child.on('error', () => fail(response));
    child.on('close', code => {
      clearTimeout(timer); completed = true;
      if (code !== 0 || !receivedHeaders) fail(response); else response.end();
      release();
    });
  });
  if (!accepted) fail(response, 503);
});
server.requestTimeout = 30000;
server.listen(port, '127.0.0.1', () => console.log(`Private NAS content proxy ready on 127.0.0.1:${port}`));
