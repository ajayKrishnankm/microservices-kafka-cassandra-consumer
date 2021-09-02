package com.bootcamp.util;

import com.bootcamp.dto.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mapper {

    public static String mapEmpToString(Employee emp) {
        ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            result = mapper.writeValueAsString(emp);
        } catch (JsonProcessingException e) {
            log.error("Error occurred during emp object serialization");
            e.printStackTrace();
        }
        return result;
    }

    public static Employee getEmpObject(String empMsg) {
        ObjectMapper mapper = new ObjectMapper();
        Employee employee = null;
        try {
            employee = mapper.readValue(empMsg, Employee.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return employee;
    }

}
