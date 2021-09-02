package com.bootcamp.util;

import com.bootcamp.dto.Employee;

import java.util.Objects;
import java.util.stream.Stream;

public class Validation {

    public static boolean isEmpValid(Employee emp) {
        return !Stream.of(emp.getCity(), emp.getJavaExp(), emp.getPhone())
                .anyMatch(Objects::isNull);
    }

}
