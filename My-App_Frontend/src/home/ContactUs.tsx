import { FormEvent, useEffect, useRef, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Checkbox, CircularProgress, Container, FormControlLabel, FormHelperText, Grid, Link, MenuItem, Stack, TextField, Typography } from '@mui/material';
import SendOutlinedIcon from '@mui/icons-material/SendOutlined';
import { useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Layout from './Layout';
import PageSection from './PageSection';
import { brandGradient } from './theme';
import { siteConfig } from './siteConfig';
import { ServiceContent } from './serviceCatalog';
import { ContactErrors, ContactField, ContactRequest, getContactAvailability, serviceFromQuery, submitContactRequest, validateContact } from './contactApi';

export default function ContactUs() {
  const { t } = useTranslation();
  const [params] = useSearchParams();
  const serviceQuery = params.get('service');
  const [values, setValues] = useState<ContactRequest>(() => ({ name: '', email: '', phone: '', service: serviceFromQuery(serviceQuery), message: '', consent: false, website: '' }));
  const [errors, setErrors] = useState<ContactErrors>({});
  const [availability, setAvailability] = useState<'checking' | 'available' | 'unavailable'>('checking');
  const [status, setStatus] = useState('idle');
  const sending = useRef(false);
  const form = useRef<HTMLFormElement>(null);
  const catalog = t('services.catalog', { returnObjects: true }) as ServiceContent[];
  useEffect(() => { setValues(current => ({ ...current, service: serviceFromQuery(serviceQuery) })); }, [serviceQuery]);
  useEffect(() => { let active = true; getContactAvailability().then(ok => { if (active) setAvailability(ok ? 'available' : 'unavailable'); }); return () => { active = false; }; }, []);
  async function recheck() { setAvailability('checking'); setAvailability(await getContactAvailability() ? 'available' : 'unavailable'); }
  function update<K extends ContactField>(field: K, value: ContactRequest[K]) {
    setValues(current => ({ ...current, [field]: value }));
    setErrors(current => ({ ...current, [field]: undefined }));
    if (status !== 'sending') setStatus('idle');
  }
  function blur(field: ContactField) { setErrors(current => ({ ...current, [field]: validateContact(values)[field] })); }
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (sending.current || availability !== 'available' || status === 'accepted') return;
    const problems = validateContact(values); setErrors(problems);
    if (Object.keys(problems).length) {
      const field = Object.keys(problems)[0]; form.current?.querySelector<HTMLElement>('[name="' + field + '"]')?.focus(); return;
    }
    sending.current = true; setStatus('sending');
    try {
      const result = await submitContactRequest({ ...values, name: values.name.trim(), email: values.email.trim(), phone: values.phone.trim(), message: values.message.trim() });
      setStatus(result.code);
      if (result.code === 'unavailable') setAvailability('unavailable');
      if (result.fieldErrors) {
        const serverErrors: ContactErrors = {};
        for (const field of Object.keys(result.fieldErrors)) if (Object.prototype.hasOwnProperty.call(values, field) && field !== 'website') serverErrors[field as ContactField] = 'server';
        setErrors(serverErrors);
      }
    } finally { sending.current = false; }
  }
  const helper = (field: ContactField) => errors[field] ? t('contact.validation.' + errors[field]) : undefined;
  return (
    <Layout>
      <Box component="section" aria-labelledby="contact-title" sx={{ background: brandGradient, color: '#ffffff', py: { xs: 7, md: 10 }, textAlign: 'center' }}>
        <Container maxWidth="md">
          <Typography component="h1" variant="h1" id="contact-title" sx={{ mb: 3 }}>{t('contact.title')}</Typography>
          <Typography sx={{ fontSize: { xs: '1.05rem', md: '1.25rem' } }}>{t('contact.subtitle')}</Typography>
        </Container>
      </Box>
      <PageSection id="contact-info" title={t('contact.infoTitle')}>
        <Grid container spacing={3}>
          {[{ icon: '📧', title: t('contact.emailTitle'), description: t('contact.emailDescription'), value: siteConfig.email, href: 'mailto:' + siteConfig.email },
            { icon: '📱', title: t('contact.phoneTitle'), description: t('contact.phoneDescription'), value: siteConfig.phone, href: siteConfig.phoneHref },
            { icon: '📍', title: t('contact.locationTitle'), description: t('contact.locationDescription'), value: t('layout.footer.location') }].map(item => <Grid key={item.title} size={{ xs: 12, md: 4 }} sx={{ display: 'flex' }}>
            <Card sx={{ width: '100%', textAlign: 'center', borderTop: '4px solid', borderColor: 'secondary.main' }}>
              <CardContent sx={{ p: 3 }}>
                <Box aria-hidden="true" sx={{ fontSize: 44, mb: 2 }}>{item.icon}</Box>
                <Typography component="h3" variant="h3" sx={{ color: 'primary.main', mb: 2 }}>{item.title}</Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>{item.description}</Typography>
                {item.href ? <Link href={item.href} sx={{ fontWeight: 600, overflowWrap: 'anywhere' }}>{item.value}</Link> : <Typography sx={{ fontWeight: 600 }}>{item.value}</Typography>}
              </CardContent>
            </Card>
          </Grid>)}
        </Grid>
        <Typography sx={{ textAlign: 'center', mt: 4, color: 'text.secondary' }}>{t('contact.hours')}</Typography>
      </PageSection>
      <PageSection id="contact-form" title={t('contact.formTitle')} muted>
        <Card sx={{ maxWidth: 800, mx: 'auto' }}>
          <CardContent sx={{ p: { xs: 3, md: 5 }, '&:last-child': { pb: { xs: 3, md: 5 } } }}>
            <Stack spacing={3}>
              {availability !== 'available' && <Alert severity="info" role="status">
                {t(availability === 'checking' ? 'contact.checking' : 'contact.unavailable')}
                {availability === 'unavailable' && <Button size="small" onClick={recheck}>{t('contact.retryAvailability')}</Button>}
              </Alert>}
              {status === 'accepted' && <Alert severity="success" role="status">{t('contact.accepted')}</Alert>}
              {['invalid_request', 'send_failed', 'network_error', 'rate_limited'].includes(status) && <Alert severity="error" role="alert">{t('contact.status.' + status)}</Alert>}
              <Box component="form" ref={form} noValidate onSubmit={submit} aria-label={t('contact.formTitle')} aria-busy={status === 'sending'}>
                <Stack spacing={3}>
                  <TextField id="contact-name" name="name" label={t('contact.name')} required fullWidth autoComplete="name" value={values.name} disabled={status === 'sending'} onChange={e => update('name', e.target.value)} onBlur={() => blur('name')} error={!!errors.name} helperText={helper('name')} slotProps={{ htmlInput: { maxLength: 100 } }} />
                  <TextField id="contact-email" name="email" label={t('contact.email')} required fullWidth type="email" autoComplete="email" value={values.email} disabled={status === 'sending'} onChange={e => update('email', e.target.value)} onBlur={() => blur('email')} error={!!errors.email} helperText={helper('email')} slotProps={{ htmlInput: { maxLength: 254 } }} />
                  <TextField id="contact-phone" name="phone" label={t('contact.phone')} fullWidth type="tel" autoComplete="tel" value={values.phone} disabled={status === 'sending'} onChange={e => update('phone', e.target.value)} onBlur={() => blur('phone')} error={!!errors.phone} helperText={helper('phone')} slotProps={{ htmlInput: { maxLength: 40 } }} />
                  <TextField id="contact-service" name="service" label={t('contact.service')} select fullWidth value={values.service} disabled={status === 'sending'} onChange={e => update('service', serviceFromQuery(e.target.value))} error={!!errors.service} helperText={helper('service')}>
                    <MenuItem value="">{t('contact.noService')}</MenuItem>
                    {catalog.map(service => <MenuItem key={service.id} value={service.id} sx={{ whiteSpace: 'normal' }}>{service.title}</MenuItem>)}
                  </TextField>
                  <TextField id="contact-message" name="message" label={t('contact.message')} required fullWidth multiline minRows={6} value={values.message} disabled={status === 'sending'} onChange={e => update('message', e.target.value)} onBlur={() => blur('message')} error={!!errors.message} helperText={helper('message') || t('contact.messageHint')} slotProps={{ htmlInput: { maxLength: 5000 } }} />
                  <Box>
                    <FormControlLabel sx={{ m: 0, alignItems: 'flex-start' }} control={<Checkbox name="consent" checked={values.consent} required disabled={status === 'sending'} onChange={e => update('consent', e.target.checked)} onBlur={() => blur('consent')} slotProps={{ input: { 'aria-describedby': 'contact-consent-help' } }} />} label={t('contact.consent')} />
                    <FormHelperText id="contact-consent-help" error={!!errors.consent}>{helper('consent')}</FormHelperText>
                  </Box>
                  <Box aria-hidden="true" sx={{ position: 'absolute', width: '1px', height: '1px', overflow: 'hidden', clipPath: 'inset(50%)' }}>
                    <label htmlFor="contact-website">Website</label><input id="contact-website" name="website" tabIndex={-1} autoComplete="off" maxLength={200} value={values.website} onChange={e => update('website', e.target.value)} />
                  </Box>
                  <Button type="submit" variant="contained" disabled={availability !== 'available' || status === 'sending' || status === 'accepted'} startIcon={status === 'sending' ? <CircularProgress size={18} color="inherit" /> : <SendOutlinedIcon />}>
                    {t(status === 'sending' ? 'contact.sending' : 'contact.send')}
                  </Button>
                </Stack>
              </Box>
            </Stack>
          </CardContent>
        </Card>
      </PageSection>
    </Layout>
  );
}
