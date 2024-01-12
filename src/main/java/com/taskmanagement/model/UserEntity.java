package com.taskmanagement.model;

import com.taskmanagement.enums.UserStatus;
import com.taskmanagement.enums.UserRole;
import lombok.*;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String name;

    @Column(name = "last_name")
    private String surname;

    private Integer year;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "verification_code")
    private String verifyCode;

    @Column(name = "reset_token")
    private String resetToken;

    @OneToMany(mappedBy = "user")
    private List<TokenEntity> tokenEntities;

    public UserEntity(Integer id, String name, String surname, Integer year, String email, String password, UserRole role, UserStatus status) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.year = year;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
    }
}