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
    try {
        const response = await fetch('api/v1/students', {
            method: "POST",
            body: JSON.stringify(student),
            headers: {
                "Content-Type": "application/json",
            }
        })
        const json = await response.json();
        console.log("Успех:", JSON.stringify(json));
        return json
    } catch (error) {
        console.error("Ошибка:", error);
        return {}
    }
}

async function deleteStudent(isu) {
    const store = await getStore("readwrite");
    return requestToPromise(store.delete(isu));
}

async function getStudent(isu) {
    return
}

async function updateStudent(student) {
    const store = await getStore("readwrite");
    return requestToPromise(store.put(student));
}

async function getAllStudent() {
    try {
        const response = await fetch('api/v1/students/')
        const json = await response.json();
        console.log("Успех:", JSON.stringify(json));
        return json
    } catch (error) {
        console.error("Ошибка:", error);
        return []
    }
}
