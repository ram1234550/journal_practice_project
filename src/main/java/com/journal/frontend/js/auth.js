// Сохранить данные после логина
function saveAuth(token, role, name, id) {
    localStorage.setItem('token', token);
    localStorage.setItem('role', role);
    localStorage.setItem('name', name);
    localStorage.setItem('userId', id);
}

// Выйти из аккаунта
function logout() {
    localStorage.clear();
    window.location.href = '../login.html';
}

// Проверить что пользователь залогинен — если нет, редирект на логин
function requireAuth(expectedRole) {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    if (!token) {
        window.location.href = '../login.html';
        return false;
    }

    if (expectedRole && role !== expectedRole) {
        alert('У вас нет доступа к этой странице');
        window.location.href = '../login.html';
        return false;
    }

    return true;
}

// Показать имя пользователя в шапке
function showUserName() {
    const name = localStorage.getItem('name');
    const el = document.getElementById('userName');
    if (el && name) el.textContent = name;
}