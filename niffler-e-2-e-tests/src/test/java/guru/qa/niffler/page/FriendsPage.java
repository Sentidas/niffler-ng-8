package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {

    private final SelenideElement friendsList = $("#friends"),
            requestsList = $("#requests"),
            peopleList = $("#all"),
            friendsPanel = $("#simple-tabpanel-friends"),
            allPeopleBar = $(byText("All people"));


    public void checkFriendIsInFriendsList(String friendName) {
        friendsList.shouldHave(text(friendName));
    }

    public void checkIncomingInvitationVisible(String requestName) {
        requestsList.shouldHave(text(requestName));
    }

    public void checkOutgoingInvitationHasWaitingStatus(String outcomeInvitationName) {
        peopleList.$$("tr")
                .findBy(text(outcomeInvitationName))
                .shouldHave(text("Waiting..."));
    }

    public void checkNoOutgoingInvitationsPresent() {
        peopleList.shouldNotHave(text("Waiting..."));
    }

    public void checkFriendsListIsEmpty() {
        friendsPanel
                .shouldHave(text("There are no users yet"));
    }

    public void goToAllPeopleBar() {
        allPeopleBar.click();

    }
}
