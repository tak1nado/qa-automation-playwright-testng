package com.project.qa.storefront.page_blocks;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import com.project.qa.helpers.web.page.UIComponent;
import io.qameta.allure.Step;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class StudentRegistrationFormModal extends UIComponent {
    private final Supplier<Locator> modalContainer = () -> getActor().getByRole(AriaRole.DIALOG);
    private final Supplier<Locator> studentFullNameLabel = () -> modalContainer.get().locator("tr:has-text('Student Name')").locator("td").nth(1);
    private final Supplier<Locator> studentEmailLabel = () -> modalContainer.get().locator("tr:has-text('Student Email')").locator("td").nth(1);
    private final Supplier<Locator> studentGenderLabel = () -> modalContainer.get().locator("tr:has-text('Gender')").locator("td").nth(1);
    private final Supplier<Locator> studentMobileLabel = () -> modalContainer.get().locator("tr:has-text('Mobile')").locator("td").nth(1);
    private final Supplier<Locator> studentDateOfBirthLabel = () -> modalContainer.get().locator("tr:has-text('Date of Birth')").locator("td").nth(1);
    private final Supplier<Locator> studentSubjectsLabel = () -> modalContainer.get().locator("tr:has-text('Subjects')").locator("td").nth(1);
    private final Supplier<Locator> studentHobbiesLabel = () -> modalContainer.get().locator("tr:has-text('Hobbies')").locator("td").nth(1);
    private final Supplier<Locator> studentPictureLabel = () -> modalContainer.get().locator("tr:has-text('Picture')").locator("td").nth(1);
    private final Supplier<Locator> studentAddressLabel = () -> modalContainer.get().locator("tr:has-text('Address')").locator("td").nth(1);
    private final Supplier<Locator> studentStateCityLabel = () -> modalContainer.get().locator("tr:has-text('State and City')").locator("td").nth(1);
    private final Supplier<Locator> closeButton = () -> modalContainer.get().getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Close"));;

    @Step("Get full name")
    public String getFullName() {
        return studentFullNameLabel.get().textContent();
    }

    @Step("Get email")
    public String getEmailField() {
        return studentEmailLabel.get().textContent();
    }

    @Step("Get gender")
    public String getGender() {
        return studentGenderLabel.get().textContent();
    }

    @Step("Get phone number")
    public String getPhoneNumber() {
        return studentMobileLabel.get().textContent();
    }

    @Step("Get date of birth")
    public String getDateOfBirth() {
        return studentDateOfBirthLabel.get().textContent();
    }

    @Step("Get subjects")
    public List<String> getSubjects() {
        return Arrays.stream(studentSubjectsLabel.get().textContent().split(",\\s*"))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    @Step("Get hobbies")
    public List<String> getHobbies() {
        return Arrays.stream(studentHobbiesLabel.get().textContent().split(",\\s*"))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    @Step("Get picture name")
    public String getPictureName() {
        return studentPictureLabel.get().textContent();
    }

    @Step("Get address")
    public String getAddress() {
        return studentAddressLabel.get().textContent();
    }

    @Step("Get state and city")
    public String getStateCity() {
        return studentStateCityLabel.get().textContent();
    }

    @Step("Close Student submission pop-up")
    public void closeModal() {
        closeButton.get().click();
    }

    public boolean isOpened() {
        return modalContainer.get().isVisible();
    }
}
