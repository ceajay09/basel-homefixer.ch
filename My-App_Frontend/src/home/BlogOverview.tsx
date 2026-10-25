import { useEffect, useRef } from 'react';
import { Alert, Box, Button, Card, CardActions, CardContent, Chip, Container, Grid, Stack, Typography } from '@mui/material';
import { Link as RouterLink, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Layout from './Layout';
import PageSection from './PageSection';
import ContactCta from './ContactCta';
import BlogPicture from './BlogPicture';
import { blogDate, localized, useBlog } from './blogApi';
import { brandGradient } from './theme';
export default function BlogOverview() {
  const { t } = useTranslation(); const [params, setParams] = useSearchParams(); const category = params.get('category') || '';
  const rawPage = params.get('page') || '0'; const page = /^\d+$/.test(rawPage) && Number(rawPage) <= 10000 ? Number(rawPage) : 0;
  const content = useBlog(undefined, category, page); const results = useRef<HTMLHeadingElement>(null); const focusAfter = useRef(false);
  useEffect(() => { if (!content.loading && focusAfter.current) { results.current?.focus(); focusAfter.current = false; } }, [content.loading]);
  function select(nextCategory: string, nextPage: number) { focusAfter.current = true; setParams({ ...(nextCategory ? { category: nextCategory } : {}), ...(nextPage ? { page: String(nextPage) } : {}) }); }
  return <Layout>
    <Box sx={{ background: brandGradient, color: 'white', py: { xs: 6, md: 9 }, textAlign: 'center' }}><Container maxWidth="md"><Typography component="h1" variant="h1" sx={{ mb: 3 }}>{t('publicBlog.title')}</Typography><Typography sx={{ fontSize: { xs: '1.05rem', md: '1.25rem' } }}>{t('publicBlog.subtitle')}</Typography></Container></Box>
    <PageSection id="blog-intro" title={t('publicBlog.aboutTitle')}><Typography sx={{ maxWidth: 850, mx: 'auto', textAlign: 'center', color: 'text.secondary' }}>{t('publicBlog.about')}</Typography></PageSection>
    <Box component="section" aria-labelledby="blog-results" sx={{ bgcolor: '#f9f8f6', py: { xs: 5, md: 8 } }}><Container>
      <Typography ref={results} tabIndex={-1} component="h2" variant="h2" id="blog-results" sx={{ textAlign: 'center', mb: 3, scrollMarginTop: 130 }}>{t('publicBlog.articles')}</Typography>
      <Stack role="group" aria-label={t('publicBlog.filter')} direction="row" sx={{ flexWrap: 'wrap', justifyContent: 'center', gap: 1, mb: 3 }}>
        {[{ id: '', label: t('publicBlog.all') }, ...content.categories].map(c => <Button key={c.id} variant={category === c.id ? 'contained' : 'outlined'} aria-pressed={category === c.id} onClick={() => select(c.id, 0)}>{c.label}</Button>)}
      </Stack>
      {content.loading && <Typography role="status" aria-live="polite" align="center">{t('publicBlog.loading')}</Typography>}
      {content.error && <Alert severity="warning">{t('publicBlog.error')} <Button onClick={content.retry}>{t('publicBlog.retry')}</Button>{(category || page > 0) && <Button onClick={() => select('', 0)}>{t('publicBlog.reset')}</Button>}</Alert>}
      {!content.loading && !content.error && !content.list?.items.length && <Stack spacing={2} sx={{ textAlign: 'center', py: 4 }}><Typography role="status">{t(category ? 'publicBlog.emptyCategory' : page > 0 ? 'publicBlog.emptyPage' : 'publicBlog.empty')}</Typography>{(category || page > 0) && <Button onClick={() => select('', 0)}>{t('publicBlog.reset')}</Button>}</Stack>}
      <Grid container spacing={3}>
        {content.list?.items.map(post => <Grid key={post.id} size={{ xs: 12, sm: 6, lg: 4 }} sx={{ display: 'flex' }}><Card component="article" variant="outlined" sx={{ width: '100%', display: 'flex', flexDirection: 'column', borderRadius: 3, overflow: 'hidden' }}>
          <BlogPicture key={post.image?.id || post.id} image={post.image} fallback={localized(post.title, content.lang)} />
          <CardContent sx={{ flex: 1, p: 3 }}><Stack direction="row" sx={{ flexWrap: 'wrap', alignItems: 'center', gap: 1, mb: 2 }}>{post.publishedAt && <Typography component="time" dateTime={post.publishedAt} variant="body2" color="text.secondary">{blogDate(post.publishedAt, content.lang)}</Typography>}{post.categories.map(id => <Chip key={id} size="small" label={content.categories.find(c => c.id === id)?.label || id} />)}</Stack><Typography component="h3" variant="h5" sx={{ color: 'primary.main', mb: 2, overflowWrap: 'anywhere' }}>{localized(post.title, content.lang)}</Typography><Typography color="text.secondary" sx={{ overflowWrap: 'anywhere' }}>{localized(post.excerpt, content.lang)}</Typography></CardContent>
          <CardActions sx={{ px: 3, pb: 3 }}><Button component={RouterLink} to={'/blog/' + encodeURIComponent(post.slug)} aria-label={t('publicBlog.readNamed', { title: localized(post.title, content.lang) })}>{t('publicBlog.read')} →</Button></CardActions>
        </Card></Grid>)}
      </Grid>
      {!!content.list && content.list.totalPages > 1 && <Stack component="nav" aria-label={t('publicBlog.pagination')} direction="row" sx={{ alignItems: 'center', justifyContent: 'center', flexWrap: 'wrap', gap: 2, mt: 4 }}><Button disabled={page === 0 || content.loading} onClick={() => select(category, page - 1)}>{t('publicBlog.previous')}</Button><Typography>{t('publicBlog.page', { page: page + 1, pages: content.list.totalPages })}</Typography><Button disabled={page + 1 >= content.list.totalPages || content.loading} onClick={() => select(category, page + 1)}>{t('publicBlog.next')}</Button></Stack>}
    </Container></Box>
    <PageSection id="blog-publisher" title={t('publicBlog.publisherTitle')}><Typography align="center">Pascal Jaquiéry & Divine Mon · Basel Home Fixer</Typography><Typography align="center" sx={{ mt: 2 }}>{t('publicBlog.notice')}</Typography></PageSection>
    <ContactCta title={t('publicBlog.ctaTitle')} description={t('publicBlog.cta')} />
  </Layout>;
}
