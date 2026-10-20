const url = new URL(document.URL);
if (url.searchParams.get('isu')) {
    (async () => {
        student = await getStudent(parseInt(url.searchParams.get('isu')));
        document.getElementById('name').textContent = student.name;
        document.getElementById('group').textContent = student.group;
        document.getElementById('isu').textContent = student.isu;
        document.getElementById('dormNum').textContent = student.dormitory;
        document.getElementById('room').textContent = student.room;
        document.getElementById('isNoRu').textContent = student.is_foregin ? "да" : "нет";
        document.getElementById('notes').textContent = student.note;
        const raw = student.check_in;
        let dateStr = '';
        if (raw) {
            const [y, m, d] = raw.split('-').map(Number);
            dateStr = new Date(y, m - 1, d).toLocaleDateString('ru-RU');
        }
        document.getElementById('date').textContent = dateStr;
    })();
}
