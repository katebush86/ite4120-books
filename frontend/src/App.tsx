import type { ReactElement } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { AnimalList } from './pages/AnimalList';
import { AnimalCreate } from './pages/AnimalCreate';
import { AnimalDetail } from './pages/AnimalDetail';
import { BookList } from './pages/BookList';
import { LendingForm } from './pages/LendingForm';
import { BookCreate } from './pages/BookCreate';

/**
 * Components that may be ABSENT from this checkout register their routes by
 * exporting `routes: ReactElement[]` (a list of <Route>s) from
 * `pages/<component>/routes.tsx`. The glob is resolved at build time and is
 * simply empty when no such folder exists — the lecturer's private library
 * module lives there, gitignored. Students: add your <Route>s below, like animals.
 */
const componentRoutes = Object.values(
  import.meta.glob<{ routes: ReactElement[] }>('./pages/*/routes.tsx', { eager: true }),
).flatMap((m) => m.routes);

export const App = () => (
  <Routes>
    <Route path="/" element={<Navigate to="/books" replace />} />

    <Route path="/animals" element={<AnimalList />} />
    <Route path="/animals/new" element={<AnimalCreate />} />
    <Route path="/animals/:id" element={<AnimalDetail />} />

    <Route path="/books" element={<BookList />} />
    <Route path="/books/new" element={<BookCreate />} />
    <Route path="/books/copies/:id" element={<LendingForm />} />

    {componentRoutes}
  </Routes>
);