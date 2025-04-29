package org.example.educonnectjavaproject.library;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibraryRepository extends JpaRepository<Library, Integer> {

    List<Library> findLibrariesByIsConfirmed(int isConfirmed);
    Library findLibraryById(int id);
}
