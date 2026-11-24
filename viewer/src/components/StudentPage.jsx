import { useCallback, useEffect, useState } from 'react';
import { deleteStudent, getAllStudent } from '../../js/db';
import { StudentFilters } from './StudentFilters';
import { StudentsTable } from './StudentsTable';

const PAGE_SIZE = 15;

const EMPTY_FILTERS = {
  name: '',
  group: '',
  isu: '',
  dormitory: '',
  room: '',
  check_in: '',
  is_foreigner: '',
  notes: '',
};

function buildFilters(draft) {
  const filters = {};

  const name = draft.name.trim();
  const group = draft.group.trim();
  const notes = draft.notes.trim();

  if (name) filters.name = name;
  if (group) filters.group = group;
  if (draft.dormitory) filters.dormitory = Number(draft.dormitory);
  if (draft.room) filters.room = Number(draft.room);
  if (draft.isu) filters.isu = Number(draft.isu);
  if (draft.check_in) filters.check_in = draft.check_in;
  if (draft.is_foreigner !== '') filters.is_foreigner = draft.is_foreigner === 'true';
  if (notes) filters.notes = notes;

  return filters;
}

export function StudentsPage() {
  const [draftFilters, setDraftFilters] = useState(EMPTY_FILTERS); // то, что в инпутах
  const [appliedFilters, setAppliedFilters] = useState({});        // то, что ушло в запрос
  const [page, setPage] = useState(0);
  const [sorting, setSorting] = useState([]);                      // SortingState TanStack
  const [students, setStudents] = useState([]);
  const [hasNext, setHasNext] = useState(false);
  const [reloadId, setReloadId] = useState(0); // принудительная перезагрузка (после удаления и т.п.)

  // Аналог loadStudents(): запрос при любом изменении фильтров/страницы/сортировки
  useEffect(() => {
    let active = true;

    const params = {
      ...appliedFilters,
      page,
      size: PAGE_SIZE,
    };

    const [sort] = sorting;
    if (sort) {
      params.sortBy = sort.id;
      params.sortDirection = sort.desc ? 'desc' : 'asc';
    }

    getAllStudent(params)
      .then((response) => {
        if (!active) return; // игнорируем устаревший ответ
        setStudents(response.items ?? []);
        setHasNext(Boolean(response.hasNext));
      })
      .catch((error) => console.error('Ошибка загрузки студентов:', error));

    return () => {
      active = false;
    };
  }, [appliedFilters, page, sorting, reloadId]);

  // Аналог changeSort(): сбрасываем страницу при смене сортировки.
  // При manualSorting авто-сброс страницы не работает, делаем его сами.
  const handleSortingChange = useCallback((updater) => {
    setSorting((old) => (typeof updater === 'function' ? updater(old) : updater));
    setPage(0);
  }, []);

  const handleApplyFilters = () => {
    setAppliedFilters(buildFilters(draftFilters));
    setPage(0);
    setReloadId((n) => n + 1);
  };

  const handleResetFilters = () => {
    setDraftFilters(EMPTY_FILTERS);
    setAppliedFilters({});
    setPage(0);
    setReloadId((n) => n + 1);
  };

  const handleDelete = useCallback(async (isu) => {
    const ok = window.confirm('Вы уверены, что хотите удалить этого студента?');
    if (!ok) return;

    try {
      await deleteStudent(isu);
      setReloadId((n) => n + 1); // перезагружаем страницу, чтобы пагинация осталась корректной
    } catch (error) {
      console.error('Ошибка удаления студента:', error);
    }
  }, []);

  return (
    <>
      <h1>Студенты</h1>

      <StudentFilters
        value={draftFilters}
        onChange={(patch) => setDraftFilters((prev) => ({ ...prev, ...patch }))}
        onApply={handleApplyFilters}
        onReset={handleResetFilters}
      />

      <div className="table-wrapper">
        <StudentsTable
          students={students}
          sorting={sorting}
          onSortingChange={handleSortingChange}
          onDelete={handleDelete}
        />
      </div>

      <div className="pagination">
        <button
          type="button"
          hidden={page === 0}
          onClick={() => setPage((p) => Math.max(0, p - 1))}
        >
          ← Назад
        </button>
        <span id="pageInfo">Страница {page + 1}</span>
        <button
          type="button"
          hidden={!hasNext}
          onClick={() => setPage((p) => p + 1)}
        >
          Вперёд →
        </button>
      </div>

      <button
        type="button"
        id="myButton"
        className="float-left submit-button"
        onClick={() => {
          window.location.href = 'form.html';
        }}
      >
        Добавить студента
      </button>
    </>
  );
}
