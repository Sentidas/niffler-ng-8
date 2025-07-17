package guru.qa.niffler.page.pages;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.page.usercontext.ExpectedUserContext;
import guru.qa.niffler.page.model.SpendEdit;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {
    private ExpectedUserContext userContext;
    private String originalDescription;
    private String newDescription;
    private Double newAmount;
    private String newCategory;


    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement submitBtn = $("#save");
    private final SelenideElement categoryList = $("ul.MuiList-padding");

    public EditSpendingPage(String originalDescription) {
        this.originalDescription = originalDescription;
    }

    public EditSpendingPage() {
    }

    public EditSpendingPage editDescription(String newDescription) {
        this.newDescription = newDescription;
        descriptionInput.clear();
        descriptionInput.setValue(newDescription);
        return this;
    }

    public EditSpendingPage editAmount(Double amount) {
        this.newAmount = amount;
        amountInput.click();
        amountInput.clear();
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    public EditSpendingPage editCategory(String name) throws InterruptedException {
        this.newCategory = name;
        categoryList.$(byText(name)).click();
        Thread.sleep(4000);
        categoryList.$$("li .MuiChip-root")
                .findBy(text(name))
                .shouldHave(cssClass("MuiChip-filledPrimary"));
        return this;
    }

    public void save() {
        submitBtn.click();
    }

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
