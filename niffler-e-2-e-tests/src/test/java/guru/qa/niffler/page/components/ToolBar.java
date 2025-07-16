package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.pages.FriendsPage;
import guru.qa.niffler.page.pages.PeoplePage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class ToolBar {

    private final PeoplePage peoplePage = new PeoplePage();
    private final FriendsPage friendsPage = new FriendsPage();

    private final SelenideElement profileLink = $("a[href='/profile']"),
            friendsLink = $("a[href='/people/friends']"),
            peopleLink = $("a[href='/people/all']"),
            profileButton = $("button[aria-label=Menu]"),
            newSpendingButton = $(byText("New spending"));
    ;

    public void openAvatarMenu() {
        profileButton.shouldBe(visible).click();
    }

    public void goToProfilePage() {
        profileLink.shouldBe(visible).click();
    }

    public void goToFriendsPage() {
        friendsLink.shouldBe(visible).click();
    }

    public void goToPeoplePage() {
        peopleLink.shouldBe(visible).click();
    }


    public void checkToolBarIsVisible() {
        newSpendingButton.shouldBe(visible);
        profileButton.shouldBe(visible);
    }
}
