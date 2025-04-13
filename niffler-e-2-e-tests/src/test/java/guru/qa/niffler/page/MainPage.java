package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {

    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement statisticsSection = $("#stat"),
            spendingSection = $("#spendings"),
            newSpendingButton = $(byText("New spending")),
            profileButton = $("button[aria-label=Menu]"),
            profileLink = $("a[href='/profile']"),
            friendsLink = $("a[href='/people/friends']"),
            peopleLink = $("a[href='/people/all']");



    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    public MainPage openAvatarMenu() {
        profileButton.click();
        return this;
    }

    public ProfilePage goToProfilePage() {
        profileLink.click();
        return new ProfilePage();
    }

    public FriendsPage goToFriendsPage() {
        friendsLink.click();
        return new FriendsPage();
    }

    public FriendsPage goToPeoplePage() {
        peopleLink.click();
        return new FriendsPage();
    }


    public void checkThatTableContains(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .should(visible);
    }

    public MainPage checkStatisticsIsVisible() {
        statisticsSection.shouldBe(visible);
        return this;
    }

    public MainPage checkHistoryOfSpendingIsVisible() {
        spendingSection.should(visible);
        return this;
    }

    public MainPage checkToolBarIsVisible() {
        newSpendingButton.shouldBe(visible);
        profileButton.shouldBe(visible);
        return this;
    }

}
