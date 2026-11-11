--liquibase formatted sql

--changeset ite4120:books-01-book
--comment Book title table

create table books.book (
    id                  bigint default nextval('core.seq_id') not null,
    isbn                text not null,
    title               text not null,
    author              text not null,
    genre               text not null,
    page_count          integer not null,
    publication_year    integer not null,

    constraint book_pk primary key (id),

    constraint book_isbn_uq unique (isbn),

    constraint book_isbn_format_chk
        check (
            isbn ~ '^[0-9]{10}$'
            or isbn ~ '^[0-9]{13}$'
        ),

    constraint book_genre_chk
        check (
            genre in (
                'FICTION',
                'NON_FICTION',
                'FANTASY',
                'SCIENCE_FICTION',
                'MYSTERY',
                'BIOGRAPHY',
                'HISTORY',
                'CHILDREN',
                'POETRY',
                'OTHER'
            )
        ),

    constraint book_title_chk
        check (length(title) <= 255),

    constraint book_author_chk
        check (length(author) <= 255),

    constraint book_page_count_chk
        check (page_count > 0),

    constraint book_publication_year_chk
        check (publication_year between 1000 and 9999)
);

select core.create_table_metadata('books.book');

--