package guru.qa.niffler.page.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class RegisterPage {

    private final SelenideElement userNameInput = $("#username"),
            passwordInput = $("#password"),
            passwordButton = $("#passwordBtn"),
            passwordSubmitInput = $("#passwordSubmit"),
            passwordSubmitButton = $("#passwordSubmitBtn"),
            submitButton = $("button.form__submit"),
            errorField = $("span.form__error"),
            titleSuccessRegistration = $("p.form__paragraph_success"),
            registrationTitleLabel = $(byText("Sign up")),
            goToLoginButton = $("a.form_sign-in");


    @Step("Set user name in register form")
    public RegisterPage setUserName(String userName) {
        userNameInput.setValue(userName);
        return this;
    }

    @Step("Set user password in register form")
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Set user password submit in register form")
    public RegisterPage setPasswordSubmit(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    @Step("Set user password submit in register form")
    public RegisterPage togglePasswordVisibility() {
        passwordButton.click();
        return this;
    }

    @Step("Select toggle for visibility user password submit")
    public RegisterPage togglePasswordSubmitVisibility() {
        passwordSubmitButton.click();
        return this;
    }

    @Step("Submit registration")
    public RegisterPage submitRegistration() {
        submitButton.click();
        return new RegisterPage();
    }

    @Step("Check error_message in register form")
    public void checkErrorMessage(String errorMessageText) {
        errorField.shouldHave(text(errorMessageText));
    }

    @Step("Check success registration")
    public void checkSuccessRegistration() {
        titleSuccessRegistration.shouldHave(text("Congratulations! You've registered!"));
        goToLoginButton.shouldBe(visible);
    }

    @Step("Check visibility input password")
    public void shouldBeVisibleInputPassword() {
        passwordButton.shouldHave(cssClass("form__password-button_active"));
        passwordInput.shouldHave(attribute("type", "text"));
    }

    @Step("Check location on register form submit")
    public void shouldBeOnRegisterPage() {
        registrationTitleLabel.shouldBe(visible);
    }

    @Step("Check visibility input password")
    public void shouldBeVisibleInputPasswordSubmit() {
        passwordSubmitButton.shouldHave(cssClass("form__password-button_active"));
        passwordSubmitInput.shouldHave(attribute("type", "text"));
    }
}
