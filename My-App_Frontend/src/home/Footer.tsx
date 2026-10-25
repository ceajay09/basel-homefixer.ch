import { Box, Container, Link, Stack, Typography } from '@mui/material';
import InstagramIcon from '@mui/icons-material/Instagram';
import { useTranslation } from 'react-i18next';
import { siteConfig } from './siteConfig';

interface FooterProps { description: string; title: string; }

export default function Footer({ description, title }: FooterProps) {
  const { t } = useTranslation();
  return (
    <Box component="footer" data-design-id="shared.footer" sx={{ bgcolor: '#1f1f1f', color: '#cccccc', py: { xs: 6, md: 10 },
      '& h2': { color: 'secondary.main', mb: 4, fontSize: '1.5rem' },
      '& p, & a': { fontSize: '0.875rem', lineHeight: 1.9 },
      '& a': { color: 'inherit', textDecoration: 'none', overflowWrap: 'anywhere', '&:hover': { color: 'secondary.main' } } }}>
      <Container>
        <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(min(250px, 100%), 1fr))', gap: 7 }}>
          <Box>
            <Typography component="h2" variant="h5">{siteConfig.name}</Typography>
            <Typography sx={{ fontWeight: 700 }}>{t('layout.footer.qualification')}</Typography>
            <Typography>{t('layout.footer.experience')}</Typography>
            <Typography sx={{ mt: 4 }}>{t('layout.footer.specialties')}</Typography>
          </Box>
          <Box>
            <Typography component="h2" variant="h5">{title}</Typography>
            {description.split(' · ').map(name => <Typography key={name} sx={{ fontWeight: 700 }}>{name}</Typography>)}
            <Typography><Box component="span" aria-hidden="true">📧 </Box><Link href={'mailto:' + siteConfig.email}>{siteConfig.email}</Link></Typography>
            <Typography><Box component="span" aria-hidden="true">📱 </Box><Link href={siteConfig.phoneHref}>{siteConfig.phone}</Link></Typography>
            <Typography><Box component="span" aria-hidden="true">📍 </Box>{t('layout.footer.location')}</Typography>
          </Box>
          <Box>
            <Typography component="h2" variant="h5">{t('layout.footer.followUs')}</Typography>
            <Stack direction="row" spacing={1} sx={{ alignItems: 'center' }}>
              <InstagramIcon aria-hidden="true" sx={{ fontSize: 18, color: 'secondary.main' }} />
              <Typography>{t('layout.footer.instagram')}</Typography>
            </Stack>
            <Typography><Box component="span" aria-hidden="true">🌐 </Box><Link href="https://basel-homefixer.ch">www.basel-homefixer.ch</Link></Typography>
          </Box>
        </Box>
        <Typography sx={{ mt: 7, pt: 7, borderTop: '1px solid #444', color: '#999999', textAlign: 'center' }}>
          {t('layout.footer.copyright', { year: new Date().getFullYear() })}
        </Typography>
      </Container>
    </Box>
  );
}
