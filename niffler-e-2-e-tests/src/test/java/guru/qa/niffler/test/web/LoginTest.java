package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.pages.LoginPage;
import guru.qa.niffler.page.pages.MainPage;
import org.junit.jupiter.api.Test;

public class LoginTest {
    public static Config CFG = Config.getInstance();

    @Test
    void shouldLoginSuccessfully() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUserName("duck")
                .setPassword("12345")
                .submitLogin();

        new MainPage().checkHistoryOfSpendingIsVisible()
                .checkStatisticsIsVisible()
                .checkToolBarIsVisible();
    }

    @Test
    void loginShouldFailWithWrongCredentials() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUserName("raven")
                .setPassword("1234")
                .submitLogin();
        new LoginPage().checkErrorMessage("Неверные учетные данные пользователя");
    }

    @Test
    void passwordShouldBeVisibleAfterClickOnVisibilityToggle() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setPassword("12345")
                .clickPasswordVisibilityButton()
                .shouldShowPasswordInPlainText();
    }

    @Test
    void shouldRedirectToRegisterPage() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccountLink()
                .shouldBeOnRegisterPage();
    }
}
