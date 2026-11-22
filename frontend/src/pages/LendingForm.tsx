import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { DatePicker, Form, Input, Select } from 'antd';
import {
  AppButtonPrimary,
  AppButtonSecondary,
  AppCard,
  useAppNotification,
} from '@helex/ui';
import dayjs, { type Dayjs } from 'dayjs';
import {
  booksApi,
  COPY_STATUSES,
  STATUSES_WITH_BORROWER,
  type CatalogueRow,
  type CopyStatus,
} from '../api';

interface LendingFormValues {
  status: CopyStatus;
  borrowerPersonalCode?: string;
  dueDate?: Dayjs;
}

export const LendingForm = () => {
  const navigate = useNavigate();
  const notify = useAppNotification();
  const { id } = useParams();
  const [form] = Form.useForm<LendingFormValues>();

  const [copy, setCopy] = useState<CatalogueRow | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const status = Form.useWatch('status', form);

  useEffect(() => {
    const copyId = Number(id);

    if (!Number.isInteger(copyId) || copyId <= 0) {
      notify.error('Could not load copy', 'Invalid copy ID');
      setLoading(false);
      return;
    }

    booksApi
      .getCopy(copyId)
      .then((result) => {
        setCopy(result);

        form.setFieldsValue({
          status: result.status,
          borrowerPersonalCode: result.borrowerPersonalCode ?? undefined,
          dueDate: result.dueDate ? dayjs(result.dueDate) : undefined,
        });
      })
      .catch((e) => notify.error('Could not load copy', e.message))
      .finally(() => setLoading(false));
  }, [id, form, notify]);

  useEffect(() => {
    if (!status) {
      return;
    }

    if (!STATUSES_WITH_BORROWER.includes(status)) {
      form.setFieldValue('borrowerPersonalCode', undefined);
    }

    if (status !== 'BORROWED') {
      form.setFieldValue('dueDate', undefined);
    }

    if (status === 'BORROWED' && !form.getFieldValue('dueDate')) {
      form.setFieldValue('dueDate', dayjs().add(14, 'day'));
    }
  }, [status, form]);

  const submit = (values: LendingFormValues) => {
    if (!copy) {
      return;
    }

    setSaving(true);

    booksApi
      .updateLending(copy.copyId, {
        status: values.status,
        borrowerPersonalCode: STATUSES_WITH_BORROWER.includes(values.status)
          ? values.borrowerPersonalCode || null
          : null,
        dueDate:
          values.status === 'BORROWED'
            ? values.dueDate?.format('YYYY-MM-DD') ?? null
            : null,
      })
      .then(() => {
        notify.success('Lending updated', `${copy.title} · copy ${copy.copyId}`);
        navigate('/books');
      })
      .catch((e) => notify.error('Could not update lending', e.message))
      .finally(() => setSaving(false));
  };

  if (loading) {
    return (
      <AppCard title="Update lending" style={{ maxWidth: 640, margin: '24px auto' }}>
        Loading…
      </AppCard>
    );
  }

  if (!copy) {
    return (
      <AppCard title="Update lending" style={{ maxWidth: 640, margin: '24px auto' }}>
        Copy not found.
      </AppCard>
    );
  }

  const borrowerEnabled =
    status === 'BORROWED' || status === 'IN_QUEUE';

  return (
    <AppCard title="Update lending" style={{ maxWidth: 640, margin: '24px auto' }}>
      <div style={{ marginBottom: 20 }}>
        <div>
          <b>{copy.title}</b>
        </div>
        <div>ISBN: {copy.isbn}</div>
        <div>Copy ID: {copy.copyId}</div>
      </div>

      <Form
        form={form}
        layout="vertical"
        onFinish={submit}
        requiredMark
      >
        <Form.Item
          name="status"
          label="Status"
          rules={[{ required: true, message: 'Status is required' }]}
        >
          <Select
            options={COPY_STATUSES.map((value) => ({
              value,
              label: value,
            }))}
          />
        </Form.Item>

        <Form.Item
          name="borrowerPersonalCode"
          label="Borrower personal code"
          rules={[
            {
              pattern: /^\d{11}$/,
              message: 'Exactly 11 digits',
            },
            {
              validator: (_, value) => {
                if (borrowerEnabled && !value) {
                  return Promise.reject(
                    new Error('Borrower personal code is required'),
                  );
                }
                return Promise.resolve();
              },
            },
          ]}
        >
          <Input
            disabled={!borrowerEnabled}
            placeholder="11-digit personal code"
          />
        </Form.Item>

        <Form.Item
          name="dueDate"
          label="Due date"
          rules={[
            {
              validator: (_, value) => {
                if (status === 'BORROWED' && !value) {
                  return Promise.reject(new Error('Due date is required'));
                }
                return Promise.resolve();
              },
            },
          ]}
        >
          <DatePicker
            disabled={status !== 'BORROWED'}
            style={{ width: '100%' }}
          />
        </Form.Item>

        <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
          <AppButtonSecondary onClick={() => navigate('/books')}>
            Cancel
          </AppButtonSecondary>

          <AppButtonPrimary htmlType="submit" loading={saving}>
            Save
          </AppButtonPrimary>
        </div>
      </Form>
    </AppCard>
  );
};