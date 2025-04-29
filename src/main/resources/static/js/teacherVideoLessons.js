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
    const addVideoBtn = document.getElementById('add-video-btn');
    const modal = document.getElementById('add-video-modal');
    const form = modal.querySelector('form');
    const videoFileInfo = document.getElementById('video-file-info');

    const itemsPerPage = 2;
    let currentPage = 1;
    let filteredLessons = [];

    videoLessonsList.style.display = 'none';
    noResults.style.display = 'flex';
    searchIcon.style.display = 'block';
    pagination.style.display = 'none';

    const deleteButtons = document.querySelectorAll('.delete-btn');
    const deleteModal = document.getElementById('delete-confirm-modal');
    const confirmDelete = document.getElementById('confirm-delete');
    const cancelDelete = document.getElementById('cancel-delete');
    let currentDeleteForm;

    deleteButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            e.preventDefault();
            currentDeleteForm = button.closest('form');
            deleteModal.style.display = 'flex';
        });
    });

    confirmDelete.addEventListener('click', () => {
        if (currentDeleteForm) {
            currentDeleteForm.submit();
        }
        deleteModal.style.display = 'none';
    });

    cancelDelete.addEventListener('click', () => {
        deleteModal.style.display = 'none';
    });

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
                videoLessonsList.style.display = 'flex';
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

    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();

            const lessonCard = this.closest('.video-lesson-card');
            const lessonTitle = lessonCard.querySelector('.lesson-title').textContent;
            const lessonSubjectName = lessonCard.getAttribute('data-subject');
            const lessonGrade = lessonCard.getAttribute('data-grade');
            const lessonDescription = lessonCard.querySelector('.lesson-description').textContent;
            const videoFile = lessonCard.querySelector('.video-thumbnail').getAttribute('data-video-url').split('/').pop();
            const lessonId = this.closest('form').action.split('/').pop();

            form.action = '/addVideo';
            form.querySelector('#video-id').value = lessonId;
            form.querySelector('#video-title').value = lessonTitle;
            form.querySelector('#video-grade').value = lessonGrade;
            form.querySelector('#video-description').value = lessonDescription;

            const subjectSelect = form.querySelector('#video-subject');
            Array.from(subjectSelect.options).forEach(option => {
                if (option.text === lessonSubjectName) {
                    subjectSelect.value = option.value;
                }
            });

            videoFileInfo.textContent = `Նոր վիդեո վերբեռնելու դեպքում հինը ավտոմատ կերպով կջնջվի`;
            videoFileInfo.classList.add('warning');
            form.querySelector('#video-file').removeAttribute('required');

            modal.style.display = 'flex';
            modal.querySelector('h3').textContent = 'Խմբագրել Վիդեոդաս';
        });
    });

    addVideoBtn.addEventListener('click', () => {
        form.action = '/addVideo';
        form.reset();
        form.querySelector('#video-id').value = '';
        videoFileInfo.textContent = '';
        form.querySelector('#video-file').setAttribute('required', '');
        modal.style.display = 'flex';
        modal.querySelector('h3').textContent = 'Ավելացնել Վիդեոդաս';
    });

    document.getElementById('cancel-add-video').addEventListener('click', () => {
        modal.style.display = 'none';
    });
});