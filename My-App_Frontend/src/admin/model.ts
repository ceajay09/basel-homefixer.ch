export type Lang = 'de' | 'en';
export type Localized = Partial<Record<Lang, string>>;
export type Kind = 'projects' | 'blog';
export interface Content {
  id: string; slug: string; status: string; version?: number; publishedAt?: string;
  title: Localized; excerpt: Localized; description: Localized; coverAlt: Localized;
  categories: string[]; mediaIds: string[]; coverImageId?: string;
  location?: Localized; challenge?: Localized; result?: Localized;
  tags?: Partial<Record<Lang, string[]>>; dates?: string[];
  duration?: { min: number; max: number; unit: string };
  investmentChf?: number; areaM2?: number; apartments?: number; thermostats?: number; doors?: number; complexity?: number;
}
export interface Media { id: string; alt: Localized; version?: number; variants: Record<string, { width: number; height: number }> }
export interface Page<T> { items: T[]; page: number; size: number; totalElements: number }
export interface Selection { projectIds: string[]; version?: number }
export interface Preview { content: { duration?: { min: number; max: number; unit: 'months' | 'weeks' }; areaM2?: number; investmentChf?: number; apartments?: number; doors?: number; thermostats?: number; complexity?: number; title: string | Localized; description: string | Localized; location?: string; challenge?: string; result?: string; dates?: string[]; tags?: string[]; image?: { id: string; alt: string } }; status: string; version: number }
export const blank = (): Content => ({ id: '', slug: '', status: 'draft', title: {}, excerpt: {}, description: {}, coverAlt: {}, categories: [], mediaIds: [], location: {}, challenge: {}, result: {}, tags: {}, dates: [] });
export function publicationMissing(c: Content, kind: Kind, featured = false): string[] {
  const missing: string[] = [];
  for (const field of [...['title', 'excerpt', 'description', 'coverAlt'], ...(kind === 'projects' ? ['location', ...(featured ? ['challenge', 'result'] : [])] : [])])
    for (const lang of ['de', 'en'] as const) if (!(c[field as keyof Content] as Localized)?.[lang]?.trim()) missing.push(field + ' ' + lang.toUpperCase());
  if (!/^[a-z0-9][a-z0-9-]{0,119}$/.test(c.slug)) missing.push('slug');
  if (!c.coverImageId) missing.push('cover');
  if (!c.categories.length) missing.push('categories');
  if (kind === 'projects' && !c.dates?.length) missing.push('dates');
  return missing;
}
export function editable(c: Content) {
  const { id, version, status, publishedAt, ...rest } = c;
  if (rest.tags) rest.tags = Object.fromEntries(Object.entries(rest.tags).map(([lang, tags]) => [lang, tags?.map(s => s.trim()).filter(Boolean)]));
  return rest;
}
