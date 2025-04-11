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

    private final ElementsCollection activeCategories = $$("div.MuiChip-filled.MuiChip-colorPrimary"),
            archivedCategories  = $$("div.MuiChip-filled.MuiChip-colorDefault");


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
        archivedCategories.findBy(exactText(nameCategory));
    }

    public void checkActiveCategoryPresent(String nameCategory) {
        activeCategories.findBy(exactText(nameCategory));
    }
}
