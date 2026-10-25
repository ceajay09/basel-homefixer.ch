import { useCallback, useEffect, useState } from 'react';
import { Alert, AppBar, Box, Button, Container, CssBaseline, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField, ThemeProvider, Toolbar, Typography } from '@mui/material';
import { useBlocker, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import theme from '../home/theme';
import { ApiError, request } from './api';
import { Kind } from './model';
import ContentEditor from './ContentEditor';
import ContentList from './ContentList';
import MediaPanel, { Call } from './MediaPanel';
import HomepageEditor from './HomepageEditor';
interface Session { token: string; expiresAt: string; account: { admin: boolean } }
export default function AdminArea() {
  const { t, i18n } = useTranslation(); const navigate = useNavigate();
  const [visited, setVisited] = useState(false);
  const [session, setSession] = useState<Session>(); const [email, setEmail] = useState(''); const [password, setPassword] = useState(''); const [loginBusy, setLoginBusy] = useState(false);
  const [notice, setNotice] = useState(''); const [error, setError] = useState(''); const [pending, setPending] = useState(0);
  const [section, setSection] = useState<Kind | 'media' | 'homepage'>('projects'); const [id, setId] = useState(''); const [revision, setRevision] = useState(0); const [dirty, setDirty] = useState(false);
  const expire = useCallback(() => { setSession(undefined); setPassword(''); setNotice('expired'); }, []);
  useEffect(() => { if (!session) return; const delay = new Date(session.expiresAt).getTime() - Date.now(); if (delay <= 0) { expire(); return; } const timer = setTimeout(expire, delay); return () => clearTimeout(timer); }, [session, expire]);
  useEffect(() => { if (!dirty && !pending) return; const warn = (e: BeforeUnloadEvent) => { e.preventDefault(); e.returnValue = ''; }; window.addEventListener('beforeunload', warn); return () => window.removeEventListener('beforeunload', warn); }, [dirty, pending]);
  const blocker = useBlocker(dirty || pending > 0);
  const fail = useCallback((err: unknown) => {
    if (err instanceof ApiError && err.status === 401) { expire(); return; }
    const code = err instanceof ApiError ? err.code : 'network';
    const fieldKey = code.replace(/^(missing|invalid)_/, '');
    const field = ({ cover_image: 'cover', cover_alt: 'coverAlt', media_alt: 'coverAlt', media_references: 'media', media_reference: 'media', metric: 'facts', template_metric: 'facts' } as Record<string,string>)[fieldKey] || fieldKey;
    if (/^(missing|invalid)_/.test(code) && i18n.exists('admin.' + field) && !i18n.exists('admin.errors.' + code)) { setError(t('admin.validationField', { field: t('admin.' + field) })); return; }
    setError(i18n.exists('admin.errors.' + code) ? t('admin.errors.' + code) : code.startsWith('missing_') || code.startsWith('invalid_') || code === 'undeclared_markdown_media' ? t('admin.validationError') : t('admin.requestError'));
  }, [expire, t, i18n]);
  const call: Call = useCallback(async <T,>(path: string, method = 'GET', body?: unknown, version?: number) => {
    setPending(n => n + 1); setError('');
    try { if (!session) throw new ApiError(401, 'unauthorized'); return await request<T>(path.startsWith('/categories?') ? '/api/content' + path : '/api/admin' + path, session.token, method, body, version); }
    catch (err) { fail(err); throw err; } finally { setPending(n => n - 1); }
  }, [session, fail]);
  function transition(action: () => void) { if (pending > 0) return; if (!dirty || window.confirm(t('admin.discard'))) { setDirty(false); setError(''); action(); } }
  async function login(e: React.FormEvent) {
    e.preventDefault(); if (loginBusy) return; setLoginBusy(true); setError('');
    try { const s = await request<Session>('/api/login', undefined, 'POST', { email, password }); if (!s.account.admin) { setError(t('admin.errors.forbidden')); return; } setVisited(true); setSession(s); setNotice(''); setPassword(''); }
    catch (err) { if (err instanceof ApiError && err.code === 'invalid_credentials') setError(t('admin.errors.invalid_credentials')); else fail(err); } finally { setLoginBusy(false); }
  }
  return <ThemeProvider theme={theme}><CssBaseline /><AppBar position="static"><Toolbar role="toolbar" sx={{ flexWrap: 'wrap', gap: 1 }}><Typography component="h1" variant="h6" sx={{ flex: 1, flexBasis: { xs: '100%', sm: 0 }, minWidth: 0 }}>{t('admin.heading')}</Typography><Button color="inherit" onClick={() => i18n.changeLanguage(i18n.language.startsWith('de') ? 'en' : 'de')}>{i18n.language.startsWith('de') ? 'EN' : 'DE'}</Button><Button color="inherit" disabled={pending > 0} onClick={() => transition(() => navigate('/'))}>{t('admin.website')}</Button>{session && <Button color="inherit" disabled={pending > 0} onClick={() => transition(() => { const token = session.token; setSession(undefined); setPassword(''); setId(''); setVisited(false); setNotice('loggedOut'); request('/api/logout', token, 'POST').catch(() => {}); })}>{t('admin.logout')}</Button>}</Toolbar></AppBar>
    <Container maxWidth="lg" sx={{ py: { xs: 2, md: 4 }, minHeight: '90vh' }}>
      {session && <Typography variant="body2" sx={{ mb: 2 }}>{t('admin.sessionHelp')}</Typography>}
      <Box sx={{ display: session ? 'block' : 'none' }}>
        <Stack component="nav" aria-label={t('admin.heading')} direction="row" sx={{ gap: 1, flexWrap: 'wrap', mb: 3 }}>{(['projects', 'blog', 'media', 'homepage'] as const).map(value => <Button key={value} disabled={pending > 0} aria-current={section === value ? 'page' : undefined} variant={section === value ? 'contained' : 'outlined'} onClick={() => transition(() => { setSection(value); setId(''); })}>{t('admin.' + value)}</Button>)}</Stack>
        {error && <Alert severity="error" role="alert" sx={{ mb: 2 }}>{error}</Alert>}
        {id && <Button sx={{ mb: 2 }} disabled={pending > 0} onClick={() => transition(() => setId(''))}>{t('admin.back')}</Button>}
        {session && (section === 'projects' || section === 'blog') && !id && <ContentList key={section} kind={section} call={call} edit={next => transition(() => setId(next))} revision={revision} />}
        {(section === 'projects' || section === 'blog') && id && <ContentEditor key={section + ':' + id} kind={section} id={id} call={call} token={session?.token || ''} expired={expire} dirty={setDirty} saved={() => setRevision(r => r + 1)} />}
        {visited && section === 'media' && <MediaPanel call={call} token={session?.token || ''} expired={expire} dirty={setDirty} />}
        {visited && section === 'homepage' && <HomepageEditor token={session?.token || ''} call={call} dirty={setDirty} />}
      </Box>
      {!session && <Typography>{t('admin.loginHelp')}</Typography>}
    </Container>
    <Dialog open={!session} fullWidth maxWidth="xs" aria-labelledby="login-title"><DialogTitle id="login-title">{t('admin.login')} <Button onClick={() => i18n.changeLanguage(i18n.language.startsWith('de') ? 'en' : 'de')}>{i18n.language.startsWith('de') ? 'EN' : 'DE'}</Button></DialogTitle><Box component="form" onSubmit={login}><DialogContent><Stack spacing={2}>{notice && <Alert severity="info">{t('admin.' + notice)}</Alert>}{error && <Alert severity="error">{error}</Alert>}<TextField autoFocus label={t('admin.email')} type="email" autoComplete="username" required value={email} onChange={e => setEmail(e.target.value)} /><TextField label={t('admin.password')} type="password" autoComplete="current-password" required value={password} onChange={e => setPassword(e.target.value)} /><Typography variant="body2">{t('admin.loginHelp')}</Typography></Stack></DialogContent><DialogActions><Button disabled={loginBusy} onClick={() => transition(() => navigate('/'))}>{t('admin.website')}</Button><Button type="submit" variant="contained" disabled={loginBusy}>{t(loginBusy ? 'admin.loading' : 'admin.login')}</Button></DialogActions></Box></Dialog>
    <Dialog open={blocker.state === 'blocked'} aria-labelledby="leave-title"><DialogTitle id="leave-title">{t('admin.unsaved')}</DialogTitle><DialogContent>{t('admin.discard')}</DialogContent><DialogActions><Button onClick={() => blocker.reset?.()}>{t('admin.cancel')}</Button><Button disabled={pending > 0} onClick={() => blocker.proceed?.()}>{t('admin.leave')}</Button></DialogActions></Dialog>
  </ThemeProvider>;
}
