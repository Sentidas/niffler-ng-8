package guru.qa.niffler.page.pages;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class BasePage <T extends BasePage<?>> {

    protected static final Config CFG = Config.getInstance();

    private final SelenideElement alert = $(".MuiAlert-message");

    @Step("Check alert message with text '{0}'")
    @Nonnull
    public T checkAlertMessage(String expectedText) throws InterruptedException {
        alert.should(visible).shouldHave(text(expectedText));
        Thread.sleep(3000);
        return (T) this;
    }
}
