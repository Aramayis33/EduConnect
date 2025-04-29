package org.example.educonnectjavaproject.files;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Integer> {

    UploadedFile findByFileName(String fileName);
}
