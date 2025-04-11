package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

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


    public RegisterPage setUserName(String userName) {
        userNameInput.setValue(userName);
        return this;
    }

    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage setPasswordSubmit(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    public RegisterPage togglePasswordVisibility() {
        passwordButton.click();
        return this;
    }

    public RegisterPage togglePasswordSubmitVisibility() {
        passwordSubmitButton.click();
        return this;
    }

    public RegisterPage submitRegistration() {
        submitButton.click();
        return new RegisterPage();
    }

    public void checkErrorMessage(String errorMessageText) {
        errorField.shouldHave(text(errorMessageText));
    }


    public void checkSuccessRegistration() {
        titleSuccessRegistration.shouldHave(text("Congratulations! You've registered!"));
        goToLoginButton.shouldBe(visible);
    }

    public void shouldBeVisibleInputPassword() {
        passwordButton.shouldHave(cssClass("form__password-button_active"));
        passwordInput.shouldHave(attribute("type", "text"));
    }

    public void shouldBeOnRegisterPage() {
        registrationTitleLabel.shouldBe(visible);
    }

    public void shouldBeVisibleInputPasswordSubmit() {
        passwordSubmitButton.shouldHave(cssClass("form__password-button_active"));
        passwordSubmitInput.shouldHave(attribute("type", "text"));
    }
}
