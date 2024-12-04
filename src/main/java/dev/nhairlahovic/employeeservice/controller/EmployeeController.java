package dev.nhairlahovic.employeeservice.controller;

import dev.nhairlahovic.employeeservice.model.Employee;
import dev.nhairlahovic.employeeservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository repository;

    @GetMapping
    public Iterable<Employee> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Employee> findById(@PathVariable String id) {
        return repository.findById(id);
    }

    @PostMapping
    public Employee create(@RequestBody Employee employee) {
        employee.setId(UUID.randomUUID().toString());
        return repository.save(employee);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable String id, @RequestBody Employee employee) {
        employee.setId(id);
        return repository.save(employee);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        repository.deleteById(id);
    }
}
