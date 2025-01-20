function updateCharCount() {
    const textarea = document.querySelector('.feedback-textarea');
    const charCount = document.getElementById('charCount');
    const currentLength = textarea.value.length;
    charCount.textContent = currentLength;
}
function addEmoji(emoji) {
    const textarea = document.querySelector('.feedback-textarea');
    textarea.value += emoji;
    updateCharCount();
}

function updateCharCount() {
    const textarea = document.querySelector('.feedback-textarea');
    const charCount = textarea.value.length;
    document.getElementById('charCount').textContent = charCount;
}