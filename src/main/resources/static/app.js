const state = {
    page: 0,
    size: 10,
    sort: 'expenseDate,desc'
};

const expenseForm = document.getElementById('expenseForm');
const filterForm = document.getElementById('filterForm');
const tableBody = document.getElementById('expenseTableBody');
const pagination = document.getElementById('pagination');
const totalSpendingEl = document.getElementById('totalSpending');
const expenseCountEl = document.getElementById('expenseCount');
const mostExpensiveEl = document.getElementById('mostExpensive');
const monthlySpendingEl = document.getElementById('monthlySpending');
const categoryBreakdownEl = document.getElementById('categoryBreakdown');
const expenseIdInput = document.getElementById('expenseId');
const submitExpenseBtn = document.getElementById('submitExpense');
const cancelEditBtn = document.getElementById('cancelEdit');
const clearFiltersBtn = document.getElementById('clearFilters');

const moneyFormatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'EGP',
    minimumFractionDigits: 2
});

function formatCurrency(value) {
    const amount = Number(value ?? 0);
    return moneyFormatter.format(amount);
}

function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString + 'T00:00:00');
    return new Intl.DateTimeFormat('en-GB', { dateStyle: 'medium' }).format(date);
}

function buildQueryString(params) {
    const searchParams = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            searchParams.append(key, value);
        }
    });

    return searchParams.toString();
}

function resetExpenseForm() {
    expenseIdInput.value = '';
    expenseForm.reset();
    document.getElementById('category').value = 'FOOD';
    submitExpenseBtn.textContent = 'Add Expense';
    cancelEditBtn.classList.add('hidden');
}

async function fetchJson(url, options = {}) {
    const response = await fetch(url, options);

    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get('content-type') || '';
    const json = contentType.includes('application/json') ? await response.json() : null;

    if (!response.ok) {
        throw new Error(json?.message || 'Request failed');
    }

    return json;
}

async function loadDashboard() {
    try {
        const total = await fetchJson('/api/expenses/statistics/total');
        const breakdown = await fetchJson('/api/expenses/statistics/by-category');
        const mostExpensive = await fetchJson('/api/expenses/statistics/most-expensive');

        totalSpendingEl.textContent = formatCurrency(total || 0);
        mostExpensiveEl.textContent = mostExpensive ? formatCurrency(mostExpensive.amount) : 'EGP 0.00';

        const currentDate = new Date();
        const monthlyFrom = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1).toISOString().slice(0, 10);
        const monthlyTo = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0).toISOString().slice(0, 10);
        const monthlyTotal = await fetchJson(`/api/expenses/statistics/total?from=${monthlyFrom}&to=${monthlyTo}`);
        monthlySpendingEl.textContent = formatCurrency(monthlyTotal || 0);

        renderCategoryBreakdown(breakdown || {});
    } catch (error) {
        console.error(error);
    }
}

function renderCategoryBreakdown(breakdown) {
    const entries = Object.entries(breakdown || {});
    const totals = entries.map(([, value]) => Number(value || 0));
    const maxValue = Math.max(...totals, 1);

    categoryBreakdownEl.innerHTML = entries.length === 0
        ? '<div class="empty-state">No spending data available.</div>'
        : entries.map(([key, value]) => {
            const width = (Number(value || 0) / maxValue) * 100;
            return `
                <div class="category-row">
                    <span class="category-label">${key}</span>
                    <div class="bar-track"><div class="bar-fill" style="width:${width}%"></div></div>
                    <strong>${formatCurrency(value || 0)}</strong>
                </div>
            `;
        }).join('');
}

async function loadExpenses() {
    const searchValue = document.getElementById('searchInput').value.trim();
    const categoryValue = document.getElementById('categoryFilter').value;
    const fromValue = document.getElementById('fromDate').value;
    const toValue = document.getElementById('toDate').value;
    const sortValue = document.getElementById('sortSelect').value || state.sort;

    state.page = Math.max(0, state.page);
    state.sort = sortValue;

    const params = {
        page: state.page,
        size: state.size,
        search: searchValue,
        category: categoryValue,
        from: fromValue,
        to: toValue,
        sort: sortValue
    };

    try {
        const result = await fetchJson(`/api/expenses?${buildQueryString(params)}`);
        renderExpenses(result.content || []);
        renderPagination(result.totalPages || 0, result.number || 0);
        expenseCountEl.textContent = `${result.totalElements || 0} expense${(result.totalElements || 0) === 1 ? '' : 's'}`;
        await loadDashboard();
    } catch (error) {
        tableBody.innerHTML = `<tr><td colspan="5" class="empty-state">${error.message}</td></tr>`;
        console.error(error);
    }
}

function renderExpenses(expenses) {
    if (!expenses.length) {
        tableBody.innerHTML = '<tr><td colspan="5" class="empty-state">No expenses available.</td></tr>';
        return;
    }

    tableBody.innerHTML = expenses.map((expense) => `
        <tr>
            <td>${formatDate(expense.expenseDate)}</td>
            <td>${expense.description}</td>
            <td>${expense.category}</td>
            <td class="amount-cell">${formatCurrency(expense.amount)}</td>
            <td>
                <div class="action-buttons">
                    <button class="edit-btn" data-id="${expense.id}" type="button">Edit</button>
                    <button class="delete-btn" data-id="${expense.id}" type="button">Delete</button>
                </div>
            </td>
        </tr>
    `).join('');

    document.querySelectorAll('.edit-btn').forEach((button) => {
        button.addEventListener('click', async () => {
            const id = button.dataset.id;
            const expense = await fetchJson(`/api/expenses/${id}`);
            fillExpenseForm(expense);
        });
    });

    document.querySelectorAll('.delete-btn').forEach((button) => {
        button.addEventListener('click', async () => {
            const id = button.dataset.id;
            const confirmed = window.confirm('Are you sure you want to delete this expense?');
            if (!confirmed) return;

            try {
                await fetchJson(`/api/expenses/${id}`, { method: 'DELETE' });
                state.page = 0;
                await loadExpenses();
                resetExpenseForm();
            } catch (error) {
                alert(error.message);
            }
        });
    });
}

function fillExpenseForm(expense) {
    expenseIdInput.value = expense.id;
    document.getElementById('amount').value = Number(expense.amount).toFixed(2);
    document.getElementById('category').value = expense.category;
    document.getElementById('description').value = expense.description;
    document.getElementById('expenseDate').value = expense.expenseDate;
    submitExpenseBtn.textContent = 'Update Expense';
    cancelEditBtn.classList.remove('hidden');
    document.getElementById('description').focus();
}

function renderPagination(totalPages, currentPage) {
    pagination.innerHTML = '';

    const prevBtn = document.createElement('button');
    prevBtn.type = 'button';
    prevBtn.textContent = 'Previous';
    prevBtn.disabled = currentPage <= 0;
    prevBtn.addEventListener('click', () => {
        if (state.page > 0) {
            state.page -= 1;
            loadExpenses();
        }
    });
    pagination.appendChild(prevBtn);

    const pageInfo = document.createElement('span');
    pageInfo.textContent = `Page ${currentPage + 1} of ${Math.max(totalPages, 1)}`;
    pagination.appendChild(pageInfo);

    const nextBtn = document.createElement('button');
    nextBtn.type = 'button';
    nextBtn.textContent = 'Next';
    nextBtn.disabled = currentPage >= totalPages - 1 || totalPages === 0;
    nextBtn.addEventListener('click', () => {
        if (state.page < totalPages - 1) {
            state.page += 1;
            loadExpenses();
        }
    });
    pagination.appendChild(nextBtn);
}

expenseForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const formData = new FormData(expenseForm);
    const payload = {
        amount: Number(formData.get('amount')),
        category: formData.get('category'),
        description: formData.get('description'),
        expenseDate: formData.get('expenseDate')
    };

    const id = expenseIdInput.value;
    const url = id ? `/api/expenses/${id}` : '/api/expenses';
    const method = id ? 'PUT' : 'POST';

    try {
        await fetchJson(url, {
            method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });
        resetExpenseForm();
        state.page = 0;
        await loadExpenses();
    } catch (error) {
        alert(error.message);
    }
});

filterForm.addEventListener('submit', (event) => {
    event.preventDefault();
    state.page = 0;
    loadExpenses();
});

clearFiltersBtn.addEventListener('click', () => {
    filterForm.reset();
    state.page = 0;
    loadExpenses();
});

cancelEditBtn.addEventListener('click', () => {
    resetExpenseForm();
});

document.getElementById('sortSelect').addEventListener('change', () => {
    state.page = 0;
    loadExpenses();
});

resetExpenseForm();
loadExpenses();
