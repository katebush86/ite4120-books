--liquibase formatted sql

--changeset ite4120:books-90-demo-data context:demo
--comment Demo books and copies for local development and the presentation
/* Applied only when the Liquibase context includes `demo`
   (spring.liquibase.contexts in application.yml).
   - One copy per status, so every rule is visible in the catalogue: an
     overdue loan, a current loan, a reservation, a repair, a pre-order.
   - Borrower codes match the imitated population registry
     (MockOwnerRegistryController), so borrower names show out of the box.
   - Due dates and the pre-order year are relative to the day the database
     is created, so the demo looks the same whenever it is rebuilt.
   - A title whose ISBN is already active is skipped, together with its
     copies, so this also runs on a database that has test rows. */
with demo_book (isbn, title, author, genre, page_count, publication_year) as (
    values
        ('9789985000001', 'Tõde ja õigus', 'A. H. Tammsaare', 'FICTION', 400, 1926),
        ('9780000000002', 'Alice''s Adventures in Wonderland', 'Lewis Carroll', 'CHILDREN', 200, 1865),
        ('9780000000003', 'Tao Te Ching', 'Laozi', 'NON_FICTION', 120, 1963),
        ('9780000000004', 'Demo Pre-order Title', 'Demo Author', 'OTHER', 300,
            extract(year from current_date)::int + 1)
),
demo_copy (isbn, status, borrower_personal_code, due_date) as (
    values
        ('9789985000001', 'BORROWED', '38102130265', current_date - 3),
        ('9789985000001', 'AVAILABLE', null, null),
        ('9780000000002', 'AVAILABLE', null, null),
        ('9780000000002', 'IN_QUEUE', '47503121234', null),
        ('9780000000002', 'UNDER_REPAIR', null, null),
        ('9780000000003', 'BORROWED', '50011020017', current_date + 10),
        ('9780000000004', 'PRE_ORDER', null, null)
),
new_book as (
    insert into books.book (isbn, title, author, genre, page_count, publication_year)
    select d.isbn, d.title, d.author, d.genre, d.page_count, d.publication_year
      from demo_book d
     where not exists (select 1 from books.book b where b.isbn = d.isbn and b.sys_status = 'A')
    returning id, isbn
)
insert into books.copy (book_id, status, borrower_personal_code, due_date)
select n.id, c.status, c.borrower_personal_code, c.due_date
  from new_book n
  join demo_copy c on c.isbn = n.isbn;
--
