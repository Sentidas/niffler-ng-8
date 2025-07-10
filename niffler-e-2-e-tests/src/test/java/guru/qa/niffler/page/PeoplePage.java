package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class PeoplePage {

    private final SelenideElement peopleTab = $("a[href='/people/friends']"),
            allTab = $("a[href='/people/all']"),
            searchPanel = $("input[placeholder=Search]"),
            peopleList = $("#all");
    private final ElementsCollection peopleRows = $("#all").$$("tr");

    public PeoplePage checkInvitationSentToUser(String username) {
        SelenideElement friendRow = peopleList.$$("tr").find(text(username));
        friendRow.shouldHave(text("Waiting..."));
        return this;
    }

    public void checkPersonIsInPeopleList(String personName) {

        SelenideElement row = peopleRows.findBy(text(personName));
        if (!row.exists()) {
            searchPanel.click();
            searchPanel.setValue(personName).pressEnter();
            peopleList.shouldHave(text(personName));
        }
    }
}
