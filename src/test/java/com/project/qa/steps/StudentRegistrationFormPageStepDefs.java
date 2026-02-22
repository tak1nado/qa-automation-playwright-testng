package com.project.qa.steps;

import com.project.qa.helpers.managers.student.StudentsManager;
import com.project.qa.helpers.models.student.StudentData;
import com.project.qa.storefront.page_blocks.StudentRegistrationFormModal;
import com.project.qa.storefront.pages.StudentRegistrationPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentRegistrationFormPageStepDefs extends AbstractStepDefs {
    @Autowired private StudentRegistrationPage studentRegistrationPage;
    @Autowired private StudentRegistrationFormModal studentRegistrationFormModal;
    @Autowired private StudentsManager studentsManager;

    @Given("Open Student Registration Form page.")
    public void openStudentRegistrationFormPage() {
        studentRegistrationPage.open();
    }

    @When("Fill in Student Registration form on Student Registration Form page.")
    public void fillInStudentRegistrationFormOnStudentRegistrationFormPage() {
        StudentData studentData = threadVarsHashMap.get(TestKeyword.STUDENT_DATA);
        studentRegistrationPage.fillInFirstNameField(studentData.getFirstname());
        studentRegistrationPage.fillInLastNameField(studentData.getLastname());
        studentRegistrationPage.fillInEmailField(studentData.getEmail());
        studentRegistrationPage.selectGenderRadiobutton(studentData.getGender());
        studentRegistrationPage.fillInPhoneNumberField(studentData.getPhoneNumber());
        studentRegistrationPage.selectDateOfBirth(studentData.getBirthDay());
        studentRegistrationPage.selectSubjects(studentData.getSubjects());
        studentRegistrationPage.selectHobbiesCheckbox(studentData.getHobbies());
        studentRegistrationPage.uploadPicture(studentData.getPicture());
        studentRegistrationPage.fillInAddress(studentData.getAddress());
        studentRegistrationPage.selectState(studentData.getState());
        studentRegistrationPage.selectCity(studentData.getCity());
    }

    @And("Click on Submit button on Student Registration Form page.")
    public void clickOnSubmitButtonOnStudentRegistrationFormPage() {
        studentRegistrationPage.clickOnSubmitButton();
        //TODO create student instance to track it in framework and delete after tests
    }

    @Then("Check that Student Registration submission pop-up is opened.")
    public void checkThatStudentRegistrationSubmissionPopUpIsOpened() {
        assertThat(studentRegistrationFormModal.isOpened())
                .as("Student Submission pop-up should be shown")
                .isTrue();
    }

    @And("Check that student data is correct in Student Registration submission pop-up.")
    public void checkThatStudentDataIsCorrectInStudentRegistrationSubmissionPopUp() {
        StudentData studentData = threadVarsHashMap.get(TestKeyword.STUDENT_DATA);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(studentRegistrationFormModal.getFullName())
                .as("Firstname and lastname data should be equal.")
                .isEqualTo(String.format("%s %s", studentData.getFirstname(), studentData.getLastname()));
        softly.assertThat(studentRegistrationFormModal.getEmailField())
                .as("Email data should be equal.")
                .isEqualTo(studentData.getEmail());
        softly.assertThat(studentRegistrationFormModal.getGender())
                .as("Gender data should be equal.")
                .isEqualTo(studentData.getGender().genderText);
        softly.assertThat(studentRegistrationFormModal.getPhoneNumber())
                .as("Phone number data should be equal.")
                .isEqualTo(studentData.getPhoneNumber());
        softly.assertThat(reformatDate(studentRegistrationFormModal.getDateOfBirth()))
                .as("Date od birth data should be equal.")
                .isEqualTo(studentData.getBirthDayString());
        softly.assertThat(studentRegistrationFormModal.getSubjects())
                .as("Subjects data should be equal.")
                .isEqualTo(studentData.getSubjects().stream().map(s -> s.subjectText).collect(Collectors.toList()));
        softly.assertThat(studentRegistrationFormModal.getHobbies())
                .as("Hobbies data should be equal.")
                .isEqualTo(studentData.getHobbies().stream().map(hobby -> hobby.hobbyText).collect(Collectors.toList()));
        softly.assertThat(studentRegistrationFormModal.getPictureName())
                .as("Picture name should be equal.")
                .isEqualTo(studentData.getPicture().getName());
        softly.assertThat(studentRegistrationFormModal.getAddress())
                .as("Address data should be equal.")
                .isEqualTo(studentData.getAddress());
        softly.assertThat(studentRegistrationFormModal.getStateCity())
                .as("State and City data should be equal.")
                .isEqualTo(String.format("%s %s", studentData.getState().stateText, studentData.getCity().cityText));
        softly.assertAll();
    }

    //TODO move to StringUtils
    public static String reformatDate(String actualDateString) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd MMMM,yyyy", Locale.ENGLISH);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(actualDateString, inputFormatter);
        return date.format(outputFormatter);
    }
}
