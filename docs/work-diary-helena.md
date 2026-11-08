# Work diary — Helena

## 2026-11-dd

**Time spent:** 

**Work completed:**
- 

**AI assistance:**
- 

**Corrections and decisions:**
- 

## 2026-11-08

**Time spent:** 4 hours (incl. 1 h team call)

**Work completed:**
- Forked the teacher's repository (`ite4120-books`) and moved our earlier docs and commit history into it; archived the old repository
- Renamed our old `readme.md` to `TEAM.md` and updated the project requirements
- Made a work plan with steps, owners and checks, reviewed it on team call and added it to `TEAM.md` 
- Updated `README.md` to describe our library books component
- Prepared setup instructions for Windows + VS Code and shared with the team and walked through the steps on team call
- Planned the table folder structure under `backend/src/main/resources/books/`
- Team call (1 h): agreed on next steps and split the work — Mari: book table and register form; Heiki: copy table and lending form; Helena: APIs (GET, POST, PUT, DELETE)
- Set up the development environment on Windows: ran the tool setup script, created the GitHub token, configured Gradle and npm
- Started the backend and frontend; the template app runs on localhost

**AI assistance:**
- Used AI to compare our spec and user story with the teacher's example and the assessment criteria
- Used AI to merge the old repository's history into the fork and open pull requests
- Used AI to explain the data model: title vs copy level, primary key vs unique ISBN, and how ESTER (Sierra) structures bibliographic and item records

**Corrections and decisions:**
- Decided to fork the teacher's repository so he can see our work, while keeping all earlier commits
- User story to be renamed to `BOOKS-US-001.md` so it matches the spec
- Docker Desktop install from the setup script failed (error 1603) because Docker was already installed — kept the existing Docker
- Frontend failed with 401 at first because the npm token was not set; fixed by setting the token with `npm config set`

## 2026-11-01

**Time spent:** 2 hours

**Work completed:**
- Added spec file
- Created a branch to commit to github repo

**AI assistance:**
- Asked AI to create a spec file based on brainstorming notes
- Asked AI help on how to protect main branch

**Corrections and decisions:**
- User Story moved to user story folder and needs some updates too after spec file

## 2026-10-25

**Time spent:** 1 hour

**Work completed:**
- Brainstormed on user story topics on team call
- Created initial draft for user story and specification and distributed workload and defined next steps

## 2026-10-21

**Time spent:** 1.5 hours

**Work completed:**
- Created the group GitHub repository and added initial project information.
- Reviewed the course notes, syllabus, README, development workflow, and assessment criteria.

**AI assistance:**
- Asked AI to explain the course project requirements, workflow, and repository setup.
- Asked AI for examples of a user story and a work-diary entry.

**Corrections and decisions:**
- Corrected the diary plan after checking the development workflow: each student should keep a separate diary.
- Clarified that the diary should record actual work, time spent, AI assistance, and corrections.
