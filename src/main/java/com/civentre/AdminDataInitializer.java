package com.civentre;

import com.civentre.entity.Department;
import com.civentre.entity.User;
import com.civentre.repository.DepartmentRepository;
import com.civentre.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataInitializer(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // ============================================================
        // CREATE / UPDATE ADMIN
        // ============================================================

        User admin = userRepository
                .findByEmail("admin@civentre.com")
                .orElse(null);

        if (admin == null) {

            admin = new User();

            admin.setName("Civentre Admin");
            admin.setEmail("admin@civentre.com");
            admin.setPassword(
                    passwordEncoder.encode("Admin@12345")
            );
            admin.setRole(User.Role.ADMIN);
            admin.setPhone("9000000001");
            admin.setActive(true);

            userRepository.save(admin);
        }


        // ============================================================
        // CREATE / UPDATE DEPARTMENTS
        // ============================================================

        Department roadsDepartment =
                departmentRepository
                        .findByName("Roads & Infrastructure")
                        .orElseGet(() -> {

                            Department department = new Department();

                            department.setName(
                                    "Roads & Infrastructure"
                            );

                            department.setDescription(
                                    "Handles potholes, damaged roads and road-related issues."
                            );

                            department.setActive(true);

                            return departmentRepository.save(department);
                        });


        Department streetlightDepartment =
                departmentRepository
                        .findByName("Streetlight")
                        .orElseGet(() -> {

                            Department department = new Department();

                            department.setName("Streetlight");

                            department.setDescription(
                                    "Handles broken and non-functional streetlights."
                            );

                            department.setActive(true);

                            return departmentRepository.save(department);
                        });


        Department sanitationDepartment =
                departmentRepository
                        .findByName("Sanitation")
                        .orElseGet(() -> {

                            Department department = new Department();

                            department.setName("Sanitation");

                            department.setDescription(
                                    "Handles garbage, waste and cleanliness-related issues."
                            );

                            department.setActive(true);

                            return departmentRepository.save(department);
                        });


        Department waterSupplyDepartment =
                departmentRepository
                        .findByName("Water Supply")
                        .orElseGet(() -> {

                            Department department = new Department();

                            department.setName("Water Supply");

                            department.setDescription(
                                    "Handles water leakage, supply and drainage-related issues."
                            );

                            department.setActive(true);

                            return departmentRepository.save(department);
                        });


        Department trafficDepartment =
                departmentRepository
                        .findByName("Traffic")
                        .orElseGet(() -> {

                            Department department = new Department();

                            department.setName("Traffic");

                            department.setDescription(
                                    "Handles traffic signals and other traffic-related issues."
                            );

                            department.setActive(true);

                            return departmentRepository.save(department);
                        });


        Department parksDepartment =
                departmentRepository
                        .findByName("Parks & Environment")
                        .orElseGet(() -> {

                            Department department = new Department();

                            department.setName("Parks & Environment");

                            department.setDescription(
                                    "Handles parks, fallen trees and environmental issues."
                            );

                            department.setActive(true);

                            return departmentRepository.save(department);
                        });


        // ============================================================
        // OFFICER 1
        // Ravi Kumar → Streetlight
        // ============================================================

        User officer1 =
                userRepository
                        .findByEmail("officer1@civentre.com")
                        .orElse(null);

        if (officer1 == null) {

            officer1 = new User();

            officer1.setName("Ravi Kumar");
            officer1.setEmail("officer1@civentre.com");
            officer1.setPassword(
                    passwordEncoder.encode("Officer@12345")
            );
            officer1.setRole(User.Role.OFFICER);
            officer1.setPhone("9000000002");
            officer1.setActive(true);
        }

        officer1.setDepartment(streetlightDepartment);

        userRepository.save(officer1);


        // ============================================================
        // OFFICER 2
        // Priya Sharma → Roads & Infrastructure
        // ============================================================

        User officer2 =
                userRepository
                        .findByEmail("officer2@civentre.com")
                        .orElse(null);

        if (officer2 == null) {

            officer2 = new User();

            officer2.setName("Priya Sharma");
            officer2.setEmail("officer2@civentre.com");
            officer2.setPassword(
                    passwordEncoder.encode("Officer@12345")
            );
            officer2.setRole(User.Role.OFFICER);
            officer2.setPhone("9000000003");
            officer2.setActive(true);
        }

        officer2.setDepartment(roadsDepartment);

        userRepository.save(officer2);


        // ============================================================
        // OFFICER 3
        // Arjun Reddy → Sanitation
        // ============================================================

        User officer3 =
                userRepository
                        .findByEmail("officer3@civentre.com")
                        .orElse(null);

        if (officer3 == null) {

            officer3 = new User();

            officer3.setName("Arjun Reddy");
            officer3.setEmail("officer3@civentre.com");
            officer3.setPassword(
                    passwordEncoder.encode("Officer@12345")
            );
            officer3.setRole(User.Role.OFFICER);
            officer3.setPhone("9000000004");
            officer3.setActive(true);
        }

        officer3.setDepartment(sanitationDepartment);

        userRepository.save(officer3);


        // ============================================================
        // OFFICER 4
        // Kiran Kumar → Water Supply
        // ============================================================

        User officer4 =
                userRepository
                        .findByEmail("officer4@civentre.com")
                        .orElse(null);

        if (officer4 == null) {

            officer4 = new User();

            officer4.setName("Kiran Kumar");
            officer4.setEmail("officer4@civentre.com");
            officer4.setPassword(
                    passwordEncoder.encode("Officer@12345")
            );
            officer4.setRole(User.Role.OFFICER);
            officer4.setPhone("9000000005");
            officer4.setActive(true);
        }

        officer4.setDepartment(waterSupplyDepartment);

        userRepository.save(officer4);


        // ============================================================
        // OFFICER 5
        // Sneha Reddy → Parks & Environment
        // ============================================================

        User officer5 =
                userRepository
                        .findByEmail("officer5@civentre.com")
                        .orElse(null);

        if (officer5 == null) {

            officer5 = new User();

            officer5.setName("Sneha Reddy");
            officer5.setEmail("officer5@civentre.com");
            officer5.setPassword(
                    passwordEncoder.encode("Officer@12345")
            );
            officer5.setRole(User.Role.OFFICER);
            officer5.setPhone("9000000006");
            officer5.setActive(true);
        }

        officer5.setDepartment(parksDepartment);

        userRepository.save(officer5);


        // ============================================================
        // OFFICER 6
        // Meera Sharma → Traffic
        // ============================================================

        User officer6 =
                userRepository
                        .findByEmail("officer6@civentre.com")
                        .orElse(null);

        if (officer6 == null) {

            officer6 = new User();

            officer6.setName("Meera Sharma");
            officer6.setEmail("officer6@civentre.com");
            officer6.setPassword(
                    passwordEncoder.encode("Officer@12345")
            );
            officer6.setRole(User.Role.OFFICER);
            officer6.setPhone("9000000007");
            officer6.setActive(true);
        }

        officer6.setDepartment(trafficDepartment);

        userRepository.save(officer6);


        // ============================================================
        // FINAL MESSAGE
        // ============================================================

        System.out.println(
                "Officer department assignments initialized successfully."
        );
    }
}