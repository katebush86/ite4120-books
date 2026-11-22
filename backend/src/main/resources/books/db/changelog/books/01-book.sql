--liquibase formatted sql

--changeset ite4120:books-01-book
--comment Book title table: one row per title, identified by ISBN
/* One row per book title; its physical copies live in books.copy.
   - id from the shared sequence core.seq_id;
   - sys_* columns declared here, filled by the core.sys_columns() trigger
     that core.create_table_metadata() installs, never by hand;
   - soft delete: sys_status goes A -> C, so ISBN is unique among active
     rows only and a removed title's ISBN may be registered again;
   - genre is a fixed list (check constraint), not a separate table. */
create table books.book (
    id                  bigint default nextval('core.seq_id') not null,
    isbn                text not null,
    title               text not null,
    author              text not null,
    genre               text not null,
    page_count          integer not null,
    publication_year    integer not null,
    sys_status          char(1),
    sys_version         int,
    sys_created_at      timestamptz,
    sys_created_by      text,
    sys_modified_at     timestamptz,
    sys_modified_by     text,
    constraint book_pk primary key (id),
    constraint book_isbn_format_chk check (isbn ~ '^[0-9]{10}$' or isbn ~ '^[0-9]{13}$'),
    constraint book_genre_chk check (genre in ('FICTION', 'NON_FICTION', 'FANTASY', 'SCIENCE_FICTION', 'MYSTERY',
                                                'BIOGRAPHY', 'HISTORY', 'CHILDREN', 'POETRY', 'OTHER')),
    constraint book_title_chk check (length(title) <= 255),
    constraint book_author_chk check (length(author) <= 255),
    constraint book_page_count_chk check (page_count > 0),
    constraint book_publication_year_chk check (publication_year between 1000 and 9999)
);

select core.create_table_metadata('books.book');

/* Unique among ACTIVE rows only: a removed title's ISBN may be reused. */
create unique index book_isbn_ukey
    on books.book (isbn) where (sys_status = 'A');
--
