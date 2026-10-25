import { useState } from 'react';
import { Box, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { BlogImage, blogApiBase, validMediaId } from './blogApi';
export default function BlogPicture({ image, fallback, hero = false }: { image?: BlogImage; fallback: string; hero?: boolean }) {
  const { t } = useTranslation(); const [failed, setFailed] = useState(false);
  if (!image || !validMediaId(image.id) || failed) return <Box sx={{ bgcolor: '#edf1eb', aspectRatio: '16 / 9', display: 'grid', placeItems: 'center', p: 2 }}><Typography color="text.secondary">{t('publicBlog.noImage')}</Typography></Box>;
  const path = blogApiBase + '/api/media/' + image.id;
  return <Box component="img" alt={image.alt?.trim() || fallback} src={path + '/large'} srcSet={image.smallWidth > 0 && image.width > image.smallWidth ? path + '/small ' + image.smallWidth + 'w, ' + path + '/large ' + image.width + 'w' : undefined}
    sizes={hero ? '(min-width: 1000px) 900px, 100vw' : '(min-width: 1200px) 360px, (min-width: 600px) 48vw, 100vw'}
    width={image.width} height={image.height} loading={hero ? 'eager' : 'lazy'} decoding="async" onError={() => setFailed(true)}
    sx={{ display: 'block', width: '100%', height: 'auto', aspectRatio: hero ? undefined : '16 / 9', maxHeight: hero ? 600 : undefined, objectFit: hero ? 'contain' : 'cover', bgcolor: '#edf1eb' }} />;
}
