function buildQueryString(filters) {
  const params = new URLSearchParams();

  if (filters.name) {
    params.set("name", filters.name);
  }
  if (filters.group) {
    params.set("group", filters.group);
  }
  if (filters.dormitory) {
    params.set("dormitory", filters.dormitory);
  }
  if (filters.room) {
    params.set("room", filters.room);
  }
  if (filters.isu) {
    params.set("isu", filters.isu);
  }
  if (filters.check_in) {
    params.set("check_in", filters.check_in);
  }
  if (filters.is_foregin !== undefined) {
    params.set("is_foregin", filters.is_foregin);
  }
  if (filters.notes) {
    params.set("notes", filters.notes);
  }

  return params.toString();
}


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

async function getAllStudent(filters = {}) {
    try {
        const json = await requestJSON('api/v1/students/?' + buildQueryString(filters))
        return json
    } catch (error) {
        return []
    }
}
