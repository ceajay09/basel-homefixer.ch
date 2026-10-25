import { useEffect, useRef } from 'react';
import { Alert, Box, Button, Chip, Container, Stack, Typography } from '@mui/material';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Layout from './Layout';
import BlogPicture from './BlogPicture';
import SafeMarkdown from './SafeMarkdown';
import ContactCta from './ContactCta';
import { blogDate, localized, useBlog } from './blogApi';
export default function BlogArticle() {
  const { slug } = useParams(); const { t } = useTranslation(); const content = useBlog(slug || ''); const title = useRef<HTMLHeadingElement>(null);
  useEffect(() => { if (!content.loading) title.current?.focus(); }, [content.loading, slug, content.lang]);
  const post = content.post;
  return <Layout><Container maxWidth="md" sx={{ py: { xs: 4, md: 7 } }}>
    <Button component={RouterLink} to="/blog" sx={{ mb: 3 }}>← {t('publicBlog.back')}</Button>
    {content.loading && <Typography role="status">{t('publicBlog.loading')}</Typography>}
    {content.error && <Alert severity="warning">{t('publicBlog.error')} <Button onClick={content.retry}>{t('publicBlog.retry')}</Button></Alert>}
    {content.notFound && <Box><Typography ref={title} tabIndex={-1} component="h1" variant="h2">{t('publicBlog.notFound')}</Typography><Typography sx={{ mt: 2 }}>{t('publicBlog.notFoundText')}</Typography></Box>}
    {post && <Box component="article"><Typography ref={title} tabIndex={-1} component="h1" variant="h1" sx={{ color: 'primary.main', overflowWrap: 'anywhere', scrollMarginTop: 130 }}>{localized(post.title, content.lang)}</Typography>
      <Stack direction="row" sx={{ alignItems: 'center', flexWrap: 'wrap', gap: 1, my: 3 }}>{post.publishedAt && <Typography component="time" dateTime={post.publishedAt} color="text.secondary">{blogDate(post.publishedAt, content.lang)}</Typography>}{post.categories.map(id => <Chip key={id} component={RouterLink} clickable to={'/blog?category=' + encodeURIComponent(id)} label={content.categories.find(c => c.id === id)?.label || id} />)}</Stack>
      <Typography sx={{ fontSize: '1.2rem', color: 'text.secondary', mb: 4 }}>{localized(post.excerpt, content.lang)}</Typography>
      <Box sx={{ mb: 5, overflow: 'hidden', borderRadius: 3 }}><BlogPicture key={post.image?.id || post.id} hero image={post.image} fallback={localized(post.title, content.lang)} /></Box>
      <SafeMarkdown allowedMediaIds={[...(post.mediaIds || []), ...(post.image?.id ? [post.image.id] : [])]}>{localized(post.description, content.lang)}</SafeMarkdown>
      <Typography sx={{ mt: 5, pt: 3, borderTop: '1px solid', borderColor: 'divider', color: 'text.secondary' }}>{t('publicBlog.notice')}</Typography>
    </Box>}
  </Container><ContactCta title={t('publicBlog.ctaTitle')} description={t('publicBlog.cta')} /></Layout>;
}
