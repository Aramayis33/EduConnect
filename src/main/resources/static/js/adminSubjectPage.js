
function openAddSubjectModal() {
    const modal = document.getElementById('addSubjectModal');
    modal.style.display = 'flex';
    setTimeout(() => modal.classList.add('show'), 10);
}

function closeAddSubjectModal() {
    const modal = document.getElementById('addSubjectModal');
    modal.classList.remove('show');
    setTimeout(() => {
        modal.style.display = 'none';
        document.getElementById('addSubjectForm').reset();
    }, 300);
}

function openEditSubjectModal(button) {
    const subjectId = button.getAttribute('data-subject-id');
    const subjectName = button.closest('.subject-card').getAttribute('data-subject-name');
    document.getElementById('editSubjectId').value = subjectId;
    document.getElementById('editSubjectName').value = subjectName;
    const modal = document.getElementById('editSubjectModal');
    modal.style.display = 'flex';
    setTimeout(() => modal.classList.add('show'), 10);
}

function closeEditSubjectModal() {
    const modal = document.getElementById('editSubjectModal');
    modal.classList.remove('show');
    setTimeout(() => {
        modal.style.display = 'none';
        document.getElementById('editSubjectForm').reset();
    }, 300);
}

function filterSubjects() {
    const searchInput = document.getElementById('searchInput').value.toLowerCase();
    const subjectCards = document.querySelectorAll('.subject-card');

    subjectCards.forEach(card => {
        const subjectName = card.getAttribute('data-subject-name').toLowerCase();
        card.style.display = subjectName.includes(searchInput) ? '' : 'none';
    });
}

function createSubject(event) {
    event.preventDefault();
    const form = document.getElementById('addSubjectForm');
    const formData = new FormData(form);
    const submitBtn = form.querySelector('.submit-btn');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Հաստատվում է...';

    fetch('/admin/subjects/new', {
        method: 'POST',
        body: formData,
        headers: {
        }
    })
        .then(response => response.json())
        .then(data => {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Հաստատել';
            if (data.success) {
                alert('Առարկան հաջողությամբ ավելացվեց');
                window.location.reload();
            } else {
                alert('Սխալ: ' + data.message);
            }
        })
        .catch(error => {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Հաստատել';
            alert('Սխալ: ' + error.message);
        });
}

function updateSubject(event) {
    event.preventDefault();
    const form = document.getElementById('editSubjectForm');
    const formData = new FormData(form);
    const submitBtn = form.querySelector('.submit-btn');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Հաստատվում է...';

    fetch('/admin/subjects/update', {
        method: 'POST',
        body: formData,
        headers: {
        }
    })
        .then(response => response.json())
        .then(data => {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Հաստատել';
            if (data.success) {
                alert('Առարկան հաջողությամբ թարմացվեց');
                window.location.reload();
            } else {
                alert('Սխալ: ' + data.message);
            }
        })
        .catch(error => {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Հաստատել';
            alert('Սխալ: ' + error.message);
        });
}