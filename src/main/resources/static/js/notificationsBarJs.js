document.addEventListener('DOMContentLoaded', () => {
    const bell = document.querySelector('.notification-bell');
    const dropdown = document.querySelector('.notifications-dropdown');
    const notificationCount = document.querySelector('.notification-count');

    bell.addEventListener('click', () => {
        dropdown.classList.toggle('show');

        const notificationItems = document.querySelectorAll('.notification-item');
        const notificationIds = Array.from(notificationItems).map(item => parseInt(item.getAttribute('data-notification-id')));

        if (notificationIds.length > 0) {
            fetch('/student/read-notification', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(notificationIds),
            })
                .then(response => {
                    if (response.ok) {
                        notificationCount.textContent = '0';
                        notificationCount.style.display = 'none';
                        console.log('Notifications marked as read');
                    } else {
                        console.error('Failed to mark notifications as read');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        }
    });

    document.addEventListener('click', (event) => {
        if (!bell.contains(event.target) && !dropdown.contains(event.target)) {
            dropdown.classList.remove('show');
        }
    });
});