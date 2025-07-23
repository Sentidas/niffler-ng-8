package guru.qa.niffler.page.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.SearchField;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class PeoplePage {

    SearchField searchField = new SearchField();

    private final SelenideElement peopleList = $("#all");

    private final ElementsCollection peopleRows = $("#all").$$("tr");

    @Step("Check outgoing friendship invitation to other user")
    public PeoplePage checkInvitationSentToUser(String username) {
        SelenideElement friendRow = peopleList.$$("tr").find(text(username));
        friendRow.shouldHave(text("Waiting..."));
        return this;
    }

    @Step("Check person is in people_list")
    public void checkPersonIsInPeopleList(String personName) {

        SelenideElement row = peopleRows.findBy(text(personName));
        searchField.search(personName);
        peopleList.shouldHave(text(personName));
    }
}
