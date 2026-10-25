import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { ProjectMetadata } from './projectCatalog';
const base = (import.meta.env.VITE_API_URL || '').replace(/\/$/, '');
export interface ContentPage { items: ProjectMetadata[]; page: number; size: number; totalElements: number; totalPages: number; }
export interface ContentCategory { id: string; label: string; }
async function get(path: string, signal: AbortSignal) {
  const response = await fetch(base + path, { signal, cache: 'no-store', credentials: 'omit' });
  if (!response.ok) throw new Error('Content unavailable');
  return response.json();
}
function project(raw: any): ProjectMetadata {
  const media = raw.image;
  return { ...raw, dates: raw.dates || [], tags: raw.tags || [], image: media ? {
    src: base + media.largeUrl, srcSet: (media.smallWidth === media.width ? '' : base + media.smallUrl + ' ' + media.smallWidth + 'w, ') + base + media.largeUrl + ' ' + media.width + 'w',
    width: media.width, height: media.height, alt: media.alt,
  } : null };
}
export async function getProject(id: string, lang: string, signal: AbortSignal): Promise<ProjectMetadata> {
  return project(await get('/api/projects/by-id/' + encodeURIComponent(id) + '?lang=' + lang, signal));
}
export function useProjectContent(featured = false, category = 'all', page = 0) {
  const { i18n } = useTranslation();
  const lang = i18n.language.startsWith('de') ? 'de' : 'en';
  const [retry, setRetry] = useState(0);
  const [state, setState] = useState<{ loading: boolean; error: boolean; data: ContentPage; categories: ContentCategory[] }>({ loading: true, error: false, data: { items: [], page: 0, size: 12, totalElements: 0, totalPages: 0 }, categories: [] });
  useEffect(() => {
    const abort = new AbortController(); const timeout = setTimeout(() => abort.abort(), 10000); let disposed = false;
    setState(current => ({ ...current, loading: true, error: false, data: { ...current.data, items: [] } }));
    const query = '?lang=' + lang + (featured ? '' : '&page=' + page + '&size=12' + (category === 'all' ? '' : '&category=' + encodeURIComponent(category)));
    Promise.all([get('/api/projects' + (featured ? '/featured' : '') + query, abort.signal), featured ? Promise.resolve([]) : get('/api/content/categories?type=projects&lang=' + lang, abort.signal)])
      .then(([data, categories]) => { if (!abort.signal.aborted) setState({ loading: false, error: false, data: { ...data, items: data.items.map(project) }, categories }); })
      .catch(() => { if (!disposed) setState(current => ({ ...current, loading: false, error: true })); })
      .finally(() => clearTimeout(timeout));
    return () => { disposed = true; clearTimeout(timeout); abort.abort(); };
  }, [lang, featured, category, page, retry]);
  return { ...state, lang, reload: () => setRetry(value => value + 1) };
}
