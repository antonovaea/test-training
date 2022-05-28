package com.example.tddmain.model;

import lombok.Value;
import org.springframework.stereotype.Component;

@Value(staticConstructor = "of")
@Component
public class User {
    Long id;
    String name;
    int age;
    String password;
}
