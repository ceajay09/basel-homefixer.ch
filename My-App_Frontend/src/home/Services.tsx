import { useEffect, useState } from 'react';
import { Accordion, AccordionDetails, AccordionSummary, Box, Button, Card, CardContent, Chip, Collapse, Container, Grid, Stack, Typography } from '@mui/material';
import CheckIcon from '@mui/icons-material/Check';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Layout from './Layout';
import PageSection from './PageSection';
import ContactCta from './ContactCta';
import { brandGradient } from './theme';
import { serviceIcons, ServiceContent, ServiceId } from './serviceCatalog';

interface ProcessStep { title: string; description: string; }
interface Faq { question: string; answer: string; }

export default function Services() {
  const { t } = useTranslation();
  const [expanded, setExpanded] = useState<Partial<Record<ServiceId, boolean>>>({});
  const { hash } = useLocation();
  useEffect(() => {
    if (!hash.startsWith('#service-')) return;
    const id = hash.slice('#service-'.length);
    if (Object.prototype.hasOwnProperty.call(serviceIcons, id) && id !== 'consulting') {
      setExpanded(current => ({ ...current, [id]: true }));
    }
  }, [hash]);
  const services = t('services.catalog', { returnObjects: true }) as ServiceContent[];
  const steps = t('services.processSteps', { returnObjects: true }) as ProcessStep[];
  const faqs = t('services.faqItems', { returnObjects: true }) as Faq[];

  return (
    <Layout>
      <Box component="section" data-design-id="services.hero" sx={{ background: brandGradient, color: '#ffffff', py: { xs: 7, md: 10 }, textAlign: 'center' }}>
        <Container maxWidth="md">
          <Typography component="h1" variant="h1" sx={{ mb: 3 }}>{t('services.title')}</Typography>
          <Typography sx={{ fontSize: { xs: '1.05rem', md: '1.25rem' }, lineHeight: 1.65 }}>{t('services.subtitle')}</Typography>
        </Container>
      </Box>
      <PageSection id="services-intro" title={t('services.introTitle')}>
        <Typography sx={{ maxWidth: 800, mx: 'auto', textAlign: 'center', color: 'text.secondary', fontSize: '1.125rem', lineHeight: 1.8 }}>
          {t('services.introDescription')}
        </Typography>
      </PageSection>
      <PageSection id="services-detail" title={t('services.catalogTitle')} muted>
        <Grid container spacing={{ xs: 3, md: 5 }} sx={{ alignItems: 'flex-start' }}>
          {services.map(service => {
            return (
              <Grid key={service.id} size={{ xs: 12, md: 6 }} sx={{ display: 'flex' }}>
                <Card component="article" id={'service-' + service.id} tabIndex={-1} aria-labelledby={'service-' + service.id + '-title'} data-design-id={'services.' + service.id}
                  sx={{ width: '100%', scrollMarginTop: '110px', display: 'flex', flexDirection: 'column', transition: 'box-shadow 180ms ease, transform 180ms ease',
                    '&:hover': { boxShadow: '0 12px 28px rgba(45,90,61,0.12)', transform: 'translateY(-4px)' } }}>
                  <Box aria-hidden="true" sx={{ background: brandGradient, height: { xs: 176, md: 220 }, display: 'grid', placeItems: 'center', color: '#ffffff' }}>
                    <Box component="span" sx={{ fontSize: 72, lineHeight: 1, fontFamily: '"Apple Color Emoji", "Segoe UI Emoji", "Noto Color Emoji", sans-serif' }}>{serviceIcons[service.id]}</Box>
                  </Box>
                  <CardContent sx={{ p: { xs: 3, md: 5 }, flex: 1, display: 'flex', flexDirection: 'column', '&:last-child': { pb: { xs: 3, md: 5 } } }}>
                    <Typography component="h3" variant="h3" id={'service-' + service.id + '-title'} sx={{ color: 'primary.main', mb: 2 }}>{service.title}</Typography>
                    {service.id === 'consulting' && <Chip label={t('services.freeConsultation')} sx={{ alignSelf: 'flex-start', bgcolor: '#f5eadc', color: '#523b22', mb: 2, fontWeight: 600 }} />}
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>{service.description}</Typography>
                    <Typography variant="overline" sx={{ color: 'primary.main', fontWeight: 700, letterSpacing: '0.06em', mb: 1 }}>{t('services.featuresLabel')}</Typography>
                    <Box component="ul" sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' }, gap: 2, listStyle: 'none', p: 0, mt: 0, mb: 4 }}>
                      {service.features.map(feature => <Box component="li" key={feature} sx={{ display: 'flex', alignItems: 'flex-start', gap: 1 }}>
                        <CheckIcon aria-hidden="true" sx={{ color: 'secondary.dark', fontSize: 19, mt: '4px', flexShrink: 0 }} />
                        <Typography variant="body2" color="text.secondary">{feature}</Typography>
                      </Box>)}
                    </Box>
                    <Box sx={{ mt: 'auto', pt: 3, borderTop: '1px solid', borderColor: 'divider', textAlign: 'center' }}>
                      {service.details.length > 0 ? (
                        <Button id={'service-' + service.id + '-toggle'} variant="contained"
                          aria-expanded={!!expanded[service.id]} aria-controls={'service-' + service.id + '-details'}
                          aria-label={t(expanded[service.id] ? 'services.lessButton' : 'services.moreButton') + ': ' + service.title}
                          onClick={() => setExpanded(current => ({ ...current, [service.id]: !current[service.id] }))}
                          endIcon={<ExpandMoreIcon sx={{ transform: expanded[service.id] ? 'rotate(180deg)' : 'none' }} />}>
                          {t(expanded[service.id] ? 'services.lessButton' : 'services.moreButton')}
                        </Button>
                      ) : (
                        <Button component={RouterLink} to={'/contactUs?service=' + service.id + '#contact-form'} variant="contained" endIcon={<ArrowForwardIcon />}>
                          {t('services.consultationButton')}
                        </Button>
                      )}
                    </Box>
                    {service.details.length > 0 && <Collapse in={!!expanded[service.id]} timeout="auto">
                      <Box id={'service-' + service.id + '-details'} role="region" aria-labelledby={'service-' + service.id + '-toggle'}
                        sx={{ pt: 4, textAlign: 'left' }}>
                        {service.details.map((step, index) => <Box key={index} sx={{ mb: 3 }}>
                          <Typography component="h4" variant="h6" sx={{ color: 'primary.main', mb: 1 }}>{step.title}</Typography>
                          <Box component="ul" sx={{ pl: 3, my: 0, color: 'text.secondary', '& li': { mb: 1 } }}>
                            {step.items.map((item, itemIndex) => <Box component="li" key={itemIndex}>
                              <Typography variant="body2">{item}</Typography>
                            </Box>)}
                          </Box>
                        </Box>)}
                        <Button component={RouterLink} to={'/contactUs?service=' + service.id + '#contact-form'} variant="outlined"
                          endIcon={<ArrowForwardIcon />} sx={{ width: { xs: '100%', sm: 'auto' } }}>{t('services.adviceButton')}</Button>
                      </Box>
                    </Collapse>}
                  </CardContent>
                </Card>
              </Grid>
            );
          })}
        </Grid>
      </PageSection>
      <PageSection id="services-process" title={t('services.processTitle')}>
        <Grid container spacing={3}>
          {steps.map((step, index) => <Grid key={step.title} size={{ xs: 12, sm: 6, lg: 3 }} sx={{ display: 'flex' }}>
            <Box sx={{ p: { xs: 3, md: 4 }, width: '100%', bgcolor: '#f9f8f6', borderLeft: '4px solid', borderColor: 'secondary.main', borderRadius: 3, textAlign: 'center' }}>
              <Typography component="span" aria-hidden="true" sx={{ display: 'block', fontSize: '3rem', lineHeight: 1.2, fontWeight: 700, color: 'primary.main', mb: 2 }}>{index + 1}</Typography>
              <Typography component="h3" variant="h5" sx={{ color: 'primary.main', mb: 2 }}>{step.title}</Typography>
              <Typography variant="body2" color="text.secondary">{step.description}</Typography>
            </Box>
          </Grid>)}
        </Grid>
      </PageSection>
      <PageSection id="services-faq" title={t('services.faqTitle')} muted>
        <Stack spacing={2} sx={{ maxWidth: 900, mx: 'auto' }}>
          {faqs.map((faq, index) => <Accordion key={faq.question} disableGutters elevation={0}
            sx={{ borderLeft: '4px solid', borderColor: 'secondary.main', borderRadius: '12px !important', '&::before': { display: 'none' } }}>
            <AccordionSummary id={'faq-' + index + '-question'} aria-controls={'faq-' + index + '-answer'} expandIcon={<ExpandMoreIcon />} sx={{ px: { xs: 2, md: 4 }, py: 1.5 }}>
              <Typography component="h3" variant="h6" sx={{ color: 'primary.main', pr: 2 }}>{faq.question}</Typography>
            </AccordionSummary>
            <AccordionDetails id={'faq-' + index + '-answer'} sx={{ px: { xs: 2, md: 4 }, pb: 3 }}>
              <Typography variant="body2" color="text.secondary">{faq.answer}</Typography>
            </AccordionDetails>
          </Accordion>)}
        </Stack>
      </PageSection>
      <ContactCta title={t('services.ctaTitle')} description={t('services.ctaDescription')} />
    </Layout>
  );
}
