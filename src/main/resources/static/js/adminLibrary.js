document.addEventListener('DOMContentLoaded', () => {
    const elements = {
        subjectFilter: document.getElementById('subject-filter'),
        gradeFilter: document.getElementById('grade-filter'),
        libraryList: document.getElementById('library-list'),
        noResults: document.getElementById('no-results'),
        noResultsText: document.getElementById('no-results-text'),
        searchIcon: document.getElementById('search-icon'),
        books: document.querySelectorAll('.library-card'),
        pagination: document.getElementById('pagination'),
        prevPageBtn: document.getElementById('prev-page'),
        nextPageBtn: document.getElementById('next-page'),
        pageNumbersContainer: document.getElementById('page-numbers'),
        addBookBtn: document.getElementById('add-book-btn'),
        addModal: document.getElementById('add-book-modal'),
        bookForm: document.getElementById('book-form'),
        thumbnailInfo: document.getElementById('thumbnail-info'),
        fileInfo: document.getElementById('file-info'),
        deleteModal: document.getElementById('delete-confirm-modal'),
        confirmDelete: document.getElementById('confirm-delete'),
        cancelDelete: document.getElementById('cancel-delete')
    };

    const config = {
        itemsPerPage: 2,
        currentPage: 1,
        filteredBooks: []
    };

    const modalManager = {
        openAddModal(isEdit = false, bookData = {}) {
            console.log('Opening modal with isEdit:', isEdit, 'and bookData:', bookData);
            elements.addModal.style.display = 'flex';
            elements.bookForm.reset();
            elements.thumbnailInfo.textContent = '';
            elements.fileInfo.textContent = '';
            elements.thumbnailInfo.classList.remove('warning');
            elements.fileInfo.classList.remove('warning');

            const modalTitle = elements.addModal.querySelector('#modal-title');
            if (!modalTitle) {
                console.error('Modal title element not found');
                return;
            }
            modalTitle.textContent = isEdit ? 'Խմբագրել Գիրք' : 'Ավելացնել Գիրք';
            console.log('Modal title set to:', modalTitle.textContent);

            if (isEdit) {
                const bookIdInput = elements.bookForm.querySelector('#book-id');
                const bookTitleInput = elements.bookForm.querySelector('#book-title');
                const bookGradeSelect = elements.bookForm.querySelector('#book-grade');
                const bookAuthorInput = elements.bookForm.querySelector('#book-author');
                const bookYearInput = elements.bookForm.querySelector('#book-year');
                const bookSubjectSelect = elements.bookForm.querySelector('#book-subject');

                bookIdInput.value = bookData.id || '';
                bookTitleInput.value = bookData.title || '';
                bookGradeSelect.value = bookData.grade || '';
                bookAuthorInput.value = bookData.author || '';
                bookYearInput.value = bookData.year || '';

                if (bookData.subjectId) {
                    bookSubjectSelect.value = bookData.subjectId;
                } else {
                    Array.from(bookSubjectSelect.options).forEach(option => {
                        if (option.text === bookData.subject) {
                            bookSubjectSelect.value = option.value;
                        }
                    });
                }
                console.log('Selected subject value:', bookSubjectSelect.value);

                if (bookData.thumbnail) {
                    elements.thumbnailInfo.textContent = `Նոր նկար վերբեռնելու դեպքում, հինը ավտոմատ կհեռացվի`;
                    elements.thumbnailInfo.classList.add('warning');
                }
                if (bookData.file) {
                    elements.fileInfo.textContent = `Նոր ֆայլ վերբեռնելու դեպքում, հինը ավտոմատ կհեռացվի`;
                    elements.fileInfo.classList.add('warning');
                }

                elements.bookForm.querySelector('#book-thumbnail').removeAttribute('required');
                elements.bookForm.querySelector('#book-file').removeAttribute('required');

                console.log('Form fields after filling:', {
                    id: bookIdInput.value,
                    title: bookTitleInput.value,
                    grade: bookGradeSelect.value,
                    author: bookAuthorInput.value,
                    year: bookYearInput.value,
                    subject: bookSubjectSelect.value
                });
            } else {
                elements.bookForm.querySelector('#book-id').value = '';
                elements.bookForm.querySelector('#book-thumbnail').setAttribute('required', '');
                elements.bookForm.querySelector('#book-file').setAttribute('required', '');
            }
        },

        closeAddModal() {
            elements.addModal.style.display = 'none';
        },

        openDeleteModal(bookId) {
            console.log('Opening delete modal for bookId:', bookId);
            elements.deleteModal.style.display = 'flex';
            elements.deleteModal.dataset.bookId = bookId;
        },

        closeDeleteModal() {
            elements.deleteModal.style.display = 'none';
            delete elements.deleteModal.dataset.bookId;
        }
    };

    const libraryManager = {
        filterBooks() {
            const selectedSubject = elements.subjectFilter.value;
            const selectedGrade = elements.gradeFilter.value;

            config.filteredBooks = Array.from(elements.books).filter(book => {
                const bookSubject = book.getAttribute('data-subject');
                const bookGrade = book.getAttribute('data-grade');

                const subjectMatch = !selectedSubject || bookSubject === selectedSubject;
                const gradeMatch = !selectedGrade || bookGrade === selectedGrade;

                return selectedSubject || selectedGrade ? subjectMatch && gradeMatch : false;
            });

            config.currentPage = 1;
            this.updateDisplay();
        },

        updateDisplay() {
            const totalPages = Math.ceil(config.filteredBooks.length / config.itemsPerPage);

            elements.books.forEach(book => book.style.display = 'none');

            if (config.filteredBooks.length > 0 && (elements.subjectFilter.value || elements.gradeFilter.value)) {
                elements.libraryList.style.display = 'grid';
                elements.noResults.style.display = 'none';
                elements.pagination.style.display = 'flex';

                const startIndex = (config.currentPage - 1) * config.itemsPerPage;
                const endIndex = Math.min(startIndex + config.itemsPerPage, config.filteredBooks.length);

                for (let i = startIndex; i < endIndex; i++) {
                    config.filteredBooks[i].style.display = 'flex';
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
                elements.libraryList.style.display = 'none';
                elements.noResults.style.display = 'flex';
                elements.pagination.style.display = 'none';
                elements.noResultsText.textContent = config.filteredBooks.length === 0 && (elements.subjectFilter.value || elements.gradeFilter.value)
                    ? 'Համապատասխան գրքեր չեն գտնվել'
                    : 'Ընտրեք առարկան կամ կուրսը ցանկալի արդյունքը տեսնելու համար';
                elements.searchIcon.style.display = config.filteredBooks.length === 0 && (elements.subjectFilter.value || elements.gradeFilter.value) ? 'none' : 'block';
            }
        }
    };

    const formManager = {
        submitBookForm(e) {
            e.preventDefault();
            const submitBtn = elements.bookForm.querySelector('.submit-btn');
            submitBtn.classList.add('loading');
            submitBtn.disabled = true;

            const formData = new FormData(elements.bookForm);
            console.log('Submitting form data:', formData);
            fetch('/addBook', {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    console.log('Add book response:', response);
                    if (!response.ok) throw new Error(`Failed to save book: ${response.status}`);
                    return response.text();
                })
                .then(() => {
                    window.location.reload();
                })
                .catch(error => {
                    console.error('Error submitting form:', error);
                    alert('Հնարավոր չէ պահպանել գիրքը։ Խնդրում ենք փորձել կրկին։');
                })
                .finally(() => {
                    submitBtn.classList.remove('loading');
                    submitBtn.disabled = false;
                    modalManager.closeAddModal();
                });
        },

        deleteBook(bookId) {
            console.log('Attempting to delete book with ID:', bookId);
            const confirmBtn = elements.confirmDelete;
            confirmBtn.classList.add('loading');
            confirmBtn.disabled = true;

            fetch(`/deleteBook/${bookId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => {
                    console.log('Delete book response:', response);
                    if (!response.ok) throw new Error(`Failed to delete book: ${response.status}`);
                    return response.text();
                })
                .then(() => {
                    console.log('Book deleted successfully');
                    window.location.reload();
                })
                .catch(error => {
                    console.error('Error deleting book:', error);
                    alert('Հնարավոր չէ ջնջել գիրքը։ Խնդրում ենք փորձել կրկին։');
                })
                .finally(() => {
                    confirmBtn.classList.remove('loading');
                    confirmBtn.disabled = false;
                    modalManager.closeDeleteModal();
                });
        }
    };

    elements.subjectFilter.addEventListener('change', () => libraryManager.filterBooks());
    elements.gradeFilter.addEventListener('change', () => libraryManager.filterBooks());

    elements.prevPageBtn.addEventListener('click', () => {
        if (config.currentPage > 1) {
            config.currentPage--;
            libraryManager.updateDisplay();
        }
    });

    elements.nextPageBtn.addEventListener('click', () => {
        const totalPages = Math.ceil(config.filteredBooks.length / config.itemsPerPage);
        if (config.currentPage < totalPages) {
            config.currentPage++;
            libraryManager.updateDisplay();
        }
    });

    elements.addBookBtn.addEventListener('click', () => {
        console.log('Add book button clicked');
        modalManager.openAddModal(false);
    });

    elements.bookForm.addEventListener('submit', formManager.submitBookForm);

    elements.addModal.querySelector('.close-btn').addEventListener('click', () => modalManager.closeAddModal());
    elements.addModal.querySelector('.cancel-btn').addEventListener('click', () => modalManager.closeAddModal());

    elements.deleteModal.querySelector('.close-btn').addEventListener('click', () => modalManager.closeDeleteModal());
    elements.cancelDelete.addEventListener('click', () => modalManager.closeDeleteModal());

    elements.confirmDelete.addEventListener('click', () => {
        const bookId = elements.deleteModal.dataset.bookId;
        if (bookId) {
            console.log('Confirm delete for bookId:', bookId);
            formManager.deleteBook(bookId);
        } else {
            console.error('No book ID found for deletion');
            alert('Ջնջման համար գրքի ID չի գտնվել։');
            modalManager.closeDeleteModal();
        }
    });

    elements.libraryList.addEventListener('click', (e) => {
        const editBtn = e.target.closest('.edit-btn');
        const deleteBtn = e.target.closest('.delete-btn');

        if (editBtn) {
            console.log('Edit button clicked');
            const bookCard = editBtn.closest('.library-card');
            if (!bookCard) {
                console.error('No book card found for edit button');
                alert('Գրքի տվյալները չեն գտնվել։');
                return;
            }
            const bookData = {
                id: editBtn.dataset.id || '',
                title: bookCard.querySelector('.book-title')?.textContent || '',
                subject: bookCard.getAttribute('data-subject') || '',
                subjectId: bookCard.getAttribute('data-subject-id') || '',
                grade: bookCard.getAttribute('data-grade') || '',
                author: bookCard.querySelector('.info-item:nth-child(4) p span')?.textContent || '',
                year: bookCard.querySelector('.info-item:nth-child(5) p span')?.textContent || '',
                thumbnail: bookCard.querySelector('.thumbnail-image')?.src.split('/').pop() || '',
                file: bookCard.querySelector('.download-btn')?.href.split('/').pop() || ''
            };
            console.log('bookData for edit:', bookData);
            modalManager.openAddModal(true, bookData);
        }

        if (deleteBtn) {
            const bookId = deleteBtn.dataset.id;
            if (bookId) {
                modalManager.openDeleteModal(bookId);
            } else {
                console.error('No book ID found for delete button');
                alert('Ջնջման համար գրքի ID չի գտնվել։');
            }
        }
    });

    elements.libraryList.style.display = 'none';
    elements.noResults.style.display = 'flex';
    elements.pagination.style.display = 'none';

    console.log('Initial DOM elements:', elements);
});