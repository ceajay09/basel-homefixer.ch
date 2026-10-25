import { ReactNode } from 'react';
import { Box, Container, Typography } from '@mui/material';

interface PageSectionProps {
  id: string;
  title: string;
  children: ReactNode;
  muted?: boolean;
}

export default function PageSection({ id, title, children, muted = false }: PageSectionProps) {
  return (
    <Box component="section" id={id} tabIndex={id === 'projects' || id === 'contact-form' ? -1 : undefined} aria-labelledby={id + '-title'} data-design-id={id}
      sx={{ py: { xs: 6, md: 10 }, bgcolor: muted ? '#f9f8f6' : 'background.default' }}>
      <Container>
        <Typography id={id + '-title'} component="h2" variant="h2" align="center" sx={{ mb: { xs: 4, md: 6 }, color: 'primary.main' }}>
          {title}
        </Typography>
        {children}
      </Container>
    </Box>
  );
}
