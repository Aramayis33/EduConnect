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

    const itemsPerPage = 2;
    let currentPage = 1;
    let filteredBooks = [];

    libraryList.style.display = 'none';
    noResults.style.display = 'flex';
    searchIcon.style.display = 'block';
    pagination.style.display = 'none';

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
});