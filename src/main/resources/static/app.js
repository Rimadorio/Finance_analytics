/* ============================================================
   Финансовая аналитика — фронтенд
   ============================================================ */

// ---------- Утилиты ----------

const fmt = (n) => {
    if (n === null || n === undefined || Number.isNaN(Number(n))) return '—';
    return new Intl.NumberFormat('ru-RU', {
        style: 'currency', currency: 'RUB', maximumFractionDigits: 2,
    }).format(Number(n));
};

const fmtNumber = (n) => {
    if (n === null || n === undefined || Number.isNaN(Number(n))) return '—';
    return new Intl.NumberFormat('ru-RU', { maximumFractionDigits: 2 }).format(Number(n));
};

const parseNum = (id, { integer = false, fallback = null } = {}) => {
    const el = document.getElementById(id);
    if (!el) return fallback;
    const raw = el.value.trim();
    if (raw === '') return fallback;
    const n = integer ? parseInt(raw, 10) : parseFloat(raw);
    return Number.isFinite(n) ? n : fallback;
};

const escapeHtml = (s) =>
    String(s)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');

const showError = (containerId, message, details) => {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.hidden = false;
    let html = `<div class="error-box"><strong>Ошибка:</strong> ${escapeHtml(message)}`;
    if (details) {
        html += '<ul>';
        for (const [field, msg] of Object.entries(details)) {
            html += `<li><b>${escapeHtml(field)}</b>: ${escapeHtml(String(msg))}</li>`;
        }
        html += '</ul>';
    }
    html += '</div>';
    el.innerHTML = html;
};

const clearResult = (containerId) => {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.innerHTML = '';
    el.hidden = true;
};

const setLoading = (button, isLoading, text = 'Расчёт...') => {
    if (!button) return;
    if (isLoading) {
        button.dataset.originalText = button.textContent;
        button.disabled = true;
        button.textContent = text;
        button.classList.add('loading');
    } else {
        button.disabled = false;
        button.textContent = button.dataset.originalText || 'Рассчитать';
        button.classList.remove('loading');
    }
};

const postJson = async (url, body) => {
    const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
    });
    let data = null;
    try { data = await res.json(); } catch (_) { /* не JSON */ }
    if (!res.ok) {
        const err = new Error(`HTTP ${res.status}`);
        err.status = res.status;
        err.payload = data;
        throw err;
    }
    return data;
};

// ---------- Работа с денежными полями ----------

// Форматирует строку с цифрами в красивый вид с пробелами
const formatMoneyString = (val) => {
    let clean = val.replace(/[^\d.,]/g, '').replace(',', '.');
    if (!clean) return '';

    let parts = clean.split('.');
    let integerPart = parts[0];
    let decimalPart = parts.length > 1 ? '.' + parts[1].slice(0, 2) : ''; // максимум 2 знака после точки

    // Добавляем пробелы в целую часть (каждые 3 цифры)
    integerPart = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, ' ');

    return integerPart + decimalPart;
};

// Парсит отформатированную строку обратно в число
const parseMoney = (str) => {
    if (!str) return null;
    const clean = str.replace(/\s/g, '').replace(',', '.');
    const num = parseFloat(clean);
    return Number.isFinite(num) ? num : null;
};

// Инициализация полей с классом .money-input
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.money-input').forEach(input => {
        // Форматируем при вводе (с сохранением позиции курсора)
        input.addEventListener('input', (e) => {
            const cursorPos = e.target.selectionStart;
            const oldLength = e.target.value.length;

            e.target.value = formatMoneyString(e.target.value);

            // Корректировка курсора, чтобы он не прыгал в конец при вводе
            const newLength = e.target.value.length;
            e.target.setSelectionRange(cursorPos + (newLength - oldLength), cursorPos + (newLength - oldLength));
        });

        // При потере фокуса принудительно форматируем (на случай, если вставили текст из буфера)
        input.addEventListener('blur', (e) => {
            e.target.value = formatMoneyString(e.target.value);
        });
    });
});

// ---------- Сайдбар: клик по «Вклад» / «Ипотека» ----------

document.querySelectorAll('.nav-btn').forEach((btn) => {
    btn.addEventListener('click', () => {
        const target = document.getElementById(btn.dataset.section);
        if (!target) return;

        const wasActive = btn.classList.contains('active');

        // Снять active со всех кнопок и секций
        document.querySelectorAll('.nav-btn').forEach((b) => b.classList.remove('active'));
        document.querySelectorAll('.section').forEach((s) => s.classList.remove('active'));

        // Если кнопка не была активна — открыть секцию
        if (!wasActive) {
            btn.classList.add('active');
            target.classList.add('active');
        }
    });
});

// ---------- ВКЛАД ----------

const DEPOSIT_DEFAULTS = {
    'dep-amount': '100 000',
    'dep-term': '12',
    'dep-rate': '12',
};

const depositForm = document.getElementById('deposit-form');

if (depositForm) {
    depositForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = depositForm.querySelector('.btn-primary');
        clearResult('deposit-result');

        const amount = parseMoney(document.getElementById('dep-amount').value);
        const termMonths = parseNum('dep-term', { integer: true });
        const annualRatePercent = parseNum('dep-rate');

        if (amount === null || amount <= 0)
            return showError('deposit-result', 'Сумма вклада должна быть больше 0');
        if (termMonths === null || termMonths < 1)
            return showError('deposit-result', 'Срок должен быть не меньше 1 месяца');
        if (annualRatePercent === null || annualRatePercent <= 0)
            return showError('deposit-result', 'Ставка должна быть больше 0');

        const body = {
            amount, termMonths, annualRatePercent,
            capitalization: document.getElementById('dep-cap').checked,
        };

        setLoading(submitBtn, true);
        try {
            const data = await postJson('/api/deposit/calculate', body);
            const resultEl = document.getElementById('deposit-result');
            resultEl.hidden = false;
            resultEl.innerHTML = `
                <h3>Результат</h3>
                <div class="metric"><span>Начальная сумма</span><span>${fmt(data.initialAmount)}</span></div>
                <div class="metric"><span>Итоговая сумма</span><span>${fmt(data.finalAmount)}</span></div>
                <div class="metric"><span>Доход</span><span class="saved">${fmt(data.income)}</span></div>
                <div class="metric"><span>Эффективная ставка, %</span><span>${fmtNumber(data.effectiveRatePercent)}%</span></div>
            `;
        } catch (err) {
            if (err.payload && typeof err.payload === 'object' && !err.payload.error) {
                showError('deposit-result', 'Проверьте введённые данные', err.payload);
            } else {
                showError('deposit-result', (err.payload && err.payload.error) || err.message);
            }
        } finally {
            setLoading(submitBtn, false);
        }
    });

    document.getElementById('dep-reset').addEventListener('click', () => {
        for (const [id, val] of Object.entries(DEPOSIT_DEFAULTS)) {
            const el = document.getElementById(id);
            if (el) el.value = val;
        }
        document.getElementById('dep-cap').checked = true;
        clearResult('deposit-result');
    });
}

// ---------- ИПОТЕКА ----------

const MORTGAGE_DEFAULTS = {
    'm-debt': '2 000 000',
    'm-term': '120',
    'm-rate': '12',
    'm-extra': '10 000',
    'm-freq': '1',
    'm-period': '0',
    'm-strategy': 'term',
};

const renderSchedule = (rows, visibleCount = 24, tableId = 'schedule') => {
    if (!Array.isArray(rows) || rows.length === 0) {
        return '<p class="muted">График пуст</p>';
    }
    const bodyRows = rows.map((r) => `
        <tr>
            <td>${r.month}</td>
            <td>${fmt(r.payment)}</td>
            <td>${fmt(r.interestPart)}</td>
            <td>${fmt(r.principalPart)}</td>
            <td>${fmt(r.extraPayment)}</td>
            <td>${fmt(r.remainingDebt)}</td>
        </tr>`).join('');

    const hiddenCount = Math.max(0, rows.length - visibleCount);
    const hiddenNote = hiddenCount > 0
        ? `<p class="muted">
               Показаны первые ${visibleCount} из ${rows.length} месяцев.
               <button type="button" class="link-btn" data-expand-table="${tableId}">
                   Показать все ${rows.length}
               </button>
           </p>`
        : '';

    return `
        <div class="table-wrap" id="${tableId}-wrap">
            <table class="schedule-table">
                <thead><tr>
                    <th>Мес.</th><th>Платёж</th><th>Проценты</th>
                    <th>Основной долг</th><th>Досрочное погашение</th><th>Остаток</th>
                </tr></thead>
                <tbody>${bodyRows}</tbody>
            </table>
            ${hiddenNote}
        </div>`;
};

document.addEventListener('click', (e) => {
    const btn = e.target.closest('[data-expand-table]');
    if (!btn) return;
    const wrap = document.getElementById(`${btn.dataset.expandTable}-wrap`);
    if (!wrap) return;
    wrap.querySelectorAll('tbody tr').forEach((tr) => (tr.style.display = ''));
    const note = wrap.querySelector('p.muted');
    if (note) note.remove();
});

const mortgageForm = document.getElementById('mortgage-form');

if (mortgageForm) {
    mortgageForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = mortgageForm.querySelector('.btn-primary');
        clearResult('mortgage-result');

        const remainingPrincipal = parseMoney(document.getElementById('m-debt').value);
        const remainingTermMonths = parseNum('m-term', { integer: true });
        const annualRatePercent = parseNum('m-rate');
        const extraMonthlyPayment = parseMoney(document.getElementById('m-extra').value) || 0;
        const extraFrequencyMonths = parseNum('m-freq', { integer: true, fallback: 1 });
        const repaymentPeriodMonths = parseNum('m-period', { integer: true, fallback: 0 });
        const strategy = document.getElementById('m-strategy').value;

        const errors = [];
        if (remainingPrincipal === null || remainingPrincipal <= 0)
            errors.push('Остаток долга должен быть больше 0');
        if (remainingTermMonths === null || remainingTermMonths < 1)
            errors.push('Оставшийся срок должен быть не меньше 1 месяца');
        if (annualRatePercent === null || annualRatePercent <= 0)
            errors.push('Ставка должна быть больше 0');
        if (extraMonthlyPayment === null || extraMonthlyPayment < 0)
            errors.push('Сумма досрочного погашения не может быть отрицательной');
        if (extraFrequencyMonths === null || extraFrequencyMonths < 1)
            errors.push('Периодичность должна быть не меньше 1 месяца');
        if (repaymentPeriodMonths === null || repaymentPeriodMonths < 0)
            errors.push('Период досрочного погашения не может быть отрицательным');

        if (errors.length) return showError('mortgage-result', errors.join('; '));

        const body = {
            remainingPrincipal, remainingTermMonths, annualRatePercent,
            extraMonthlyPayment, extraFrequencyMonths, repaymentPeriodMonths,
            reduceTerm: strategy === 'term',
        };

        setLoading(submitBtn, true, 'Считаем график...');
        try {
            const data = await postJson('/api/mortgage/calculate', body);
            const resultEl = document.getElementById('mortgage-result');
            resultEl.hidden = false;

            const savedClass = Number(data.totalSaved) > 0 ? 'saved' : 'muted';
            resultEl.innerHTML = `
                <h3>Экономия: <span class="${savedClass}">${fmt(data.totalSaved)}</span></h3>

                <div class="metric"><span>Ежемесячный платёж (без учета досрочного погашения)</span>
                    <span>${fmt(data.monthlyPaymentWithoutExtra)}</span></div>
                <div class="metric"><span>Ежемесячный платёж (1-й месяц с учетом досрочного погашения)</span>
                    <span>${fmt(data.monthlyPaymentWithExtra)}</span></div>
                <div class="metric"><span>Срок без учета досрочного погашения</span>
                    <span>${data.termMonthsWithoutExtra} мес.</span></div>
                <div class="metric"><span>Срок с учетом досрочного погашения</span>
                    <span>${data.termMonthsWithExtra} мес.</span></div>
                <div class="metric"><span>Проценты без учета досрочного погашения</span>
                    <span>${fmt(data.totalInterestWithoutExtra)}</span></div>
                <div class="metric"><span>Проценты с учетом досрочного погашения</span>
                    <span>${fmt(data.totalInterestWithExtra)}</span></div>
                <div class="metric"><span>Сумма досрочных платежей</span>
                    <span>${fmt(data.totalExtraPaid)}</span></div>

                <h4>График без учета досрочного погашения</h4>
                ${renderSchedule(data.scheduleWithoutExtra, 24, 'schedule-base')}
                <h4>График с учетом досрочного погашения</h4>
                ${renderSchedule(data.scheduleWithExtra, 24, 'schedule-extra')}
            `;
        } catch (err) {
            if (err.payload && typeof err.payload === 'object' && !err.payload.error) {
                showError('mortgage-result', 'Проверьте введённые данные', err.payload);
            } else {
                showError('mortgage-result', (err.payload && err.payload.error) || err.message);
            }
        } finally {
            setLoading(submitBtn, false);
        }
    });

    document.getElementById('mort-reset').addEventListener('click', () => {
        for (const [id, val] of Object.entries(MORTGAGE_DEFAULTS)) {
            const el = document.getElementById(id);
            if (el) el.value = val;
        }
        clearResult('mortgage-result');
    });
}