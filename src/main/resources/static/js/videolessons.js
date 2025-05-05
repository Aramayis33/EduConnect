document.addEventListener('DOMContentLoaded', () => {
    const subjectFilter = document.getElementById('subject-filter');
    const gradeFilter = document.getElementById('grade-filter');
    const videoLessonsList = document.getElementById('video-lessons-list');
    const noResults = document.getElementById('no-results');
    const noResultsText = document.getElementById('no-results-text');
    const searchIcon = document.getElementById('search-icon');
    const lessons = document.querySelectorAll('.video-lesson-card');
    const pagination = document.getElementById('pagination');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageNumbersContainer = document.getElementById('page-numbers');

    const itemsPerPage = 3;
    let currentPage = 1;
    let filteredLessons = [];

    videoLessonsList.style.display = 'none';
    noResults.style.display = 'flex';
    searchIcon.style.display = 'block';
    pagination.style.display = 'none';

    function updatePagination() {
        const totalPages = Math.ceil(filteredLessons.length / itemsPerPage);

        lessons.forEach(lesson => lesson.style.display = 'none');

        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = Math.min(startIndex + itemsPerPage, filteredLessons.length);

        for (let i = startIndex; i < endIndex; i++) {
            filteredLessons[i].style.display = 'flex';
        }

        pageNumbersContainer.innerHTML = '';
        for (let i = 1; i <= totalPages; i++) {
            const pageNum = document.createElement('div');
            pageNum.classList.add('page-number');
            pageNum.textContent = i;
            if (i === currentPage) pageNum.classList.add('active');
            pageNum.addEventListener('click', () => {
                currentPage = i;
                updatePagination();
            });
            pageNumbersContainer.appendChild(pageNum);
        }

        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;
    }

    function updateFilter() {
        const selectedSubject = subjectFilter.value;
        const selectedGrade = gradeFilter.value;

        filteredLessons = Array.from(lessons).filter(lesson => {
            const lessonSubject = lesson.getAttribute('data-subject');
            const lessonGrade = lesson.getAttribute('data-grade').toString();

            const subjectMatch = !selectedSubject || lessonSubject === selectedSubject;
            const gradeMatch = !selectedGrade || lessonGrade === selectedGrade;

            return subjectMatch && gradeMatch;
        });

        currentPage = 1;

        if (selectedSubject && selectedGrade) {
            if (filteredLessons.length > 0) {
                videoLessonsList.style.display = 'block';
                noResults.style.display = 'none';
                pagination.style.display = 'flex';
                updatePagination();
            } else {
                videoLessonsList.style.display = 'none';
                noResults.style.display = 'flex';
                pagination.style.display = 'none';
                noResultsText.textContent = 'Համապատասխան վիդեոդասեր չեն գտնվել';
                searchIcon.style.display = 'none';
            }
        } else {
            videoLessonsList.style.display = 'none';
            noResults.style.display = 'flex';
            pagination.style.display = 'none';
            noResultsText.textContent = 'Ընտրեք առարկան և կուրսը ցանկալի արդյունքը տեսնելու համար';
            searchIcon.style.display = 'block';
        }
    }

    subjectFilter.addEventListener('change', updateFilter);
    gradeFilter.addEventListener('change', updateFilter);
    prevPageBtn.addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            updatePagination();
        }
    });
    nextPageBtn.addEventListener('click', () => {
        const totalPages = Math.ceil(filteredLessons.length / itemsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            updatePagination();
        }
    });

    const thumbnails = document.querySelectorAll('.video-thumbnail');
    thumbnails.forEach(thumbnail => {
        thumbnail.addEventListener('click', () => {
            const videoUrl = thumbnail.getAttribute('data-video-url');
            const video = document.createElement('video');
            video.src = videoUrl;
            video.controls = true;
            video.controlsList = 'nodownload';
            video.oncontextmenu = () => false;
            video.classList.add('fullscreen-video');

            document.body.appendChild(video);
            video.requestFullscreen().then(() => video.play());

            video.addEventListener('fullscreenchange', () => {
                if (!document.fullscreenElement) {
                    video.remove();
                }
            });
        });
    });
});