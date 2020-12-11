package com.spring.files.upload.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class UserCredentials {

    @Id
    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;
}
