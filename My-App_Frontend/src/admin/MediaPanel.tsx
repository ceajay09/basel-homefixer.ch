import { useEffect, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Checkbox, FormControlLabel, Stack, TextField, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { Localized, Media, Page } from './model';
import ProtectedImage from './ProtectedImage';
export type Call = <T>(path: string, method?: string, body?: unknown, version?: number) => Promise<T>;
function AltEditor({ media, call, changed, dirty }: { media: Media; call: Call; changed: (m: Media) => void; dirty: (value: boolean) => void }) {
  const { t } = useTranslation(); const [source, setSource] = useState(media); const [alt, setAlt] = useState(media.alt); const [busy, setBusy] = useState(false);
  const modified = JSON.stringify(alt) !== JSON.stringify(source.alt);
  return <Stack spacing={1}>
    {(['de', 'en'] as const).map(lang => <TextField disabled={busy} key={lang} label={t('admin.coverAlt') + ' ' + lang.toUpperCase()} value={alt[lang] || ''} slotProps={{ htmlInput: { maxLength: 1000 } }} onChange={e => { const next = { ...alt, [lang]: e.target.value }; setAlt(next); dirty(JSON.stringify(next) !== JSON.stringify(source.alt)); }} />)}
    <Button disabled={!modified || busy} onClick={async () => { setBusy(true); try { const next = await call<Media>('/media/' + media.id + '/alt', 'PUT', { alt }, source.version || 0); setSource(next); changed(next); dirty(false); } catch { /* parent presents error */ } finally { setBusy(false); } }}>{t('admin.saveAlt')}</Button>
  </Stack>;
}
export default function MediaPanel({ call, token, expired, choose, associated = [], toggle, dirty }: { call: Call; token: string; expired: () => void; choose?: (m: Media) => void; associated?: string[]; toggle?: (id: string) => void; dirty: (value: boolean) => void }) {
  const { t } = useTranslation(); const [page, setPage] = useState(0); const [data, setData] = useState<Page<Media>>(); const [retry, setRetry] = useState(0); const [error, setError] = useState(false);
  const [alt, setAlt] = useState<Localized>({}); const [file, setFile] = useState<File>(); const [busy, setBusy] = useState(false); const [edit, setEdit] = useState<string>(); const [uploadDirty, setUploadDirty] = useState(false); const [altDirty, setAltDirty] = useState(false);
  useEffect(() => { dirty(uploadDirty || altDirty); return () => dirty(false); }, [uploadDirty, altDirty, dirty]);
  useEffect(() => { if (!token) return; let active = true; setError(false); call<Page<Media>>('/media?page=' + page + '&size=6').then(value => { if (active) setData(value); }).catch(() => { if (active) setError(true); }); return () => { active = false; }; }, [call, page, retry, token]);
  function leaveEdit(action: () => void) { if (!altDirty || window.confirm(t('admin.discard'))) { setAltDirty(false); action(); } }
  return <Stack spacing={2}>
    <Typography component="h2" variant="h5">{t('admin.media')}</Typography>
    <Typography>{t('admin.uploadHelp')}</Typography>
    <Box component="form" onSubmit={async e => { e.preventDefault(); if (!file || busy) return; setBusy(true); try { const form = new FormData(); form.append('file', file); form.append('alt', new Blob([JSON.stringify(alt)], { type: 'application/json' })); const m = await call<Media>('/media', 'POST', form); setRetry(r => r + 1); setFile(undefined); setAlt({}); setUploadDirty(false); choose?.(m); (e.target as HTMLFormElement).reset(); } catch { /* retain upload and alt inputs */ } finally { setBusy(false); } }}>
      <Stack spacing={2}>
        <Box component="input" aria-label={t('admin.chooseFile')} type="file" accept="image/jpeg,image/png" disabled={busy} onChange={(e: React.ChangeEvent<HTMLInputElement>) => { setFile(e.target.files?.[0]); setUploadDirty(!!e.target.files?.[0] || Object.values(alt).some(Boolean)); }} />
        {(['de', 'en'] as const).map(lang => <TextField disabled={busy} key={lang} label={t('admin.uploadAlt') + ' ' + lang.toUpperCase()} value={alt[lang] || ''} slotProps={{ htmlInput: { maxLength: 1000 } }} onChange={e => { setAlt(a => ({ ...a, [lang]: e.target.value })); setUploadDirty(true); }} />)}
        <Button type="submit" variant="contained" disabled={!file || busy}>{t(busy ? 'admin.loading' : 'admin.upload')}</Button>
      </Stack>
    </Box>
    {error && <Alert severity="error">{t('admin.loadError')} <Button onClick={() => setRetry(r => r + 1)}>{t('admin.retry')}</Button></Alert>}
    {!data && !error && <Typography role="status">{t('admin.loading')}</Typography>}
    {data?.items.length === 0 && <Typography>{t('admin.empty')}</Typography>}
    <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', lg: 'repeat(3, 1fr)' }, gap: 2 }}>
      {data?.items.map(m => <Card key={m.id} variant="outlined"><CardContent><Stack spacing={1}>
        <ProtectedImage id={m.id} token={token} alt={m.alt.de || m.alt.en || ''} expired={expired} />
        <Typography sx={{ overflowWrap: 'anywhere' }}>{m.alt.de || m.alt.en || t('admin.noAlt')}</Typography>
        <Typography variant="caption" sx={{ overflowWrap: 'anywhere' }}>{m.id}</Typography>
        {choose && <Button disabled={busy} onClick={() => choose(m)}>{t('admin.useCover')}</Button>}
        {toggle && <FormControlLabel control={<Checkbox disabled={busy} checked={associated.includes(m.id)} onChange={() => toggle(m.id)} />} label={t('admin.associate')} />}
        <Button disabled={busy} onClick={() => { if (edit) leaveEdit(() => setEdit(edit === m.id ? undefined : m.id)); else setEdit(m.id); }}>{t('admin.editAlt')}</Button>
        {edit === m.id && <AltEditor key={m.id} media={m} call={call} dirty={setAltDirty} changed={next => setData(d => d ? { ...d, items: d.items.map(value => value.id === next.id ? next : value) } : d)} />}
      </Stack></CardContent></Card>)}
    </Box>
    <Stack direction="row" spacing={2}><Button disabled={page === 0 || busy} onClick={() => { if (!edit) setPage(p => p - 1); else leaveEdit(() => { setEdit(undefined); setPage(p => p - 1); }); }}>{t('admin.previous')}</Button><Typography>{page + 1}</Typography><Button disabled={!data || (page + 1) * 6 >= data.totalElements || busy} onClick={() => { if (!edit) setPage(p => p + 1); else leaveEdit(() => { setEdit(undefined); setPage(p => p + 1); }); }}>{t('admin.next')}</Button></Stack>
  </Stack>;
}
