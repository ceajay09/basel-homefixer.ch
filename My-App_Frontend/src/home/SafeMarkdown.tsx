import { ReactNode } from 'react';
import Markdown from 'markdown-to-jsx';
import { Box, Link } from '@mui/material';
export function safeLink(value = ''): string | undefined {
  if (/^https?:\/\//i.test(value) || /^mailto:[^\s<>]+$/i.test(value) || /^\/(?!\/)[^\\\s]*$/.test(value) || /^#[\w-]+$/.test(value)) return value;
}
export const mediaId = (src = '') => /^\/api\/(?:admin\/)?media\/([a-f0-9-]{36})\/(?:small|large)$/.exec(src)?.[1];
/** Shared by administrative previews and the future public article view. Raw HTML is always text. */
export default function SafeMarkdown({ children, renderImage, allowedMediaIds = [] }: { children: string; allowedMediaIds?: readonly string[]; renderImage?: (id: string, alt: string) => ReactNode }) {
  return <Box sx={{ overflowWrap: 'anywhere', '& img': { maxWidth: '100%', height: 'auto' }, '& pre': { overflowX: 'auto' } }}>
    <Markdown options={{ disableParsingRawHTML: true, overrides: {
      a: { component: ({ href, children: text }: { href?: string; children?: ReactNode }) => safeLink(href) ? <Link href={safeLink(href)} rel="noopener noreferrer">{text}</Link> : <span>{text}</span> },
      img: { component: ({ src, alt = '' }: { src?: string; alt?: string }) => { const id = mediaId(src); return id && alt.trim() && allowedMediaIds.includes(id) ? renderImage ? renderImage(id, alt) : <img src={(import.meta.env.VITE_API_URL || '').replace(/\/$/, '') + '/api/media/' + id + '/large'} alt={alt} loading="lazy" /> : <span>{alt}</span>; } },
    } }}>{children}</Markdown>
  </Box>;
}
