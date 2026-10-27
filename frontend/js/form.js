const studentForm = document.getElementById("student-form");

const student_name = document.getElementById("fullName");
const group = document.getElementById("group");
const isuInput = document.getElementById("ISU");
const dormNum = document.getElementById("dormNum");
const room = document.getElementById("room");
const date = document.getElementById("date");
const notes = document.getElementById("notes");

const today = new Date().toISOString().split('T')[0];
date.max = today;

let isEdit = false;
let originalStudent = null;
let originalIsu = null;

const url = new URL(document.URL);
if (url.searchParams.get('isu')) {
    isEdit = true;
    originalIsu = parseInt(url.searchParams.get('isu'));
    document.getElementById("submit-btn").textContent = 'Применить';
    (async () => {
        originalStudent = await getStudent(originalIsu);

        student_name.setAttribute('value', originalStudent.name);
        group.setAttribute('value', originalStudent.group);
        ISU.setAttribute('value', originalStudent.isu);
        dormNum.setAttribute('value', originalStudent.dormitory);
        room.setAttribute('value', originalStudent.room);
        const raw = originalStudent.check_in;
        let check_in;
        if (raw) {
            const [y, m, d] = raw.split('-');
            check_in = `${y}-${m}-${d}`
        }
        date.value = check_in;
        notes.setAttribute('value', originalStudent.notes ?? "");
        document.getElementById("isNoRu").checked = originalStudent.is_foreign;

    })();
}

function validateFullName() {
    const fullName = student_name.value.trim();
    const nameParts = fullName.split(/\s+/);
    if (nameParts.length < 2) {
        return "ФИО должно содержать минимум 2 отдельных слова";
    } else if (nameParts.some(part => part.length < 2)) {
        return "Длина каждого должна составлять не менее 2 символов";
    } else if (/\d/.test(fullName)) {
        return "В имени не должно быть цифр";
    }
    return null;
}

function validateGroup() {
    const value = group.value.trim();
    if (!value) return "Поле группа не может быть пустым";
    if (!/^[A-Z][1-9][1-4]\d{2}$/.test(value)) return "Поле группа не соответствует требуемой форме";
    return null;
}

async function validateIsu() {
    const isuId = isuInput.valueAsNumber;
    if (!isuId) return "Поле ИСУ не может быть пустым";
    if (!(isuId >= 100000 && isuId <= 999999)) return "ИСУ не соответствует требуемой форме";
    if (!(await isIsuIdUnique(isuId) && (isEdit || originalIsu !== isuId))) {
        return "Студент с таким ИСУ ID уже существует";
    }
    return null;
}

function validateDorm() {
    const value = dormNum.valueAsNumber;
    if (!value) return "Поле номер общежития не может быть пустым";
    if (!(value >= 1 && value <= 99)) return "Поле номер общежития должен быть в диапазоне от 1 до 99";
    return null;
}

function validateRoom() {
    const value = room.valueAsNumber;
    if (!value) return "Поле комната не может быть пустым";
    
    if (!(value >= 1 && value <= 9999)) {
        return "Комната должна быть от 1 до 9999";
    }
    return null;
}

function validateDate() {
    if (!date.value.trim()) return "Поле дата не может быть пустым";

    const picked = new Date(date.value);
    if (isNaN(picked.getTime())) return "Некорректная дата";

    let todayDate = new Date();
    todayDate++;

    const minDate = new Date();
    minDate.setFullYear(minDate.getFullYear() - 10);
    minDate.setHours(0, 0, 0, 0);

    if (picked > todayDate) return "Дата не может быть в будущем";
    if (picked < minDate) return "Дата не может быть раньше, чем 10 лет назад";
    return null;
}

student_name.addEventListener("input", validateFullName);


async function isIsuIdUnique(isuId) {
    try {
        const student = await getStudent(isuId);
    } catch (error) {
        if (error.status === 404) {
            return true;
        }
    }
    return false;
}

function clearErrors() {
    document.querySelectorAll('.error-text').forEach(el => el.remove());
}

function showError(message, input) {
    const errorEl = document.createElement("div");
    errorEl.className = 'error-text';
    errorEl.textContent = message;
    errorEl.style.color = 'red';
    errorEl.style.fontSize = '14px';
    input.parentElement.appendChild(errorEl);
}

function diffStudent(original, next) {
    const changes = {};
    for (const key of Object.keys(next)) {
        const a = original?.[key];
        const b = next[key];
        if (key === "check_in") {
            if (String(a).split("T")[0] !== String(b).split("T")[0]) changes[key] = b;
        } else if (a !== b) {
            changes[key] = b;
        }
    }
    return changes;
}


studentForm.addEventListener("submit", async function (event) {
    event.preventDefault();
    clearErrors();

    const checks = [
        [student_name, validateFullName],
        [group, validateGroup],
        [isuInput, validateIsu],
        [dormNum, validateDorm],
        [room, validateRoom],
        [date, validateDate],
    ];

    let isValid = true;
    for (const [input, validator] of checks) {
        const message = await validator();
        if (message) {
            showError(message, input);
            isValid = false;
        }
    }
    if (!isValid) return;

    const data = {
        name: student_name.value.trim(),
        group: group.value.trim(),
        isu: parseInt(isuInput.value.trim()),
        dormitory: parseInt(dormNum.value.trim()),
        room: parseInt(dormNum.value.trim()),
        check_in: date.value,
        is_foreign: document.getElementById("isNoRu").checked,
        notes: notes.value.trim()
    };

    if (isEdit) {
        if (originalIsu !== data.isu) {
            if (validateIsu) {
                await createStudent(data);
                await deleteStudent(originalIsu);
            } else {

            }
        } else {
            const changes = diffStudent(originalStudent, data);
            const count = Object.keys(changes).length;
            if (count === 0) {
                
            } else if (count <= 3) {
                await patchStudent(data.isu, changes);
            } else {
                await putStudent(data);
            }
        }
    } else {
        await createStudent(data);
    }

    location.href = 'index.html'
})
