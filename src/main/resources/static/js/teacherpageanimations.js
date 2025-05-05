function openGradePopupFromData(button) {
    console.log('Opening popup from button:', button);

    const studentId = button.getAttribute("data-student-id");
    const studentName = button.getAttribute("data-student-name");
    const date = button.getAttribute("data-date");
    const subjectId = document.getElementById("subjectFilter").value;

    console.log('Student ID:', studentId, 'Date:', date, 'Subject ID:', subjectId);

    const ratings = Array.isArray(window.ratingsData) ? window.ratingsData : [];
    console.log('Available ratings:', ratings);

    const gradeCell = button.parentElement;
    const existingGrade = gradeCell.querySelector('.grade-value');

    let rating = '';
    let lessonTopic = '';
    let gradeType = '';
    let comment = '';

    if (existingGrade && existingGrade.textContent) {
        rating = existingGrade.textContent.trim();
        console.log('Existing grade found:', rating);

        const matchingRating = ratings.find(r => {
            if (!r || !r.student || !r.date) {
                console.warn('Invalid rating object:', r);
                return false;
            }
            const ratingDate = new Date(r.date).toISOString().split('T')[0];
            return r.student.id.toString() === studentId && ratingDate === date;
        });

        if (matchingRating) {
            console.log('Matching rating found:', matchingRating);
            lessonTopic = matchingRating.topic || '';
            gradeType = matchingRating.ratingType || 'current';
            comment = matchingRating.comment || '';
            console.log('Raw rating_type from DB:', matchingRating.ratingType);
        } else {
            console.log('No matching rating found for student and date');
        }
    } else {
        console.log('No existing grade, looking for same day rating');
        const sameDayRating = ratings.find(r => {
            if (!r || !r.date) return false;
            const ratingDate = new Date(r.date).toISOString().split('T')[0];
            return ratingDate === date && r.topic;
        });

        if (sameDayRating) {
            lessonTopic = sameDayRating.topic;
            console.log('Same day lesson topic:', lessonTopic);
        }
    }

    const validGradeTypes = ['current', 'written', 'intermediate'];
    gradeType = gradeType.toLowerCase().trim();
    console.log('Processed gradeType:', gradeType);
    console.log('Final values:', { rating, lessonTopic, gradeType, comment, subjectId });

    openGradePopup(studentId, studentName, date, rating, lessonTopic, gradeType, comment, subjectId);
}

function openGradePopup(studentId, studentName, date, rating, lessonTopic, gradeType, comment, subjectId) {
    const elements = {
        studentId: document.getElementById("studentId"),
        studentName: document.getElementById("studentName"),
        gradeDate: document.getElementById("gradeDate"),
        gradeScore: document.getElementById("gradeScore"),
        lessonTopic: document.getElementById("lessonTopic"),
        gradeType: document.getElementById("gradeType"),
        gradeComment: document.getElementById("gradeComment"),
        subjectId: document.getElementById("subjectId"),
        overlay: document.getElementById("overlay"),
        gradePopup: document.getElementById("gradePopup")
    };

    for (const [key, element] of Object.entries(elements)) {
        if (!element) {
            console.error(`Element not found: ${key}`);
            return;
        }
    }

    elements.studentId.value = studentId || '';
    elements.studentName.value = studentName || '';
    elements.gradeDate.value = date || '';
    elements.gradeScore.value = rating || '';
    elements.lessonTopic.value = lessonTopic || '';
    elements.gradeType.value = gradeType;
    elements.gradeComment.value = comment || '';
    elements.subjectId.value = subjectId || '';

    console.log('Setting subjectId to:', subjectId);

    elements.overlay.style.display = "block";
    elements.gradePopup.style.display = "block";
}

function closePopup() {
    const overlay = document.getElementById("overlay");
    const gradePopup = document.getElementById("gradePopup");
    if (overlay && gradePopup) {
        overlay.style.display = "none";
        gradePopup.style.display = "none";
    } else {
        console.error('Overlay or popup not found');
    }
}
document.addEventListener("DOMContentLoaded", () => {
    const table = document.getElementById("gradesTable");
    const tbody = document.getElementById("gradesBody");
    const headers = table.querySelectorAll(".table-header th");

    headers.forEach((header, index) => {
        const sortBtn = header.querySelector(".sort-btn");
        if (sortBtn) {
            sortBtn.addEventListener("click", () => sortTable(index));
        }
    });

    let sortDirection = {};

    function sortTable(columnIndex) {
        const rows = Array.from(tbody.querySelectorAll("tr"));
        const isStudentColumn = columnIndex === 0;
        const sortKey = headers[columnIndex].getAttribute("data-sort");

        sortDirection[sortKey] = sortDirection[sortKey] === "asc" ? "desc" : "asc";
        const direction = sortDirection[sortKey] === "asc" ? 1 : -1;

        headers.forEach(th => th.setAttribute("aria-sort", "none"));
        headers[columnIndex].setAttribute("aria-sort", sortDirection[sortKey]);

        const icon = headers[columnIndex].querySelector(".fas");
        icon.className = `fas fa-sort-${sortDirection[sortKey] === "asc" ? "up" : "down"}`;

        rows.sort((a, b) => {
            if (isStudentColumn) {
                const aValue = a.querySelector(".student-column").textContent.trim();
                const bValue = b.querySelector(".student-column").textContent.trim();
                return direction * aValue.localeCompare(bValue, "hy");
            } else {
                const aCell = a.cells[columnIndex];
                const bCell = b.cells[columnIndex];
                const aGrade = aCell.querySelector(".grade-value")?.textContent.trim() || "";
                const bGrade = bCell.querySelector(".grade-value")?.textContent.trim() || "";

                const aNum = aGrade ? parseInt(aGrade, 10) : 0;
                const bNum = bGrade ? parseInt(bGrade, 10) : 0;

                return direction * (aNum - bNum);
            }
        });

        rows.forEach(row => tbody.appendChild(row));
    }



});


//սա այն դեպքն է, երբ թույլ է տալիս բոլոր օրերին
// document.addEventListener('DOMContentLoaded', function() {
//     const monthFilter = document.getElementById('monthFilter');
//     const groupFilter = document.getElementById('groupFilter');
//
//     let selectedMonth = monthFilter.value;
//     let selectedGroup = groupFilter.value;
//
//     function attachGradeButtonListeners() {
//         const gradeButtons = document.querySelectorAll('.add-grade-btn');
//         gradeButtons.forEach(button => {
//             button.onclick = function() {
//                 openGradePopupFromData(this);
//             };
//         });
//     }
//
//     function handleFilterSubmit() {
//         selectedMonth = monthFilter.value;
//         selectedGroup = groupFilter.value;
//         console.log('Submitting: month=', selectedMonth, 'group=', selectedGroup);
//
//         fetch('/search-emis', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/x-www-form-urlencoded',
//             },
//             body: `month=${encodeURIComponent(selectedMonth)}&group=${encodeURIComponent(selectedGroup)}`
//         })
//             .then(response => {
//                 if (response.redirected) {
//                     return fetch('/teacher', {
//                         method: 'GET',
//                         headers: {
//                             'Accept': 'text/html'
//                         }
//                     });
//                 }
//                 throw new Error('No redirect');
//             })
//             .then(response => response.text())
//             .then(html => {
//                 const parser = new DOMParser();
//                 const doc = parser.parseFromString(html, 'text/html');
//
//                 const newTbody = doc.getElementById('gradesBody');
//                 if (newTbody) {
//                     document.getElementById('gradesBody').innerHTML = newTbody.innerHTML;
//                 }
//
//                 const newThead = doc.querySelector('#gradesTable thead');
//                 if (newThead) {
//                     document.querySelector('#gradesTable thead').innerHTML = newThead.innerHTML;
//                 }
//
//                 monthFilter.value = selectedMonth;
//                 groupFilter.value = selectedGroup;
//
//                 attachGradeButtonListeners();
//             })
//             .catch(error => {
//                 console.error('Error:', error);
//                 const form = document.createElement('form');
//                 form.method = 'POST';
//                 form.action = '/search-emis';
//                 form.innerHTML = `
//         <input type="hidden" name="month" value="${selectedMonth}">
//         <input type="hidden" name="group" value="${selectedGroup}">
//       `;
//                 document.body.appendChild(form);
//                 form.submit();
//             });
//     }
//
//     monthFilter.addEventListener('change', handleFilterSubmit);
//     groupFilter.addEventListener('change', handleFilterSubmit);
//
//     attachGradeButtonListeners();
// });

document.addEventListener('DOMContentLoaded', function() {
    const monthFilter = document.getElementById('monthFilter');
    const groupFilter = document.getElementById('groupFilter');
    const subjectFilter = document.getElementById('subjectFilter');

    let selectedMonth = monthFilter.value;
    let selectedGroup = groupFilter.value;
    let selectedSubject=subjectFilter.value;

    function attachGradeButtonListeners() {
        const gradeButtons = document.querySelectorAll('.add-grade-btn');
        gradeButtons.forEach(button => {
            const dateStr = button.dataset.date;
            const buttonDate = new Date(dateStr);
            const today = new Date();

            const buttonDateStr = buttonDate.toISOString().split('T')[0];
            const todayStr = today.toISOString().split('T')[0];
            const twoWeeksAgo = new Date(today);
            twoWeeksAgo.setDate(today.getDate() - 14);
            const twoWeeksAgoStr = twoWeeksAgo.toISOString().split('T')[0];

            if (buttonDateStr < twoWeeksAgoStr) {
                button.disabled = true;
                button.classList.add('disabled-past');
                button.title = 'Հնարավոր չէ գնահատել 2 շաբաթից հին ամսաթվի համար';
            } else if
                 (buttonDateStr > todayStr) {
                button.disabled = true;
                button.classList.add('disabled-future');
                button.title = 'Հնարավոր չէ գնահատել ապագայի ամսաթվի համար';
            } else {
                button.disabled = false;
                button.classList.remove('disabled-future', 'disabled-past');
                button.title = 'Ավելացնել կամ խմբագրել գնահատական';
                button.onclick = function() {
                    openGradePopupFromData(this);
                };
            }
        });
    }
    function updateGradesDisplay() {
        const gradeValues = document.querySelectorAll('.grade-value');
        gradeValues.forEach(grade => {
            const rating = grade.textContent.trim();
            if (rating === '0') {
                grade.textContent = 'Բ';
                grade.classList.add('absent');
            }
        });
    }

    function attachSortListeners() {
        const table = document.getElementById("gradesTable");
        const tbody = document.getElementById("gradesBody");
        const headers = table.querySelectorAll(".table-header th");
        let sortDirection = {};

        headers.forEach((header, index) => {
            const sortBtn = header.querySelector(".sort-btn");
            if (sortBtn) {
                sortBtn.removeEventListener("click", sortBtn._sortHandler);
                sortBtn._sortHandler = () => sortTable(index);
                sortBtn.addEventListener("click", sortBtn._sortHandler);
            }
        });

        function sortTable(columnIndex) {
            const rows = Array.from(tbody.querySelectorAll("tr"));
            const isStudentColumn = columnIndex === 0;
            const sortKey = headers[columnIndex].getAttribute("data-sort");

            sortDirection[sortKey] = sortDirection[sortKey] === "asc" ? "desc" : "asc";
            const direction = sortDirection[sortKey] === "asc" ? 1 : -1;

            headers.forEach(th => th.setAttribute("aria-sort", "none"));
            headers[columnIndex].setAttribute("aria-sort", sortDirection[sortKey]);

            const icon = headers[columnIndex].querySelector(".fas");
            icon.className = `fas fa-sort-${sortDirection[sortKey] === "asc" ? "up" : "down"}`;

            rows.sort((a, b) => {
                if (isStudentColumn) {
                    const aValue = a.querySelector(".student-column").textContent.trim();
                    const bValue = b.querySelector(".student-column").textContent.trim();
                    return direction * aValue.localeCompare(bValue, "hy");
                } else {
                    const aCell = a.cells[columnIndex];
                    const bCell = b.cells[columnIndex];
                    const aGrade = aCell.querySelector(".grade-value")?.textContent.trim() || "";
                    const bGrade = bCell.querySelector(".grade-value")?.textContent.trim() || "";

                    const aNum = aGrade === "Բ" ? -1 : (aGrade ? parseInt(aGrade, 10) : 0);
                    const bNum = bGrade === "Բ" ? -1 : (bGrade ? parseInt(bGrade, 10) : 0);

                    return direction * (aNum - bNum);
                }
            });

            rows.forEach(row => tbody.appendChild(row));
        }
    }

    gradeForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const formData = new FormData(gradeForm);

        console.log('FormData contents:');
        for (let [key, value] of formData.entries()) {
            console.log(`${key}: ${value}`);
        }

        fetch('/add-rating', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                // console.log('Response status:', response.status);
                // console.log('Response headers:', response.headers);
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(`Failed to add rating: ${response.status} - ${text}`);
                    });
                }
                return response.text();
            })
            .then(data => {
                // console.log('Success response:', data);
                closePopup();
                handleFilterSubmit();
            })
            .catch(error => {
                // console.error('Error adding rating:', error);
                alert('Գնահատականը չհաջողվեց ավելացնել: ' + error.message);
            });
    });
    function handleFilterSubmit() {
        selectedMonth = monthFilter.value;
        selectedGroup = groupFilter.value;
        selectedSubject=subjectFilter.value;
        console.log('Submitting: month=', selectedMonth, 'group=', selectedGroup);

        fetch('/search-emis', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `month=${encodeURIComponent(selectedMonth)}&group=${encodeURIComponent(selectedGroup)}&subject=${encodeURIComponent(selectedSubject)}`
        })
            .then(response => {
                if (response.redirected) {
                    return fetch('/teacher', {
                        method: 'GET',
                        headers: {
                            'Accept': 'text/html'
                        }
                    });
                }
                throw new Error('No redirect');
            })
            .then(response => response.text())
            .then(html => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, 'text/html');

                const newTbody = doc.getElementById('gradesBody');
                if (newTbody) {
                    document.getElementById('gradesBody').innerHTML = newTbody.innerHTML;
                }

                const newThead = doc.querySelector('#gradesTable thead');
                if (newThead) {
                    document.querySelector('#gradesTable thead').innerHTML = newThead.innerHTML;
                }

                monthFilter.value = selectedMonth;
                groupFilter.value = selectedGroup;
                subjectFilter.value=selectedSubject;

                attachGradeButtonListeners();
                updateGradesDisplay();
                attachSortListeners();
            })
            .catch(error => {
                console.error('Error:', error);
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '/search-emis';
                form.innerHTML = `
                <input type="hidden" name="month" value="${selectedMonth}">
                <input type="hidden" name="group" value="${selectedGroup}">
            `;
                document.body.appendChild(form);
                form.submit();
            });
    }

    monthFilter.addEventListener('change', handleFilterSubmit);
    groupFilter.addEventListener('change', handleFilterSubmit);
    subjectFilter.addEventListener('change',handleFilterSubmit);

    attachGradeButtonListeners();
    updateGradesDisplay();
    attachSortListeners();








});

function showList(type) {
    document.getElementById("current-list").style.display = "none";
    document.getElementById("expired-list").style.display = "none";
    document.getElementById("unchecked-list").style.display = "none";

    document.getElementById(type + "-list").style.display = "block";

    document.querySelectorAll(".assignment-btn").forEach(btn => btn.classList.remove("active"));
    document.querySelector(`button[onclick="showList('${type}')"]`).classList.add("active");
}

function showGrades() {
    document.getElementById("grades-section").style.display = "block";
    document.getElementById("assignments-section").style.display = "none";
    document.getElementById("summary-results-section").style.display = "none";

    document.querySelectorAll(".nav-btn").forEach(btn => {
        btn.classList.remove("active");
        btn.style.backgroundColor = "";
        btn.style.color = "";
    });
    const gradesBtn = document.querySelector("button[onclick='showGrades()']");
    gradesBtn.classList.add("active");
    gradesBtn.style.backgroundColor = "#007afd";
    gradesBtn.style.color = "white";
}

function showAssignments() {
    document.getElementById("grades-section").style.display = "none";
    document.getElementById("assignments-section").style.display = "block";
    document.getElementById("summary-results-section").style.display = "none";
    showList("current");

    document.querySelectorAll(".nav-btn").forEach(btn => {
        btn.classList.remove("active");
        btn.style.backgroundColor = "";
        btn.style.color = "";
    });
    const assignmentsBtn = document.querySelector("button[onclick='showAssignments()']");
    assignmentsBtn.classList.add("active");
    assignmentsBtn.style.backgroundColor = "#007afd";
    assignmentsBtn.style.color = "white";
}

function showSummaryResults() {
    document.getElementById("grades-section").style.display = "none";
    document.getElementById("assignments-section").style.display = "none";
    document.getElementById("summary-results-section").style.display = "block";

    document.querySelectorAll(".nav-btn").forEach(btn => {
        btn.classList.remove("active");
        btn.style.backgroundColor = "";
        btn.style.color = "";
    });
    const summaryBtn = document.querySelector("button[onclick='showSummaryResults()']");
    summaryBtn.classList.add("active");
    summaryBtn.style.backgroundColor = "#007afd";
    summaryBtn.style.color = "white";
}

window.onload = function() {
    showGrades();
};
function checkExercise(button) {
    const exerciseId = button.getAttribute('data-id');
    const card = button.closest('.assignment-card');
    button.disabled = true;

    console.log('Sending request for exercise ID:', exerciseId);

    fetch(`/exercise-done/check-click/${exerciseId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            console.log('Response status:', response.status);
            if (response.status < 200 || response.status >= 300) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Response data:', data);
            if (data.success === true) {
                card.remove();
                const uncheckedList = document.getElementById('unchecked-list');
                if (uncheckedList.querySelectorAll('.assignment-card').length === 0) {
                    uncheckedList.innerHTML = '<p class="no-items">Չստուգված վարժություններ չկան</p>';
                }
                showNotification(data.message || 'Առաջադրանքը ստուգված է');
            } else {
                showNotification(data.message || 'Առաջադրանքը չհաջողվեց ստուգել', true);
            }
        })
        .catch(error => {
            console.error('Fetch error:', error);
            showNotification('Ինչ-որ սխալ տեղի ունեցավ: ' + error.message, true);
        })
        .finally(() => {
            button.disabled = false;
        });
}

function showNotification(message, isError = false) {
    const notification = document.getElementById('notification');
    const messageSpan = document.getElementById('notification-message');

    messageSpan.textContent = message;
    notification.style.display = 'flex';
    notification.classList.remove('fade-out');
    notification.classList.toggle('error', isError);

    setTimeout(hideNotification, 3000);
}

function hideNotification() {
    const notification = document.getElementById('notification');
    notification.classList.add('fade-out');
    setTimeout(() => {
        notification.style.display = 'none';
        notification.classList.remove('fade-out');
    }, 300);
}


document.addEventListener('DOMContentLoaded', function() {

    const summaryGroupFilter = document.getElementById('summaryGroupFilter');
    const summarySubjectFilter = document.getElementById('summarySubjectFilter');

    let selectedSummaryGroup = summaryGroupFilter.value;
    let selectedSummarySubject = summarySubjectFilter.value;

    function handleSummaryFilterSubmit() {
        selectedSummaryGroup = summaryGroupFilter.value;
        selectedSummarySubject = summarySubjectFilter.value;
        console.log('Submitting summary filters: group=', selectedSummaryGroup, 'subject=', selectedSummarySubject);

        fetch('/group-results', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `group=${encodeURIComponent(selectedSummaryGroup)}&subject=${encodeURIComponent(selectedSummarySubject)}`
        })
            .then(response => {
                if (response.redirected) {
                    return fetch('/teacher', {
                        method: 'GET',
                        headers: {
                            'Accept': 'text/html'
                        }
                    });
                }
                throw new Error('No redirect');
            })
            .then(response => response.text())
            .then(html => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, 'text/html');

                const newSummaryBody = doc.getElementById('summaryResultsBody');
                const summaryResultsBody = document.getElementById('summaryResultsBody');
                if (newSummaryBody) {
                    summaryResultsBody.innerHTML = newSummaryBody.innerHTML;
                } else {
                    summaryResultsBody.innerHTML = '<tr><td colspan="4" class="no-items">Ամփոփիչ արդյունքներ դեռ չկան</td></tr>';
                }

                summaryGroupFilter.value = selectedSummaryGroup;
                summarySubjectFilter.value = selectedSummarySubject;

                attachSummarySortListeners();
            })
            .catch(error => {
                console.error('Error fetching summary results:', error);
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '/group-results';
                form.innerHTML = `
                <input type="hidden" name="group" value="${selectedSummaryGroup}">
                <input type="hidden" name="subject" value="${selectedSummarySubject}">
            `;
                document.body.appendChild(form);
                form.submit();
            });
    }

    function attachSummarySortListeners() {
        const table = document.getElementById('summaryResultsTable');
        const tbody = document.getElementById('summaryResultsBody');
        const headers = table.querySelectorAll('.table-header th');
        let sortDirection = {};

        headers.forEach((header, index) => {
            const sortBtn = header.querySelector('.sort-btn');
            if (sortBtn) {
                sortBtn.removeEventListener('click', sortBtn._sortHandler);
                sortBtn._sortHandler = () => sortSummaryTable(index);
                sortBtn.addEventListener('click', sortBtn._sortHandler);
            }
        });

        function sortSummaryTable(columnIndex) {
            const rows = Array.from(tbody.querySelectorAll('tr'));
            const isStudentColumn = columnIndex === 0;
            const sortKey = headers[columnIndex].getAttribute('data-sort');

            sortDirection[sortKey] = sortDirection[sortKey] === 'asc' ? 'desc' : 'asc';
            const direction = sortDirection[sortKey] === 'asc' ? 1 : -1;

            headers.forEach(th => th.setAttribute('aria-sort', 'none'));
            headers[columnIndex].setAttribute('aria-sort', sortDirection[sortKey]);

            const icon = headers[columnIndex].querySelector('.fas');
            icon.className = `fas fa-sort-${sortDirection[sortKey] === 'asc' ? 'up' : 'down'}`;

            rows.sort((a, b) => {
                if (isStudentColumn) {
                    const aValue = a.querySelector('.student-column').textContent.trim();
                    const bValue = b.querySelector('.student-column').textContent.trim();
                    return direction * aValue.localeCompare(bValue, 'hy');
                } else {
                    const aGrade = a.cells[columnIndex].textContent.trim() || '0';
                    const bGrade = b.cells[columnIndex].textContent.trim() || '0';
                    const aNum = parseFloat(aGrade) || 0;
                    const bNum = parseFloat(bGrade) || 0;
                    return direction * (aNum - bNum);
                }
            });

            rows.forEach(row => tbody.appendChild(row));
        }
    }

    summaryGroupFilter.addEventListener('change', handleSummaryFilterSubmit);
    summarySubjectFilter.addEventListener('change', handleSummaryFilterSubmit);

    attachSummarySortListeners();
});