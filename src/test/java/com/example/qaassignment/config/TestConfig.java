package com.example.qaassignment.config;

import com.example.qaassignment.QaAssignmentApplicationTests;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = QaAssignmentApplicationTests.class)
public class TestConfig {
}
