import ProjectFacts from './ProjectFacts';
import { useEffect, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Chip, Container, Grid, Stack, Typography } from '@mui/material';
import PlaceOutlinedIcon from '@mui/icons-material/PlaceOutlined';
import { useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Layout from './Layout';
import PageSection from './PageSection';
import ContactCta from './ContactCta';
import { brandGradient } from './theme';
import { formatProjectInvestment, ProjectMetadata, projectDurationOptions, projectLocale, projectSummary } from './projectCatalog';
import { getProject, useProjectContent } from './contentApi';

export default function Projects() {
  const { t, i18n } = useTranslation();
  const { hash, key } = useLocation();
  const navigate = useNavigate();
  const [category, setCategory] = useState('all');
  const [page, setPage] = useState(0);
  const content = useProjectContent(false, category, page);
  const [linked, setLinked] = useState<ProjectMetadata | null>(null);
  const [anchorMissing, setAnchorMissing] = useState(false);
  useEffect(() => { if (hash) { setCategory('all'); setPage(0); } }, [hash, key]);
  useEffect(() => {
    setLinked(null); setAnchorMissing(false);
    if (!hash || content.loading || content.error || category !== 'all') return;
    let id: string;
    try { id = decodeURIComponent(hash.slice(1)); } catch { setAnchorMissing(true); return; }
    if (!/^[a-z0-9][a-z0-9-]{0,79}$/.test(id)) { setAnchorMissing(true); return; }
    if (content.data.items.some(project => project.id === id)) return;
    const abort = new AbortController(); const timer = setTimeout(() => abort.abort(), 10000); let disposed = false;
    getProject(id, content.lang, abort.signal).then(project => { if (!disposed) setLinked(project); })
      .catch(() => { if (!disposed) setAnchorMissing(true); }).finally(() => clearTimeout(timer));
    return () => { disposed = true; clearTimeout(timer); abort.abort(); };
  }, [hash, key, content.loading, content.error, content.data.items, content.lang, category]);
  const visibleProjects = linked && !content.data.items.some(project => project.id === linked.id) ? [...content.data.items, linked] : content.data.items;
  useEffect(() => {
    if (content.loading || !hash) return;
    const frame = requestAnimationFrame(() => {
      let id: string; try { id = decodeURIComponent(hash.slice(1)); } catch { return; }
      const target = document.getElementById(id);
      if (target?.tagName === 'ARTICLE') { target.scrollIntoView({ behavior: 'instant', block: 'start' }); target.focus({ preventScroll: true }); }
    });
    return () => cancelAnimationFrame(frame);
  }, [content.loading, content.data.items, linked, hash, key]);
  const dateFormatter = new Intl.DateTimeFormat(projectLocale(i18n.language), { month: 'long', year: 'numeric', timeZone: 'UTC' });

  return (
    <Layout>
      <Box component="section" aria-labelledby="projects-page-title" data-design-id="projects.hero"
        sx={{ background: brandGradient, color: '#ffffff', py: { xs: 7, md: 10 }, textAlign: 'center' }}>
        <Container maxWidth="md">
          <Typography component="h1" variant="h1" id="projects-page-title" sx={{ mb: 3 }}>{t('projects.title')}</Typography>
          <Typography sx={{ fontSize: { xs: '1.05rem', md: '1.25rem' } }}>{t('projects.subtitle')}</Typography>
        </Container>
      </Box>
      <PageSection id="projects-intro" title={t('projects.introTitle')}>
        <Typography sx={{ maxWidth: 850, mx: 'auto', textAlign: 'center', color: 'text.secondary', fontSize: '1.125rem' }}>{t('projects.intro')}</Typography>
      </PageSection>
      <PageSection id="projects-list" title={t('projects.selectionTitle')} muted>
        <Typography component="h3" variant="h6" id="projects-filter-title" sx={{ textAlign: 'center', mb: 2 }}>{t('projects.filterTitle')}</Typography>
        <Box role="group" aria-labelledby="projects-filter-title" sx={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center', gap: 1, mb: 2 }}>
          {[{ id: 'all', label: t('projects.categories.all') }, ...content.categories].map(value => <Button key={value.id} variant={category === value.id ? 'contained' : 'outlined'} size="small"
            aria-pressed={category === value.id} onClick={() => { setCategory(value.id); setPage(0); if (hash) navigate('/projects', { replace: true }); }}>{value.label}</Button>)}
        </Box>
        <Typography role="status" aria-live="polite" aria-atomic="true" sx={{ textAlign: 'center', mb: 4, color: 'text.secondary' }}>{t(content.loading ? 'projects.loading' : 'projects.resultCount', { count: content.data.totalElements })}</Typography>
        {content.error && <Alert severity="warning" sx={{ mb: 3 }}>{t('projects.loadError')} <Button onClick={content.reload}>{t('projects.retry')}</Button></Alert>}
        {anchorMissing && <Alert severity="info" sx={{ mb: 3 }}>{t('projects.anchorMissing')}</Alert>}
        {!content.loading && !content.error && !visibleProjects.length && <Typography sx={{ textAlign: 'center', py: 4 }}>{t('projects.empty')}</Typography>}
        <Grid container spacing={{ xs: 3, md: 4 }}>
          {visibleProjects.map(project => {
            const tags = project.tags;
            return <Grid key={project.id} size={{ xs: 12, md: 6 }} sx={{ display: 'flex' }}>
              <Card component="article" id={project.id} tabIndex={-1} aria-labelledby={'project-' + project.id + '-title'}
                sx={{ width: '100%', scrollMarginTop: '110px', display: 'flex', flexDirection: 'column' }}>
                <Box sx={{ position: 'relative' }}>
                  {project.image ? <Box component="img" src={project.image.src} srcSet={project.image.srcSet}
                    sizes="(min-width: 1200px) 552px, (min-width: 900px) 46vw, 100vw" alt={project.image.alt}
                    width={project.image.width} height={project.image.height} loading="lazy" decoding="async"
                    sx={{ display: 'block', width: '100%', aspectRatio: '3 / 2', height: 'auto', objectFit: 'cover', objectPosition: project.image.position || 'center', bgcolor: '#e8e8e8' }} /> : <Box sx={{ aspectRatio: '3 / 2', display: 'grid', placeItems: 'center', bgcolor: '#e8e8e8' }}>{t('projects.noImage')}</Box>}
                  {project.dates.length > 0 && <Chip label={project.dates[0].slice(0, 4)} sx={{ position: 'absolute', top: 16, right: 16, bgcolor: 'secondary.main', color: 'secondary.contrastText', fontWeight: 700 }} />}
                </Box>
                <CardContent sx={{ p: { xs: 3, md: 4 }, display: 'flex', flexDirection: 'column', flex: 1, '&:last-child': { pb: { xs: 3, md: 4 } } }}>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    {project.dates.map((date, index) => <Box component="span" key={date}>{index > 0 && ' & '}<time dateTime={date}>{dateFormatter.format(new Date(date + '-01T00:00:00Z'))}</time></Box>)}
                  </Typography>
                  <Stack direction="row" spacing={0.5} sx={{ alignItems: 'flex-start', color: 'text.secondary', mb: 2 }}>
                    <PlaceOutlinedIcon aria-hidden="true" sx={{ fontSize: 18, mt: '3px' }} /><Typography variant="body2">{project.location}</Typography>
                  </Stack>
                  <Typography component="h3" variant="h3" id={'project-' + project.id + '-title'} sx={{ color: 'primary.main', mb: 2 }}>{project.title}</Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>{project.description}</Typography>
                  <ProjectFacts project={project} />
                  <Box component="ul" aria-label={t('projects.tagsLabel')} sx={{ listStyle: 'none', p: 0, m: 0, display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {tags.map(tag => <Box component="li" key={tag}><Chip label={tag} size="small" sx={{ bgcolor: '#f5eadc', color: '#523b22', maxWidth: '100%', height: 'auto', '& .MuiChip-label': { whiteSpace: 'normal', py: 0.5 } }} /></Box>)}
                  </Box>
                </CardContent>
              </Card>
            </Grid>;
          })}
        </Grid>
        {content.data.totalPages > 1 && <Stack direction="row" spacing={2} sx={{ justifyContent: 'center', alignItems: 'center', mt: 4 }}>
          <Button disabled={page === 0 || content.loading} onClick={() => setPage(value => value - 1)}>{t('projects.previous')}</Button>
          <Typography>{t('projects.pageLabel', { page: page + 1, pages: content.data.totalPages })}</Typography>
          <Button disabled={page + 1 >= content.data.totalPages || content.loading} onClick={() => setPage(value => value + 1)}>{t('projects.next')}</Button>
        </Stack>}
      </PageSection>
      <PageSection id="projects-stats" title={t('projects.statsTitle')}>
        <Grid container spacing={3}>
          {[{ value: projectSummary.completed, label: t('projects.completedLabel') }, { value: projectSummary.customers, label: t('projects.customersLabel') }, { value: projectSummary.experienceYears, label: t('projects.experienceLabel') }].map(stat => <Grid key={stat.label} size={{ xs: 12, sm: 4 }}>
            <Box sx={{ textAlign: 'center', p: 4, bgcolor: '#f9f8f6', borderRadius: 3 }}>
              <Typography sx={{ color: 'primary.main', fontWeight: 700, fontSize: '2.5rem' }}>{stat.value}</Typography>
              <Typography color="text.secondary">{stat.label}</Typography>
            </Box>
          </Grid>)}
        </Grid>
      </PageSection>
      <ContactCta title={t('projects.ctaTitle')} description={t('projects.ctaDescription')} />
    </Layout>
  );
}
