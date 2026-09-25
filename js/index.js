const schema = {
  name: value => /^([A-Z][a-z\-]* )+[A-Z][a-z\-]*( \w+\.?)?$/.test(value),
  isu: value => parseInt(value) === Number(value) && 100000 <= value <= 999999,
  //   phone: value => /^(\+?\d{1,2}-)?\d{3}-\d{3}-\d{4}$/.test(value)
};

let info = {
  name: 'John Doaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaae',
  group: 'G1234',
  isu: 12345,
  dorm: 1,
  room: 1013,
  data: '10344901',
  isUnru: false,
  note: ''
};

let info2 = {
  name: 'John Doaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaae',
  group: 'G1234',
  isu: 121345,
  dorm: 1,
  room: 1013,
  data: '10344901',
  isUnru: false,
  note: ''
};


const validate = (object, schema) => Object
  .keys(schema)
  .filter(key => !schema[key](object[key]))
  .map(key => new Error(`${key} is invalid.`));

const errors = validate(info, schema);

if (errors.length > 0) {
  for (const { message } of errors) {
    console.log(message);
  }
} else {
  console.log('info is valid');
}

function rowToStudent(cells) {
  return { 
    name: cells[0], 
    group: cells[1], 
    isu: cells[2], 
    dorm: cells[3], 
    room: cells[4], 
    data: cells[5] 
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
  td.textContent = student.dorm;
  tr.appendChild(td);

  td = document.createElement('td');
  td.textContent = student.room;
  tr.appendChild(td);

  td = document.createElement('td');
  td.textContent = student.data;
  tr.appendChild(td);

  tbody.appendChild(tr);
}


async function updateTable() {
  const studentFromDB = await getAllStudent();
  const studentsMap = {};
  studentFromDB.forEach(element => {
    studentsMap[element.isu] = element
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
    Object.entries(student).forEach(([key, value]) => {value.textContent = studentsMap[isu][key]});
    delete studentsMap[isu];
  }
  Object.entries(studentsMap).forEach(([key, value]) => {addToTable(value)});
}

const intervalId = setInterval(updateTable, 1000);
