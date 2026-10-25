import { Alert, Box, Button, Card, CardContent, Chip, Container, Grid, Stack, Typography } from '@mui/material';
import CheckIcon from '@mui/icons-material/Check';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { Link as RouterLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Layout from './Layout';
import PageSection from './PageSection';
import ContactCta from './ContactCta';
import { brandGradient } from './theme';
import { siteConfig } from './siteConfig';
import { homeServiceIds, serviceIcons, ServiceContent } from './serviceCatalog';
import { projectDurationOptions } from './projectCatalog';
import { useProjectContent } from './contentApi';

interface PromiseContent { title: string; description: string; }
const hoverCard = { transition: 'transform 180ms ease, box-shadow 180ms ease', '&:hover': { transform: 'translateY(-4px)', boxShadow: '0 12px 28px rgba(45,90,61,0.12)' } };

export default function Home() {
  const { t } = useTranslation();
  const content = useProjectContent(true);
  const promises = t('home.promises', { returnObjects: true }) as PromiseContent[];
  const catalog = t('services.catalog', { returnObjects: true }) as ServiceContent[];
  const services = homeServiceIds.map(id => catalog.find(service => service.id === id))
    .filter((service): service is ServiceContent => service !== undefined);

  return (
    <Layout>
      <Box component="section" aria-labelledby="home-title" data-design-id="home.hero"
        sx={{ background: brandGradient, color: '#ffffff', py: { xs: 7, md: 10 }, textAlign: 'center' }}>
        <Container maxWidth="md">
          <Typography component="h1" variant="h1" id="home-title" sx={{ mb: 4 }}>{t('home.title')}</Typography>
          <Typography sx={{ mb: 6, fontSize: { xs: '1.05rem', md: '1.25rem' }, lineHeight: 1.65 }}>{t('home.subtitle')}</Typography>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={{ xs: 2, sm: 3 }} sx={{ justifyContent: 'center' }}>
            <Button component={RouterLink} to={siteConfig.contactHref} variant="contained" color="secondary">{t('services.consultationButton')}</Button>
            <Button component={RouterLink} to={siteConfig.projectsHref} variant="outlined"
              sx={{ color: '#ffffff', borderColor: '#ffffff', '&:hover': { borderColor: '#ffffff', bgcolor: 'rgba(255,255,255,0.1)' } }}>{t('home.projectsButton')}</Button>
          </Stack>
        </Container>
      </Box>
      <PageSection id="home-promise" title={t('home.promiseTitle')} muted>
        <Grid container spacing={{ xs: 3, md: 4 }}>
          {promises.map(promise => <Grid key={promise.title} size={{ xs: 12, sm: 6, lg: 4 }} sx={{ display: 'flex' }}>
            <Card sx={{ ...hoverCard, width: '100%', borderLeft: '5px solid', borderColor: 'secondary.main', textAlign: 'center' }}>
              <CardContent sx={{ p: { xs: 3, md: 4 }, height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'center', '&:last-child': { pb: { xs: 3, md: 4 } } }}>
                <Typography component="h3" variant="h3" sx={{ color: 'primary.main', mb: 2 }}><Box component="span" aria-hidden="true">✓ </Box>{promise.title}</Typography>
                <Typography color="text.secondary">{promise.description}</Typography>
              </CardContent>
            </Card>
          </Grid>)}
          <Grid size={{ xs: 12, sm: 6, lg: 4 }} sx={{ display: 'flex' }}>
            <Card sx={{ width: '100%', borderLeft: '5px solid', borderColor: 'secondary.main', display: 'grid', placeItems: 'center', p: 4, minHeight: 240 }}>
              <Box component="img" src="/assets/home/logo.webp" alt={t('home.logoAlt')} width={480} height={458} loading="lazy" decoding="async"
                sx={{ maxWidth: '100%', width: 'auto', height: 'auto', maxHeight: 200, objectFit: 'contain' }} />
            </Card>
          </Grid>
        </Grid>
      </PageSection>
      <PageSection id="home-services" title={t('home.servicesTitle')}>
        <Grid container spacing={{ xs: 3, md: 4 }}>
          {services.map(service => <Grid key={service.id} size={{ xs: 12, sm: 6, lg: 4 }} sx={{ display: 'flex' }}>
            <Card component="article" sx={{ ...hoverCard, width: '100%', bgcolor: '#f9f8f6', border: '1px solid', borderColor: 'divider', display: 'flex', flexDirection: 'column' }}>
              <CardContent sx={{ p: { xs: 3, md: 4 }, flex: 1, display: 'flex', flexDirection: 'column', '&:last-child': { pb: { xs: 3, md: 4 } } }}>
                <Box aria-hidden="true" sx={{ fontSize: 44, lineHeight: 1.3, mb: 3 }}>{serviceIcons[service.id]}</Box>
                <Typography component="h3" variant="h3" sx={{ color: 'primary.main', mb: 2 }}>{service.title}</Typography>
                {service.id === 'consulting' && <Chip label={t('services.freeConsultation')} sx={{ alignSelf: 'flex-start', bgcolor: '#f5eadc', color: '#523b22', mb: 2, fontWeight: 600 }} />}
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>{service.description}</Typography>
                <Box component="ul" sx={{ listStyle: 'none', p: 0, m: 0, mb: 4 }}>
                  {service.features.map(feature => <Box component="li" key={feature} sx={{ display: 'flex', alignItems: 'flex-start', gap: 1, mb: 1 }}>
                    <CheckIcon aria-hidden="true" sx={{ color: 'secondary.dark', fontSize: 19, mt: '4px', flexShrink: 0 }} />
                    <Typography variant="body2" color="text.secondary">{feature}</Typography>
                  </Box>)}
                </Box>
                <Button component={RouterLink} to={service.id === 'consulting' ? '/contactUs?service=consulting#contact-form' : '/services#service-' + service.id} variant="text" endIcon={<ArrowForwardIcon />}
                  sx={{ mt: 'auto', alignSelf: 'flex-start', px: 0 }}>{t(service.id === 'consulting' ? 'services.consultationButton' : 'services.moreButton')}</Button>
              </CardContent>
            </Card>
          </Grid>)}
        </Grid>
        <Box sx={{ textAlign: 'center', mt: 5 }}>
          <Button component={RouterLink} to="/services" variant="outlined" endIcon={<ArrowForwardIcon />}>{t('home.allServicesButton')}</Button>
        </Box>
      </PageSection>
      <PageSection id="projects" title={t('home.projectsTitle')} muted>
        {content.loading && <Typography role="status" sx={{ textAlign: 'center', mb: 3 }}>{t('projects.loading')}</Typography>}
        {content.error && <Alert severity="warning" sx={{ mb: 3 }}>{t('projects.loadError')} <Button onClick={content.reload}>{t('projects.retry')}</Button></Alert>}
        {!content.loading && !content.error && !content.data.items.length && <Typography sx={{ textAlign: 'center', mb: 3 }}>{t('projects.empty')}</Typography>}
        <Typography sx={{ maxWidth: 650, mx: 'auto', mb: 6, textAlign: 'center', color: 'text.secondary', fontSize: '1.125rem' }}>{t('home.projectsIntro')}</Typography>
        <Grid container spacing={{ xs: 3, md: 4 }}>
          {content.data.items.map(project => {
            return <Grid key={project.id} size={{ xs: 12, md: 6, lg: 4 }} sx={{ display: 'flex' }}>
              <Card component="article" aria-labelledby={'project-' + project.id} sx={{ ...hoverCard, width: '100%', display: 'flex', flexDirection: 'column' }}>
                {project.image && <Box component="img" src={project.image.src}
                  srcSet={project.image.srcSet}
                  sizes="(min-width: 1200px) 360px, (min-width: 900px) 46vw, 100vw"
                  alt={project.image.alt} width={project.image.width} height={project.image.height} loading="lazy" decoding="async"
                  sx={{ display: 'block', width: '100%', height: { xs: 220, sm: 280 }, objectFit: 'cover', bgcolor: '#e8e8e8' }} />}
                <CardContent sx={{ p: { xs: 3, md: 4 }, display: 'flex', flexDirection: 'column', flex: 1, '&:last-child': { pb: { xs: 3, md: 4 } } }}>
                  <Typography component="h3" variant="h3" id={'project-' + project.id} sx={{ color: 'primary.main', mb: 2 }}>{project.title}</Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}><strong>{t('home.challengeLabel')} </strong>{project.challenge}</Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}><strong>{t('home.resultLabel')} </strong>{project.result}</Typography>
                  <Stack spacing={1} sx={{ mt: 'auto', pt: 3, borderTop: '1px solid', borderColor: 'divider' }}>
                    {project.complexity != null && <Typography variant="body2" color="text.secondary">{t('home.complexity', { count: project.complexity! })} <Box component="span" aria-hidden="true" sx={{ color: 'secondary.dark' }}>{'★'.repeat(project.complexity!)}</Box></Typography>}
                    {project.duration && <Typography variant="body2" color="text.secondary">{t('projects.totalDurationLabel')}: {t('projects.duration.' + project.duration!.unit, projectDurationOptions(project.duration!))}</Typography>}
                  </Stack>
                  <Button component={RouterLink} to={siteConfig.projectsHref + '#' + project.id} endIcon={<ArrowForwardIcon />}
                    sx={{ mt: 2, px: 0, alignSelf: 'flex-start' }} aria-label={t('projects.readProject') + ': ' + project.title}>{t('projects.readProject')}</Button>
                </CardContent>
              </Card>
            </Grid>;
          })}
        </Grid>
        <Box sx={{ textAlign: 'center', mt: 5 }}>
          <Button component={RouterLink} to={siteConfig.projectsHref} variant="outlined" endIcon={<ArrowForwardIcon />}>{t('projects.allProjects')}</Button>
        </Box>
      </PageSection>
      <ContactCta title={t('home.ctaTitle')} description={t('home.ctaDescription')} />
    </Layout>
  );
}
