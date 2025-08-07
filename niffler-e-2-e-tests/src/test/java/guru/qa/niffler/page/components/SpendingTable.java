package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SpendingTable extends BaseComponent<SpendingTable> {

    SearchField searchField = new SearchField();
    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement deleteBtm = $("#delete"),
            searchPanel = $("input[placeholder=Search]"),

    deleteDialogBtm = $("div[role='dialog']").$(byText("Delete"));

    public SpendingTable() {
        super($("#spendings"));
    }


    public void editSpend(String description) {
        SelenideElement row = tableRows.findBy(text(description))
                .shouldBe(visible);

        row.$("button[aria-label='Edit spending']").click();
    }

    public void deleteSpend(String category, String description) {
        selectSpend(category, description);
        confirmDeleteSpend();
        verifySpendDelete(category, description);
    }

    private void selectSpend(String category, String description) {
        SelenideElement row = tableRows
                .filterBy(text(category))
                .filterBy(text(description))
                .shouldBe(sizeGreaterThan(0))
                .first();

        row.$("input[type='checkbox']").click();
    }

    private void confirmDeleteSpend() {
        deleteBtm.click();
        deleteDialogBtm.click();
    }

    private void verifySpendDelete(String category, String description) {
        tableRows.filterBy(text(category))
                .filterBy(text(description))
                .shouldHave(size(0), Duration.ofSeconds(3));
    }

    @Step("Check table contains spending")
    public void checkTableContains(String expectedSpend) {
        SelenideElement row = tableRows.findBy(text(expectedSpend));
        searchField.search(expectedSpend);

        tableRows.find(text(expectedSpend))
                .should(visible);
    }

    @Step("Check table contains spending with date")
    public void checkTableContainsWithData(String expectedSpend, LocalDate date) {
        SelenideElement row = tableRows.findBy(text(expectedSpend));
        searchField.search(expectedSpend);

        tableRows.find(text(expectedSpend))
                .should(visible);

        String expectedDate = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH));
        row.$$("td span").findBy(text(expectedDate)).shouldBe(visible);
    }
}
