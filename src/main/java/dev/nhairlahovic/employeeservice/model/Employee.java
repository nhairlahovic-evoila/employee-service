package dev.nhairlahovic.employeeservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "employee")
public class Employee {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String department;
}
