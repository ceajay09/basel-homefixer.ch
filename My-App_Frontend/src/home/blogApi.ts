import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Localized } from '../admin/model';
export interface BlogImage { id: string; smallWidth: number; width: number; height: number; alt: string }
export interface BlogPost { id: string; slug: string; title: Localized; excerpt: Localized; description?: Localized; categories: string[]; image?: BlogImage; publishedAt?: string; mediaIds?: string[] }
export interface BlogPage { items: BlogPost[]; page: number; size: number; totalElements: number; totalPages: number }
export interface Category { id: string; label: string }
export const blogApiBase = (import.meta.env.VITE_API_URL || '').replace(/\/$/, '');
export const validMediaId = (id = '') => /^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$/.test(id);
export function localized(values: Localized | undefined, lang: 'de' | 'en') { return values?.[lang] || values?.de || values?.en || ''; }
export function blogDate(value: string | undefined, lang: string) {
  if (!value || !Number.isFinite(new Date(value).getTime())) return '';
  return new Intl.DateTimeFormat(lang.startsWith('de') ? 'de-CH' : 'en-GB', { day: 'numeric', month: 'long', year: 'numeric', timeZone: 'Europe/Zurich' }).format(new Date(value));
}
class BlogError extends Error { constructor(public status: number) { super('Blog unavailable'); } }
async function get<T>(path: string, signal: AbortSignal): Promise<T> {
  const r = await fetch(blogApiBase + path, { signal, credentials: 'omit', cache: 'no-store', headers: { Accept: 'application/json' } });
  if (!r.ok) throw new BlogError(r.status);
  return r.json();
}
export function useBlog(slug: string | undefined, category = '', page = 0) {
  const { i18n } = useTranslation(); const lang: 'de' | 'en' = i18n.language.startsWith('de') ? 'de' : 'en';
  const [state, setState] = useState<{ loading: boolean; error: boolean; notFound: boolean; post?: BlogPost; list?: BlogPage; categories: Category[] }>({ loading: true, error: false, notFound: false, categories: [] });
  const [revision, setRevision] = useState(0);
  useEffect(() => {
    const controller = new AbortController(); let active = true; const timer = setTimeout(() => controller.abort(), 10000);
    setState({ loading: true, error: false, notFound: false, categories: [] });
    if (slug !== undefined && !/^[a-z0-9][a-z0-9-]{0,119}$/.test(slug)) { setState({ loading: false, error: false, notFound: true, categories: [] }); clearTimeout(timer); return () => controller.abort(); }
    const path = slug !== undefined ? '/api/blog/' + encodeURIComponent(slug) + '?lang=' + lang : '/api/blog?lang=' + lang + '&page=' + page + '&size=6' + (category ? '&category=' + encodeURIComponent(category) : '');
    Promise.all([get<BlogPost | BlogPage>(path, controller.signal), get<Category[]>('/api/content/categories?type=blog&lang=' + lang, controller.signal)])
      .then(([data, categories]) => { if (active) setState({ loading: false, error: false, notFound: false, categories, ...(slug === undefined ? { list: data as BlogPage } : { post: data as BlogPost }) }); })
      .catch(error => { if (active) setState({ loading: false, error: !(slug !== undefined && error instanceof BlogError && error.status === 404), notFound: slug !== undefined && error instanceof BlogError && error.status === 404, categories: [] }); })
      .finally(() => clearTimeout(timer));
    return () => { active = false; clearTimeout(timer); controller.abort(); };
  }, [slug, category, page, lang, revision]);
  return { ...state, lang, retry: () => setRevision(n => n + 1) };
}
