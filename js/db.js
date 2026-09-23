const dbPromise = new Promise((resolve, reject) => {
    const openRequest = indexedDB.open("students", 1);
    openRequest.onupgradeneeded = () => {
        const db = openRequest.result;
        if (!db.objectStoreNames.contains('students')) {
            db.createObjectStore('students', { keyPath: 'isu' });
        }
    };
    openRequest.onsuccess = () => resolve(openRequest.result);
    openRequest.onerror = () => reject(openRequest.error);
});

async function createStudent(student) {
    const db = await dbPromise;
    return new Promise((resolve, reject) => {
        const transaction = db.transaction("students", "readwrite");
        const request = transaction.objectStore("students").add(student);
        request.onsuccess = () => resolve(request.result);
        request.onerror = () => reject(request.error);
    });
}

async function deleteStudent(student) {
    const db = await dbPromise;
    return new Promise((resolve, reject) => {
        const transaction = db.transaction("students", "readwrite");
        const request = transaction.objectStore("students").delete(student.isu);
        request.onsuccess = () => resolve(request.result);
        request.onerror = () => reject(request.error);
    });
}
