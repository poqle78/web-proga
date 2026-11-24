export function StudentFilters({ value, onChange, onApply, onReset }) {
  const set = (field) => (e) => onChange({ [field]: e.target.value });

  return (
    <section className="filters">
      <div className="filter-field">
        <label htmlFor="filterFullName">ФИО</label>
        <input
          type="text"
          id="filterFullName"
          placeholder="Например, Евгений"
          value={value.name}
          onChange={set('name')}
        />
      </div>

      <div className="filter-field">
        <label htmlFor="filterGroup">Группа</label>
        <input
          type="text"
          id="filterGroup"
          placeholder="Например, P3120"
          pattern="[A-Z][1-9][1-4][0-9]{2}"
          value={value.group}
          onChange={set('group')}
        />
      </div>

      <div className="filter-field">
        <label htmlFor="filterIsuId">ИСУ ID</label>
        <input
          type="number"
          id="filterIsuId"
          min="100000"
          max="999999"
          value={value.isu}
          onChange={set('isu')}
        />
      </div>

      <div className="filter-field">
        <label htmlFor="filterDormitory">Общежитие</label>
        <input
          type="number"
          id="filterDormitory"
          min="1"
          max="99"
          value={value.dormitory}
          onChange={set('dormitory')}
        />
      </div>

      <div className="filter-field">
        <label htmlFor="filterRoom">Комната</label>
        <input
          type="number"
          id="filterRoom"
          min="1"
          max="9999"
          value={value.room}
          onChange={set('room')}
        />
      </div>

      <div className="filter-field">
        <label htmlFor="filterMoveInDate">Дата заселения</label>
        <input
          type="date"
          id="filterMoveInDate"
          value={value.check_in}
          onChange={set('check_in')}
        />
      </div>

      <div className="filter-field">
        <label htmlFor="filterIsForeign">Иностранный студент</label>
        <select
          id="filterIsForeign"
          value={value.is_foreigner}
          onChange={set('is_foreigner')}
        >
          <option value="">Все</option>
          <option value="true">Да</option>
          <option value="false">Нет</option>
        </select>
      </div>

      <div className="filter-field">
        <label htmlFor="filterNotes">Заметки</label>
        <input
          type="text"
          id="filterNotes"
          placeholder="Поиск по тексту"
          value={value.notes}
          onChange={set('notes')}
        />
      </div>

      <div className="filter-actions">
        <button type="button" onClick={onApply}>Найти</button>
        <button type="button" onClick={onReset}>Сбросить</button>
      </div>
    </section>
  );
}