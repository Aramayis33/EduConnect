package org.example.educonnectjavaproject.filegroup;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.files.UploadedFile;

import java.util.List;

@Entity
public class FileGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "fileGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UploadedFile> files;

    public FileGroup() {
    }

    public int getId() {
        return id;
    }

    public FileGroup(List<UploadedFile> files) {
        this.files = files;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<UploadedFile> getFiles() {
        return files;
    }

    public void setFiles(List<UploadedFile> files) {
        this.files = files;
    }
}
