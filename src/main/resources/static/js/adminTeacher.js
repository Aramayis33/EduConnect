function filterTeachers() {
    const searchInput = document.getElementById('searchInput').value.toLowerCase();
    const subjectFilter = document.getElementById('subjectFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    const cards = document.querySelectorAll('.teacher-card');

    cards.forEach(card => {
        const name = card.querySelector('h3').textContent.toLowerCase();
        const email = card.querySelector('p:nth-child(2) span').textContent.toLowerCase();
        const subjects = card.querySelector('p:nth-child(3)').textContent.toLowerCase();
        const isActive = card.querySelector('input[type="checkbox"]').checked ? '0' : '1';

        const matchesSearch = name.includes(searchInput) || email.includes(searchInput);
        const matchesSubject = !subjectFilter || subjects.includes(document.querySelector(`#subjectFilter option[value="${subjectFilter}"]`)?.textContent.toLowerCase());
        const matchesStatus = !statusFilter || isActive === statusFilter;

        card.style.display = (matchesSearch && matchesSubject && matchesStatus) ? '' : 'none';
    });
}

function toggleActivation(checkbox) {
    const teacherId = checkbox.getAttribute('data-id');
    const isBlocked = checkbox.checked ? 0 : 1;

    fetch('/toggle-teacher-activation', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id: teacherId, isBlocked: isBlocked })
    })
        .then(response => response.json())
        .then(data => {
            if (!data.success) {
                checkbox.checked = !checkbox.checked;
                alert('Կարգավիճակը փոխելու սխալ: ' + data.message);
            } else {
            }
        })
        .catch(error => {
            checkbox.checked = !checkbox.checked;
            alert('Խնդիր՝ ' + error.message);
        });
}

function openAddTeacherModal() {
    document.getElementById('addTeacherModal').style.display = 'flex';
}

function closeAddTeacherModal() {
    document.getElementById('addTeacherModal').style.display = 'none';
}


function addTag(select) {
    const value = select.value;
    const text = select.options[select.selectedIndex].text;
    if (value && !document.querySelector(`.tag[data-value="${value}"]`)) {
        const tagsDiv = document.querySelector('.tags');
        const tag = document.createElement('div');
        tag.className = 'tag';
        tag.dataset.value = value;
        tag.innerHTML = `${text} <span class="remove-tag" onclick="removeTag(this)">×</span>`;
        tagsDiv.appendChild(tag);

        updateSubjectIds();
    }
    select.value = '';
}

function removeTag(element) {
    element.parentElement.remove();
    updateSubjectIds();
}

function updateSubjectIds() {
    const tags = document.querySelectorAll('.tag');
    const subjectIds = Array.from(tags).map(tag => tag.dataset.value).join(',');
    document.getElementById('subjectIds').value = subjectIds;
}

function createTeacher(event) {
    event.preventDefault();
    const formData = new FormData(document.getElementById('addTeacherForm'));

    fetch('/admin/teachers/new', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Դասախոսը հաջողությամբ ավելացվել է');
                window.location.reload();
            } else {
                alert('Խնդիր՝ ' + data.message);
            }
        })
        .catch(error => alert('Խնդիր՝ ' + error.message));
}