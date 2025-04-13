package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;

import guru.qa.niffler.jupiter.Category;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Selenide.open;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @Category(
            username = "duck",
            archived = true
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(CategoryJson categoryJson) {

        open(CFG.frontUrl(), LoginPage.class)
                .setUserName("duck")
                .setPassword("12345")
                .submitLogin();

                new MainPage().openAvatarMenu()
                .goToProfilePage()
                .showArchivedCategories()
                .checkArchivedCategoryPresent(categoryJson.name());
    }

    @Category(
            username = "duck",
            archived = false
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson categoryJson) throws InterruptedException {

        open(CFG.frontUrl(), LoginPage.class)
                .setUserName("duck")
                .setPassword("12345")
                .submitLogin();

                new MainPage().openAvatarMenu()
                .goToProfilePage();
        new ProfilePage().checkActiveCategoryPresent(categoryJson.name());

    }
}
