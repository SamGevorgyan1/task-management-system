package com.taskmanagement.repository;

import com.taskmanagement.enums.UserRole;
import com.taskmanagement.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer> {

    UserEntity findByEmail(String email);

    List<UserEntity> getByNameAndSurnameAndRole(String name, String surname, UserRole role);

    List<UserEntity> getByNameAndSurname(String name, String surname);

    List<UserEntity> getByName(String name);

    List<UserEntity> getBySurname(String surname);

    List<UserEntity> getByRole(UserRole role);

    List<UserEntity> getByNameAndRole(String name, UserRole role);

    List<UserEntity> getBySurnameAndRole(String name, UserRole userRole);
}
