import { Box, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { formatProjectInvestment, ProjectMetadata, projectDurationOptions } from './projectCatalog';
/** Same figures in public cards and the authenticated preview. Missing facts remain absent. */
export default function ProjectFacts({ project }: { project: Partial<Pick<ProjectMetadata, 'duration' | 'areaM2' | 'apartments' | 'doors' | 'thermostats' | 'investmentChf'>> }) {
  const { t, i18n } = useTranslation();
  const stats = [
    ...(project.areaM2 ? [{ value: project.areaM2 + ' m²', label: t('projects.areaLabel') }] : []),
    ...(project.apartments ? [{ value: String(project.apartments), label: t('projects.apartmentsLabel') }] : []),
    ...(project.doors ? [{ value: t('projects.doorCount', { count: project.doors }), label: t('projects.glazingLabel') }] : []),
    ...(project.thermostats ? [{ value: String(project.thermostats), label: t('projects.thermostatsLabel') }] : []),
    ...(project.duration ? [{ value: t('projects.duration.' + project.duration.unit, projectDurationOptions(project.duration)), label: t(project.duration.unit === 'months' ? 'projects.totalDurationLabel' : 'projects.durationLabel') }] : []),
    ...(!project.duration && project.investmentChf ? [{ value: formatProjectInvestment(project.investmentChf, i18n.language), label: t('projects.investmentLabel') }] : []),
  ];
  return <Box component="dl" sx={{ display: 'grid', gridTemplateColumns: 'repeat(2, minmax(0, 1fr))', gap: 2, mt: 'auto', mb: 3, pt: 3, borderTop: '1px solid', borderColor: 'divider' }}>
    {stats.map(stat => <Box key={stat.label} sx={{ minWidth: 0 }}><Typography component="dt" variant="body2" color="text.secondary">{stat.label}</Typography><Typography component="dd" sx={{ m: 0, color: 'primary.main', fontSize: { xs: '1.15rem', sm: '1.4rem' }, fontWeight: 700 }}>{stat.value}</Typography></Box>)}
  </Box>;
}
