package org.example.educonnectjavaproject.files;

import jakarta.persistence.*;
import org.example.educonnectjavaproject.filegroup.FileGroup;

@Entity
public class UploadedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="file_group_id")
    private FileGroup fileGroup;
    @Column(nullable=false, unique=true)
    private String fileName;

    public UploadedFile() {
    }

    public UploadedFile(FileGroup fileGroup, String fileName) {
        this.fileGroup = fileGroup;
        this.fileName = fileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FileGroup getFileGroup() {
        return fileGroup;
    }

    public void setFileGroup(FileGroup fileGroup) {
        this.fileGroup = fileGroup;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
