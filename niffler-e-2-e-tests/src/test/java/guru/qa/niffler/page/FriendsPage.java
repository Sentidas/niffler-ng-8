package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {

    private final SelenideElement friendsList = $("#friends"),
            requestsList = $("#requests"),
            peopleList = $("#all"),
            friendsPanel = $("#simple-tabpanel-friends"),
            cleanBtn = $("#input-clear"),
            searchPanel = $("input[placeholder=Search]"),
            allPeopleBar = $(byText("All people"));
    private final ElementsCollection friendRow = friendsList.$$("tr");


    public void checkFriendIsInFriendsList(String friendName) {
            searchPanel.click();
            searchPanel.setValue(friendName).pressEnter();
            friendsList.shouldHave(text(friendName));
            cleanBtn.click();

    }


    public void checkPersonIsInPeopleList(String friendName) {
        SelenideElement row = friendRow.findBy(text(friendName));
        if (!row.exists()) {
            searchPanel.click();
            searchPanel.setValue(friendName).pressEnter();
            friendsList.shouldHave(text(friendName));
        }
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
