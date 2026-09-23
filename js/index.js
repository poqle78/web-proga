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


function addToTable(student) {
    const tbody = document.getElementById('studsTable').getElementsByTagName('tbody')[0];

    var tr = document.createElement('tr');

    var td = document.createElement('td');
    td.classList.add('truncate');
    td.textContent = student.name;
    tr.appendChild(td);

    var td = document.createElement('td');
    td.textContent = student.group;
    tr.appendChild(td);

    var td = document.createElement('td');
    td.textContent = student.isu;
    tr.appendChild(td);

    var td = document.createElement('td');
    td.textContent = student.dorm;
    tr.appendChild(td);

    var td = document.createElement('td');
    td.textContent = student.room;
    tr.appendChild(td);

    var td = document.createElement('td');
    td.textContent = student.data;
    tr.appendChild(td);

    tbody.appendChild(tr);
}

addToTable(info);
addToTable(info);
addToTable(info);
addToTable(info);
addToTable(info);