package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.pages.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

public class RegistrationTest {

    private static final Config CFG = Config.getInstance();
    private static final String existingUsername = "Catty";
    private static final String password = "12345";

    @Test
    void shouldRegisterNewUser() {

        String uniqueUsername = RandomDataUtils.randomUsername();
        System.out.println("Сгенерированное имя: " + uniqueUsername);

        open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccountLink()
                .setUserName(uniqueUsername)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkSuccessRegistration();
    }

    @Test
    void shouldNotRegisterUserIfNameExists() {

        open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccountLink()
                .setUserName(existingUsername)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkErrorMessage("Username `" + existingUsername + "` already exists");
    }

    @Test
    void shouldNotRegisterUserIfPasswordsAreNotEqual() {

        open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccountLink()
                .setUserName(existingUsername)
                .setPassword(password)
                .setPasswordSubmit("1234")
                .submitRegistration()
                .checkErrorMessage("Passwords should be equal");
    }

    @Test
    void shouldShowPasswordInPlainText() {

        open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccountLink()
                .setUserName(existingUsername)
                .setPassword(password)
                .togglePasswordVisibility()
                .shouldBeVisibleInputPassword();
    }

    @Test
    void shouldShowPasswordConfirmationInPlainText() {

        open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccountLink()
                .setUserName(existingUsername)
                .setPassword(password)
                .setPasswordSubmit(password)
                .togglePasswordSubmitVisibility()
                .shouldBeVisibleInputPasswordSubmit();
    }

    @Test
    void shouldNotRegisterUserIfUsernameTooShort() {
        String shortUserName = "Ca";
        open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccountLink()
                .setUserName(shortUserName)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkErrorMessage("Allowed username length should be from 3 to 50 characters");
    }

    @Test
    void shouldNotRegisterUserIfUsernameTooLong() {
        String longUserName = "m6WLYoyioVWhpwgTK7FytQBNav4Y76jD0dgihDatfpi5QzVVkQP6908p6QuCFDpu";

        open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccountLink()
                .setUserName(longUserName)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkErrorMessage("Allowed username length should be from 3 to 50 characters");
    }

    @Test
    void shouldNotRegisterUserIfPasswordTooShort(){
        String shortPassword = "1";

        open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccountLink()
                .setUserName(existingUsername)
                .setPassword(shortPassword)
                .setPasswordSubmit(shortPassword)
                .submitRegistration()
                .checkErrorMessage("Allowed password length should be from 3 to 12 characters");
    }

    @Test
    void shouldNotRegisterUserIfPasswordTooLong() {
        String longPassword = "123456789988575757575757575";

        open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccountLink()
                .setUserName(existingUsername)
                .setPassword(longPassword)
                .setPasswordSubmit(longPassword)
                .submitRegistration()
                .checkErrorMessage("Allowed password length should be from 3 to 12 characters");
    }
}
