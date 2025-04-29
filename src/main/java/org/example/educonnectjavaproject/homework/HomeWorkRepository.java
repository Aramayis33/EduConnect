package org.example.educonnectjavaproject.homework;

import org.example.educonnectjavaproject.exercises.Exercise;
import org.example.educonnectjavaproject.exercises.ExerciseRepository;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.teacher.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

public interface HomeWorkRepository extends JpaRepository<HomeWork, Integer> {

    List<HomeWork> findHomeWorkByGroupNumber(int groupNumber);

//    List<HomeWork> findAllById(int id);
    HomeWork findById(int id);
//    @Query("SELECT h FROM HomeWork h WHERE h.groupNumber = :groupNumber AND h.expirationDate >= CURRENT_DATE ")
//    List<HomeWork> findActiveHomeworksByGroupNumberAndActive(int groupNumber);

    @Query("SELECT DISTINCT h FROM HomeWork h " +
            "JOIN h.fileGroup fg " +
            "JOIN fg.files uf " +
            "WHERE h.groupNumber = :groupNumber " +
            "AND h.expirationDate >= current_date")
    List<HomeWork> findActiveHomeworksByGroupNumberAndActive(@Param("groupNumber") int groupNumber);


    @Query("SELECT h FROM HomeWork h " +
            "LEFT JOIN FETCH h.fileGroup fg " +
            "LEFT JOIN FETCH fg.files " +
            "WHERE  h.expirationDate >= CURRENT_DATE AND h.teacher= :teacher")
    List<HomeWork>findActiveHomeworksByTeacher(Teacher teacher);

    @Query("SELECT h FROM HomeWork h WHERE h.groupNumber = :groupNumber AND h.expirationDate < CURRENT_DATE")
    List<HomeWork> findExpiredHomeWorksByGroupNumber(int groupNumber);

    @Query("SELECT h FROM HomeWork h WHERE h.teacher = :teacher AND h.expirationDate < CURRENT_DATE")
    List<HomeWork> findExpiredHomeWorksByTeacher(Teacher teacher);


    HomeWork findHomeWorkById(Integer id);

    default List<HomeWork> findActiveHomeWorksByGroupNumberAndExercises(int groupNumber, List<Exercise> exercises) {
        List<HomeWork> homeWorks = findActiveHomeworksByGroupNumberAndActive(groupNumber);
        Set<Integer> exerciseHomeworkIds = exercises.stream()
                .map(exercise -> exercise.getHomeWork().getId())
                .collect(Collectors.toSet());

        homeWorks.removeIf(homeWork -> exerciseHomeworkIds.contains(homeWork.getId()));

        return homeWorks;
    }

    default List<HomeWork> findExpiredHomeWorksByGroupNumberAndExercises(int groupNumber,List<Exercise> exercises) {
        List<HomeWork>expiredHomeworks=findExpiredHomeWorksByGroupNumber(groupNumber);
        List<HomeWork>homeWorks=findActiveHomeworksByGroupNumberAndActive(groupNumber);


        for(Exercise exercise:exercises) {
            for(HomeWork homeWork:homeWorks) {
                if(homeWork.getId()==exercise.getHomeWork().getId()) {
                    expiredHomeworks.add(homeWork);
                }
            }
        }
        return expiredHomeworks;


    }

    default Map<Integer, Integer> getHomeWorkCount(List<GroupInfo> groupInfos, String period) {
        Map<Integer, Integer> homeWorkCount = new HashMap<>();
        List<HomeWork> homeWorkList = findAll();
        Date currentDate = new Date(System.currentTimeMillis());

        for (GroupInfo groupInfo : groupInfos) {
            int homeWorks = 0;
            for (HomeWork homeWork : homeWorkList) {
                if ("all".equals(period)) {
                    if (groupInfo.getGroupNumber() == homeWork.getGroupNumber()) {
                        homeWorks++;
                    }
                } else if ("current".equals(period)) {
                    Date expirationDate = homeWork.getExpirationDate();
                    if (groupInfo.getGroupNumber() == homeWork.getGroupNumber() &&
                            (expirationDate == null || expirationDate.after(currentDate))) {
                        homeWorks++;
                    }
                }
            }
            homeWorkCount.put(groupInfo.getGroupNumber(), homeWorks);
        }

        return homeWorkCount.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }



}
