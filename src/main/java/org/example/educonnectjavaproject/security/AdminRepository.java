package org.example.educonnectjavaproject.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Admin findByUsername(String username);

    @Query("select u from Admin u where u.username=:login and u.password=:password ")
    List<Admin> findUser(String login, String password);

    Admin findAdminByUsername(String username);

    Admin findAdminById(int id);
}
