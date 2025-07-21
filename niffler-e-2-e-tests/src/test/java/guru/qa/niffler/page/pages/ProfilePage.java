package guru.qa.niffler.page.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.model.CategoryEdit;
import guru.qa.niffler.page.usercontext.ExpectedUserContext;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class ProfilePage {

    private final Header header = new Header();

    private final SelenideElement
            uploadPictureAction = $("span.MuiButtonBase-root").$(byText("Upload new picture")),
            userNameField = $("#username"),
            nameInput = $("#name"),
            saveChangesButton = $("button.MuiLoadingButton-root"),
            categoryInput = $("#category"),
            categoryEditButton = $("button[aria-label='Edit category']"),
            categoryArchiveButton = $("button[aria-label='Archive  category']"),
            categoryUnarchiveButton = $("button[aria-label='Unarchive category']"),
            toggleShowArchived = $(".MuiSwitch-switchBase.MuiSwitch-colorPrimary"),
            imageInput = $("#image__input"),
            avatarImage = $("img.MuiAvatar-img"),
            mainLink = $("a[href='/main']");

    private final ElementsCollection activeCategories = $$("div.MuiChip-filled.MuiChip-colorPrimary"),
            archivedCategories = $$("div.MuiChip-filled.MuiChip-colorDefault");

    @Step("Upload avatar photo")
    public ProfilePage uploadAvatarPhoto(String photo) throws IOException {
        File avatarFile = new ClassPathResource(photo).getFile();
        imageInput.uploadFile(avatarFile);
        return this;
    }

    @Step("Set name in profile")
    public ProfilePage setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Step("Save changes in profile")
    public ProfilePage saveChanges() {
        saveChangesButton.click();
        return this;
    }

    @Step("Compare avatar user in profile with expected")
    public ProfilePage checkAvatar(BufferedImage expectedAvatar) throws IOException {
        BufferedImage actual = ImageIO.read(avatarImage.screenshot());
        assertFalse(new ScreenDiffResult(actual, expectedAvatar));
        return this;
    }

    @Step("Create new category")
    public ProfilePage createNewCategory(String categoryName) {
        categoryInput.setValue(categoryName);
        return this;
    }

    @Step("Archive category")
    public ProfilePage archiveCategory(String categoryName) {
        SelenideElement categoryBlock = $$("div.MuiGrid-root.css-17u3xlq")
                .findBy(text(categoryName));

        categoryBlock.$("button[aria-label='Archive category']").click();
        $(byText("Archive")).click();
        return this;
    }

    @Step("Archive category with userContext")
    public ProfilePage archiveCategory(String categoryName, ExpectedUserContext  userContext) {
        SelenideElement categoryBlock = $$("div.MuiGrid-root.css-17u3xlq")
                .findBy(text(categoryName));

        categoryBlock.$("button[aria-label='Archive category']").click();
        $(byText("Archive")).click();
        userContext.applyCategoryEdit(getEditData(categoryName));
        return this;
    }

    private CategoryEdit getEditData(String categoryName) {
        return new CategoryEdit(categoryName, true);
    }


    public MainPage goToMain() {
       header.toMainPage();
        return new MainPage();
    }

    @Step("Unarchive category")
    public ProfilePage unarchiveCategory(String categoryName) {
        categoryArchiveButton.click();
        return this;
    }

    @Step("Click edit category button")
    public ProfilePage editCategory(String categoryName) {
        categoryArchiveButton.click();
        return this;
    }

    @Step("Show archived categories")
    public ProfilePage showArchivedCategories() {
        if (!toggleShowArchived.has(cssClass("Mui-checked"))) {
            toggleShowArchived.click();
        }
        return this;
    }

    @Step("Check archived category present")
    public void checkArchivedCategoryPresent(String nameCategory) {
        archivedCategories.findBy(exactText(nameCategory));
    }

    @Step("Check active category present")
    public void checkActiveCategoryPresent(String nameCategory) {
        activeCategories.findBy(exactText(nameCategory));
    }
}
