import { useEffect, useState } from 'react';
import { Alert, Button, Card, CardContent, Stack, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { Call } from './MediaPanel';
import { Content, Page, Selection } from './model';
export default function HomepageEditor({ call, dirty, token }: { token: string; call: Call; dirty: (value: boolean) => void }) {
  const { t, i18n } = useTranslation(); const [selection, setSelection] = useState<Selection>(); const [original, setOriginal] = useState(''); const [projects, setProjects] = useState<Page<Content>>(); const [page, setPage] = useState(0); const [busy, setBusy] = useState(false); const [retry, setRetry] = useState(0);
  const modified = !!selection && selection.projectIds.join('|') !== original;
  useEffect(() => { dirty(modified); return () => dirty(false); }, [modified, dirty]);
  useEffect(() => { if (!token || selection) return; let active = true; call<Selection>('/homepage-projects').then(s => { if (active) { setSelection(s); setOriginal(s.projectIds.join('|')); } }).catch(() => {}); return () => { active = false; }; }, [call, retry, token, selection]);
  useEffect(() => { if (!token) return; let active = true; call<Page<Content>>('/projects?page=' + page + '&size=10').then(p => { if (active) setProjects(p); }).catch(() => {}); return () => { active = false; }; }, [call, page, retry, token]);
  const lang = i18n.language.startsWith('de') ? 'de' : 'en';
  if (!selection) return <Button onClick={() => setRetry(r => r + 1)}>{t('admin.retry')}</Button>;
  function ids(projectIds: string[]) { setSelection(s => s ? { ...s, projectIds } : s); }
  return <Stack spacing={2}><Typography component="h2" variant="h4">{t('admin.homepage')}</Typography><Alert severity="info">{t('admin.selectionHelp')}</Alert>
    {selection.projectIds.map((id, index) => <Card key={id} variant="outlined"><CardContent><Typography sx={{ overflowWrap: 'anywhere' }}>{index + 1}. {projects?.items.find(p => p.id === id)?.title[lang] || id}</Typography><Stack direction="row" spacing={1} sx={{ flexWrap: 'wrap' }}><Button disabled={busy || index === 0} onClick={() => { const next = [...selection.projectIds]; [next[index - 1], next[index]] = [next[index], next[index - 1]]; ids(next); }}>{t('admin.up')}</Button><Button disabled={busy || index === selection.projectIds.length - 1} onClick={() => { const next = [...selection.projectIds]; [next[index + 1], next[index]] = [next[index], next[index + 1]]; ids(next); }}>{t('admin.down')}</Button><Button disabled={busy} onClick={() => ids(selection.projectIds.filter(value => value !== id))}>{t('admin.remove')}</Button></Stack></CardContent></Card>)}
    <Button variant="contained" disabled={!modified || busy} onClick={async () => { setBusy(true); try { const next = await call<Selection>('/homepage-projects', 'PUT', { projectIds: selection.projectIds }, selection.version || 0); setSelection(next); setOriginal(next.projectIds.join('|')); } catch { /* retain selection on conflict */ } finally { setBusy(false); } }}>{t('admin.save')}</Button>
    {projects?.items.map(p => <Stack key={p.id} direction="row" spacing={2} sx={{ alignItems: 'center' }}><Typography sx={{ flex: 1 }}>{p.title[lang] || p.slug} – {t('admin.' + (p.status || 'legacy'))}</Typography><Button disabled={busy || selection.projectIds.length >= 3 || selection.projectIds.includes(p.id)} onClick={() => ids([...selection.projectIds, p.id])}>{t('admin.add')}</Button></Stack>)}
    <Stack direction="row"><Button disabled={page === 0} onClick={() => setPage(p => p - 1)}>{t('admin.previous')}</Button><Button disabled={!projects || (page + 1) * 10 >= projects.totalElements} onClick={() => setPage(p => p + 1)}>{t('admin.next')}</Button></Stack>
  </Stack>;
}
