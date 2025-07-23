package guru.qa.niffler.page.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class BasePage <T extends BasePage<?>> {

    private final SelenideElement alert = $(".MuiAlert-message");

    public T checkAlertMessage(String text) {
        alert.shouldHave(text(text));
        return (T) this;
    }
}
