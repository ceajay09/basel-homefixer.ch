import { Box, Card, CardContent, Container, Grid, Stack, Typography } from '@mui/material';
import CheckIcon from '@mui/icons-material/Check';
import { useTranslation } from 'react-i18next';
import Layout from './Layout';
import PageSection from './PageSection';
import ContactCta from './ContactCta';
import { brandGradient } from './theme';

interface TeamMember { name: string; role: string; imageAlt: string; bio: string; expertise: string[]; }
interface Statement { title: string; description: string; }
const teamMembers = ['pascal', 'divine'] as const;
const visionIcons = ['🎯', '🌟', '🚀'];
const valueIcons = ['🎯', '🏆', '🤝', '⚡', '😌', '🌍'];

export default function AboutUs() {
  const { t } = useTranslation();
  const paragraphs = t('aboutUs.introParagraphs', { returnObjects: true }) as string[];
  const members = t('aboutUs.members', { returnObjects: true }) as Record<typeof teamMembers[number], TeamMember>;
  const vision = t('aboutUs.vision', { returnObjects: true }) as Statement[];
  const values = t('aboutUs.values', { returnObjects: true }) as Statement[];

  return (
    <Layout>
      <Box component="section" aria-labelledby="about-title" data-design-id="about.hero"
        sx={{ background: brandGradient, color: '#ffffff', py: { xs: 7, md: 10 }, textAlign: 'center' }}>
        <Container maxWidth="md">
          <Typography component="h1" variant="h1" id="about-title" sx={{ mb: 3 }}>{t('aboutUs.title')}</Typography>
          <Typography sx={{ fontSize: { xs: '1.05rem', md: '1.25rem' } }}>{t('aboutUs.subtitle')}</Typography>
        </Container>
      </Box>
      <PageSection id="team-intro" title={t('aboutUs.introTitle')}>
        <Grid container spacing={{ xs: 4, md: 7 }} sx={{ alignItems: 'center' }}>
          <Grid size={{ xs: 12, md: 6 }}>
            <Box component="img" src="/assets/team/team-960.webp" srcSet="/assets/team/team-480.webp 480w, /assets/team/team-960.webp 960w"
              sizes="(min-width: 1200px) 540px, (min-width: 900px) 46vw, 100vw" width={960} height={1280}
              alt={t('aboutUs.teamImageAlt')} decoding="async"
              sx={{ display: 'block', width: '100%', height: { xs: 400, sm: 520 }, objectFit: 'cover', objectPosition: 'center 35%', borderRadius: 3, boxShadow: '0 8px 24px rgba(0,0,0,0.08)' }} />
          </Grid>
          <Grid size={{ xs: 12, md: 6 }}>
            {paragraphs.map((paragraph, index) => <Typography key={index} color="text.secondary" sx={{ mb: 3, lineHeight: 1.9 }}>{paragraph}</Typography>)}
            <Box component="blockquote" sx={{ m: 0, mt: 4, p: { xs: 3, md: 4 }, borderLeft: '5px solid', borderColor: 'secondary.main', bgcolor: '#f9f8f6', borderRadius: 3 }}>
              <Typography lang="en" sx={{ fontSize: { xs: '1.4rem', md: '1.625rem' }, fontWeight: 600, color: 'primary.main' }}>“{t('aboutUs.quote')}”</Typography>
            </Box>
          </Grid>
        </Grid>
      </PageSection>
      <PageSection id="team" title={t('aboutUs.partnersTitle')} muted>
        <Grid container spacing={{ xs: 3, md: 5 }}>
          {teamMembers.map(id => {
            const member = members[id];
            return <Grid key={id} size={{ xs: 12, md: 6 }} sx={{ display: 'flex' }}>
              <Card component="article" aria-labelledby={'team-' + id + '-name'} sx={{ width: '100%' }}>
                <Box component="img" src={'/assets/team/' + id + '-960.webp'}
                  srcSet={'/assets/team/' + id + '-480.webp 480w, /assets/team/' + id + '-960.webp 960w'}
                  sizes="(min-width: 1200px) 540px, (min-width: 900px) 46vw, 100vw"
                  alt={member.imageAlt} width={960} height={1280} loading="lazy" decoding="async"
                  sx={{ display: 'block', width: '100%', height: { xs: 340, sm: 400 }, objectFit: 'cover', objectPosition: 'center 10%', bgcolor: '#e8e8e8' }} />
                <CardContent sx={{ p: { xs: 3, md: 5 }, '&:last-child': { pb: { xs: 3, md: 5 } } }}>
                  <Typography component="h3" variant="h3" id={'team-' + id + '-name'} sx={{ color: 'primary.main', mb: 1 }}>{member.name}</Typography>
                  <Typography sx={{ fontWeight: 600, color: 'secondary.dark', mb: 3 }}>{member.role}</Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>{member.bio}</Typography>
                  <Typography component="h4" variant="overline" sx={{ display: 'block', fontWeight: 700, color: 'primary.main', mb: 2 }}>{t('aboutUs.expertiseLabel')}</Typography>
                  <Box component="ul" sx={{ p: 0, m: 0, listStyle: 'none', display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' }, gap: 2 }}>
                    {member.expertise.map(item => <Box component="li" key={item} sx={{ display: 'flex', alignItems: 'flex-start', gap: 1 }}>
                      <CheckIcon aria-hidden="true" sx={{ fontSize: 19, color: 'secondary.dark', mt: '4px', flexShrink: 0 }} />
                      <Typography variant="body2" color="text.secondary">{item}</Typography>
                    </Box>)}
                  </Box>
                </CardContent>
              </Card>
            </Grid>;
          })}
        </Grid>
      </PageSection>
      <PageSection id="team-vision" title={t('aboutUs.visionTitle')}>
        <Stack spacing={3} sx={{ maxWidth: 900, mx: 'auto' }}>
          {vision.map((statement, index) => <Box key={statement.title} sx={{ p: { xs: 3, md: 4 }, bgcolor: '#f9f8f6', borderLeft: '5px solid', borderColor: 'secondary.main', borderRadius: 3 }}>
            <Typography component="h3" variant="h3" sx={{ color: 'primary.main', mb: 2 }}><Box component="span" aria-hidden="true">{visionIcons[index]} </Box>{statement.title}</Typography>
            <Typography color="text.secondary">{statement.description}</Typography>
          </Box>)}
        </Stack>
      </PageSection>
      <PageSection id="team-values" title={t('aboutUs.valuesTitle')} muted>
        <Grid container spacing={3}>
          {values.map((value, index) => <Grid key={value.title} size={{ xs: 12, sm: 6, lg: 4 }} sx={{ display: 'flex' }}>
            <Card sx={{ width: '100%', textAlign: 'center' }}>
              <CardContent sx={{ p: { xs: 3, md: 4 }, '&:last-child': { pb: { xs: 3, md: 4 } } }}>
                <Box aria-hidden="true" sx={{ fontSize: 44, mb: 2 }}>{valueIcons[index]}</Box>
                <Typography component="h3" variant="h3" sx={{ color: 'primary.main', mb: 2 }}>{value.title}</Typography>
                <Typography variant="body2" color="text.secondary">{value.description}</Typography>
              </CardContent>
            </Card>
          </Grid>)}
        </Grid>
      </PageSection>
      <ContactCta title={t('aboutUs.ctaTitle')} description={t('aboutUs.ctaDescription')} />
    </Layout>
  );
}
