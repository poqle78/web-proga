import {
  createSortedRowModel,
  rowSortingFeature,
  sortFns,
  tableFeatures,
  useTable,
} from '@tanstack/react-table'
import { useEffect } from "react";
import { Route, Routes, BrowserRouter } from "react-router-dom";
import useAuthStore from './store';
import AuthForm from './components/AuthForm';

const data = [
  { firstName: 'tanner', lastName: 'linsley', age: 24 },
  { firstName: 'tandy', lastName: 'miller', age: 40 },
  { firstName: 'joe', lastName: 'dirte', age: 45 },
]

// 4. Define your columns
const columns = [
  {
    accessorKey: 'firstName',
    header: 'First Name',
    cell: (info) => info.getValue(),
  },
  {
    accessorFn: (row) => row.lastName,
    id: 'lastName',
    header: () => <span>Last Name</span>,
    cell: (info) => <i>{info.getValue()}</i>,
  },
  {
    accessorKey: 'age',
    header: () => 'Age',
  },
]


const features = tableFeatures({
  rowSortingFeature,
  sortedRowModel: createSortedRowModel(),
  sortFns,
})

export function PersonTable() {
  const table = useTable({
    key: 'person-table',
    features,
    columns,
    data,
  })

  return (
    <table>
      <thead>
        {table.getHeaderGroups().map((headerGroup) => (
          <tr key={headerGroup.id}>
            {headerGroup.headers.map((header) => (
              <th key={header.id}>
                {header.isPlaceholder ? null : (
                  <div
                    style={{
                      cursor: header.column.getCanSort()
                        ? 'pointer'
                        : undefined,
                    }}
                    onClick={header.column.getToggleSortingHandler()}
                  >
                    <table.FlexRender header={header} />
                    {{
                      asc: ' 🔼',
                      desc: ' 🔽',
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
              <td key={cell.id}>
                <table.FlexRender cell={cell} />
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  )
}

export default function MyApp() {
  useEffect(() => {
    useAuthStore.getState().checkAuth();
  }, []);
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<AuthForm />} />
      </Routes>
    </BrowserRouter>
  );
}