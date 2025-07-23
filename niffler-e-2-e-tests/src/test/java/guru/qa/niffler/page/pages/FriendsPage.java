package guru.qa.niffler.page.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.SearchField;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class FriendsPage {

    SearchField searchField = new SearchField();

    private final SelenideElement friendsList = $("#friends"),
            requestsList = $("#requests"),
            peopleList = $("#all"),
            friendsPanel = $("#simple-tabpanel-friends"),
            allPeopleBar = $(byText("All people")),
            acceptDeclineDialog = $("div[role='dialog']"),
            declineDialogBtn = acceptDeclineDialog.$(byText("Decline")),
            alertMessage = $("div.MuiAlert-message");
    private final ElementsCollection friendRow = friendsList.$$("tr");


    @Step("Check present friend in friend_list")
    public void checkFriendIsInFriendsList(String friendName) {
        searchField.search(friendName);
        friendsList.shouldHave(text(friendName));
        searchField.clear();
    }

    @Step("Check absent friend in friend_list")
    public void checkNoPresentFriendIsInFriendsList(String friendName) {
        searchField.search(friendName);
        checkFriendsListIsEmpty();
    }

    @Step("Check incoming friendship invitation")
    public void checkIncomingInvitationVisible(String requestName) {
        requestsList.shouldHave(text(requestName));
    }

    @Step("Check outgoing friendship invitation")
    public void checkOutgoingInvitationHasWaitingStatus(String outcomeInvitationName) {
        peopleList.$$("tr")
                .findBy(text(outcomeInvitationName))
                .shouldHave(text("Waiting..."));
    }

    @Step("Check absent outgoing friendship invitation")
    public void checkNoOutgoingInvitationsPresent() {
        peopleList.shouldNotHave(text("Waiting..."));
    }

    @Step("Check friend_list is empty")
    public void checkFriendsListIsEmpty() {
        friendsPanel
                .shouldHave(text("There are no users yet"));
    }

    @Step("Accept incoming invitation")
    public FriendsPage acceptIncomingInvitation(String username) {
        SelenideElement row = requestsList.$$("tr")
                .findBy(text(username));

        row.$(byText("Accept")).click();
        return this;
    }

    @Step("Decline incoming invitation")
    public FriendsPage declineIncomingInvitation(String username) {
        SelenideElement row = requestsList.$$("tr")
                .findBy(text(username));

        row.$(byText("Decline")).click();
        declineDialogBtn.click();
        return this;
    }

    @Step("Check accept incoming invitation alert_message")
    public FriendsPage checkAcceptAlertMessage(String username) {
        alertMessage.$(byText("Invitation of " + username + " accepted")).shouldHave(visible);
        return this;
    }

    @Step("Check decline incoming invitation alert_message")
    public FriendsPage checkDeclineAlertMessage(String username) {
        alertMessage.$(byText("Invitation of " + username + " is declined")).shouldHave(visible);
        return this;
    }
}
