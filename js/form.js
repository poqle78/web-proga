document.getElementById("student-form").addEventListener("submit", function (event) {
    event.preventDefault();

    document.querySelectorAll('.error-text').forEach(el => el.remove());

    let isValid = true;

    let name = document.getElementById("fullName");
    let value = name.value.trim()
    let group = document.getElementById("group");
    let ISU = document.getElementById("ISU");
    let dormNum = document.getElementById("dormNum");
    let room = document.getElementById("room");
    let date = document.getElementById("date");
    let dateValue = date.value;
    let notes = document.getElementById("notes");

    const errorForm = (message, input) => {
        let errorEl = document.createElement("div");
        errorEl.className = 'error-text';
        errorEl.textContent = message;
        errorEl.style.color = 'red';
        errorEl.style.fontSize = '14px';
        input.parentElement.appendChild(errorEl);
        isValid = false;
    }

    if (!value) {
        errorForm("Поле ФИО не может быть пустым", name);
    } else if (!/^[A-Za-zА-Яа-яЁё]{2,}(?:[-\s][A-Za-zА-Яа-яЁё]{2,})*$/.test(value)) {
        errorForm("Поле ФИО не соответствует требуемой форме", name);
    }



    if (!group.value.trim()) {
        errorForm("Поле группа не может быть пустым", group);
    } else if (!/^[A-Z][1-9][1-4]\d{2}$/.test(group.value)) {
        errorForm("Поле группа не соответствует требуемой форме", group);
    }

    if (!ISU.value.trim()) {
        errorForm("Поле ИСУ не может быть пустым", ISU);
    } else if (!/^[1-9]\d{5}$/.test(ISU.value)) {
        errorForm("ИСУ не соответствует требуемой форме", ISU);
    }

    if (!dormNum.value.trim()) {
        errorForm("Поле номер общежития не может быть пустым", dormNum);
    } else if (!/^[1-5]$/.test(dormNum.value)) {
        errorForm("Поле номер общежития не соответствует требуемой форме", dormNum);
    }

    if (!room.value.trim()) {
        errorForm("Поле комната не может быть пустым", room);
    } else if (!/^(?:[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01])$/.test(room.value)) {
        errorForm("Поле комната не соответсвует требуемой форме", room);
    }

    if (!date.value.trim()) {
        errorForm("Поле дата не может быть пустым", date);
    } else {
        let picked = new Date(dateValue);
        let today = new Date();
        today.setHours(0, 0, 0, 0);

        let minDate = new Date();
        minDate.setFullYear(minDate.getFullYear() - 6);
        minDate.setHours(0, 0, 0, 0);

        if (isNaN(picked.getTime())) {
            errorForm("Некорректная дата", date);
        } else if (picked > today) {
            errorForm("Дата не может быть в будущем", date);
        } else if (picked < minDate) {
            errorForm("Дата не может быть раньше, чем 6 лет назад", date);
        }
    }

    if (isValid) {
        let isUnru = document.getElementById("isNoRu").checked;
        createStudent({
            name: value, group: group.value.trim(), isu: parseInt(ISU.value.trim()), dorm: parseInt(dormNum.value.trim()), room: room.value.trim(), date: new Date(dateValue), isUnru: isUnru, note: notes.value.trim()
        });
        location.href = 'index.html'
    }
})