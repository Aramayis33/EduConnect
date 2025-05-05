function showTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.getElementById(tabId).classList.add('active');
    document.querySelector(`[onclick="showTab('${tabId}')"]`).classList.add('active');
}

function openEditModal() {
    document.getElementById('editModal').style.display = 'flex';
    document.body.style.overflow = 'hidden';
}

function closeEditModal() {
    document.getElementById('editModal').style.display = 'none';
    document.body.style.overflow = 'auto';
}

function updateStudent(event) {
    event.preventDefault();
    const formData = new FormData(document.getElementById('editForm'));

    fetch('/admin/students/update', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) throw new Error('Հարցումը ձախողվեց');
            return response.json();
        })
        .then(data => {
            if (data.success) {
                alert(data.message || 'Տվյալները հաջողությամբ թարմացվել են');
                window.location.reload();
            } else {
                alert(data.message || 'Ինչ-որ խնդիր կա');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Խնդիր՝ ' + error.message);
        });
}

window.onclick = function(event) {
    const modal = document.getElementById('editModal');
    if (event.target === modal) {
        closeEditModal();
    }
};

document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape') {
        closeEditModal();
    }
});
// function confirmDelete() {
//     return confirm('Հաստատո՞ւմ եք, որ ցանկանում եք ջնջել այս ուսանողին:');
// }

function openDeleteModal() {
    document.getElementById('deleteModal').style.display = 'flex';
    // document.body.style.overflow = 'hidden';
}

function closeDeleteModal() {
    document.getElementById('deleteModal').style.display = 'none';
    document.body.style.overflow = 'auto';
}

window.onclick = function(event) {
    const editModal = document.getElementById('editModal');
    const deleteModal = document.getElementById('deleteModal');
    if (event.target === editModal) {
        closeEditModal();
    }
    if (event.target === deleteModal) {
        closeDeleteModal();
    }
};

document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape') {
        closeEditModal();
        closeDeleteModal();
    }
});