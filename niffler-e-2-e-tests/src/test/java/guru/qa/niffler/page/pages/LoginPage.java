package guru.qa.niffler.page.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage {

    private final SelenideElement usernameInput = $("input[name='username']"),
            passwordInput = $("input[name='password']"),
            submitButton = $("button[type='submit']"),
            createAccountLink = $("a.form__register"),
            errorMessageText = $("p.form__error"),
            togglePasswordVisibility = $("button.form__password-button");

    @Step("Success login with credentials")
    public MainPage successLoginWithCredentials(String username, String password) {
        usernameInput.shouldBe(visible, Duration.ofSeconds(5)).setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage();
    }
    @Step("Set user name in login form")
    public LoginPage setUserName(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Set user password in login form")
    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Click password visibility button in login form")
    public LoginPage clickPasswordVisibilityButton() {
        togglePasswordVisibility.click();
        return this;
    }

    @Step("Click submit button in login form")
    public void submitLogin() {
        submitButton.click();
    }

    @Step("Click create new account link")
    public RegisterPage clickCreateNewAccountLink() {
        createAccountLink.click();
        return new RegisterPage();
    }

    @Step("Check error_message in login form")
    public void checkErrorMessage(String errorMessageText) {
        this.errorMessageText.shouldHave(text(errorMessageText));
    }

    @Step("Check show password in plain text")
    public void shouldShowPasswordInPlainText() {
        togglePasswordVisibility.shouldHave(cssClass("form__password-button_active"));
        passwordInput.shouldHave(attribute("type", "text"));
    }
}
