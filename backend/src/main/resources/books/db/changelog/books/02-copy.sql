--liquibase formatted sql

--changeset ite4120:books-02-copy
--comment Physical copies of book titles and their current lending status

create table books.copy (
    id                       bigint default nextval('core.seq_id') not null,
    book_id                  bigint not null,
    status                   text not null,
    borrower_personal_code   text,
    due_date                 date,
    sys_status               char(1),
    sys_version              int,
    sys_created_at           timestamptz,
    sys_created_by           text,
    sys_modified_at          timestamptz,
    sys_modified_by          text,
    
    constraint copy_pk primary key (id),

    constraint copy_book_fk
        foreign key (book_id)
        references books.book (id),
        
    constraint copy_status_chk
        check (
            status in (
                'AVAILABLE',
                'BORROWED',
                'IN_QUEUE',
                'UNDER_REPAIR',
                'PRE_ORDER',
                'LOST'
            )
        ),

    constraint copy_personal_code_chk
        check (
            borrower_personal_code is null
            or borrower_personal_code ~ '^[0-9]{11}$'
        ),

    constraint copy_lending_rules_chk
        check (
            (status = 'BORROWED'
                and borrower_personal_code is not null
                and due_date is not null)

            or

            (status = 'IN_QUEUE'
                and borrower_personal_code is not null
                and due_date is null)

            or

            (status in ('AVAILABLE', 'UNDER_REPAIR', 'PRE_ORDER', 'LOST')
                and borrower_personal_code is null
                and due_date is null)
        )
    );

select core.create_table_metadata('books.copy');

create index copy_book_idx
    on books.copy (book_id)
    where (sys_status = 'A');

--