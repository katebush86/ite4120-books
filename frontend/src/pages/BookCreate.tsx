import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Input, InputNumber, Select } from 'antd';
import { AppButtonPrimary, AppButtonSecondary, AppCard, useAppNotification } from '@helex/ui';
import { booksApi, GENRES, type Genre } from '../api';

/**
 * Registration form. Field-by-field this follows the UI specification in
 * docs/specifications/BOOKS.01-book-register.md.
 * Every input is implemented using library components.
 */

interface BookCreateValues {
  isbn: string;
  title: string;
  author: string;
  genre: Genre;
  pageCount: number;
  publicationYear: number;
  copyCount: number;
}

/** Display names for the genre codes in the spec's fixed list. */
const GENRE_LABELS: Record<Genre, string> = {
  FICTION: 'Fiction',
  NON_FICTION: 'Non-fiction',
  FANTASY: 'Fantasy',
  SCIENCE_FICTION: 'Science Fiction',
  MYSTERY: 'Mystery',
  BIOGRAPHY: 'Biography',
  HISTORY: 'History',
  CHILDREN: 'Children',
  POETRY: 'Poetry',
  OTHER: 'Other',
};

export const BookCreate = () => {
  const navigate = useNavigate();
  const notify = useAppNotification();
  const [form] = Form.useForm<BookCreateValues>();
  const [saving, setSaving] = useState(false);

  // Spec: a future year shows the hint "copies will be registered as Pre-order".
  const publicationYear = Form.useWatch('publicationYear', form);
  const isFutureYear =
    typeof publicationYear === 'number' && publicationYear > new Date().getFullYear();

  const submit = (values: BookCreateValues) => {
    setSaving(true);
    booksApi
      .create({
        isbn: values.isbn,
        title: values.title,
        author: values.author,
        genre: values.genre,
        pageCount: values.pageCount,
        publicationYear: values.publicationYear,
        copyCount: values.copyCount,
      })
      .then((created) => {
        notify.success('Registered', `${created.book.title} · ${created.book.isbn}`);
        navigate('/books');
      })
      .catch((e) => notify.error('Registration failed', e.message)) // 400/409 problem detail, verbatim
      .finally(() => setSaving(false));
  };

  return (
    <AppCard title="Register a new book" style={{ maxWidth: 640, margin: '24px auto' }}>
      <Form
        form={form}
        layout="vertical"
        onFinish={submit}
        requiredMark
        initialValues={{ copyCount: 1 }}
      >
        <Form.Item
          name="isbn"
          label="ISBN"
          rules={[
            { required: true, message: 'ISBN is required' },
            {
              pattern: /^(\d{10}|\d{13})$/,
              message: 'ISBN must be exactly 10 or 13 digits, without hyphens or spaces',
            },
          ]}
          extra="10 or 13 digits, no hyphens"
        >
          <Input placeholder="9780123456789" />
        </Form.Item>

        <Form.Item
          name="title"
          label="Title"
          rules={[
            { required: true, message: 'Title is required' },
            { max: 255, message: 'Title can be at most 255 characters' },
          ]}
        >
          <Input placeholder="Book title" />
        </Form.Item>

        <Form.Item
          name="author"
          label="Author"
          rules={[
            { required: true, message: 'Author is required' },
            { max: 255, message: 'Author can be at most 255 characters' },
          ]}
        >
          <Input placeholder="Author name" />
        </Form.Item>

        <Form.Item name="genre" label="Genre" rules={[{ required: true, message: 'Genre is required' }]}>
          <Select
            options={GENRES.map((value) => ({ value, label: GENRE_LABELS[value] }))}
            placeholder="Choose a genre"
          />
        </Form.Item>

        <Form.Item
          name="pageCount"
          label="Page count"
          rules={[
            { required: true, message: 'Page count is required' },
            { type: 'integer', min: 1, message: 'Page count must be a whole number greater than 0' },
          ]}
        >
          <InputNumber precision={0} style={{ width: '100%' }} placeholder="e.g. 250" />
        </Form.Item>

        <Form.Item
          name="publicationYear"
          label="Publication year"
          rules={[
            { required: true, message: 'Publication year is required' },
            { type: 'integer', min: 1000, max: 9999, message: 'Publication year must have 4 digits' },
          ]}
          extra={isFutureYear ? 'Copies will be registered as Pre-order' : undefined}
        >
          <InputNumber precision={0} style={{ width: '100%' }} placeholder="e.g. 2024" />
        </Form.Item>

        <Form.Item
          name="copyCount"
          label="Copies"
          rules={[
            { required: true, message: 'Number of copies is required' },
            { type: 'integer', min: 1, max: 100, message: 'Copies must be between 1 and 100' },
          ]}
        >
          <InputNumber precision={0} style={{ width: '100%' }} />
        </Form.Item>

        <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
          <AppButtonSecondary onClick={() => navigate('/books')}>Cancel</AppButtonSecondary>
          <AppButtonPrimary htmlType="submit" loading={saving}>
            Register
          </AppButtonPrimary>
        </div>
      </Form>
    </AppCard>
  );
};
