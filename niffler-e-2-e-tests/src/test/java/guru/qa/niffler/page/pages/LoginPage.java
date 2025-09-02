package guru.qa.niffler.page.pages;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {

    public static final String URL = CFG.authUrl() + "login";

    private final SelenideElement usernameInput;
    private final SelenideElement passwordInput;
    private final SelenideElement submitButton;
    private final SelenideElement createAccountLink;
    private final SelenideElement errorMessageText;
    private final SelenideElement togglePasswordVisibility;

    public LoginPage(SelenideDriver driver) {
        this.usernameInput = driver.$("input[name='username']");
        this.passwordInput = driver.$("input[name='password']");
        this.submitButton = driver.$("button[type='submit']");
        this.createAccountLink = driver.$("a.form__register");
        this.errorMessageText = driver.$("p.form__error");
        this.togglePasswordVisibility = driver.$("button.form__password-button");
    }

    public LoginPage() {
        this.usernameInput = $("input[name='username']");
        this.passwordInput = $("input[name='password']");
        this.submitButton = $("button[type='submit']");
        this.createAccountLink = $("a.form__register");
        this.errorMessageText = $("p.form__error");
        this.togglePasswordVisibility = $("button.form__password-button");
    }

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
