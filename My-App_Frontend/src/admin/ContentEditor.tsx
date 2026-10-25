import ProjectFacts from '../home/ProjectFacts';
import { useEffect, useState } from 'react';
import { Alert, Box, Button, Chip, Dialog, DialogActions, DialogContent, DialogTitle, FormControlLabel, Checkbox, MenuItem, Stack, TextField, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { Content, Kind, Lang, Localized, Media, Preview, Selection, blank, editable, publicationMissing } from './model';
import MediaPanel, { Call } from './MediaPanel';
import ProtectedImage from './ProtectedImage';
import SafeMarkdown from '../home/SafeMarkdown';
const facts = ['investmentChf', 'areaM2', 'apartments', 'thermostats', 'doors', 'complexity'] as const;
export default function ContentEditor({ kind, id, call, token, expired, dirty, saved }: { kind: Kind; id: string; call: Call; token: string; expired: () => void; dirty: (value: boolean) => void; saved: () => void }) {
  const { t, i18n } = useTranslation(); const [doc, setDoc] = useState<Content>(blank); const [baseline, setBaseline] = useState(''); const [loaded, setLoaded] = useState(id === 'new');
  const [lang, setLang] = useState<Lang>(i18n.language.startsWith('de') ? 'de' : 'en'); const [categories, setCategories] = useState<{ id: string; label: string }[]>([]);
  const [featured, setFeatured] = useState(false);
  const [busy, setBusy] = useState(false); const [mediaDirty, setMediaDirty] = useState(false); const [preview, setPreview] = useState<Preview>(); const [confirm, setConfirm] = useState<'publish' | 'withdraw'>(); const [hint, setHint] = useState('');
  const modified = loaded && (baseline ? JSON.stringify(doc) !== baseline : JSON.stringify(doc) !== JSON.stringify(blank()));
  useEffect(() => { dirty(modified || mediaDirty); return () => dirty(false); }, [modified, mediaDirty, dirty]);
  useEffect(() => { if (id === 'new' || loaded || !token) return; call<Content>('/' + kind + '/' + id).then(value => { setDoc(value); setBaseline(JSON.stringify(value)); setLoaded(true); }).catch(() => {}); }, [id, kind, loaded, call, token]);
  useEffect(() => { if (!token) return; call<{ id: string; label: string }[]>('/categories?type=' + kind + '&lang=' + (i18n.language.startsWith('de') ? 'de' : 'en')).then(setCategories).catch(() => {}); }, [kind, i18n.language, call, token]);
  useEffect(() => { if (!token) { setPreview(undefined); return; } if (kind === 'projects') call<Selection>('/homepage-projects').then(s => setFeatured(s.projectIds.includes(doc.id))).catch(() => {}); }, [call, token, kind, doc.id]);
  const readOnly = doc.status === 'published';
  const update = (value: Partial<Content>) => setDoc(d => ({ ...d, ...value }));
  const localized = (field: string) => (doc[field as keyof Content] || {}) as Localized;
  async function action(mode: 'save' | 'preview' | 'publish' | 'withdraw') {
    if (busy) return; setBusy(true); setHint('');
    try {
      if (mode === 'preview') { setPreview(await call<Preview>('/' + kind + '/' + doc.id + '/preview?lang=' + lang)); return; }
      const path = '/' + kind + (doc.id ? '/' + doc.id : '');
      const result = mode === 'save' ? await call<Content>(path, doc.id ? 'PUT' : 'POST', editable(doc), doc.id ? doc.version || 0 : undefined)
        : await call<Content>(path + '/' + mode, 'POST', undefined, doc.version || 0);
      setDoc(result); setBaseline(JSON.stringify(result)); setHint(t('admin.saved')); saved();
    } catch { /* keep all unsaved fields */ } finally { setBusy(false); setConfirm(undefined); }
  }
  if (!loaded) return <Stack><Typography role="status">{t('admin.loading')}</Typography><Button onClick={() => call<Content>('/' + kind + '/' + id).then(c => { setDoc(c); setBaseline(JSON.stringify(c)); setLoaded(true); }).catch(() => {})}>{t('admin.retry')}</Button></Stack>;
  const missing = publicationMissing(doc, kind, featured);
  const localField = (field: string, multiline = false, max = 1000) => <TextField key={field} fullWidth label={t('admin.' + field) + ' ' + lang.toUpperCase()} value={localized(field)[lang] || ''} multiline={multiline} minRows={multiline ? field === 'description' ? 8 : 3 : undefined} slotProps={{ htmlInput: { maxLength: max } }} onChange={e => update({ [field]: { ...localized(field), [lang]: e.target.value } })} />;
  const displayText = (value: string | Localized | undefined) => typeof value === 'string' ? value : value?.[lang] || '';
  return <Stack spacing={3}>
    <Stack direction="row" spacing={1} sx={{ alignItems: 'center' }}><Typography component="h2" variant="h4">{t('admin.' + kind)}</Typography><Chip label={t('admin.' + (doc.status || 'legacy'))} color={readOnly ? 'success' : 'default'} /></Stack>
    {readOnly && <Alert severity="info">{t('admin.withdrawHelp')}</Alert>}
    {hint && <Alert severity="success" role="status">{hint}</Alert>}
    <Stack direction="row" spacing={1}><Button aria-pressed={lang === 'de'} variant={lang === 'de' ? 'contained' : 'outlined'} onClick={() => setLang('de')}>DE</Button><Button aria-pressed={lang === 'en'} variant={lang === 'en' ? 'contained' : 'outlined'} onClick={() => setLang('en')}>EN</Button></Stack>
    <Box component="fieldset" disabled={busy || readOnly} sx={{ border: 0, p: 0, m: 0, minWidth: 0 }}><Stack spacing={2}>
      <TextField label={t('admin.slug')} value={doc.slug} slotProps={{ htmlInput: { maxLength: 120 } }} helperText={t('admin.slugHelp')} onChange={e => update({ slug: e.target.value })} />
      {localField('title', false, 200)}{localField('excerpt', true)}{localField('description', true, 50000)}
      {kind === 'blog' && <><Alert severity="info">{t('admin.markdownHelp')}</Alert><Typography component="h3" variant="h6">{t('admin.markdownPreview')}</Typography><SafeMarkdown allowedMediaIds={[...doc.mediaIds, ...(doc.coverImageId ? [doc.coverImageId] : [])]} renderImage={(imageId, alt) => <ProtectedImage id={imageId} token={token} alt={alt} expired={expired} large />}>{doc.description[lang] || ''}</SafeMarkdown></>}
      <Typography component="h3" variant="h6">{t('admin.categories')}</Typography>
      <Box>{categories.map(c => <FormControlLabel key={c.id} control={<Checkbox checked={doc.categories.includes(c.id)} onChange={e => update({ categories: e.target.checked ? [...doc.categories, c.id] : doc.categories.filter(id => id !== c.id) })} />} label={c.label} />)}</Box>
      {kind === 'projects' && <>
        {localField('location', false, 250)}{localField('challenge', true, 3000)}{localField('result', true, 3000)}
        <TextField label={t('admin.tags') + ' ' + lang.toUpperCase()} value={doc.tags?.[lang]?.join(', ') || ''} helperText={t('admin.commaHelp')} onChange={e => update({ tags: { ...doc.tags, [lang]: e.target.value.split(',').map(s => s.trim()) } })} />
        <TextField label={t('admin.dates')} value={doc.dates?.join(', ') || ''} helperText={t('admin.datesHelp')} onChange={e => update({ dates: e.target.value ? e.target.value.split(',').map(s => s.trim()) : [] })} />
        <Typography component="h3" variant="h6">{t('admin.duration')}</Typography>
        <TextField select label={t('admin.unit')} value={doc.duration?.unit || ''} onChange={e => update({ duration: e.target.value ? { min: doc.duration?.min ?? ('' as unknown as number), max: doc.duration?.max ?? ('' as unknown as number), unit: e.target.value } : undefined })}>
          <MenuItem value="">{t('admin.unspecified')}</MenuItem><MenuItem value="months">{t('admin.months')}</MenuItem><MenuItem value="weeks">{t('admin.weeks')}</MenuItem>
        </TextField>
        {doc.duration && <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>{(['min', 'max'] as const).map(field => <TextField key={field} label={t('admin.' + field)} type="number" value={doc.duration![field]} slotProps={{ htmlInput: { min: 1, max: 1200, step: 1 } }} onChange={e => update({ duration: { ...doc.duration!, [field]: e.target.value === '' ? '' : Number(e.target.value) } as Content['duration'] })} />)}</Stack>}
        <Typography component="h3" variant="h6">{t('admin.facts')}</Typography>
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' }, gap: 2 }}>{facts.map(field => <TextField key={field} label={t('admin.' + field)} type="number" value={doc[field] ?? ''} slotProps={{ htmlInput: { min: 0, step: 1, ...(field === 'complexity' ? { max: 5 } : {}) } }} onChange={e => update({ [field]: e.target.value === '' ? undefined : Number(e.target.value) })} />)}</Box>
      </>}
      <Typography component="h3" variant="h6">{t('admin.cover')}</Typography>
      {doc.coverImageId ? <><ProtectedImage id={doc.coverImageId} token={token} alt={doc.coverAlt[lang] || ''} expired={expired} /><Button onClick={() => update({ coverImageId: undefined, coverAlt: {} })}>{t('admin.removeCover')}</Button></> : <Typography>{t('admin.noCover')}</Typography>}
      {localField('coverAlt')}
    </Stack></Box>
    {!readOnly && <Box component="fieldset" disabled={busy} sx={{ border: 0, p: 0, m: 0, minWidth: 0 }}><MediaPanel call={call} token={token} expired={expired} dirty={setMediaDirty} associated={doc.mediaIds} choose={(media: Media) => update({ coverImageId: media.id, coverAlt: { ...media.alt } })} toggle={mediaId => update({ mediaIds: doc.mediaIds.includes(mediaId) ? doc.mediaIds.filter(id => id !== mediaId) : [...doc.mediaIds, mediaId] })} /></Box>}
    {kind === 'blog' && doc.mediaIds.length > 0 && !readOnly && <Stack spacing={1}><Typography>{t('admin.insertImageHelp')}</Typography>{doc.mediaIds.map(imageId => <Button key={imageId} disabled={busy} onClick={() => update({ description: { ...doc.description, [lang]: (doc.description[lang] || '') + '\n\n![' + t('admin.replaceAlt') + '](/api/media/' + imageId + '/large)' } })}>{t('admin.insertImage')} {imageId.slice(0, 8)}</Button>)}</Stack>}
    {!readOnly && missing.length > 0 && <Alert severity="info"><Typography>{t('admin.missing')}</Typography><Box component="ul" sx={{ pl: 3 }}>{missing.map(field => { const [key, language] = field.split(' '); return <li key={field}>{t('admin.' + key)} {language}</li>; })}</Box><Typography>{t('admin.serverValidation')}</Typography></Alert>}
    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ flexWrap: 'wrap', gap: 1 }}>
      <Button variant="contained" disabled={busy || readOnly || mediaDirty || !modified && !!doc.id} onClick={() => action('save')}>{t('admin.save')}</Button>
      <Button disabled={busy || !doc.id || modified || mediaDirty} onClick={() => action('preview')}>{t('admin.preview')}</Button>
      {readOnly ? <Button color="warning" variant="outlined" disabled={busy} onClick={() => setConfirm('withdraw')}>{t('admin.withdraw')}</Button> : <Button color="success" variant="outlined" disabled={busy || !doc.id || modified || mediaDirty || missing.length > 0} onClick={() => setConfirm('publish')}>{t('admin.publish')}</Button>}
    </Stack>
    {(modified || mediaDirty) && <Typography role="status">{t('admin.unsaved')}</Typography>}
    <Dialog open={!!confirm} onClose={() => !busy && setConfirm(undefined)} aria-labelledby="publish-title"><DialogTitle id="publish-title">{t('admin.' + (confirm || 'publish'))}</DialogTitle><DialogContent>{t(confirm === 'publish' ? 'admin.publishConfirm' : 'admin.withdrawConfirm')}</DialogContent><DialogActions><Button disabled={busy} onClick={() => setConfirm(undefined)}>{t('admin.cancel')}</Button><Button disabled={busy} onClick={() => confirm && action(confirm)}>{t('admin.confirm')}</Button></DialogActions></Dialog>
    <Dialog open={!!preview} onClose={() => setPreview(undefined)} fullWidth maxWidth="md" aria-labelledby="preview-title"><DialogTitle id="preview-title">{t('admin.preview')} – {lang.toUpperCase()}</DialogTitle><DialogContent><Stack spacing={2}><Alert severity="info">{t('admin.previewSaved')}</Alert><Typography variant="h4" component="h2">{displayText(preview?.content.title)}</Typography>{preview?.content.image && <ProtectedImage large id={preview.content.image.id} token={token} alt={preview.content.image.alt} expired={expired} />}{kind === 'projects' && <><Typography>{preview?.content.location}</Typography><Typography>{preview?.content.dates?.join(' · ')}</Typography><Typography>{preview?.content.tags?.join(' · ')}</Typography></>}<SafeMarkdown allowedMediaIds={[...doc.mediaIds, ...(doc.coverImageId ? [doc.coverImageId] : [])]} renderImage={(imageId, alt) => <ProtectedImage id={imageId} token={token} alt={alt} expired={expired} large />}>{displayText(preview?.content.description)}</SafeMarkdown>{kind === 'projects' && <><Typography variant="h6">{t('admin.challenge')}</Typography><Typography>{preview?.content.challenge}</Typography><ProjectFacts project={preview?.content || {}} /><Typography variant="h6">{t('admin.result')}</Typography><Typography>{preview?.content.result}</Typography></>}</Stack></DialogContent><DialogActions><Button onClick={() => setPreview(undefined)}>{t('admin.close')}</Button></DialogActions></Dialog>
  </Stack>;
}
