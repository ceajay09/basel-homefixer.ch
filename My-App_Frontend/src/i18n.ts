import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import HttpBackend from 'i18next-http-backend';

  i18n
  .use(HttpBackend)
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    supportedLngs: ['de', 'en'],
    load: 'languageOnly',
    detection: { order: ['localStorage', 'navigator', 'htmlTag'], caches: ['localStorage'] },
    fallbackLng: 'en',
    debug: import.meta.env.DEV,
    backend: {
      loadPath: '/locales/{{lng}}/{{lng}}.json'
    },
    interpolation: {
      escapeValue: false
    }
  });

export default i18n;
