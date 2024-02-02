package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class SamplePerson extends AbstractEntity {

    private String firstName;
    private String lastName;
    private int age;


}
