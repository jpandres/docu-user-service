package com.jpandres.docuuserservice.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class User {

    private final UUID id;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final int age;
}
