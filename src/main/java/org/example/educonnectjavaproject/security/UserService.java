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
    Admin findUserByUsername(String username){
        return adminRepository.findAdminByUsername(username);
    }

    public void save(Admin admin){
        adminRepository.save(admin);
    }

}
