function filterStudents() {
    const searchQuery = document.getElementById('searchInput').value.toLowerCase();
    const groupFilter = document.getElementById('groupFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    const rows = document.querySelectorAll('#studentsTable tbody tr');

    rows.forEach(row => {
        const name = row.cells[0].textContent.toLowerCase();
        const surname = row.cells[1].textContent.toLowerCase();
        const email = row.cells[3].textContent.toLowerCase();
        const groupId = row.getAttribute('data-group-id') || '';
        const status = row.cells[4].querySelector('input').checked ? '0' : '1';

        const searchMatch = !searchQuery ||
            name.includes(searchQuery) ||
            surname.includes(searchQuery) ||
            email.includes(searchQuery);
        const groupMatch = !groupFilter || groupId === groupFilter;
        const statusMatch = !statusFilter || status === statusFilter;

        row.style.display = (searchMatch && groupMatch && statusMatch) ? '' : 'none';
    });
}

function toggleActivation(checkbox) {
    const studentId = checkbox.getAttribute('data-id');
    const isActivated = checkbox.checked ? 0 : 1;

    fetch('/toggle-student-activation', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ id: studentId, isActivated: isActivated })
    })
        .then(response => response.json())
        .then(data => {
            if (!data.success) {
                checkbox.checked = !checkbox.checked;
                alert(data.message || 'Կարգավիճակը փոխելու սխալ');
            }
        })
        .catch(error => {
            checkbox.checked = !checkbox.checked;
            console.error('Error:', error);
            alert('Կարգավիճակը փոխելու սխալ');
        });
}


function createStudent(event) {
    event.preventDefault();

    let name = document.getElementById('name').value;
    let surname = document.getElementById('surname').value;
    let email = document.getElementById('email').value;
    let birthDay = document.getElementById('birthDay').value;
    let groupId = document.getElementById('groupId').value;
    let fee = document.getElementById('fee').value;

    let formData = new FormData();
    formData.append('name', name);
    formData.append('surname', surname);
    formData.append('email', email);
    formData.append('birthDay', birthDay);
    formData.append('groupId', groupId);
    formData.append('fee', fee);

    fetch('/admin/students/new', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Հարցումը չհաջողվեց');
            }
            closeAddStudentModal();
            return response.json();

        })
        .then(data => {
            alert(data.message);
            location.reload();
        })
        .catch(error => {
            alert("There is a problem: " + error.message);
        });
}


function openAddStudentModal() {
    document.getElementById('addStudentModal').style.display = 'block';
}

function closeAddStudentModal() {
    document.getElementById('addStudentModal').style.display = 'none';
    document.getElementById('addStudentForm').reset();
}