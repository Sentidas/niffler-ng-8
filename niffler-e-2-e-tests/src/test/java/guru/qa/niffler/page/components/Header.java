package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Header {

    private final SelenideElement self = $("header div.MuiToolbar-root"),
            mainLink = self.$("a[href='/main']"),
            profileLink = $("a[href='/profile']"),
            friendsLink = $("a[href='/people/friends']"),
            peopleLink = $("a[href='/people/all']"),
            profileBtn = self.$("button[aria-label=Menu]"),
            newSpendingBtn = self.$("a[href='/spending']");
    ;

    @Step("Open user menu")
    public void openAvatarMenu() {
        profileBtn.shouldBe(visible).click();
    }

    @Step("Go to Main page")
    public void toMainPage() {
        mainLink.click();
    }

    @Step("Go to Profile page")
    public void toProfilePage() {
        profileLink.shouldBe(visible).click();
    }

    @Step("Go to Friends page")
    public void toFriendsPage() {
        friendsLink.shouldBe(visible).click();
    }

    @Step("Go to People page")
    public void toPeoplePage() {
        peopleLink.shouldBe(visible).click();
    }

    @Step("Check spend and avatar buttons in Header")
    public void checkToolBarIsVisible() {
        newSpendingBtn.shouldBe(visible);
        profileBtn.shouldBe(visible);
    }
}
