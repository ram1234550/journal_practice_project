function saveAuth(token, role, name, id, roles) {
    localStorage.setItem('token', token);
    localStorage.setItem('role', role);
    localStorage.setItem('roles', JSON.stringify(roles || [role]));
    localStorage.setItem('name', name);
    localStorage.setItem('userId', id);
}

function logout() {
    localStorage.clear();
    window.location.href = '../login.html';
}

function requireAuth(expectedRole) {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    const roles = JSON.parse(localStorage.getItem('roles') || JSON.stringify(role ? [role] : []));

    if (!token) {
        window.location.href = '../login.html';
        return false;
    }

    const allowedRoles = Array.isArray(expectedRole) ? expectedRole : [expectedRole];

    if (expectedRole && !allowedRoles.some(value => roles.includes(value))) {
        alert('У вас нет доступа к этой странице');
        window.location.href = '../login.html';
        return false;
    }

    return true;
}

function showUserName() {
    const name = localStorage.getItem('name');
    const el = document.getElementById('userName');
    if (el && name) el.textContent = name;
}
