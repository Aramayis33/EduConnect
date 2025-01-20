package org.example.educonnectjavaproject.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private AdminRepository adminRepository;

    public List<Admin> findUserByLogin(String login, String password){

        return adminRepository.findUser(login, password);

    }
}
