package org.example.educonnectjavaproject.teacher;

import org.example.educonnectjavaproject.schedule.Schedule;
import org.example.educonnectjavaproject.subject.Subjects;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    Teacher findTeacherByEmail(String username);
    Teacher findTeacherById(int id);
    Teacher findTeacherByEmailAndIsActivated(String username, int isActivated);
    Teacher findTeacherByEmailAndIsActivatedAndIsBlockedAndDeletedAtIsNull(String username,int isActivated, int isBlocked);

    default Map<String, Integer> getTeacherClassHourCount(List<Schedule> schedules) {
        Map<String, Integer> map = new HashMap<>();

        for (Teacher teacher : findAll()) {
            int count = 0;
            for (Schedule schedule : schedules) {
                if (schedule.getTeacher().equals(teacher)) {
                    count++;
                }
            }
            map.put(teacher.getName() + " " + teacher.getSurname(), count);
        }

        return map.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    List<Teacher> findAllByDeletedAtIsNull();

}
