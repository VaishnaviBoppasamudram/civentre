package com.civentre;

import com.civentre.entity.Department;
import com.civentre.repository.DepartmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DepartmentDataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;

    public DepartmentDataInitializer(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public void run(String... args) {

        addDepartment(
                "Roads & Infrastructure",
                "Handles potholes, damaged roads and road-related issues."
        );

        addDepartment(
                "Streetlight",
                "Handles broken and non-functional streetlights."
        );

        addDepartment(
                "Sanitation",
                "Handles garbage, waste and cleanliness-related issues."
        );

        addDepartment(
                "Water Supply",
                "Handles water leakage, supply and drainage-related issues."
        );

        addDepartment(
                "Traffic",
                "Handles traffic signals and other traffic-related issues."
        );

        addDepartment(
                "Parks & Environment",
                "Handles parks, fallen trees and environmental issues."
        );
    }

    private void addDepartment(String name, String description) {

        if (departmentRepository.findByName(name).isEmpty()) {

            Department department =
                    new Department(name, description);

            departmentRepository.save(department);

            System.out.println(
                    "Department created: " + name
            );
        }
    }
}

