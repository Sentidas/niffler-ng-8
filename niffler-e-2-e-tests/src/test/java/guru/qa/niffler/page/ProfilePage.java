package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage {

    private final SelenideElement uploadPictureAction = $("span.MuiButtonBase-root").$(byText("Upload new picture")),
            userNameField = $("#username"),
            nameInput = $("#name"),
            saveChangesButton = $("#\\:r7\\:"),
            categoryInput = $("#category"),
            categoryEditButton = $("button[aria-label='Edit category']"),
            categoryArchiveButton = $("button[aria-label='Archive  category']"),
            categoryUnarchiveButton = $("button[aria-label='Unarchive category']"),
            toggleShowArchived = $(".MuiSwitch-switchBase.MuiSwitch-colorPrimary");

    private final ElementsCollection allCategories = $$("div.MuiGrid-container.MuiGrid-spacing-xs-2 div.MuiGrid-item");


    public ProfilePage uploadPicture(String path) {
        uploadPictureAction.uploadFromClasspath(path);
        return this;
    }

    public ProfilePage setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    public ProfilePage saveChanges() {
        saveChangesButton.click();
        return this;
    }

    public ProfilePage setNewCategory(String categoryName) {
        categoryInput.setValue(categoryName);
        return this;
    }

    public ProfilePage archiveCategory(String categoryName) {
        categoryArchiveButton.click();
        return this;
    }

    public ProfilePage unarchiveCategory(String categoryName) {
        categoryArchiveButton.click();
        return this;
    }

    public ProfilePage editCategory(String categoryName) {
        categoryArchiveButton.click();
        return this;
    }

    public ProfilePage showArchivedCategories() {
        if (!toggleShowArchived.has(cssClass("Mui-checked"))) {
            toggleShowArchived.click();
        }
        return this;
    }

    public void checkArchivedCategoryPresent(String nameCategory) {

        for (SelenideElement category : allCategories) {
            boolean hasName = category.$("span.MuiChip-label ").has(text(nameCategory));
            boolean isArchived = category.$("button[aria-label='Unarchive category']").exists();

            if (hasName && isArchived) {
                category.shouldBe(visible);
                return;
            }
        }
        throw new AssertionError("Не найдена архивная категория с именем: " + nameCategory);

    }

    public void checkActiveCategoryPresent(String nameCategory) {

        for (SelenideElement category : allCategories) {
            boolean hasName = category.$("span.MuiChip-label ").has(text(nameCategory));
            boolean isNotArchived = category.$("button[aria-label='Archive category']").exists();

            if (hasName && isNotArchived) {
                category.shouldBe(visible);
                return;
            }
        }
        throw new AssertionError("Не найдена активная категория с именем: " + nameCategory);
    }
}
