# Work diary — Heiki

## 2026-10-21

Time spent ca 2 hours

**Work completed:**

- Reviewed course materials and lecture notes.
- Set up Git and GitHub Desktop.
- Looked through the group repository and tried to understand the basic GitHub workflow.

**AI assistance:**

- Used AI to better understand course requirements and GitHub basics.

## 2026-10-23

Time spent ca 2 hours

**Work completed:**

- Reviewed the lecture materials about front-end development, REST APIs and Storybook.
- Tried to understand how the front end, backend and database work together.

## 2026-10-25

Time spent ca 1 hour

**Work completed:**

- Participated in a team call about the library application.
- Discussed the book register, lending register, statuses and main rules.
- Discussed possible validation rules and tests.

## 2026-11-03

Time spent ca 2 hours

**Work completed:**

- Reviewed the team user story, specification and GitHub repository.
- Updated the user story acceptance criteria to separate book registration data from lending information.
- Created a separate branch, committed the change and opened a pull request for review.
- Reviewed Helena's specification pull request and noticed that it uses a book + physical copy data model, which differs from the earlier user story.
- Decided not to merge or make further changes before discussing the data model with the team.

**AI assistance:**

- Used AI to review the user story and specification.
- Used AI to better understand GitHub branches, commits and pull requests.
- Used AI to identify the differences between the user story and the proposed specification.

**Corrections and decisions:**

- Kept my pull request open for discussion.
- Decided to confirm the final data model during the next team call before making further changes.

## 2026-11-08

Time spent ca 2 hours

**Work completed:**

- Participated in a team call about the library application.
- Discussed the book register, lending register, statuses and main rules.
- Discussed possible validation rules and tests.

## 2026-11-10

Time spent ca 4 hours

**Work completed:**

- Set up and tested the project development environment.
- Created my Git branch heiki-copy-table.
- Created the Liquibase migration for the books.copy table, including the foreign key, status and lending validation rules.
- Added the migration to the changelog and successfully tested it with the backend and PostgreSQL.
- Installed and used DBeaver to verify the table structure, constraints and indexes.
- Committed and pushed the changes to the group GitHub repository.

**AI assistance:**

- Used AI for step-by-step guidance with Liquibase, Git, development environment setup and troubleshooting.
- Used AI to review the database structure against the project specification and lecturer examples.
  
**Corrections and decisions:**
- Confirmed that ISBN identifies a book title/edition and physical copies are stored separately.
- Kept lending information at copy level.

## 2026-11-15

Time spent ca 1.5 hours

**Work completed:**

- Participated in a team call about the current status and the next steps.
- Agreed on the remaining division of work, including my responsibility for the catalogue screen, lending form and related routes.

## 2026-11-22

Time spent ca 6 hours

**Work completed:**

- Updated the local project from the latest main branch and created a separate frontend branch.
- Created the book catalogue screen BookList.tsx with search, genre/status filters, copy details and book title removal action.
- Created the lending update form LendingForm.tsx with status-dependent validation for borrower personal code and due date.
- Added routes for the catalogue and lending form.
- Built and tested the frontend together with the backend and PostgreSQL.
- Tested book search and filtering, lending updates, validation rules, removal restrictions and book registration.
- Committed and pushed the frontend changes to GitHub and verified that the final main branch contains the completed functionality.
- Participated in a team call for joint testing and finalising the work.

**AI assistance:**

- Used AI for step-by-step guidance with React/TypeScript, Git, project structure and troubleshooting.
- Used AI to review the implementation against the project specification and to support functional testing.

**Corrections and decisions:**

- Fixed the catalogue detail panel so that it opens when a row is selected.
- Reset the local development database after a Liquibase checksum conflict caused by changes merged into main.
- Confirmed that the main application functionality works together end-to-end.
