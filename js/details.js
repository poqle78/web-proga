const url = new URL(document.URL);
if (url.searchParams.get('isu')) {
    (async () => {
        student = await getStudent(parseInt(url.searchParams.get('isu')));
        document.getElementById('name').textContent = student.name;
        document.getElementById('group').textContent = student.group;
        document.getElementById('isu').textContent = student.isu;
        document.getElementById('dormNum').textContent = student.dorm;
        document.getElementById('room').textContent = student.room;
        document.getElementById('isNoRu').textContent = student.isUnru ? "да" : "нет";
        document.getElementById('notes').textContent = student.note;
        document.getElementById('date').textContent = student.date.toLocaleDateString('ru-RU');
    })();
}
