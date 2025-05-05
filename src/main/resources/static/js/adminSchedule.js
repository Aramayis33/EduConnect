
function updateFilters() {
    const semester = document.getElementById('semester').value;
    const semesterPart = document.getElementById('semesterPart').value;
    const groupNumber = document.getElementById('groupNumber').value;
    window.location.href = `/admin/schedule?semester=${semester}&semesterPart=${semesterPart}&groupNumber=${groupNumber}`;
}

function openAddScheduleModal(weekday) {
    const modal = document.getElementById('addScheduleModal');
    modal.style.display = 'block';
    const weekdaySelect = document.getElementById('addWeekday');
    weekdaySelect.value = weekday || '';
    document.getElementById('addClassHour').value = '';
    document.getElementById('addSubjectId').value = '';
    document.getElementById('addTeacherId').value = '';
    document.getElementById('addClassroom').value = '';
    const checkbox = document.getElementById('allSemesterCheckbox');
    checkbox.checked = false;
    const semesterPartSelect = document.getElementById('addSemesterPart');
    semesterPartSelect.value = semesterPartSelect.getAttribute('data-default') || 'first';
}

function closeAddScheduleModal() {
    document.getElementById('addScheduleModal').style.display = 'none';
}

function toggleSemesterPart() {
    const checkbox = document.getElementById('allSemesterCheckbox');
    const semesterPartSelect = document.getElementById('addSemesterPart');
    if (checkbox.checked) {
        semesterPartSelect.innerHTML = '<option value="all" selected>Ամբողջը</option>';
        semesterPartSelect.value = 'all';
    } else {
        const defaultValue = semesterPartSelect.getAttribute('data-default') || 'first';
        const defaultText = defaultValue === 'first' ? 'Առաջին' : 'Երկրորդ';
        semesterPartSelect.innerHTML = `<option value="${defaultValue}" selected>${defaultText}</option>`;
        semesterPartSelect.value = defaultValue;
    }
}

function addSchedule(event) {
    event.preventDefault();
    const form = document.getElementById('addScheduleForm');
    const formData = new FormData(form);

    fetch('/admin/schedule/new', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message || (data.success ? 'Գործողությունն ավարտված է' : 'Անհայտ սխալ'));

            if (data.success) {
                closeAddScheduleModal();
                window.location.reload();
            }
        })
        .catch(error => {
            alert('Ցանցային սխալ: ' + error.message);
        });
}

function deleteSchedule(scheduleId) {
    if (!confirm('Հաստատե՞լ դասաժամի ջնջումը։')) {
        return;
    }

    fetch(`/admin/schedule/delete/${scheduleId}`, {
        method: 'POST'
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message || (data.success ? 'Դասաժամը ջնջված է' : 'Սխալ ջնջման ժամանակ'));
            if (data.success) {
                window.location.reload();
            }
        })
        .catch(error => {
            alert('Ցանցային սխալ: ' + error.message);
        });
}


function openEditScheduleModalFromButton(button) {
    const id = button.getAttribute('data-id') || null;
    const day = button.getAttribute('data-day') || '';
    const hour = button.getAttribute('data-hour') || '';
    const subjectId = button.getAttribute('data-subject-id') || null;
    const teacherId = button.getAttribute('data-teacher-id') || null;
    const classroom = button.getAttribute('data-classroom') || '';

    openEditScheduleModal(id, day, hour, subjectId, teacherId, classroom);
}

function openEditScheduleModal(id, day, hour, subjectId, teacherId, classroom) {
    const modal = document.getElementById('editScheduleModal');
    modal.style.display = 'block';

    document.getElementById('editScheduleId').value = id || '';
    document.getElementById('weekday').value = day || '';
    document.getElementById('classHour').value = hour || '';
    document.getElementById('subjectId').value = subjectId || '';
    document.getElementById('teacherId').value = teacherId || '';
    document.getElementById('classroom').value = classroom || '';
}
function closeEditScheduleModal() {
    document.getElementById('editScheduleModal').style.display = 'none';
}

function updateSchedule(event) {
    event.preventDefault();
    const form = document.getElementById('editScheduleForm');
    const formData = new FormData(form);

    fetch('/admin/schedule/update', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message || (data.success ? 'Գործողությունն ավարտված է' : 'Անհայտ սխալ'));

            if (data.success) {
                closeAddScheduleModal();
                window.location.reload();
            }
        })
        .catch(error => {
            alert('Ցանցային սխալ: ' + error.message);
        });
}

window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        closeAddScheduleModal();
        closeEditScheduleModal();
    }
};

document.addEventListener('DOMContentLoaded', function() {
    const semesterPartSelect = document.getElementById('addSemesterPart');
    if (semesterPartSelect) {
        semesterPartSelect.setAttribute('data-default', semesterPartSelect.value);
    }
});