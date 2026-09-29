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

    const errorForm = (message,input) => {
        let errorEl = document.createElement("div");
        errorEl.className = 'error-text';
        errorEl.textContent = message;
        errorEl.style.color = 'red';
        errorEl.style.fontSize = '14px';
        input.parentElement.appendChild(errorEl);
        isValid = false;
    }

    if(!value) {
        errorForm("Name is required", name);
    } else if(!/^(?:[А-ЯЁ][а-яё]+(?:-[А-ЯЁ][а-яё]+){0,2}(?:\s+[А-ЯЁ][а-яё]+(?:-[А-ЯЁ][а-яё]+){0,2}){1,2})$/.test(value)) {
        errorForm("Name is not correct", name);
    }



    if(!group.value.trim()) {
        errorForm("Group is required", group);
    } else if(!/^[A-Z][1-4][1-9]\d{2}$/.test(group.value)) {
        errorForm("Group is invalid", group);
    }

    if(!ISU.value.trim()) {
        errorForm("ISU is required", ISU);
    } else if(!/^[1-9]\d{5}$/.test(ISU.value)) {
        errorForm("ISU is invalid", ISU);
    }

    if(!dormNum.value.trim()) {
        errorForm("Dormitory num is required", dormNum);
    } else if(!/^[1-5]$/.test(dormNum.value)) {
        errorForm("Dormitory is invalid", dormNum);
    }

    if(!room.value.trim()) {
        errorForm("Room num is required", room);
    } else if(!/^(?:[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01])[ABC]$/.test(room.value)) {
        errorForm("Room is invalid", room);
    }

    if(!date.value.trim()) {
        errorForm("Date is required", date);
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

})