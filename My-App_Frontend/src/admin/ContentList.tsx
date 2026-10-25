import { useEffect, useState } from 'react';
import { Button, Card, CardContent, Chip, MenuItem, Stack, TextField, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { Content, Kind, Page } from './model';
import { Call } from './MediaPanel';
export default function ContentList({ kind, call, edit, revision }: { kind: Kind; call: Call; edit: (id: string) => void; revision: number }) {
  const { t, i18n } = useTranslation(); const [page, setPage] = useState(0); const [status, setStatus] = useState(''); const [data, setData] = useState<Page<Content>>(); const [retry, setRetry] = useState(0); const [loading, setLoading] = useState(true);
  useEffect(() => { let active = true; setLoading(true); setData(undefined); call<Page<Content>>('/' + kind + '?page=' + page + '&size=10' + (status ? '&status=' + status : '')).then(value => { if (active) setData(value); }).catch(() => {}).finally(() => { if (active) setLoading(false); }); return () => { active = false; }; }, [call, kind, status, page, revision, retry]);
  const lang = i18n.language.startsWith('de') ? 'de' : 'en';
  return <Stack spacing={2}><Typography component="h2" variant="h4">{t('admin.' + kind)}</Typography><Button variant="contained" onClick={() => edit('new')}>{t('admin.create')}</Button>
    <TextField select label={t('admin.status')} value={status} onChange={e => { setStatus(e.target.value); setPage(0); }}>{['', 'draft', 'published', 'legacy'].map(s => <MenuItem key={s} value={s}>{t('admin.' + (s || 'all'))}</MenuItem>)}</TextField>
    {loading && <Typography role="status">{t('admin.loading')}</Typography>}
    {!loading && !data && <Button onClick={() => setRetry(r => r + 1)}>{t('admin.retry')}</Button>}
    {data?.items.length === 0 && <Typography>{t('admin.empty')}</Typography>}
    {data?.items.map(c => <Card key={c.id} variant="outlined"><CardContent><Stack spacing={1}><Typography component="h3" variant="h6">{c.title[lang] || c.title.de || c.slug}</Typography><Chip sx={{ alignSelf: 'flex-start' }} label={t('admin.' + (c.status || 'legacy'))} color={c.status === 'published' ? 'success' : 'default'} /><Typography sx={{ overflowWrap: 'anywhere' }}>{c.slug}</Typography><Button onClick={() => edit(c.id)}>{t('admin.edit')}</Button></Stack></CardContent></Card>)}
    <Stack direction="row" spacing={2}><Button disabled={loading || page === 0} onClick={() => setPage(p => p - 1)}>{t('admin.previous')}</Button><Typography>{page + 1}</Typography><Button disabled={loading || !data || (page + 1) * 10 >= data.totalElements} onClick={() => setPage(p => p + 1)}>{t('admin.next')}</Button></Stack>
  </Stack>;
}
