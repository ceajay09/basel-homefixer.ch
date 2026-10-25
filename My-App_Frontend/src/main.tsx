import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { I18nextProvider } from 'react-i18next';
import i18n from './i18n';
import { LanguageProvider } from './LanguageContext';
// 🔁 Wenn dein Routing in der URL sein soll, nutze BrowserRouter
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
const router = createBrowserRouter([{ path: '*', element: <App /> }]);

const root = ReactDOM.createRoot(document.getElementById('root')!);

root.render(
  <React.StrictMode>
    <I18nextProvider i18n={i18n}>
      <LanguageProvider>
        <RouterProvider router={router} />
      </LanguageProvider>
    </I18nextProvider>
  </React.StrictMode>
);

// optional: Wenn du Web Vitals nutzen willst
reportWebVitals();
