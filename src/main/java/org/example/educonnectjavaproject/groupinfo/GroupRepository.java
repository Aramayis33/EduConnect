package org.example.educonnectjavaproject.groupinfo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupInfo, Integer> {

    GroupInfo findFirstByOrderByGroupNumber();
    GroupInfo findGroupByGroupNumber(int groupNumber);
}
