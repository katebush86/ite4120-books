--liquibase formatted sql

--changeset ite4120:books-schema
--comment Create books schema

create schema if not exists books;

--