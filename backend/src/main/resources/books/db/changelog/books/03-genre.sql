--liquibase formatted sql

--changeset ite4120:books-03-genre
--comment Genre reference table
/* The lookup list books.book points at, like animals.species. Natural-key
   reference data: no core.seq_id, no sys columns. */
create table books.genre (
    code                text not null,
    name                text not null,
    constraint genre_pk primary key (code)
);
--

--changeset ite4120:books-03-genre-seed
--comment Seed the genre reference list
/* The same ten codes as the spec's genre list. Convergent: ON CONFLICT DO
   NOTHING lets it land safely on a database that already holds some rows. */
insert into books.genre (code, name) values
    ('FICTION',         'Fiction'),
    ('NON_FICTION',     'Non-fiction'),
    ('FANTASY',         'Fantasy'),
    ('SCIENCE_FICTION', 'Science fiction'),
    ('MYSTERY',         'Mystery'),
    ('BIOGRAPHY',       'Biography'),
    ('HISTORY',         'History'),
    ('CHILDREN',        'Children'),
    ('POETRY',          'Poetry'),
    ('OTHER',           'Other')
on conflict do nothing;
--

--changeset ite4120:books-03-book-genre-fk
--comment book.genre becomes a foreign key to the genre table
/* 01-book.sql is released, so it is not edited: this changeset appends the
   foreign key. The genre table now holds the list, so the check constraint
   that repeated it is dropped — one source of truth. */
alter table books.book drop constraint book_genre_chk;

alter table books.book
    add constraint book_genre_fk foreign key (genre) references books.genre (code);
--
