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

function requestToPromise(request) {
    return new Promise((resolve, reject) => {
        request.onsuccess = () => resolve(request.result);
        request.onerror = () => reject(request.error);
    });
}

async function getStore(mode) {
    const db = await dbPromise;
    const transaction = db.transaction("students", mode);
    return transaction.objectStore("students");
}


async function createStudent(student) {
    const store = await getStore("readwrite");
    return requestToPromise(store.add(student));
}

async function deleteStudent(student) {
    const store = await getStore("readwrite");
    return requestToPromise(store.delete(student.isu));
}

async function readStudent(isu) {
    const store = await getStore("readonly");
    return requestToPromise(store.get(isu));
}

async function updateStudent(student) {
    const store = await getStore("readwrite");
    return requestToPromise(store.put(student));
}
