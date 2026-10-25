// Presentation types and formatters only. Project records come exclusively from the content API.
export interface ProjectMetadata {
  id: string; slug: string; category: string;
  image: { src: string; srcSet: string; width: number; height: number; alt: string; position?: string } | null;
  dates: string[]; duration?: { min: number; max: number; unit: 'months' | 'weeks' };
  investmentChf?: number; areaM2?: number; apartments?: number; thermostats?: number; doors?: number; complexity?: number;
  title: string; description: string; location: string; tags: string[]; challenge: string; result: string;
}
// Customer-supplied marketing claims, not counts derived from the reference records.
export const projectSummary = { completed: '20+', customers: '40+', experienceYears: 16 } as const;
export function projectLocale(language: string) { return language.startsWith('de') ? 'de-CH' : 'en-GB'; }
export function formatProjectInvestment(value: number, language: string) { return 'CHF ' + new Intl.NumberFormat(projectLocale(language)).format(value); }
export function projectDurationOptions(duration: NonNullable<ProjectMetadata['duration']>) { return { range: duration.min === duration.max ? String(duration.min) : duration.min + '–' + duration.max }; }
