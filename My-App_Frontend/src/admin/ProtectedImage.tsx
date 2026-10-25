import { useEffect, useState } from 'react';
import { Alert, Box, CircularProgress } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { ApiError, imageBlob } from './api';
export default function ProtectedImage({ id, token, alt, expired, large = false }: { id: string; token: string; alt: string; expired: () => void; large?: boolean }) {
  const { t } = useTranslation(); const [url, setUrl] = useState(''); const [error, setError] = useState(false);
  useEffect(() => {
    const abort = new AbortController(); const timer = setTimeout(() => abort.abort(), 15000); let objectUrl = ''; let active = true;
    setUrl(''); setError(false); if (!token) return () => { active = false; clearTimeout(timer); abort.abort(); };
    imageBlob(id, token, abort.signal, large).then(blob => { if (active) { objectUrl = URL.createObjectURL(blob); setUrl(objectUrl); } }).catch(err => { if (active) { setError(true); if (err instanceof ApiError && err.status === 401) expired(); } }).finally(() => clearTimeout(timer));
    return () => { active = false; abort.abort(); if (objectUrl) URL.revokeObjectURL(objectUrl); };
  }, [id, token, large, expired]);
  return error ? <Alert severity="warning">{t('admin.imageError')}</Alert> : url ? <Box component="img" src={url} alt={alt} sx={{ maxWidth: '100%', width: '100%', maxHeight: large ? 600 : 160, objectFit: 'contain' }} /> : <CircularProgress size={24} aria-label={t('admin.loading')} />;
}
