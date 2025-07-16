package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SpendingTable {


    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement deleteBtm = $("#delete"),
            searchPanel = $("input[placeholder=Search]"),

    deleteDialogBtm = $("div[role='dialog']").$(byText("Delete"));


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

    public void checkThatSpendTableContains(String spendingDescription) {
        SelenideElement row = tableRows.findBy(text(spendingDescription));
        if (!row.exists()) {
            searchPanel.click();
            searchPanel.setValue(spendingDescription).pressEnter();
            tableRows.find(text(spendingDescription))
                    .should(visible);
        }
    }
}
