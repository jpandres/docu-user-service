package com.jpandres.docuuserservice.data;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
@Builder
public class UserVO {

    @NotEmpty
    String username;
    @NotEmpty
    String firstname;
    @NotEmpty
    String lastname;
    @Positive
    int age;
}
