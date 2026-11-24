import { useMemo } from 'react';
import {
  rowSortingFeature,
  tableFeatures,
  useTable,
} from '@tanstack/react-table';

const features = tableFeatures({
  rowSortingFeature,
});

function formatDate(raw) {
  if (!raw) return '';
  const [y, m, d] = raw.split('-').map(Number);
  return new Date(y, m - 1, d).toLocaleDateString('ru-RU');
}

export function StudentsTable({ students, sorting, onSortingChange, onDelete }) {
  const columns = [
      { accessorKey: 'name', header: 'ФИО' },
      { accessorKey: 'group', header: 'Группа' },
      { accessorKey: 'isu', header: 'ИСУ ID' },
      { accessorKey: 'dormitory', header: 'Общежитие' },
      { accessorKey: 'room', header: 'Комната' },
      {
        accessorKey: 'check_in',
        header: 'Срок заселения',
        cell: (info) => formatDate(info.getValue()),
      },
      {
        id: 'actions',
        header: 'Действия',
        enableSorting: false,
        cell: ({ row }) => (
          <>
            <button
              type="button"
              className="btn-details"
              onClick={() => {
                window.location.href = 'details.html?isu=' + row.original.isu;
              }}
            >
              Подробнее
            </button>
            <button
              type="button"
              className="btn-edit"
              onClick={() => {
                window.location.href = 'form.html?isu=' + row.original.isu;
              }}
            >
              Изменить
            </button>
            <button
              type="button"
              className="btn-delete"
              onClick={() => onDelete(row.original.isu)}
            >
              Удалить
            </button>
          </>
        ),
      },
    ]

  const table = useTable({
    features,
    columns,
    data: students,
    getRowId: (row) => String(row.isu),
    manualSorting: true,
    enableMultiSort: false,
    enableSortingRemoval: true,
    sortDescFirst: false,
    state: { sorting },
    onSortingChange,
  });

  return (
    <table id="studsTable">
      <thead>
        {table.getHeaderGroups().map((headerGroup) => (
          <tr key={headerGroup.id}>
            {headerGroup.headers.map((header) => (
              <th key={header.id}>
                {header.isPlaceholder ? null : (
                  <div
                    style={{
                      cursor: header.column.getCanSort() ? 'pointer' : undefined,
                    }}
                    onClick={header.column.getToggleSortingHandler()}
                  >
                    <table.FlexRender header={header} />
                    {{
                      asc: ' ▲',
                      desc: ' ▼',
                    }[header.column.getIsSorted()] ?? null}
                  </div>
                )}
              </th>
            ))}
          </tr>
        ))}
      </thead>
      <tbody>
        {table.getRowModel().rows.map((row) => (
          <tr key={row.id}>
            {row.getAllCells().map((cell) => (
              <td
                key={cell.id}
                className={cell.column.id === 'name' ? 'truncate' : undefined}
              >
                <table.FlexRender cell={cell} />
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}
