async function requestJSON(url, options = {}) {
    const response = await fetch(url, {
        headers: { "Content-Type": "application/json" },
        ...options,
    });

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        const error = new Error(
            data?.detail || data?.message || `HTTP ${response.status}`
        );
        error.status = response.status;
        error.data = data;
        throw error;
    }
    return data;
}


async function createStudent(student) {
    try {
        const json = await requestJSON('api/v1/students/', {
            method: "POST",
            body: JSON.stringify(student)
        })
        return json
    } catch (error) {
        console.error("Ошибка:", error);
        return {}
    }
}

async function deleteStudent(isu) {
    const json = await requestJSON(`api/v1/students/${isu}`, { method: "DELETE" });
    return json;
}

async function getStudent(isu) {
    const json = await requestJSON(`api/v1/students/${isu}`);
    return json;
}

async function putStudent(student) {
    try {
        const json = await requestJSON(`api/v1/students/${student.isu}`, {
            method: "PUT",
            body: JSON.stringify(student),
        });
        return json;
    } catch (error) {
        return null;
    }
}

async function patchStudent(isu, changes) {
    try {
        const json = await requestJSON(`api/v1/students/${isu}`, {
            method: "PATCH",
            body: JSON.stringify(changes),
        });
        return json;
    } catch (error) {
        return null;
    }
}

async function getAllStudent() {
    try {
        const json = await requestJSON('api/v1/students/')
        return json
    } catch (error) {
        return []
    }
}
