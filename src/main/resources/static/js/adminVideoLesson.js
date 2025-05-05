document.addEventListener('DOMContentLoaded', () => {
    const elements = {
        subjectFilter: document.getElementById('subject-filter'),
        gradeFilter: document.getElementById('grade-filter'),
        videoList: document.getElementById('video-list'),
        noResults: document.getElementById('no-results'),
        noResultsText: document.getElementById('no-results-text'),
        searchIcon: document.getElementById('search-icon'),
        videos: document.querySelectorAll('.video-card'),
        pagination: document.getElementById('pagination'),
        prevPageBtn: document.getElementById('prev-page'),
        nextPageBtn: document.getElementById('next-page'),
        pageNumbersContainer: document.getElementById('page-numbers'),
        addVideoBtn: document.getElementById('add-video-btn'),
        addModal: document.getElementById('add-video-modal'),
        videoForm: document.getElementById('video-form'),
        fileInfo: document.getElementById('video-file-info'),
        deleteModal: document.getElementById('delete-confirm-modal'),
        confirmDelete: document.getElementById('confirm-delete'),
        cancelDelete: document.getElementById('cancel-delete')
    };

    const config = {
        itemsPerPage: 2,
        currentPage: 1,
        filteredVideos: []
    };

    const modalManager = {
        openAddModal(isEdit = false, videoData = {}) {
            console.log('Opening modal with isEdit:', isEdit, 'and videoData:', videoData);
            elements.addModal.style.display = 'flex';
            elements.videoForm.reset();
            elements.fileInfo.textContent = '';
            elements.fileInfo.classList.remove('warning');

            const modalTitle = elements.addModal.querySelector('#modal-title');
            if (!modalTitle) {
                console.error('Modal title element not found');
                return;
            }
            modalTitle.textContent = isEdit ? 'Խմբագրել Վիդեոդաս' : 'Ավելացնել Վիդեոդաս';
            console.log('Modal title set to:', modalTitle.textContent);

            if (isEdit) {
                const videoIdInput = elements.videoForm.querySelector('#video-id');
                const videoTitleInput = elements.videoForm.querySelector('#video-title');
                const videoGradeSelect = elements.videoForm.querySelector('#video-grade');
                const videoSubjectSelect = elements.videoForm.querySelector('#video-subject');
                const videoDescriptionInput = elements.videoForm.querySelector('#video-description');

                videoIdInput.value = videoData.id || '';
                videoTitleInput.value = videoData.title || '';
                videoGradeSelect.value = videoData.grade || '';
                videoDescriptionInput.value = videoData.description || '';

                if (videoData.subjectId) {
                    videoSubjectSelect.value = videoData.subjectId;
                } else {
                    Array.from(videoSubjectSelect.options).forEach(option => {
                        if (option.text === videoData.subject) {
                            videoSubjectSelect.value = option.value;
                        }
                    });
                }
                console.log('Selected subject value:', videoSubjectSelect.value);

                if (videoData.videoUrl) {
                    elements.fileInfo.textContent = `Ընթացիկ: ${videoData.videoUrl}`;
                    elements.fileInfo.classList.add('warning');
                }

                elements.videoForm.querySelector('#video-file').removeAttribute('required');

                console.log('Form fields after filling:', {
                    id: videoIdInput.value,
                    title: videoTitleInput.value,
                    grade: videoGradeSelect.value,
                    subject: videoSubjectSelect.value,
                    description: videoDescriptionInput.value
                });
            } else {
                elements.videoForm.querySelector('#video-id').value = '';
                elements.videoForm.querySelector('#video-file').setAttribute('required', '');
            }
        },

        closeAddModal() {
            elements.addModal.style.display = 'none';
        },

        openDeleteModal(videoId) {
            console.log('Opening delete modal for videoId:', videoId);
            elements.deleteModal.style.display = 'flex';
            elements.deleteModal.dataset.videoId = videoId;
        },

        closeDeleteModal() {
            elements.deleteModal.style.display = 'none';
            delete elements.deleteModal.dataset.videoId;
        }
    };

    const videoManager = {
        filterVideos() {
            const selectedSubject = elements.subjectFilter.value;
            const selectedGrade = elements.gradeFilter.value;

            config.filteredVideos = Array.from(elements.videos).filter(video => {
                const videoSubject = video.getAttribute('data-subject');
                const videoGrade = video.getAttribute('data-grade');

                const subjectMatch = !selectedSubject || videoSubject === selectedSubject;
                const gradeMatch = !selectedGrade || videoGrade === selectedGrade;

                return selectedSubject || selectedGrade ? subjectMatch && gradeMatch : false;
            });

            config.currentPage = 1;
            this.updateDisplay();
        },

        updateDisplay() {
            const totalPages = Math.ceil(config.filteredVideos.length / config.itemsPerPage);

            elements.videos.forEach(video => video.style.display = 'none');

            if (config.filteredVideos.length > 0 && (elements.subjectFilter.value || elements.gradeFilter.value)) {
                elements.videoList.style.display = 'grid';
                elements.noResults.style.display = 'none';
                elements.pagination.style.display = 'flex';

                const startIndex = (config.currentPage - 1) * config.itemsPerPage;
                const endIndex = Math.min(startIndex + config.itemsPerPage, config.filteredVideos.length);

                for (let i = startIndex; i < endIndex; i++) {
                    config.filteredVideos[i].style.display = 'flex';
                }

                elements.pageNumbersContainer.innerHTML = '';
                for (let i = 1; i <= totalPages; i++) {
                    const pageNum = document.createElement('button');
                    pageNum.classList.add('page-number');
                    pageNum.textContent = i;
                    if (i === config.currentPage) pageNum.classList.add('active');
                    pageNum.addEventListener('click', () => {
                        config.currentPage = i;
                        this.updateDisplay();
                    });
                    elements.pageNumbersContainer.appendChild(pageNum);
                }

                elements.prevPageBtn.disabled = config.currentPage === 1;
                elements.nextPageBtn.disabled = config.currentPage === totalPages || totalPages === 0;
            } else {
                elements.videoList.style.display = 'none';
                elements.noResults.style.display = 'flex';
                elements.pagination.style.display = 'none';
                elements.noResultsText.textContent = config.filteredVideos.length === 0 && (elements.subjectFilter.value || elements.gradeFilter.value)
                    ? 'Համապատասխան վիդեոդասեր չեն գտնվել'
                    : 'Ընտրեք առարկան կամ կուրսը ցանկալի արդյունքը տեսնելու համար';
                elements.searchIcon.style.display = config.filteredVideos.length === 0 && (elements.subjectFilter.value || elements.gradeFilter.value) ? 'none' : 'block';
            }
        }
    };

    const formManager = {
        submitVideoForm(e) {
            e.preventDefault();
            const submitBtn = elements.videoForm.querySelector('.submit-btn');
            submitBtn.classList.add('loading');
            submitBtn.disabled = true;

            const formData = new FormData(elements.videoForm);
            console.log('Submitting form data:', formData);
            fetch('/addVideo', {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    console.log('Add video response:', response);
                    if (!response.ok) throw new Error(`Failed to save video: ${response.status}`);
                    return response.text();
                })
                .then(() => {
                    window.location.reload();
                })
                .catch(error => {
                    console.error('Error submitting form:', error);
                    alert('Հնարավոր չէ պահպանել վիդեոդասը։ Խնդրում ենք փորձել կրկին։');
                })
                .finally(() => {
                    submitBtn.classList.remove('loading');
                    submitBtn.disabled = false;
                    modalManager.closeAddModal();
                });
        },

        deleteVideo(videoId) {
            console.log('Attempting to delete video with ID:', videoId);
            const confirmBtn = elements.confirmDelete;
            confirmBtn.classList.add('loading');
            confirmBtn.disabled = true;

            fetch(`/deleteVideo/${videoId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => {
                    console.log('Delete video response:', response);
                    if (!response.ok) throw new Error(`Failed to delete video: ${response.status}`);
                    return response.text();
                })
                .then(() => {
                    console.log('Video deleted successfully');
                    window.location.reload();
                })
                .catch(error => {
                    console.error('Error deleting video:', error);
                    alert('Հնարավոր չէ ջնջել վիդեոդասը։ Խնդրում ենք փորձել կրկին։');
                })
                .finally(() => {
                    confirmBtn.classList.remove('loading');
                    confirmBtn.disabled = false;
                    modalManager.closeDeleteModal();
                });
        }
    };

    elements.subjectFilter.addEventListener('change', () => videoManager.filterVideos());
    elements.gradeFilter.addEventListener('change', () => videoManager.filterVideos());

    elements.prevPageBtn.addEventListener('click', () => {
        if (config.currentPage > 1) {
            config.currentPage--;
            videoManager.updateDisplay();
        }
    });

    elements.nextPageBtn.addEventListener('click', () => {
        const totalPages = Math.ceil(config.filteredVideos.length / config.itemsPerPage);
        if (config.currentPage < totalPages) {
            config.currentPage++;
            videoManager.updateDisplay();
        }
    });

    elements.addVideoBtn.addEventListener('click', () => {
        console.log('Add video button clicked');
        modalManager.openAddModal(false);
    });

    elements.videoForm.addEventListener('submit', formManager.submitVideoForm);

    elements.addModal.querySelector('.close-btn').addEventListener('click', () => modalManager.closeAddModal());
    elements.addModal.querySelector('.cancel-btn').addEventListener('click', () => modalManager.closeAddModal());

    elements.deleteModal.querySelector('.close-btn').addEventListener('click', () => modalManager.closeDeleteModal());
    elements.cancelDelete.addEventListener('click', () => modalManager.closeDeleteModal());

    elements.confirmDelete.addEventListener('click', () => {
        const videoId = elements.deleteModal.dataset.videoId;
        if (videoId) {
            console.log('Confirm delete for videoId:', videoId);
            formManager.deleteVideo(videoId);
        } else {
            console.error('No video ID found for deletion');
            alert('Ջնջման համար վիդեոդասի ID չի գտնվել։');
            modalManager.closeDeleteModal();
        }
    });

    elements.videoList.addEventListener('click', (e) => {
        const editBtn = e.target.closest('.edit-btn');
        const deleteBtn = e.target.closest('.delete-btn');

        if (editBtn) {
            console.log('Edit button clicked');
            const videoCard = editBtn.closest('.video-card');
            if (!videoCard) {
                console.error('No video card found for edit button');
                alert('Վիդեոդասի տվյալները չեն գտնվել։');
                return;
            }
            const videoData = {
                id: editBtn.dataset.id || '',
                title: videoCard.querySelector('.book-title')?.textContent || '',
                subject: videoCard.getAttribute('data-subject') || '',
                subjectId: videoCard.getAttribute('data-subject-id') || '',
                grade: videoCard.getAttribute('data-grade') || '',
                description: videoCard.querySelector('.info-item:nth-child(4) p span')?.textContent || '',
                videoUrl: videoCard.querySelector('.download-btn')?.href.split('/').pop() || ''
            };
            console.log('videoData for edit:', videoData);
            modalManager.openAddModal(true, videoData);
        }

        if (deleteBtn) {
            const videoId = deleteBtn.dataset.id;
            if (videoId) {
                modalManager.openDeleteModal(videoId);
            } else {
                console.error('No video ID found for delete button');
                alert('Ջնջման համար վիդեոդասի ID չի գտնվել։');
            }
        }
    });

    elements.videoList.style.display = 'none';
    elements.noResults.style.display = 'flex';
    elements.pagination.style.display = 'none';

    console.log('Initial DOM elements:', elements);
});