# Group Project for IT and eGov Technologies

**University:** Tallinn University of Technology (TalTech)  
**Course:** ITE4120 Introduction to IT and eGov Technologies  
**Semester:** Autumn 2026

## About
A group project for the IT and eGov Technologies course: a library book catalogue and lending component (BOOKS), built on the course template.

## Team
- Mari Tarum-Seilenthal
- Heiki Ilisson
- Helena Toompalu

## Project requirements
- Database with three tables: `books.book`, `books.copy` and the reference table `books.genre` (foreign keys copy → book, book → genre)
- APIs: list/search books, register a book, update lending, remove a book
- Borrower name fetched from the mock registry (X-Road style)
- Two forms: register form and lending form, with field validation as in the spec
- At least two business tests as passing automated tests
- Each team member commits their own part and keeps a work diary

## Work plan

- [ ] **0. Set up the app on each laptop.** Follow the setup steps.
  - *Check:* the Animals page opens at http://localhost:18640.
  - [ ] Mari
  - [x] Heiki
  - [x] Helena
- [x] **1. Create user story & specification.** Mari creates the user story and Helena the spec. Then both read each other's version to check that they describe the same thing.  
  - [x] User story (Mari)
  - [x] Specification (Helena)
- [x] **2. Agree on the table design.** Fields agreed on the team call and written into the spec.
  - Table 1: books (one row per book title)
  - Table 2: copies (one row per physical copy, with its current lending info)
  - Table 3: genres (reference list of the genre codes)
- [ ] **3. Create the tables.** The books table must exist first, because copies point to it.
  - [x] Books table (Mari)
  - [x] Copies table (Heiki)
  - [ ] Demo data `90-demo-data.sql` (Helena)
  - [ ] *Check:* the backend starts without errors, and the Animals page still works
- [ ] **4. Build the APIs.** Helena.
  - [x] List/search books (`GET /api/books`)
  - [x] One book with its copies (`GET /api/books/{id}`)
  - [x] Register a book (`POST /api/books`)
  - [x] One copy (`GET /api/copies/{id}`)
  - [x] Update lending (`PUT /api/copies/{id}`)
  - [x] Remove a book (`DELETE /api/books/{id}`)
  - [x] Borrower name from the mock registry
  - [x] Frontend link to the API (`frontend/src/api.ts`)
  - [ ] Automated tests (`BookBusinessRulesIT`, at least 2 business tests)
  - [x] *Check:* all APIs work in Swagger UI (http://localhost:18440/swagger-ui.html) and the data appears in the database
- [ ] **5. Build the screens.**
  - [ ] Catalogue screen `BookList.tsx` (Heiki)
  - [ ] Routes in `App.tsx`: `/books`, `/books/new`, `/books/copies/:id` (Heiki)
  - [ ] Register form (Mari)
  - [ ] Lending form (Heiki)
  - [ ] *Check:* wrong input is refused with an error (e.g. letters in page count, a personal code with only 10 digits instead of 11); correct input is saved and shows in the list
- [ ] **6. Test everything together on localhost.** On the computer you will present from, before 27.11.
  - [ ] Docs: links in `docs/src/SUMMARY.md`, page titles (Helena)
  - [ ] Spec and user story state `Confirmed` (all)


### Who does what

| Person | Tasks |
|--------|-------|
| Mari   | User story, books table, register form |
| Heiki  | Copies table, catalogue screen, routes, lending form |
| Helena | Spec, APIs, `api.ts`, demo data, automated tests, docs, final localhost test |

### Comments

- **Step 1:** rename the user story to `BOOKS-US-001.md` so it matches the spec. When all three agree, change the state of both from `Proposed` to `Confirmed`. No coding before that (teacher's rule).
- **Step 2:** the tables are `books.book`, `books.copy` and `books.genre`. Genres are a reference table (like the teacher's `animals.species`); `book.genre` is a foreign key to it.
- **Step 3:** table files go in `backend/src/main/resources/books/db/changelog/books/`. Run `.\scripts\check-changesets.ps1` before each pull request. Never edit a merged SQL file; add a new numbered file


- **Deadlines:** defence 27.11.2026, all deliverables 11.12.2026. 