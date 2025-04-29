let subjectChart;
let groupHomeworkChart;
let teacherChart;

function createSubjectChart() {
    const subjectOccupationMap = window.subjectOccupationMap || {};
    const subjectLabels = Object.keys(subjectOccupationMap);
    const subjectData = Object.values(subjectOccupationMap);

    const ctxSubject = document.getElementById('subjectChart').getContext('2d');
    if (subjectChart) subjectChart.destroy();
    subjectChart = new Chart(ctxSubject, {
        type: 'pie',
        data: {
            labels: subjectLabels.length ? subjectLabels : ['Տվյալներ չկան'],
            datasets: [{
                data: subjectData.length ? subjectData : [1],
                backgroundColor: subjectData.length ?
                    ['#4CAF50', '#FF9800', '#2196F3', '#F44336', '#9C27B0', '#FFEB3B', '#00BCD4', '#E91E63', '#3F51B5', '#8BC34A'] :
                    ['#d3d3d3'],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            animation: { duration: 1000, easing: 'easeOutQuart' },
            plugins: {
                legend: { position: 'top', labels: { font: { size: 14 } } },
                tooltip: {
                    callbacks: {
                        label: context => subjectData.length ? `${context.label}: ${context.raw} ժամ` : 'Տվյալներ չկան'
                    }
                }
            }
        }
    });
}

function createHomeworkChart() {
    const filter = document.getElementById('homeworkFilter').value || 'current';
    const groupHomeworkMap = (filter === 'current') ? (window.currentHomeWorkCount || {}) : (window.allTimeHomeWorkCount || {});
    const homeworkLabels = Object.keys(groupHomeworkMap).map(num => `Խումբ ${num}`);
    const homeworkData = Object.values(groupHomeworkMap);

    const ctxHomework = document.getElementById('groupHomeworkChart').getContext('2d');
    if (groupHomeworkChart) groupHomeworkChart.destroy();
    groupHomeworkChart = new Chart(ctxHomework, {
        type: 'pie',
        data: {
            labels: homeworkLabels.length ? homeworkLabels : ['Տվյալներ չկան'],
            datasets: [{
                data: homeworkData.length ? homeworkData : [1],
                backgroundColor: homeworkData.length ?
                    ['#4CAF50', '#FF9800', '#2196F3', '#F44336', '#9C27B0', '#FFEB3B', '#00BCD4', '#E91E63', '#3F51B5', '#8BC34A'] :
                    ['#d3d3d3'],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            animation: { duration: 1000, easing: 'easeOutQuart' },
            plugins: {
                legend: { position: 'top', labels: { font: { size: 14 } } },
                tooltip: {
                    callbacks: {
                        label: context => homeworkData.length ? `${context.label}: ${context.raw} տնային` : 'Տվյալներ չկան'
                    }
                }
            }
        }
    });
}

function createTeacherChart() {
    const teacherClassHours = window.teacherClassHours || {};
    const teacherLabels = Object.keys(teacherClassHours);
    const teacherData = Object.values(teacherClassHours);

    const ctxTeacher = document.getElementById('teacherChart').getContext('2d');
    if (teacherChart) teacherChart.destroy();
    teacherChart = new Chart(ctxTeacher, {
        type: 'bar',
        data: {
            labels: teacherLabels.length ? teacherLabels : ['Տվյալներ չկան'],
            datasets: [{
                label: 'Դասաժամեր',
                data: teacherData.length ? teacherData : [0],
                backgroundColor: '#4a90e2',
                borderColor: '#357abd',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            animation: { duration: 1000, easing: 'easeOutQuart' },
            scales: {
                y: { beginAtZero: true, title: { display: true, text: 'Դասաժամերի քանակ', font: { size: 14 } } },
                x: { title: { display: true, text: 'Ուսուցիչներ', font: { size: 14 } } }
            },
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: context => teacherData.length ? `${context.raw} դասաժամ` : 'Տվյալներ չկան'
                    }
                }
            }
        }
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const observerOptions = {
        root: null, // Viewport-ը
        rootMargin: '0px',
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const chartId = entry.target.id;
                if (chartId === 'subjectChart' && !subjectChart) {
                    createSubjectChart();
                } else if (chartId === 'groupHomeworkChart' && !groupHomeworkChart) {
                    createHomeworkChart();
                } else if (chartId === 'teacherChart' && !teacherChart) {
                    createTeacherChart();
                }
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    document.querySelectorAll('#subjectChart, #groupHomeworkChart, #teacherChart').forEach(chart => {
        observer.observe(chart);
    });

    document.getElementById('homeworkFilter').addEventListener('change', () => {
        if (groupHomeworkChart) groupHomeworkChart.destroy();
        createHomeworkChart();
    });
});