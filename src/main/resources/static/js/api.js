const API_URL = `${window.location.origin}/api`;

function getToken() {
    return localStorage.getItem('token');
}

function authHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + getToken()
    };
}

async function parseResponse(res, fallbackMessage) {
    if (res.ok) {
        if (res.status === 204) {
            return null;
        }
        return await res.json();
    }

    let message = fallbackMessage;

    try {
        const data = await res.json();
        if (data.message) {
            message = data.message;
        }
    } catch (e) {
        try {
            const text = await res.text();
            if (text) {
                message = text;
            }
        } catch (ignored) {
        }
    }

    throw new Error(message);
}

async function login(email, password) {
    const res = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    });
    return parseResponse(res, 'Неверный email или пароль');
}

async function register(name, email, password) {
    const res = await fetch(`${API_URL}/users/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password })
    });
    return parseResponse(res, 'Ошибка регистрации');
}

async function createArticle(title, topic, content) {
    const res = await fetch(`${API_URL}/articles`, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify({ title, topic, content })
    });
    return parseResponse(res, 'Ошибка создания статьи');
}

async function getMyArticles() {
    const res = await fetch(`${API_URL}/articles/my`, {
        headers: authHeaders()
    });
    return parseResponse(res, 'Ошибка загрузки статей');
}

async function resubmitArticle(articleId, content) {
    const res = await fetch(`${API_URL}/articles/${articleId}/resubmit`, {
        method: 'PUT',
        headers: authHeaders(),
        body: JSON.stringify({ content })
    });
    return parseResponse(res, 'Ошибка отправки доработки');
}

async function getPendingArticles() {
    const res = await fetch(`${API_URL}/admin/articles/pending`, {
        headers: authHeaders()
    });
    return parseResponse(res, 'Ошибка загрузки');
}

async function getReviewers() {
    const res = await fetch(`${API_URL}/admin/reviewers`, {
        headers: authHeaders()
    });
    return parseResponse(res, 'Ошибка загрузки рецензентов');
}

async function assignReviewer(articleId, reviewerId) {
    const res = await fetch(`${API_URL}/admin/assign`, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify({ articleId, reviewerId })
    });
    return parseResponse(res, 'Ошибка назначения');
}

async function getArticlesForReviewer() {
    const res = await fetch(`${API_URL}/articles/reviewer/me`, {
        headers: authHeaders()
    });
    return parseResponse(res, 'Ошибка загрузки');
}

async function submitReview(articleId, verdict, comment) {
    const res = await fetch(`${API_URL}/articles/submit-review`, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify({ articleId, verdict, comment })
    });
    return parseResponse(res, 'Ошибка отправки рецензии');
}

async function getPublishedArticles() {
    const res = await fetch(`${API_URL}/articles/published`);
    return parseResponse(res, 'Ошибка загрузки статей');
}

async function getPublishedArticle(articleId) {
    const res = await fetch(`${API_URL}/articles/published/${articleId}`);
    return parseResponse(res, 'Ошибка загрузки статьи');
}
