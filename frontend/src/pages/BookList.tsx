import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Select, Space } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import {
  AppButton,
  AppButtonPrimary,
  AppPopconfirm,
  AppTag,
  ResourceList,
  useAppNotification,
  type ResourceListColumn,
} from '@helex/ui';
import {
  booksApi,
  COPY_STATUSES,
  GENRES,
  type CatalogueRow,
  type CopyStatus,
  type Genre,
} from '../api';

export const BookList = () => {
  const navigate = useNavigate();
  const notify = useAppNotification();

  const [books, setBooks] = useState<CatalogueRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  const [selected, setSelected] = useState<CatalogueRow | null>(null);
  const [detailOpen, setDetailOpen] = useState(false);

  const [genreFilter, setGenreFilter] = useState<Genre | undefined>();
  const [statusFilter, setStatusFilter] = useState<CopyStatus | undefined>();

  const [appliedGenre, setAppliedGenre] = useState<Genre | undefined>();
  const [appliedStatus, setAppliedStatus] = useState<CopyStatus | undefined>();

  const load = useCallback(() => {
    setLoading(true);

    booksApi
      .list({
        textContains: search || undefined,
        genre: appliedGenre,
        status: appliedStatus,
      })
      .then((result) => setBooks(result.data))
      .catch((e) => notify.error('Could not load books', e.message))
      .finally(() => setLoading(false));
  }, [search, appliedGenre, appliedStatus, notify]);

  useEffect(() => {
    const t = setTimeout(load, 250);
    return () => clearTimeout(t);
  }, [load]);

  const removeBook = async (bookId: number) => {
    try {
      await booksApi.remove(bookId);
      setSelected(null);
      setDetailOpen(false);
      load();
    } catch (e) {
      const error = e as Error;
      notify.error('Could not remove book', error.message);
    }
  };

  const columns: ResourceListColumn<CatalogueRow>[] = [
    {
      key: 'isbn',
      title: 'ISBN',
      dataIndex: 'isbn',
    },
    {
      key: 'title',
      title: 'Title',
      dataIndex: 'title',
    },
    {
      key: 'author',
      title: 'Author',
      dataIndex: 'author',
    },
    {
      key: 'genre',
      title: 'Genre',
      dataIndex: 'genre',
      render: (genre: Genre) => <AppTag>{genre}</AppTag>,
    },
    {
      key: 'publicationYear',
      title: 'Year',
      dataIndex: 'publicationYear',
    },
    {
      key: 'pageCount',
      title: 'Pages',
      dataIndex: 'pageCount',
      defaultVisible: false,
    },
    {
      key: 'status',
      title: 'Status',
      dataIndex: 'status',
      render: (status: CopyStatus) => <AppTag>{status}</AppTag>,
    },
    {
      key: 'dueDate',
      title: 'Due date',
      dataIndex: 'dueDate',
    },
    {
      key: 'overdueDays',
      title: 'Overdue',
      dataIndex: 'overdueDays',
      render: (days: number) =>
        days > 0 ? <AppTag>{days} days overdue</AppTag> : null,
    },
    {
      key: 'actions',
      title: 'Actions',
      render: (_: unknown, row: CatalogueRow) => (
        <Space>
          <AppButtonPrimary
            size="small"
            onClick={(e) => {
              e.stopPropagation();
              navigate(`/books/copies/${row.copyId}`);
            }}
          >
            Update lending
          </AppButtonPrimary>

          <AppPopconfirm
            title="Remove this book title and all its copies?"
            okText="Remove"
            cancelText="Cancel"
            onConfirm={() => removeBook(row.bookId)}
          >
            <AppButton
              size="small"
              danger
              onClick={(e) => e.stopPropagation()}
            >
              Remove book
            </AppButton>
          </AppPopconfirm>
        </Space>
      ),
    },
  ];

  return (
    <ResourceList<CatalogueRow>
      title="Books"
      columns={columns}
      dataSource={books}
      rowKey="copyId"
      loading={loading}
      search={{
        value: search,
        onChange: setSearch,
        placeholder: 'ISBN, title or author…',
      }}
      filter={{
        content: (
          <Space>
            <Select
              style={{ minWidth: 200 }}
              placeholder="Genre"
              allowClear
              value={genreFilter}
              onChange={setGenreFilter}
              options={GENRES.map((genre) => ({
                value: genre,
                label: genre,
              }))}
            />

            <Select
              style={{ minWidth: 200 }}
              placeholder="Status"
              allowClear
              value={statusFilter}
              onChange={setStatusFilter}
              options={COPY_STATUSES.map((status) => ({
                value: status,
                label: status,
              }))}
            />
          </Space>
        ),
        onApply: () => {
          setAppliedGenre(genreFilter);
          setAppliedStatus(statusFilter);
        },
        onClear: () => {
          setGenreFilter(undefined);
          setStatusFilter(undefined);
          setAppliedGenre(undefined);
          setAppliedStatus(undefined);
        },
      }}
      actions={
        <AppButtonPrimary
          icon={<PlusOutlined />}
          onClick={() => navigate('/books/new')}
        >
          Register book
        </AppButtonPrimary>
      }
      detailView={{
        open: detailOpen,
        onOpenChange: setDetailOpen,
        selectedRecord: selected,
        onSelectedRecordChange: (record) => {
          setSelected(record);
          if (record) {
            setDetailOpen(true);
          }
        },
        title: 'Book copy',
        render: (row) => (
          <div style={{ display: 'grid', gap: 8 }}>
            <div>
              <b>{row.title}</b> · {row.isbn}
            </div>
            <div>Author: {row.author}</div>
            <div>Copy ID: {row.copyId}</div>
            <div>Status: {row.status}</div>

            {row.borrowerPersonalCode && (
              <div>Borrower code: {row.borrowerPersonalCode}</div>
            )}

            {row.borrowerName && (
              <div>Borrower: {row.borrowerName}</div>
            )}

            {row.dueDate && (
              <div>Due date: {row.dueDate}</div>
            )}

            {row.overdueDays > 0 && (
              <div>
                <AppTag>{row.overdueDays} days overdue</AppTag>
              </div>
            )}
          </div>
        ),
      }}
    />
  );
};