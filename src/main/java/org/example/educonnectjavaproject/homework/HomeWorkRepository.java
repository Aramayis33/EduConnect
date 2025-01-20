package org.example.educonnectjavaproject.homework;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeWorkRepository extends JpaRepository<HomeWork, Integer> {

    List<HomeWork> findHomeWorkByGroupNumber(int groupNumber);

}
