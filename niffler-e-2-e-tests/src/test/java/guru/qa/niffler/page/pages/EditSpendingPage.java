package guru.qa.niffler.page.pages;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.page.components.Calendar;
import guru.qa.niffler.page.model.SpendEdit;
import guru.qa.niffler.page.usercontext.ExpectedUserContext;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage {

    private String originalDescription;
    private String newDescription;
    private Double newAmount;
    private String newCategory;

    private final Calendar calendar = new Calendar();

    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement submitBtn = $("#save");
    private final SelenideElement categoryList = $("ul.MuiList-padding");
    private final SelenideElement selectCalendarBtn = $("button img[alt=Calendar]");
    private final SelenideElement inputCalendar = $("input[name=date]");

    public EditSpendingPage(String originalDescription) {
        this.originalDescription = originalDescription;
    }

    public EditSpendingPage() {
    }

   @Step("Set spend description")
    public EditSpendingPage setSpendDescription(String newDescription) {
        this.newDescription = newDescription;
        descriptionInput.clear();
        descriptionInput.setValue(newDescription);
        return this;
    }
    @Step("Set spend amount")
    public EditSpendingPage setSpendAmount(Double amount) {
        this.newAmount = amount;
        amountInput.click();
        amountInput.clear();
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    @Step("Select spend date")
    public EditSpendingPage selectDate(LocalDate date) throws InterruptedException {
        selectCalendarBtn.click();
        calendar.selectDateInCalendar(date);
        return this;
    }

    @Step("Select category in spend")
    public EditSpendingPage selectCategory(String name) {
        this.newCategory = name;
        categoryList.$(byText(name)).click();

        categoryList.$$("li .MuiChip-root")
                .findBy(text(name))
                .shouldHave(cssClass("MuiChip-filledPrimary"));
        return this;
    }

    @Step("Save changes spend")
    public void save() {
        submitBtn.click();
    }

    @Step("Save changes spend")
    public void save(ExpectedUserContext userContext) {
        submitBtn.click();

        if (userContext != null) {
            userContext.applySpendEdit(getEditData());
        }
    }

    private SpendEdit getEditData() {
        return new SpendEdit(
                originalDescription,
                newDescription,
                newAmount,
                null, // newCurrency
                null, // newDate
                newCategory != null
                        ? new CategoryJson(null, newCategory, null, false, null)
                        : null
        );
    }
}
