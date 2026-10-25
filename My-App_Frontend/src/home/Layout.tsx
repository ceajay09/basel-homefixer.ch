import { ReactNode, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { Box, CssBaseline, ThemeProvider } from '@mui/material';
import { useTranslation } from 'react-i18next';
import Header from './Header';
import Footer from './Footer';
import theme from './theme';
import { siteConfig } from './siteConfig';

interface LayoutProps { children: ReactNode; }

export default function Layout({ children }: LayoutProps) {
  const { t } = useTranslation();
  const { pathname, hash, key } = useLocation();

  useEffect(() => {
    if (!hash) return;
    // React Router changes the URL; scroll after the destination page has mounted.
    const frame = requestAnimationFrame(() => {
      let id: string;
      try { id = decodeURIComponent(hash.slice(1)); } catch { return; }
      const target = document.getElementById(id);
      target?.scrollIntoView({ block: 'start', behavior: 'instant' });
      if (target?.hasAttribute('tabindex')) target.focus({ preventScroll: true });
    });
    return () => cancelAnimationFrame(frame);
  }, [pathname, hash, key]);
  const sections = [
    { title: t('layout.sections.home'), url: '/' },
    { title: t('layout.sections.services'), url: '/services' },
    { title: t('layout.sections.projects'), url: siteConfig.projectsHref },
    { title: t('layout.sections.aboutUs'), url: '/aboutUs' },
    { title: t('layout.sections.blog'), url: '/blog' },
    { title: t('layout.sections.contactUs'), url: '/contactUs' },
    { title: t('layout.sections.realEstateForRent'), url: siteConfig.airbnbUrl },
  ];

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
        <Header title={siteConfig.name} sections={sections} />
        <Box component="main" id="main-content" sx={{ flex: 1 }}>{children}</Box>
        <Footer title={t('layout.footer.title')} description={t('layout.footer.description')} />
      </Box>
    </ThemeProvider>
  );
}
