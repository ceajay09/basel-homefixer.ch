import { serviceIcons, ServiceId } from './serviceCatalog';

export interface ContactRequest { name: string; email: string; phone: string; service: ServiceId | ''; message: string; consent: boolean; website: string; }
export type ContactField = keyof ContactRequest;
export type ContactErrors = Partial<Record<ContactField, 'required' | 'length' | 'email' | 'phone' | 'service' | 'consent' | 'server'>>;
export function serviceFromQuery(value: string | null): ServiceId | '' {
  return value && Object.prototype.hasOwnProperty.call(serviceIcons, value) ? value as ServiceId : '';
}
export function validateContact(data: ContactRequest): ContactErrors {
  const errors: ContactErrors = {};
  if (!data.name.trim()) errors.name = 'required';
  else if (data.name.trim().length < 2 || data.name.length > 100 || /[\r\n]/.test(data.name)) errors.name = 'length';
  if (!data.email.trim()) errors.email = 'required';
  else if (data.email.length > 254 || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(data.email.trim())) errors.email = 'email';
  if (data.phone.length > 40 || !/^[+0-9() .-]*$/.test(data.phone)) errors.phone = 'phone';
  if (data.service && !Object.prototype.hasOwnProperty.call(serviceIcons, data.service)) errors.service = 'service';
  if (!data.message.trim()) errors.message = 'required';
  else if (data.message.trim().length < 10 || data.message.length > 5000) errors.message = 'length';
  if (!data.consent) errors.consent = 'consent';
  return errors;
}
const base = (import.meta.env.VITE_API_URL || '').replace(/\/$/, '');
async function request(path: string, options: RequestInit, timeout: number) {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), timeout);
  try { return await fetch(base + path, { ...options, signal: controller.signal, cache: 'no-store', credentials: 'omit' }); }
  finally { clearTimeout(timer); }
}
export async function getContactAvailability(): Promise<boolean> {
  try {
    const response = await request('/api/contact/status', {}, 10000);
    return response.ok && (await response.json()).available === true;
  } catch { return false; }
}
export async function submitContactRequest(data: ContactRequest): Promise<{ code: string; fieldErrors?: Record<string, string> }> {
  try {
    const response = await request('/api/contact', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data) }, 30000);
    const result = await response.json();
    if (response.ok && result.code === 'accepted') return { code: 'accepted' };
    if ([400, 413].includes(response.status)) return { code: 'invalid_request', fieldErrors: result.fieldErrors };
    if (response.status === 429) return { code: 'rate_limited' };
    if (response.status === 503) return { code: 'unavailable' };
    return { code: 'send_failed' };
  } catch { return { code: 'network_error' }; }
}
