import { Box, Button, Container, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { brandGradient } from './theme';
import { siteConfig } from './siteConfig';

interface ContactCtaProps { title: string; description: string; }

export default function ContactCta({ title, description }: ContactCtaProps) {
  const { t } = useTranslation();
  return (
    <Box component="section" aria-labelledby="contact-cta-title" data-design-id="contact-cta"
      sx={{ background: brandGradient, color: '#ffffff', textAlign: 'center', py: { xs: 7, md: 10 } }}>
      <Container maxWidth="md">
        <Typography id="contact-cta-title" variant="h2" component="h2" sx={{ mb: 3 }}>{title}</Typography>
        <Typography sx={{ mb: 4, fontSize: { xs: '1rem', md: '1.125rem' } }}>{description}</Typography>
        <Button component={RouterLink} to={siteConfig.contactHref} variant="contained" color="secondary"
          endIcon={<ArrowForwardIcon />} sx={{ px: { xs: 3, sm: 5 } }}>{t('services.consultationButton')}</Button>
      </Container>
    </Box>
  );
}
