package dev.nhairlahovic.employeeservice.repository;

import dev.nhairlahovic.employeeservice.model.Employee;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EmployeeRepository extends ElasticsearchRepository<Employee, String> {
}
