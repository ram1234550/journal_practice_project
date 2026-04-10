// Адрес бэкенда — один раз здесь, везде используем
const API_URL = 'http://localhost:8080/api';

// Получить токен из localStorage
function getToken() {
    return localStorage.getItem('token');
}

// Заголовки с токеном для защищённых запросов
function authHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + getToken()
    };
}

// ── Авторизация ──────────────────────────────────────
async function login(email, password) {
    const res = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    });
    if (!res.ok) throw new Error('Неверный email или пароль');
    return await res.json();
}

async function register(name, email, password, role) {
    const res = await fetch(`${API_URL}/users/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password, role })
    });
    if (!res.ok) throw new Error('Ошибка регистрации');
    return await res.json();
}

// ── Статьи ───────────────────────────────────────────
async function createArticle(title, topic, content, authorId) {
    const res = await fetch(`${API_URL}/articles`, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify({ title, topic, content, author: { id: authorId } })
    });
    if (!res.ok) throw new Error('Ошибка создания статьи');
    return await res.json();
}

async function getMyArticles(authorId) {
    const res = await fetch(`${API_URL}/articles/author/${authorId}`, {
        headers: authHeaders()
    });
    if (!res.ok) throw new Error('Ошибка загрузки статей');
    return await res.json();
}

async function resubmitArticle(articleId, content) {
    const res = await fetch(`${API_URL}/articles/${articleId}/resubmit?content=${encodeURIComponent(content)}`, {
        method: 'PUT',
        headers: authHeaders()
    });
    if (!res.ok) throw new Error('Ошибка отправки доработки');
    return await res.json();
}

// ── Админ ────────────────────────────────────────────
async function getPendingArticles() {
    const res = await fetch(`${API_URL}/admin/articles/pending`, {
        headers: authHeaders()
    });
    if (!res.ok) throw new Error('Ошибка загрузки');
    return await res.json();
}

async function getReviewers() {
    const res = await fetch(`${API_URL}/admin/reviewers`, {
        headers: authHeaders()
    });
    if (!res.ok) throw new Error('Ошибка загрузки рецензентов');
    return await res.json();
}

async function assignReviewer(articleId, reviewerId) {
    const res = await fetch(`${API_URL}/admin/assign`, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify({ articleId, reviewerId })
    });
    if (!res.ok) throw new Error('Ошибка назначения');
    return await res.json();
}

// ── Рецензент ────────────────────────────────────────
async function getArticlesForReviewer(reviewerId) {
    const res = await fetch(`${API_URL}/articles/reviewer/${reviewerId}`, {
        headers: authHeaders()
    });
    if (!res.ok) throw new Error('Ошибка загрузки');
    return await res.json();
}

async function getBlindArticle(articleId) {
    const res = await fetch(`${API_URL}/articles/${articleId}/blind`, {
        headers: authHeaders()
    });
    if (!res.ok) throw new Error('Ошибка загрузки статьи');
    return await res.json();
}

async function submitReview(articleId, reviewerId, verdict, comment) {
    const res = await fetch(`${API_URL}/articles/submit-review`, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify({ articleId, reviewerId, verdict, comment })
    });
    if (!res.ok) throw new Error('Ошибка отправки рецензии');
    return await res.json();
}