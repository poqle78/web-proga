// Выбор по ID (рекомендуется для конкретных форм)
const form = document.getElementById('signup-form');

// Доступ к коллекции элементов формы
const forms = document.forms;
const signupForm = forms['signup-form']; // по id/name
const firstForm = forms[0]; // по индексу