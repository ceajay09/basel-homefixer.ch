import { createTheme } from '@mui/material/styles';

export const brandGradient = 'linear-gradient(135deg, #2d5a3d 0%, #3d7a4d 100%)';

const theme = createTheme({
  palette: {
    primary: { main: '#2d5a3d', light: '#3d7a4d', dark: '#20432c', contrastText: '#ffffff' },
    secondary: { main: '#d4a574', light: '#e8c9a0', dark: '#9a6835', contrastText: '#1f1f1f' },
    background: { default: '#ffffff', paper: '#ffffff' },
    text: { primary: '#1f1f1f', secondary: '#5a5a5a' },
    divider: '#d9d9d9',
  },
  shape: { borderRadius: 12 },
  typography: {
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", sans-serif',
    fontSize: 16,
    h1: { fontSize: 'clamp(1.85rem, 4vw, 3rem)', fontWeight: 700, lineHeight: 1.2 },
    h2: { fontSize: 'clamp(1.6rem, 3vw, 2rem)', fontWeight: 700, lineHeight: 1.3 },
    h3: { fontSize: '1.5rem', fontWeight: 700, lineHeight: 1.4 },
    h4: { fontSize: '1.3rem', fontWeight: 600 },
    h5: { fontSize: '1.15rem', fontWeight: 600 },
    h6: { fontSize: '1.05rem', fontWeight: 600 },
    body1: { fontSize: '1rem', lineHeight: 1.7 },
    body2: { fontSize: '0.9375rem', lineHeight: 1.8 },
    button: { textTransform: 'none', fontWeight: 600 },
  },
  components: {
    MuiCssBaseline: { styleOverrides: {
      body: { overflowWrap: 'anywhere' },
      'a, button, input, textarea, select': { WebkitTapHighlightColor: 'transparent' },
      ':focus-visible': { outline: '3px solid #9a6835', outlineOffset: '4px' },
      'section[id]': { scrollMarginTop: '110px' },
      '@media (prefers-reduced-motion: reduce)': { '*, *::before, *::after': { animation: 'none !important', transition: 'none !important', scrollBehavior: 'auto !important' } },
    } },
    MuiContainer: { defaultProps: { maxWidth: 'lg' } },
    MuiButton: { defaultProps: { disableElevation: true }, styleOverrides: {
      root: { borderRadius: 12, padding: '12px 28px', minHeight: 44 },
      sizeSmall: { padding: '6px 10px', minWidth: 40 },
    } },
    MuiPaper: { styleOverrides: { root: { backgroundImage: 'none' } } },
    MuiCard: { styleOverrides: { root: { borderRadius: 12, boxShadow: '0 4px 16px rgba(0,0,0,0.06)' } } },
    MuiLink: { defaultProps: { underline: 'hover' } },
  },
});

export default theme;
