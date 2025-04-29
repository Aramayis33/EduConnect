package org.example.educonnectjavaproject.videolessons;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoLessonRepository extends JpaRepository<VideoLesson, Integer> {

    VideoLesson findVideoLessonById(int id);
}
