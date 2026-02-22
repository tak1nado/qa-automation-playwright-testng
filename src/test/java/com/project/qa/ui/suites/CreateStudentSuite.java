package com.project.qa.ui.suites;

import com.project.qa.runners.CucumberTestsRunner;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;

@Test(groups = "ui")
@CucumberOptions(
        features = "classpath:features/student",
        glue = {"com.project.qa.steps", "com.project.qa.runners"},
        plugin = {
                "pretty",
                "html:target/cucumber-html-reports/report.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class CreateStudentSuite extends CucumberTestsRunner {
}
