import { describe, expect, it } from 'vitest';
import { blogDate, localized, validMediaId } from './blogApi';
describe('public blog values', () => {
  it('uses supplied translations with a safe language fallback', () => { expect(localized({ de: 'Deutsch', en: 'English' }, 'en')).toBe('English'); expect(localized({ de: 'Deutsch' }, 'en')).toBe('Deutsch'); expect(localized(undefined, 'de')).toBe(''); });
  it('never fabricates publication dates', () => { expect(blogDate(undefined, 'de')).toBe(''); expect(blogDate('invalid', 'en')).toBe(''); expect(blogDate('2026-10-25T12:00:00Z', 'de')).toContain('2026'); });
  it('only recognises server media IDs, never paths or query tokens', () => { expect(validMediaId('12345678-1234-1234-1234-123456789abc')).toBe(true); for (const id of ['../image', 'https://elsewhere.invalid/image', '12345678-1234-1234-1234-123456789abc?token=x']) expect(validMediaId(id)).toBe(false); });
});
