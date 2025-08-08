package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.pages.RegisterPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

public class RegistrationTest {

    private static final Config CFG = Config.getInstance();
    private static final String existingUsername = "catty";
    private static final String password = "12345";


    @Test
    void shouldRegisterNewUser() {

        String username = RandomDataUtils.randomUsername();
        System.out.println("Сгенерированное имя: " + username);

        open(RegisterPage.URL, RegisterPage.class)
                .setUserName(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkSuccessRegistration();
    }

    @Test
    void shouldNotRegisterUserIfNameExists() {

        open(RegisterPage.URL, RegisterPage.class)
                .setUserName(existingUsername)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkErrorMessage("Username `" + existingUsername + "` already exists");
    }

    @Test
    void shouldNotRegisterUserIfPasswordsAreNotEqual() {

        open(RegisterPage.URL, RegisterPage.class)
                .setUserName(existingUsername)
                .setPassword(password)
                .setPasswordSubmit("1234")
                .submitRegistration()
                .checkErrorMessage("Passwords should be equal");
    }

    @Test
    void shouldShowPasswordInPlainText() {

        open(RegisterPage.URL, RegisterPage.class)
                .setUserName(existingUsername)
                .setPassword(password)
                .togglePasswordVisibility()
                .shouldBeVisibleInputPassword();
    }

    @Test
    void shouldShowPasswordConfirmationInPlainText() {

        open(RegisterPage.URL, RegisterPage.class)
                .setUserName(existingUsername)
                .setPassword(password)
                .setPasswordSubmit(password)
                .togglePasswordSubmitVisibility()
                .shouldBeVisibleInputPasswordSubmit();
    }

    @Test
    void shouldNotRegisterUserIfUsernameTooShort() {
        String shortUserName = "Ca";
        open(RegisterPage.URL, RegisterPage.class)
                .setUserName(shortUserName)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkErrorMessage("Allowed username length should be from 3 to 50 characters");
    }

    @Test
    void shouldNotRegisterUserIfUsernameTooLong() throws InterruptedException {
        String longUserName = "m6WLYoyioVWhpwgTK7FytQBNav4Y76jD0dgihDatfpi5QzVVkQP6908p6QuCFDpu";

        open(RegisterPage.URL, RegisterPage.class)
                .setUserName(longUserName)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkErrorMessage("Allowed username length should be from 3 to 50 characters");
    }

    @Test
    void shouldNotRegisterUserIfPasswordTooShort() {
        String shortPassword = "1";

        open(RegisterPage.URL, RegisterPage.class)
                .setUserName(existingUsername)
                .setPassword(shortPassword)
                .setPasswordSubmit(shortPassword)
                .submitRegistration()
                .checkErrorMessage("Allowed password length should be from 3 to 12 characters");
    }

    @Test
    void shouldNotRegisterUserIfPasswordTooLong() {
        String longPassword = "123456789988575757575757575";

        open(RegisterPage.URL, RegisterPage.class)
                .setUserName(existingUsername)
                .setPassword(longPassword)
                .setPasswordSubmit(longPassword)
                .submitRegistration()
                .checkErrorMessage("Allowed password length should be from 3 to 12 characters");
    }
}
