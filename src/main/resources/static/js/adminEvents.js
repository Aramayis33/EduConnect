const ITEMS_PER_PAGE = 10;

function filterTable() {
    const filter = document.getElementById('tableFilter').value;
    const studentTable = document.getElementById('studentNotificationsTable').parentElement;
    const teacherTable = document.getElementById('teacherNotificationsTable').parentElement;
    const studentHeader = studentTable.previousElementSibling;
    const teacherHeader = teacherTable.previousElementSibling;

    if (filter === 'student') {
        studentTable.classList.remove('hidden');
        studentHeader.classList.remove('hidden');
        teacherTable.classList.add('hidden');
        teacherHeader.classList.add('hidden');
        paginateTable('student');
    } else {
        teacherTable.classList.remove('hidden');
        teacherHeader.classList.remove('hidden');
        studentTable.classList.add('hidden');
        studentHeader.classList.add('hidden');
        paginateTable('teacher');
    }

    // Պահպանել ընտրված ֆիլտրը URL-ում
    window.history.replaceState(null, null, `/admin/events?filter=${filter}`);
}

function paginateTable(type) {
    const tableBody = document.getElementById(`${type}NotificationsBody`);
    const paginationContainer = document.getElementById(`${type}Pagination`);
    const rows = Array.from(tableBody.getElementsByTagName('tr'));
    const totalPages = Math.ceil(rows.length / ITEMS_PER_PAGE);
    let currentPage = 1;

    function displayPage(page) {
        currentPage = page;
        rows.forEach((row, index) => {
            row.style.display = (index >= (page - 1) * ITEMS_PER_PAGE && index < page * ITEMS_PER_PAGE) ? '' : 'none';
        });
        renderPagination();
    }

    function renderPagination() {
        paginationContainer.innerHTML = '';
        for (let i = 1; i <= totalPages; i++) {
            const button = document.createElement('button');
            button.textContent = i;
            button.className = i === currentPage ? 'active' : '';
            button.addEventListener('click', () => displayPage(i));
            paginationContainer.appendChild(button);
        }
    }

    if (rows.length > 0) {
        displayPage(1);
    } else {
        paginationContainer.innerHTML = '';
    }
}

function openModal(type) {
    document.getElementById(`${type}Modal`).style.display = 'flex';
}

function closeModal(type) {
    document.getElementById(`${type}Modal`).style.display = 'none';
}

function createStudentNotification(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    fetch('/admin/notify/student', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                window.location.reload();
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            alert('Error: ' + error.message);
        });
}

function createTeacherNotification(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    fetch('/admin/notify/teacher', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                window.location.reload();
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            alert('Error: ' + error.message);
        });
}

function createStudentGroupNotification(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    fetch('/admin/notify/student-group', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                window.location.reload();
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            alert('Error: ' + error.message);
        });
}

function createTeacherGroupNotification(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    fetch('/admin/notify/teacher-group', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                window.location.reload();
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            alert('Error: ' + error.message);
        });
}

document.addEventListener('DOMContentLoaded', () => {
    filterTable();
});