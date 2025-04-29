document.addEventListener('DOMContentLoaded', () => {
    const subjectFilter = document.getElementById('subject-filter');
    const gradeFilter = document.getElementById('grade-filter');
    const libraryList = document.getElementById('library-list');
    const noResults = document.getElementById('no-results');
    const noResultsText = document.getElementById('no-results-text');
    const searchIcon = document.getElementById('search-icon');
    const books = document.querySelectorAll('.library-card');
    const pagination = document.getElementById('pagination');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageNumbersContainer = document.getElementById('page-numbers');
    const addBookBtn = document.getElementById('add-book-btn');
    const modal = document.getElementById('add-book-modal');
    const form = modal.querySelector('form');
    const thumbnailInfo = document.getElementById('thumbnail-info');
    const fileInfo = document.getElementById('file-info');

    const itemsPerPage = 2;
    let currentPage = 1;
    let filteredBooks = [];

    libraryList.style.display = 'none';
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
        const totalPages = Math.ceil(filteredBooks.length / itemsPerPage);

        books.forEach(book => book.style.display = 'none');

        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = Math.min(startIndex + itemsPerPage, filteredBooks.length);

        for (let i = startIndex; i < endIndex; i++) {
            filteredBooks[i].style.display = 'flex';
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

        filteredBooks = Array.from(books).filter(book => {
            const bookSubject = book.getAttribute('data-subject');
            const bookGrade = book.getAttribute('data-grade').toString();

            const subjectMatch = !selectedSubject || bookSubject === selectedSubject;
            const gradeMatch = !selectedGrade || bookGrade === selectedGrade;

            return subjectMatch && gradeMatch;
        });

        currentPage = 1;

        if (selectedSubject && selectedGrade) {
            if (filteredBooks.length > 0) {
                libraryList.style.display = 'flex';
                noResults.style.display = 'none';
                pagination.style.display = 'flex';
                updatePagination();
            } else {
                libraryList.style.display = 'none';
                noResults.style.display = 'flex';
                pagination.style.display = 'none';
                noResultsText.textContent = 'Համապատասխան գրքեր չեն գտնվել';
                searchIcon.style.display = 'none';
            }
        } else {
            libraryList.style.display = 'none';
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
        const totalPages = Math.ceil(filteredBooks.length / itemsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            updatePagination();
        }
    });

    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();

            const bookCard = this.closest('.library-card');
            const bookTitle = bookCard.querySelector('.book-title').textContent;
            const bookSubjectName = bookCard.getAttribute('data-subject');
            const bookGrade = bookCard.getAttribute('data-grade');
            const bookAuthor = bookCard.querySelector('.book-author').textContent.replace('Հեղինակ: ', '');
            const bookYear = bookCard.querySelector('.book-year').textContent.replace('Հրատարակման տարի: ', '');
            const thumbnailFile = bookCard.querySelector('.thumbnail-image').src.split('/').pop();
            const bookFile = bookCard.querySelector('.download-btn').href.split('/').pop();
            const bookId = this.closest('form').action.split('/').pop();

            form.action = '/addBook';
            form.querySelector('#book-id').value = bookId;
            form.querySelector('#book-title').value = bookTitle;
            form.querySelector('#book-grade').value = bookGrade;
            form.querySelector('#book-author').value = bookAuthor;
            form.querySelector('#book-year').value = bookYear;

            const subjectSelect = form.querySelector('#book-subject');
            Array.from(subjectSelect.options).forEach(option => {
                if (option.text === bookSubjectName) {
                    subjectSelect.value = option.value;
                }
            });

            thumbnailInfo.textContent = `Նոր նկար վերբեռնելու դեպքում հինը ավտոմատ կերպով կջնջվի`;
            thumbnailInfo.classList.add('warning');
            fileInfo.textContent = `Նոր ֆայլ վերբեռնելու դեպքում հինը ավտոմատ կերպով կջնջվի`;
            fileInfo.classList.add('warning');

            form.querySelector('#book-thumbnail').removeAttribute('required');
            form.querySelector('#book-file').removeAttribute('required');

            modal.style.display = 'block';
            modal.querySelector('h3').textContent = 'Խմբագրել Գիրք';
        });
    });

    addBookBtn.addEventListener('click', () => {
        form.action = '/addBook';
        form.reset();
        form.querySelector('#book-id').value = '';
        thumbnailInfo.textContent = '';
        fileInfo.textContent = '';
        form.querySelector('#book-thumbnail').setAttribute('required', '');
        form.querySelector('#book-file').setAttribute('required', '');
        modal.style.display = 'block';
        modal.querySelector('h3').textContent = 'Ավելացնել Գիրք';
    });

    document.getElementById('cancel-add-book').addEventListener('click', () => {
        modal.style.display = 'none';
    });
});