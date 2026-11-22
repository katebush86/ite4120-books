// The animals API client — thin fetch wrappers over the backend the group owns.
// The Vite dev proxy forwards /api to the backend, so paths are same-origin.
// The Authorization header is set by MockAuthProvider's session (dev).

export interface Animal {
  id: number;
  registryCode: string;
  name: string;
  speciesCode: string;
  birthDate?: string;
  ownerIsikukood?: string;
  chipNumber?: string;
  /** Platform columns, filled by the database trigger; read-only. */
  sysStatus?: string;
  sysVersion?: number;
  sysCreatedAt?: string;
  sysCreatedBy?: string;
  sysModifiedAt?: string;
  sysModifiedBy?: string;
}

export interface OwnerInfo {
  isikukood: string;
  firstName: string;
  lastName: string;
  address: string;
}

export interface QueryResult<T> {
  data: T[];
  meta?: { total?: number };
}

function authHeaders(): Record<string, string> {
  // MockAuthProvider persists the signed-in mock user here.
  const user = localStorage.getItem('helex_mock_user') || 'superadmin';
  return { Authorization: `Bearer ${user}` };
}

async function handle<T>(response: Response): Promise<T> {
  if (!response.ok) {
    // The backend answers application/problem+json — surface its human text.
    const problem = await response.json().catch(() => null);
    throw new Error(problem?.detail || `HTTP ${response.status}`);
  }
  return response.json();
}

export const animalsApi = {
  list: (textContains?: string, speciesCode?: string): Promise<QueryResult<Animal>> =>
    fetch(
      '/api/animals?limit=50' +
        (textContains ? `&textContains=${encodeURIComponent(textContains)}` : '') +
        (speciesCode ? `&speciesCode=${encodeURIComponent(speciesCode)}` : ''),
      { headers: authHeaders() },
    ).then((r) => handle<QueryResult<Animal>>(r)),

  get: (id: number): Promise<Animal> =>
    fetch(`/api/animals/${id}`, { headers: authHeaders() }).then((r) => handle<Animal>(r)),

  create: (body: Omit<Animal, 'id'>): Promise<Animal> =>
    fetch('/api/animals', {
      method: 'POST',
      headers: { ...authHeaders(), 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    }).then((r) => handle<Animal>(r)),

  /** PUT — the registry code is immutable; the backend keeps the stored one whatever is sent. */
  update: (id: number, body: Omit<Animal, 'id'>): Promise<Animal> =>
    fetch(`/api/animals/${id}`, {
      method: 'PUT',
      headers: { ...authHeaders(), 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    }).then((r) => handle<Animal>(r)),

  /** DELETE — a soft delete: 204, and the registry code becomes reusable. */
  retire: (id: number): Promise<void> =>
    fetch(`/api/animals/${id}`, { method: 'DELETE', headers: authHeaders() }).then((r) =>
      r.ok ? undefined : handle<void>(r),
    ),

  owner: (id: number): Promise<OwnerInfo> =>
    fetch(`/api/animals/${id}/owner`, { headers: authHeaders() }).then((r) =>
      handle<OwnerInfo>(r),
    ),

  species: (): Promise<{ code: string; name: string }[]> =>
    fetch('/api/animals/species', { headers: authHeaders() }).then((r) =>
      handle<{ code: string; name: string }[]>(r),
    ),
};

// ---------------------------------------------------------------------------
// The books API client — our component (spec BOOKS.01 § API). Same pattern as
// animalsApi above: one function per endpoint, auth header added, problem
// detail text passed on as the Error message for the screens to show.
// ---------------------------------------------------------------------------

/** The fixed genre list (spec § Genre codes) — for the genre Select and filter. */
export const GENRES = [
  'FICTION',
  'NON_FICTION',
  'FANTASY',
  'SCIENCE_FICTION',
  'MYSTERY',
  'BIOGRAPHY',
  'HISTORY',
  'CHILDREN',
  'POETRY',
  'OTHER',
] as const;
export type Genre = (typeof GENRES)[number];

/** The six copy statuses (spec § Status codes) — for the status Select and filter. */
export const COPY_STATUSES = [
  'AVAILABLE',
  'BORROWED',
  'IN_QUEUE',
  'UNDER_REPAIR',
  'PRE_ORDER',
  'LOST',
] as const;
export type CopyStatus = (typeof COPY_STATUSES)[number];

/** Statuses that need a borrower personal code (spec § Status codes). */
export const STATUSES_WITH_BORROWER: readonly CopyStatus[] = ['BORROWED', 'IN_QUEUE'];

/** One book title, as `GET /api/books/{id}` returns it. */
export interface Book {
  id: number;
  isbn: string;
  title: string;
  author: string;
  genre: Genre;
  pageCount: number;
  publicationYear: number;
  /** Platform columns, filled by the database trigger; read-only. */
  sysStatus?: string;
  sysVersion?: number;
  sysCreatedAt?: string;
  sysCreatedBy?: string;
  sysModifiedAt?: string;
  sysModifiedBy?: string;
}

/** One physical copy, as listed under a book. */
export interface Copy {
  id: number;
  bookId: number;
  status: CopyStatus;
  borrowerPersonalCode?: string | null;
  /** ISO date, e.g. "2026-12-01". */
  dueDate?: string | null;
}

/** `GET /api/books/{id}` — a title with its active copies. */
export interface BookDetail {
  book: Book;
  copies: Copy[];
}

/** One catalogue row = one copy with its title's data (`GET /api/books`, `GET /api/copies/{id}`). */
export interface CatalogueRow {
  copyId: number;
  bookId: number;
  isbn: string;
  title: string;
  author: string;
  genre: Genre;
  pageCount: number;
  publicationYear: number;
  status: CopyStatus;
  borrowerPersonalCode?: string | null;
  dueDate?: string | null;
  /** Greater than 0 only for an overdue BORROWED copy. */
  overdueDays: number;
  /** From the borrower registry; null when unknown or the registry is down. */
  borrowerName?: string | null;
}

/** Body of `POST /api/books` (the register form). */
export interface BookCreate {
  /** Exactly 10 or 13 digits, no hyphens or spaces. */
  isbn: string;
  title: string;
  author: string;
  genre: Genre;
  pageCount: number;
  publicationYear: number;
  /** 1–100; 1 when left out. */
  copyCount?: number;
}

/**
 * Body of `PUT /api/copies/{id}` (the lending form). The three fields replace
 * the copy's lending data as a whole: send null for a field that does not apply
 * to the status — a borrower only for BORROWED / IN_QUEUE, a due date only for
 * BORROWED (left out → today + 14 days).
 */
export interface LendingUpdate {
  status: CopyStatus;
  borrowerPersonalCode?: string | null;
  dueDate?: string | null;
}

/** Search and filters for the catalogue (`GET /api/books`). */
export interface CatalogueQuery {
  textContains?: string;
  genre?: Genre;
  status?: CopyStatus;
  limit?: number;
  offset?: number;
  /** e.g. "title", or "-title" for descending. */
  sort?: string;
}

function jsonHeaders(): Record<string, string> {
  return { ...authHeaders(), 'Content-Type': 'application/json' };
}

export const booksApi = {
  /** The catalogue: one row per copy, 50 per page unless `limit` says otherwise. */
  list: (query: CatalogueQuery = {}): Promise<QueryResult<CatalogueRow>> => {
    const params = new URLSearchParams({ limit: String(query.limit ?? 50) });
    if (query.textContains) params.set('textContains', query.textContains);
    if (query.genre) params.set('genre', query.genre);
    if (query.status) params.set('status', query.status);
    if (query.offset) params.set('offset', String(query.offset));
    if (query.sort) params.set('sort', query.sort);
    return fetch(`/api/books?${params}`, { headers: authHeaders() }).then((r) =>
      handle<QueryResult<CatalogueRow>>(r),
    );
  },

  get: (id: number): Promise<BookDetail> =>
    fetch(`/api/books/${id}`, { headers: authHeaders() }).then((r) => handle<BookDetail>(r)),

  /** POST — 201 with the new title and its copies; 400 / 409 throw with the problem text. */
  create: (body: BookCreate): Promise<BookDetail> =>
    fetch('/api/books', {
      method: 'POST',
      headers: jsonHeaders(),
      body: JSON.stringify(body),
    }).then((r) => handle<BookDetail>(r)),

  /** DELETE — soft delete of the title and all its copies: 204; 409 while a copy is out. */
  remove: (id: number): Promise<void> =>
    fetch(`/api/books/${id}`, { method: 'DELETE', headers: authHeaders() }).then((r) =>
      r.ok ? undefined : handle<void>(r),
    ),

  /** One copy with its title data — what the lending form loads. */
  getCopy: (copyId: number): Promise<CatalogueRow> =>
    fetch(`/api/copies/${copyId}`, { headers: authHeaders() }).then((r) => handle<CatalogueRow>(r)),

  /** PUT — sets status, borrower and due date of one copy; 400 throws with the problem text. */
  updateLending: (copyId: number, body: LendingUpdate): Promise<CatalogueRow> =>
    fetch(`/api/copies/${copyId}`, {
      method: 'PUT',
      headers: jsonHeaders(),
      body: JSON.stringify(body),
    }).then((r) => handle<CatalogueRow>(r)),
};
