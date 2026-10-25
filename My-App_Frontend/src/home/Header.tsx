import { useState } from 'react';
import { Box, Button, Collapse, Container, IconButton, Link, Stack, Typography } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import CloseIcon from '@mui/icons-material/Close';
import OpenInNewIcon from '@mui/icons-material/OpenInNew';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useLanguage } from '../LanguageContext';

interface Section { title: string; url: string; }
interface HeaderProps { sections: ReadonlyArray<Section>; title: string; }

export default function Header({ sections, title }: HeaderProps) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const { currentLanguage, changeLanguage } = useLanguage();
  const { t } = useTranslation();
  const { pathname, hash } = useLocation();
  const language = currentLanguage?.startsWith('de') ? 'de' : 'en';

  const links = sections.map(section => {
    const external = section.url.startsWith('http');
    const [sectionPath, sectionHash] = section.url.split('#');
    const active = sectionHash ? pathname === sectionPath && hash === '#' + sectionHash
      : section.url === '/' ? (pathname === '/' && !hash) : pathname === section.url || section.url === '/blog' && pathname.startsWith('/blog/');
    return (
      <Link key={section.url} component={external ? 'a' : RouterLink}
        {...(external ? { href: section.url, target: '_blank', rel: 'noopener noreferrer' } : { to: section.url })}
        aria-current={active ? (sectionHash ? 'location' : 'page') : undefined}
        aria-label={external ? section.title + ' (' + t('layout.newWindow') + ')' : undefined}
        onClick={() => setMobileOpen(false)}
        sx={{ color: active ? 'primary.main' : 'text.primary', fontWeight: active ? 700 : 500,
          fontSize: '0.9375rem', py: 1, display: 'inline-flex', alignItems: 'center', gap: 0.5,
          textDecoration: active ? 'underline' : 'none', textUnderlineOffset: '6px',
          '&:hover': { color: 'primary.main' } }}>
        {section.title}{external && <OpenInNewIcon sx={{ fontSize: 14 }} />}
      </Link>
    );
  });

  return (
    <Box component="header" sx={{ position: 'sticky', top: 0, zIndex: 1100, bgcolor: 'background.paper',
      borderBottom: '1px solid', borderColor: 'divider', boxShadow: '0 1px 4px rgba(0,0,0,0.04)' }}>
      <Link href="#main-content" sx={{ position: 'absolute', left: 16, top: -100, bgcolor: 'background.paper', p: 1,
        '&:focus': { top: 8, zIndex: 1 } }}>{t('layout.skipToContent')}</Link>
      <Container>
        <Stack direction="row" spacing={2} sx={{ alignItems: 'center', justifyContent: 'space-between', minHeight: { xs: 76, lg: 92 } }}>
          <Typography component={RouterLink} to="/" onClick={() => setMobileOpen(false)}
            sx={{ color: 'primary.main', textDecoration: 'none', fontWeight: 750,
              fontSize: { xs: '1.2rem', sm: '1.5rem' }, lineHeight: 1.2, flexShrink: 0 }}>{title}</Typography>
          <Stack component="nav" aria-label={t('layout.navigation')} direction="row" spacing={2.5}
            sx={{ display: { xs: 'none', lg: 'flex' }, alignItems: 'center' }}>{links}</Stack>
          <Stack direction="row" spacing={0.5} sx={{ alignItems: 'center' }}>
            <Stack direction="row" role="group" aria-label={t('layout.language')}>
              {(['de', 'en'] as const).map(lng => <Button key={lng} size="small" aria-pressed={language === lng}
                aria-label={lng === 'de' ? 'Deutsch' : 'English'}
                onClick={() => { changeLanguage(lng); setMobileOpen(false); }}
                sx={{ color: language === lng ? 'primary.main' : 'text.secondary', fontWeight: language === lng ? 750 : 400,
                  bgcolor: language === lng ? '#f0f4ef' : 'transparent' }}>{lng.toUpperCase()}</Button>)}
            </Stack>
            <IconButton aria-label={t(mobileOpen ? 'layout.closeMenu' : 'layout.openMenu')}
              aria-expanded={mobileOpen} aria-controls={mobileOpen ? 'mobile-navigation' : undefined}
              onClick={() => setMobileOpen(open => !open)} sx={{ display: { lg: 'none' } }}>
              {mobileOpen ? <CloseIcon /> : <MenuIcon />}
            </IconButton>
          </Stack>
        </Stack>
        <Collapse in={mobileOpen} unmountOnExit sx={{ display: { lg: 'none' } }}>
          <Stack component="nav" id="mobile-navigation" aria-label={t('layout.navigation')}
            sx={{ pb: 2, alignItems: 'flex-start', borderTop: '1px solid', borderColor: 'divider' }}>{links}</Stack>
        </Collapse>
      </Container>
    </Box>
  );
}
