const filterRoomInput = document.getElementById("filterRoom");
const filterIsuIdInput = document.getElementById("filterIsuId");
const filterGroupInput = document.getElementById("filterGroup");
const filterDormitoryInput = document.getElementById("filterDormitory");
const filterIsForeignSelect = document.getElementById("filterIsForeign");
const filterFullNameInput = document.getElementById("filterFullName");
const filterMoveInDateInput = document.getElementById("filterMoveInDate");
const filterNotesInput = document.getElementById("filterNotes");
const applyFiltersButton = document.getElementById("applyFilters");
const resetFiltersButton = document.getElementById("resetFilters");


function readFilters() {
  const filters = {};

  const name = filterFullNameInput.value.trim();
  const group = filterGroupInput.value.trim();
  const dormitory = filterDormitoryInput.value.trim();
  const roomNumber = filterRoomInput.value.trim();
  const isu = filterIsuIdInput.value.trim();
  const check_in = filterMoveInDateInput.value;
  const is_foregin = filterIsForeignSelect.value;
  const notes = filterNotesInput.value.trim();

  if (name) {
    filters.name = name;
  }
  if (group) {
    filters.group = group;
  }
  if (dormitory) {
    filters.dormitory = Number(dormitory);
  }
  if (roomNumber) {
    filters.roomNumber = Number(roomNumber);
  }
  if (isu) {
    filters.isu = Number(isu);
  }
  if (check_in) {
    filters.check_in = check_in;
  }
  if (is_foregin !== "") {
    filters.is_foregin = is_foregin === "true";
  }
  if (notes) {
    filters.notes = notes;
  }

  return filters;
}


async function loadStudents(filters = {}) {
  try {
    const students = getAllStudent(filters);
    updateTable(students);
  } catch (error) {
    console.error("Ошибка загрузки студентов:", error);
  }
}


async function applyFilters() {
  const filters = readFilters();
  await loadStudents(filters);
}

function resetFilters() {
    filterFullNameInput.value = "";
    filterGroupInput.value = "";
    filterDormitoryInput.value = "";
    filterMoveInDateInput.value = "";
    filterIsForeignSelect.value = "";
    filterNotesInput.value = "";
    filterRoomInput.value = "";
    filterIsuIdInput.value = "";

    loadStudents();
}

function rowToStudent(cells) {
  return {
    name: cells[0],
    group: cells[1],
    isu: cells[2],
    dormitory: cells[3],
    room: cells[4],
    date: cells[5]
  }
}


function addToTable(student) {
  const tbody = document.getElementById('studsTable').getElementsByTagName('tbody')[0];

  let tr = document.createElement('tr');

  let td = document.createElement('td');
  td.classList.add('truncate');
  td.textContent = student.name;
  tr.appendChild(td);

  td = document.createElement('td');
  td.textContent = student.group;
  tr.appendChild(td);

  td = document.createElement('td');
  td.textContent = student.isu;
  tr.appendChild(td);

  td = document.createElement('td');
  td.textContent = student.dormitory;
  tr.appendChild(td);

  td = document.createElement('td');
  td.textContent = student.room;
  tr.appendChild(td);

  td = document.createElement('td');
  td.textContent = student.date;
  tr.appendChild(td);

  td = document.createElement('td');
  let button_d = document.createElement('button');
  button_d.textContent = 'Удалить';
  button_d.className = 'btn-delete';


  button_d.onclick = async function () {
    const ok = confirm("Вы уверены, что хотите удалить этого студента?")
    if (!ok) return;
    await deleteStudent(student.isu);
    tr.remove();
  };

  let button_edit = document.createElement('button');
  button_edit.textContent = 'Изменить';
  button_edit.className = 'btn-edit';

  button_edit.onclick = function () {
    location.href = 'form.html?isu=' + student.isu;
  };

  let button_details = document.createElement('button');
  button_details.textContent = 'Подробнее';
  button_details.className = 'btn-details';

  button_details.onclick = function () {
    location.href = 'details.html?isu=' + student.isu;
  };

  td.appendChild(button_details);
  td.appendChild(button_edit);
  td.appendChild(button_d);
  tr.appendChild(td);


  tbody.appendChild(tr);
}


async function updateTable(get_students_func) {
  let studentFromDB = []
  try {
    studentFromDB = await get_students_func;
  } catch (error) {
    console.error("Ошибка загрузки студентов:", error);
  }

  const studentsMap = {};
  studentFromDB.forEach(element => {
    const raw = element.check_in;
    let dateStr = '';
    if (raw) {
      const [y, m, d] = raw.split('-').map(Number);
      dateStr = new Date(y, m - 1, d).toLocaleDateString('ru-RU');
    }
    studentsMap[element.isu] = { ...element, date: dateStr }
  });
  const tbody = document.getElementById('studsTable').getElementsByTagName('tbody')[0];

  const rows = tbody.querySelectorAll('tr');
  for (let i = 1; i < rows.length; i++) {
    const cells = rows[i].querySelectorAll('td');
    const student = rowToStudent(cells);
    let isu = parseInt(student.isu.textContent);

    if (!(isu in studentsMap)) {
      rows[i].remove();
      continue;
    }
    Object.entries(student).forEach(([key, value]) => { value.textContent = studentsMap[isu][key] });
    delete studentsMap[isu];
  }
  Object.entries(studentsMap).forEach(([_, value]) => { addToTable(value) });
}
updateTable(getAllStudent({}));
// const intervalId = setInterval(updateTable, 1000);

applyFiltersButton.addEventListener("click", applyFilters);
resetFiltersButton.addEventListener("click", resetFilters);