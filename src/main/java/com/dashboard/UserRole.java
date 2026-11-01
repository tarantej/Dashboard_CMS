package com.dashboard;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "role", length = 100)
    private String role;

    public Integer getId() {
        return id;
    }

    public String getRole() {
        return role;
    }
}
