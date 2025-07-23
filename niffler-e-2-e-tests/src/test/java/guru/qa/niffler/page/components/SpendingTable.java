package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.model.DataFilterValues;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendingTable {

    SearchField searchField = new SearchField();

    private final SelenideElement self = $("#spendings");

    private final SelenideElement deleteBtm = self.$("#delete"),
            period = self.$("#period"),
            periodList = self.$("ul[role=listbox]"),
            deleteDialogBtn = $("div[role='dialog']").$(byText("Delete"));


    private final ElementsCollection tableRows = self.$$("tbody tr"),
            periodOptions = periodList.$$("li[role=option]");


    @Step("Edit spending with description {0}")
    public void editSpend(String description) {
        SelenideElement row = tableRows.findBy(text(description))
                .shouldBe(visible);

        row.$("button[aria-label='Edit spending']").click();
    }

    @Step("Delete spending with description {0}")
    public void deleteSpend(String description) {
        selectSpend(description);
        confirmDeleteSpend();
        verifySpendDelete(description);
    }
    @Step("Delete spending with category {0} and description {1}")
    public void deleteSpend(String category, String description) {
        selectSpend(category, description);
        confirmDeleteSpend();
        verifySpendDelete(category, description);
    }


    @Step("Select period {0}")
    public SpendingTable selectPeriod(DataFilterValues period) {
        this.period.click();
        periodOptions.findBy(text(period.getUiText())).click();
        return this;
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

    @Step("Check table contains spendings")
    public void checkTableContains(String... expectedSpends) {
        for (String expectedSpend : expectedSpends) {
            checkTableContains(expectedSpend);
        }
    }

    @Step("Check table size is {0}")
    public void checkTableSize(int expectedSize) {
        assertEquals(tableRows.size(), expectedSize);
    }

    @Step("Search spending with description {0}")
    public void selectSpend(String description) {
        SelenideElement row = tableRows
                .filterBy(text(description))
                .shouldBe(sizeGreaterThan(0))
                .first();

        row.$("input[type='checkbox']").click();
    }

    @Step("Search spending with category {0} and description {1}")
    public void selectSpend(String category, String description) {
        SelenideElement row = tableRows
                .filterBy(text(category))
                .filterBy(text(description))
                .shouldBe(sizeGreaterThan(0))
                .first();

        row.$("input[type='checkbox']").click();
    }

    @Step("Confirm delete Spend in alert")
    public void confirmDeleteSpend() {
        deleteBtm.click();
        deleteDialogBtn.click();
    }

    private void verifySpendDelete(String description) {
        tableRows.filterBy(text(description))
                .shouldHave(size(0), Duration.ofSeconds(3));
    }

    private void verifySpendDelete(String category, String description) {
        tableRows.filterBy(text(category))
                .filterBy(text(description))
                .shouldHave(size(0), Duration.ofSeconds(3));
    }
}
