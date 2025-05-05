// adminGroup.js

function openAddGroupModal() {
    const modal = document.getElementById('addGroupModal');
    modal.style.display = 'flex';
    setTimeout(() => modal.classList.add('show'), 10);
}

function closeAddGroupModal() {
    const modal = document.getElementById('addGroupModal');
    modal.classList.remove('show');
    setTimeout(() => {
        modal.style.display = 'none';
        document.getElementById('addGroupForm').reset();
    }, 300);
}

function filterGroups() {
    const searchInput = document.getElementById('searchInput').value.toLowerCase();
    const groupCards = document.querySelectorAll('.group-card');

    groupCards.forEach(card => {
        const groupNumber = card.getAttribute('data-group-number').toLowerCase();
        card.style.display = groupNumber.includes(searchInput) ? '' : 'none';
    });
}

function createGroup(event) {
    event.preventDefault();
    const form = document.getElementById('addGroupForm');
    const formData = new FormData(form);
    const submitBtn = form.querySelector('.submit-btn');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Հաստատվում է...';

    fetch('/admin/groups/new', {
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
                alert('Խումբը հաջողությամբ ավելացվեց');
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

function deleteGroup(button) {
    const groupNumber = button.getAttribute('data-group-number');
    if (!confirm(`Հաստատե՞լ Խումբ ${groupNumber}-ի ջնջումը:`)) {
        return;
    }

    button.disabled = true;
    button.textContent = 'Ջնջվում է...';

    fetch(`/admin/groups/${groupNumber}/delete`, {
        method: 'POST',
        headers: {
        }
    })
        .then(response => response.json())
        .then(data => {
            button.disabled = false;
            button.textContent = 'Ջնջել';
            if (data.success) {
                alert('Խումբը հաջողությամբ ջնջվեց');
                window.location.reload();
            } else {
                alert('Սխալ: ' + data.message);
            }
        })
        .catch(error => {
            button.disabled = false;
            button.textContent = 'Ջնջել';
            alert('Սխալ: ' + error.message);
        });
}