package guru.qa.niffler.page.pages;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;


public class LoginPage {

    private final SelenideElement usernameInput = $("input[name='username']"),
            passwordInput = $("input[name='password']"),
            submitButton = $("button[type='submit']"),
            createAccountLink = $("a.form__register"),
            errorMessageText = $("p.form__error"),
            togglePasswordVisibility = $("button.form__password-button");

    public MainPage successLoginWithCredentials(String username, String password) {
        usernameInput.shouldBe(visible, Duration.ofSeconds(5)).setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage();
    }

    public LoginPage setUserName(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public LoginPage clickPasswordVisibilityButton() {
        togglePasswordVisibility.click();
        return this;
    }

    public void submitLogin() {
        submitButton.click();
    }

    public RegisterPage clickCreateNewAccountLink() {
        createAccountLink.click();
        return new RegisterPage();
    }

    public void checkErrorMessage(String errorMessageText) {
        this.errorMessageText.shouldHave(text(errorMessageText));
    }

    public void shouldShowPasswordInPlainText() {
        togglePasswordVisibility.shouldHave(cssClass("form__password-button_active"));
        passwordInput.shouldHave(attribute("type", "text"));
    }
}
