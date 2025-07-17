package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.pages.LoginPage;
import guru.qa.niffler.page.pages.MainPage;
import org.junit.jupiter.api.Test;

public class LoginTest {
    public static Config CFG = Config.getInstance();

    @User
    void shouldLoginSuccessfully(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password());

        new MainPage().checkHistoryOfSpendingIsVisible()
                .checkStatisticsIsVisible()
                .checkToolBarIsVisible();
    }

    @User
    void loginShouldFailWithWrongCredentials() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUserName("raven")
                .setPassword("1234")
                .submitLogin();
        new LoginPage().checkErrorMessage("Неверные учетные данные пользователя");
    }

    @User
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
