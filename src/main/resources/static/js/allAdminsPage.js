function filterAdmins() {
    const searchInput = document.getElementById('searchInput').value.toLowerCase();
    const statusFilter = document.getElementById('statusFilter').value;
    const rows = document.querySelectorAll('#adminsTable tbody tr');

    rows.forEach(row => {
        const name = row.cells[0].textContent.toLowerCase();
        const surname = row.cells[1].textContent.toLowerCase();
        const username = row.cells[2].textContent.toLowerCase();
        const isActive = row.querySelector('input[type="checkbox"]').checked ? '1' : '0';

        const matchesSearch = name.includes(searchInput) || surname.includes(searchInput) || username.includes(searchInput);
        const matchesStatus = statusFilter === '' || isActive === statusFilter;

        row.style.display = matchesSearch && matchesStatus ? '' : 'none';
    });
}

function toggleActivation(checkbox) {
    const adminId = checkbox.dataset.id;
    const isActive = checkbox.checked ? 1 : 0;

    fetch(`/admin/admins/toggle/${adminId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ isActive })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to toggle admin status');
            }
            console.log(`Admin ${adminId} status updated to ${isActive}`);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Հնարավոր չէ փոխել կարգավիճակը: Խնդրում ենք փորձել կրկին:');
            checkbox.checked = !checkbox.checked;
        });
}

function openAddAdminModal() {
    const modal = document.getElementById('addAdminModal');
    modal.classList.add('active');
    document.getElementById('addAdminForm').reset();
}

function closeAddAdminModal() {
    const modal = document.getElementById('addAdminModal');
    modal.classList.remove('active');
}

function createAdmin(event) {
    event.preventDefault();

    const submitButton = document.querySelector('#addAdminForm button[type="submit"]');
    const originalButtonText = submitButton.textContent;
    submitButton.textContent = 'Բեռնվում է...';
    submitButton.disabled = true;

    const form = document.getElementById('addAdminForm');
    const formData = new FormData(form);

    const adminData = {};
    formData.forEach((value, key) => {
        adminData[key] = value;
    });

    fetch('/admin/admins/create', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            submitButton.textContent = originalButtonText;
            submitButton.disabled = false;

            alert(data.message || (data.success ? 'Գործողությունն ավարտված է' : 'Անհայտ սխալ'));

            if (data.success) {
                window.location.reload();
            }
        })
        .catch(error => {
            submitButton.textContent = originalButtonText;
            submitButton.disabled = false;

            alert('Ցանցային սխալ: ' + error.message);
        });
}

function openEditAdminModal(button) {
    const adminId = button.dataset.id;
    const row = button.closest('tr');
    const name = row.cells[0].textContent;
    const surname = row.cells[1].textContent;
    const username = row.cells[2].textContent;

    const modal = document.getElementById('editAdminModal');
    modal.classList.add('active');

    document.getElementById('editAdminId').value = adminId;
    document.getElementById('editName').value = name;
    document.getElementById('editSurname').value = surname;
    document.getElementById('editUsername').value = username;
    document.getElementById('editPassword').value = '';
}

function closeEditAdminModal() {
    const modal = document.getElementById('editAdminModal');
    modal.classList.remove('active');
}

function updateAdmin(event) {
    event.preventDefault();
    const form = document.getElementById('editAdminForm');
    const formData = new FormData(form);

    const adminData = {};
    formData.forEach((value, key) => {
        if (key === 'password' && value === '') return;
        adminData[key] = value;
    });

    fetch('/admin/admins/update', {
        method: 'POST',
        body:formData
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message || (data.success ? 'Գործողությունն ավարտված է' : 'Անհայտ սխալ'));

            if (data.success) {
                window.location.reload();
            }
        })
        .catch(error => {
            alert('Ցանցային սխալ: ' + error.message);
        });
}

document.addEventListener('DOMContentLoaded', () => {
    filterAdmins();

    window.onclick = function(event) {
        if (event.target.classList.contains('modal')) {
            closeAddAdminModal();
            closeEditAdminModal();
        }
    };
});