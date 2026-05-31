// ==========================================
// 獨立模組：Telegram Bot 雙向通訊 (純前端直連版)
// ==========================================

console.log("Telegram 獨立模組已載入");

let tgLastUpdateId = localStorage.getItem('nova_tg_last_update_id') || 0;

// ★ 安全取得 appState，避免 ReferenceError
function _tgState() {
    return (typeof appState !== 'undefined') ? appState : null;
}

window.pushToTelegram = async function(text) {
    const s = _tgState();
    if (!s || !s.tgToken || !s.tgChatId) return;
    const url = `https://api.telegram.org/bot${s.tgToken}/sendMessage`;
    try {
        await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ chat_id: s.tgChatId, text: text })
        });
    } catch(e) { console.warn('TG Push Error', e); }
};

window.pollTelegramLocal = async function() {
    const s = _tgState();
    if (!s || !s.tgToken || s.isThinking) return;

    const url = `https://api.telegram.org/bot${s.tgToken}/getUpdates?offset=${tgLastUpdateId}&timeout=5`;
    try {
        const res = await fetch(url);
        if (!res.ok) return;
        const data = await res.json();

        if (data.ok && data.result.length > 0) {
            for (const update of data.result) {
                tgLastUpdateId = update.update_id + 1;
                localStorage.setItem('nova_tg_last_update_id', tgLastUpdateId);

                if (update.message && update.message.text) {
                    if (s.tgChatId && String(update.message.chat.id) !== String(s.tgChatId)) continue;
                    const inputEl = document.getElementById('user-input');
                    if (inputEl) {
                        inputEl.value = update.message.text;
                        if (typeof handleSendMessage === 'function') handleSendMessage();
                    }
                }
            }
        }
    } catch(e) {}
};

setInterval(window.pollTelegramLocal, 5000);
