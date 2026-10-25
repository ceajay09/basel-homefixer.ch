const base = (import.meta.env.DEV ? '' : import.meta.env.VITE_API_URL || '').replace(/\/$/, '');
export class ApiError extends Error {
  constructor(public status: number, public code: string) { super(code); }
}
export async function request<T>(path: string, token?: string, method = 'GET', body?: unknown, version?: number): Promise<T> {
  if (!/^\/api\/(admin(?:\/|$)|login$|logout$|content\/categories\?)/.test(path)) throw new Error('Invalid API route');
  const abort = new AbortController(); const timer = setTimeout(() => abort.abort(), 30000);
  const headers: Record<string, string> = {};
  if (token) headers.Authorization = 'Bearer ' + token;
  if (version !== undefined) headers['If-Match'] = '"' + version + '"';
  const multipart = body instanceof FormData;
  if (body !== undefined && !multipart) headers['Content-Type'] = 'application/json';
  try {
    const response = await fetch(base + path, { method, headers, body: body === undefined ? undefined : multipart ? body : JSON.stringify(body), signal: abort.signal, credentials: 'omit', cache: 'no-store' });
    if (!response.ok) { const error = await response.json().catch(() => ({})); throw new ApiError(response.status, error.code || 'request_failed'); }
    return await response.json();
  } finally { clearTimeout(timer); }
}
export async function imageBlob(id: string, token: string, signal: AbortSignal, large = false) {
  if (!/^[a-f0-9-]{36}$/.test(id)) throw new Error('Invalid media ID');
  const r = await fetch(base + '/api/admin/media/' + id + (large ? '/large' : '/small'), { headers: { Authorization: 'Bearer ' + token }, signal, credentials: 'omit', cache: 'no-store' });
  if (!r.ok) throw new ApiError(r.status, 'image_unavailable');
  if (!r.headers.get('Content-Type')?.startsWith('image/jpeg')) throw new Error('Invalid image');
  return r.blob();
}
