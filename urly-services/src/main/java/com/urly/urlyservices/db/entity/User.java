package com.urly.urlyservices.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.urly.urlyservices.enums.AuthProvider;
import lombok.Data;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@Table(name = "USER")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nullable
    private String username;

    private String email;

    @JsonIgnore
    @Nullable
    private String password;

    private String roles;

    @Nullable
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;
}
