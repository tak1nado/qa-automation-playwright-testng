package com.project.qa.storefront.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.project.qa.storefront.StorefrontBasePage;
import com.project.qa.helpers.models.student.*;
import io.qameta.allure.Step;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

@Component
public class StudentRegistrationPage extends StorefrontBasePage {
    private static final String PAGE_URL = "automation-practice-form";
    //locators
    private final Supplier<Locator> firstNameField = () -> getActor().getByPlaceholder("First Name");
    private final Supplier<Locator> lastNameField = () -> getActor().getByPlaceholder("Last Name");
    private final Supplier<Locator> emailField = () -> getActor().locator("#userEmail");
    private final Supplier<Locator> genderBlock = () -> getActor().locator("#genterWrapper");
    private final Supplier<Locator> mobileField = () -> getActor().getByPlaceholder("Mobile Number");
    private final Supplier<Locator> dateOfBirthField = () -> getActor().locator("#dateOfBirth");
    private final Supplier<Locator> dateOfBirthYearSelect = () -> getActor().locator(".react-datepicker__year-select");
    private final Supplier<Locator> dateOfBirthMonthSelect = () -> getActor().locator(".react-datepicker__month-select");
    private static final String DATE_OF_BIRTH_FORM_DAY_XPATH = "//div[contains(@class, 'react-datepicker__day') and not(contains(@class, 'outside-month')) and text()='%s']";
    private final Supplier<Locator> subjectsField = () -> getActor().locator("#subjectsContainer");
    private final Supplier<Locator> hobbiesBlock = () -> getActor().locator("#hobbiesWrapper");
    private final Supplier<Locator> uploadPictureInput = () -> getActor().locator("#uploadPicture");
    private final Supplier<Locator> currentAddressField = () -> getActor().getByPlaceholder("Current Address");
    private final Supplier<Locator> cityDropDown = () -> getActor().locator("#city");
    private final Supplier<Locator> stateDropDown = () -> getActor().locator("#state");
    private final Supplier<Locator> submitButton = () -> getActor().getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));

    @Step("Fill in firstname field with: {0}")
    public void fillInFirstNameField(String firstName) {
        firstNameField.get().fill(firstName);
    }

    @Step("Fill in lastname field with: {0}")
    public void fillInLastNameField(String lastName) {
        lastNameField.get().fill(lastName);
    }

    @Step("Fill in email field with: {0}")
    public void fillInEmailField(String email) {
        emailField.get().fill(email);
    }

    @Step("Select gender: {0}")
    public void selectGenderRadiobutton(Gender gender) {
        genderBlock.get()
                .getByRole(AriaRole.RADIO, new Locator.GetByRoleOptions().setName(gender.genderText).setExact(true))
                .check();
    }

    @Step("Fill in phone number field with: {0}")
    public void fillInPhoneNumberField(String phoneNumber) {
        mobileField.get().fill(phoneNumber);
    }

    @Step("Select date of birth: {0}")
    public void selectDateOfBirth(LocalDate dateOfBirth) {
        dateOfBirthField.get().click();
        selectDate(dateOfBirth);
    }

    private void selectDate(LocalDate targetDate) {
        String targetYear = String.valueOf(targetDate.getYear());
        String targetMonth = targetDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String targetDay = String.valueOf(targetDate.getDayOfMonth());
        //Select year
        dateOfBirthYearSelect.get().selectOption(targetYear);
        //Select month two options
//        String targetMonthIndex = String.valueOf(targetDate.getMonthValue() - 1);
//        dateOfBirthMonthSelect.get().selectOption(targetMonthIndex);
        dateOfBirthMonthSelect.get().selectOption(targetMonth);
        //Select day
        String finalDayLocator = String.format(DATE_OF_BIRTH_FORM_DAY_XPATH, targetDay);
        getActor().locator(finalDayLocator).click();
    }

    @Step("Select subjects: {0}")
    public void selectSubjects(List<Subject> subjects) {
        subjects.forEach(this::selectSubject);
    }

    @Step("Select subject: {0}")
    public void selectSubject(Subject subject) {
        subjectsField.get().fill(subject.subjectText);
        subjectsField.get().getByText(subject.subjectText).click();
    }

    @Step("Select hobbies: {0}")
    public void selectHobbiesCheckbox(List<Hobby> hobbies) {
        hobbies.forEach(this::selectHobbyCheckbox);
    }

    @Step("Select hobby: {0}")
    public void selectHobbyCheckbox(Hobby hobby) {
        hobbiesBlock.get().getByLabel(hobby.hobbyText).check();
    }

    @Step("Upload picture")
    public void uploadPicture(File file) {
        uploadPictureInput.get().setInputFiles(Paths.get(file.getAbsolutePath()));
    }

    @Step("Fill in address field with: {0}")
    public void fillInAddress(String address) {
        currentAddressField.get().fill(address);
    }

    @Step("Select city: {0}")
    public void selectCity(City city) {
        cityDropDown.get().click();
        cityDropDown.get().getByText(city.cityText, new Locator.GetByTextOptions().setExact(true)).click();
    }

    @Step("Select state: {0}")
    public void selectState(State state) {
        stateDropDown.get().click();
        stateDropDown.get().getByText(state.stateText, new Locator.GetByTextOptions().setExact(true)).click();
    }

    @Step("Click on Submit button")
    public void clickOnSubmitButton() {
        submitButton.get().click();
    }

    @Override
    public String getPageUrl() {
        return storefrontCockpit.getBaseUrl() + PAGE_URL;
    }
}