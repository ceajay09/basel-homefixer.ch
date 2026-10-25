import { describe, expect, it } from 'vitest';
import { renderToStaticMarkup } from 'react-dom/server';
import SafeMarkdown, { mediaId, safeLink } from '../home/SafeMarkdown';
import { blank, editable, publicationMissing } from './model';
describe('administration content contracts', () => {
  it('omits missing figures rather than filling with zero/null and excludes server fields', () => {
    const doc = { ...blank(), id: 'stable', version: 3, status: 'published', publishedAt: '2026-01-01', areaM2: undefined, tags: { de: ['one', ' ', 'two'] } };
    const json = JSON.parse(JSON.stringify(editable(doc)));
    expect(json).not.toHaveProperty('areaM2'); expect(json).not.toHaveProperty('id'); expect(json).not.toHaveProperty('status'); expect(json).not.toHaveProperty('version'); expect(json).not.toHaveProperty('publishedAt'); expect(json.tags.de).toEqual(['one', 'two']);
  });
  it('identifies both language requirements and only asks for teaser fields for selected projects', () => {
    const c = blank(); c.location = { de: 'Basel', en: 'Basel' };
    expect(publicationMissing(c, 'blog')).toContain('title EN'); expect(publicationMissing(c, 'projects')).not.toContain('challenge DE'); expect(publicationMissing(c, 'projects', true)).toContain('challenge DE');
  });
});
describe('safe reusable Markdown', () => {
  it('keeps formatting but renders embedded HTML as text', () => {
    const html = renderToStaticMarkup(<SafeMarkdown>{'## Title\n\n**Bold**\n\n<script>alert(1)</script><iframe src="https://evil.invalid"></iframe><img src=x onerror="alert(1)">'}</SafeMarkdown>);
    expect(html).toContain('<h2'); expect(html).toContain('<strong>'); expect(html).not.toContain('<script'); expect(html).not.toContain('<iframe'); expect(html).not.toContain('<img src="x"');
  });
  it('rejects active and protocol-relative URLs and external images', () => {
    for (const value of ['javascript:alert(1)', 'data:text/html,x', '//evil.invalid', '/\\evil.invalid', 'file:///secret']) expect(safeLink(value)).toBeUndefined();
    const html = renderToStaticMarkup(<SafeMarkdown>{'[click](javascript:alert)\n\n![photo](https://evil.invalid/pixel)\n\n[site](https://example.org)'}</SafeMarkdown>);
    expect(html).not.toContain('href="javascript:'); expect(html).not.toContain('<img'); expect(html).toContain('href="https://example.org"');
  });
  it('requires a declared image reference and meaningful alt text in every view', () => {
    const id = '12345678-1234-1234-1234-123456789abc';
    const unassigned = renderToStaticMarkup(<SafeMarkdown>{'![Photo](/api/media/' + id + '/large)'}</SafeMarkdown>);
    const blankAlt = renderToStaticMarkup(<SafeMarkdown allowedMediaIds={[id]}>{'![](/api/media/' + id + '/large)'}</SafeMarkdown>);
    const assigned = renderToStaticMarkup(<SafeMarkdown allowedMediaIds={[id]}>{'![Photo](/api/media/' + id + '/large)'}</SafeMarkdown>);
    expect(unassigned).not.toContain('<img'); expect(blankAlt).not.toContain('<img'); expect(assigned).toContain('<img');
  });
  it('only resolves known media routes and supports authenticated rendering', () => {
    const id = '12345678-1234-1234-1234-123456789abc'; expect(mediaId('/api/media/' + id + '/large')).toBe(id);
    expect(mediaId('/api/media/' + id + '/large?token=secret')).toBeUndefined();
    const html = renderToStaticMarkup(<SafeMarkdown allowedMediaIds={[id]} renderImage={(media, alt) => <span>{media}:{alt}</span>}>{'![Kitchen](/api/media/' + id + '/large)'}</SafeMarkdown>);
    expect(html).toContain(id + ':Kitchen'); expect(html).not.toContain('<img');
  });
});
