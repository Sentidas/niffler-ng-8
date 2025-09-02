package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.converter.BrowserConverter;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.Browser;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.pages.LoginPage;
import guru.qa.niffler.page.pages.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import static guru.qa.niffler.utils.SelenideUtils.chromeConfig;

public class LoginTest {
    public static Config CFG = Config.getInstance();

    @RegisterExtension
    private static final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver chromeDriver = new SelenideDriver(chromeConfig);

    @ParameterizedTest
    @EnumSource(Browser.class)
    void loginShouldFailWithWrongCredentialsInTwoBrowsers(
            @ConvertWith(BrowserConverter.class) SelenideDriver driver
    ) {

        browserExtension.drivers().add(driver);

        driver.open(LoginPage.URL);
        LoginPage page = new LoginPage(driver);

        page.setUserName(RandomDataUtils.randomUsername())
                .setPassword("1234")
                .submitLogin();

        page.checkErrorMessage("Неверные учетные данные пользователя");
    }

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
