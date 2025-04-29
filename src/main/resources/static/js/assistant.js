function sendMessage() {
    const userInput = document.getElementById('userInput');
    const chatArea = document.getElementById('chatArea');
    const inputValue = userInput.value.trim();

    if (inputValue === '') return;

    const userMessage = document.createElement('div');
    userMessage.className = 'message user';
    userMessage.textContent = inputValue;
    chatArea.appendChild(userMessage);

    fetch('assistant/ask', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ question: inputValue })
    })
        .then(response => response.text())
        .then(data => {
            const assistantMessage = document.createElement('div');
            assistantMessage.className = 'message assistant';
            assistantMessage.textContent = data;
            chatArea.appendChild(assistantMessage);
            chatArea.scrollTop = chatArea.scrollHeight;
        })
        .catch(error => {
            console.error('Error:', error);
            const assistantMessage = document.createElement('div');
            assistantMessage.className = 'message assistant';
            assistantMessage.textContent = 'Կապի խնդիր։';
            chatArea.appendChild(assistantMessage);
        });

    userInput.value = '';
}

document.getElementById('userInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        sendMessage();
    }
});
