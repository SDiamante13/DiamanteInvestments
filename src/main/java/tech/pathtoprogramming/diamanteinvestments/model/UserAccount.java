package tech.pathtoprogramming.diamanteinvestments.model;

import lombok.Value;

@Value
public class UserAccount {
    String firstName;
    String lastName;
    String email;
    String username;
    String password;
    String confirmPassword;
}
