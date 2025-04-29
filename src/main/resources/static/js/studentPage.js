let currentPage = 1;
let rowsPerPage = 2;
function setActiveButton(activeButtonId) {
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    const activeButton = document.querySelector(`[onclick="${activeButtonId}"]`);
    if (activeButton) {
        activeButton.classList.add('active');
    } else {
        console.error(`Կոճակը չի գտնվել: onclick="${activeButtonId}"`);
    }
}

function showGrades() {
    console.log("Գնահատականներ բաժինը ցուցադրվում է");
    document.getElementById("gradesContainer").style.display = 'block';
    document.getElementById("assignmentsContainer").style.display = 'none';
    document.getElementById("summaryContainer").style.display = 'none';
    document.getElementById("groupMatesContainer").style.display = 'none';
    setActiveButton('showGrades()');
    filterGrades();
}

function showAssignments() {
    console.log("Առաջադրանքներ բաժինը ցուցադրվում է");
    document.getElementById("assignmentsContainer").style.display = 'block';
    document.getElementById("gradesContainer").style.display = 'none';
    document.getElementById("summaryContainer").style.display = 'none';
    document.getElementById("groupMatesContainer").style.display = 'none';
    setActiveButton('showAssignments()');
}

function showSummaryResults() {
    console.log("Ամփոփիչ արդյունք բաժինը ցուցադրվում է");
    document.getElementById("summaryContainer").style.display = 'block';
    document.getElementById("gradesContainer").style.display = 'none';
    document.getElementById("assignmentsContainer").style.display = 'none';
    document.getElementById("groupMatesContainer").style.display = 'none';
    setActiveButton('showSummaryResults()');
}

function showMyGroup() {
    console.log("Համակուրսեցիներ բաժինը ցուցադրվում է");
    document.getElementById("summaryContainer").style.display = 'none';
    document.getElementById("gradesContainer").style.display = 'none';
    document.getElementById("assignmentsContainer").style.display = 'none';
    document.getElementById("groupMatesContainer").style.display = 'block';
    setActiveButton('showMyGroup()');
}

function applyGradeColors() {
    const rows = document.querySelectorAll("#gradesBody tr");
    console.log("Գույներ կիրառվում են, տողերի քանակ:", rows.length);
    rows.forEach(row => {
        const ratingType = row.getAttribute("data-rating-type")?.toLowerCase();
        if (ratingType === "current") {
            row.style.backgroundColor = "#d4edda";
        } else if (ratingType === "written") {
            row.style.backgroundColor = "#fff3cd";
        } else if (ratingType === "intermediate") {
            row.style.backgroundColor = "#af7777";
        } else {
            row.style.backgroundColor = "";
        }
    });
}

function filterGrades() {
    const subject = document.getElementById("subjectSelect").value;
    const startDate = document.getElementById("startDate").value ? new Date(document.getElementById("startDate").value) : null;
    const endDate = document.getElementById("endDate").value ? new Date(document.getElementById("endDate").value) : null;
    const rows = document.querySelectorAll("#gradesBody tr");

    console.log("Ֆիլտրում - առարկա:", subject, "սկիզբ:", startDate, "վերջ:", endDate);

    let visibleRowsCount = 0;
    rows.forEach(row => {
        const subjectCell = row.cells[0].textContent.toLowerCase();
        const dateCellText = row.cells[4].textContent;
        const dateCell = new Date(dateCellText);

        const matchesSubject = subject === "all" || subjectCell === subject;
        const matchesDate = (startDate === null || dateCell >= startDate) &&
            (endDate === null || dateCell <= endDate);

        if (matchesSubject && matchesDate) {
            row.dataset.visible = "true";
            visibleRowsCount++;
        } else {
            row.dataset.visible = "false";
            row.style.display = "none";
        }
    });

    console.log("Տեսանելի տողերի քանակ:", visibleRowsCount);
    currentPage = 1;
    paginateGrades();
}

function paginateGrades() {
    const rows = Array.from(document.querySelectorAll("#gradesBody tr"));
    rowsPerPage = parseInt(document.getElementById("rowsPerPage").value);
    const visibleRows = rows.filter(row => row.dataset.visible === "true");
    const totalPages = Math.ceil(visibleRows.length / rowsPerPage) || 1;

    console.log("Էջավորում - ընդհանուր տողեր:", rows.length, "տեսանելի:", visibleRows.length, "էջեր:", totalPages, "ընթացիկ:", currentPage, "տողեր/էջ:", rowsPerPage);

    if (currentPage < 1) currentPage = 1;
    if (currentPage > totalPages) currentPage = totalPages;

    const start = (currentPage - 1) * rowsPerPage;
    const end = start + rowsPerPage;

    rows.forEach(row => {
        if (row.dataset.visible === "true") {
            const index = visibleRows.indexOf(row);
            row.style.display = (index >= start && index < end) ? "" : "none";
        } else {
            row.style.display = "none";
        }
    });

    document.getElementById("pageInfo").textContent = `Էջ ${currentPage} / ${totalPages}`;
    document.getElementById("prevPage").disabled = currentPage === 1;
    document.getElementById("nextPage").disabled = currentPage === totalPages;

    applyGradeColors();
}

function changePage(direction) {
    console.log("Էջի փոփոխություն - նախքան:", currentPage);
    currentPage += direction;
    console.log("Էջի փոփոխություն - հետո:", currentPage);
    paginateGrades();
}

function filterActiveAssignments() {
    const startDate = document.getElementById("startDateActive").value ? new Date(document.getElementById("startDateActive").value) : null;
    const endDate = document.getElementById("endDateActive").value ? new Date(document.getElementById("endDateActive").value) : null;
    const cards = document.querySelectorAll("#activeTab .assignments-list .card");

    cards.forEach(card => {
        const deadlineText = card.querySelector(".card-body p:nth-child(3) span").textContent;
        const [day, month, year] = deadlineText.split('.');
        const deadlineDate = new Date(`${year}-${month}-${day}`);

        const matchesDate = (startDate === null || deadlineDate >= startDate) &&
            (endDate === null || deadlineDate <= endDate);
        card.style.display = matchesDate ? "" : "none";
    });
}

function filterHistoryAssignments() {
    const startDate = document.getElementById("startDateHistory").value ? new Date(document.getElementById("startDateHistory").value) : null;
    const endDate = document.getElementById("endDateHistory").value ? new Date(document.getElementById("endDateHistory").value) : null;
    const cards = document.querySelectorAll("#historyTab .assignments-list .card");

    cards.forEach(card => {
        const deadlineText = card.querySelector(".card-body p:nth-child(2) span").textContent;
        const [day, month, year] = deadlineText.split('.');
        const deadlineDate = new Date(`${year}-${month}-${day}`);

        const matchesDate = (startDate === null || deadlineDate >= startDate) &&
            (endDate === null || deadlineDate <= endDate);
        card.style.display = matchesDate ? "" : "none";
    });
}

function showTab(tabId) {
    console.log("Բաժինը փոխվում է:", tabId);

    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));

    document.querySelectorAll('.tab-content').forEach(content => content.style.display = 'none');

    document.querySelector(`button[onclick="showTab('${tabId}')"]`).classList.add('active');
    document.getElementById(tabId).style.display = 'block';

    if (tabId === 'activeTab') {
        filterActiveAssignments();
    } else if (tabId === 'historyTab') {
        filterHistoryAssignments();
    }
}

window.onload = function() {
    console.log("Էջը բեռնվեց");
    showGrades();
};

