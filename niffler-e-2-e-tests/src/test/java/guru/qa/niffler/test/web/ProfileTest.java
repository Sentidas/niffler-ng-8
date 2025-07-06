package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Selenide.open;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @User(
            categories = @Category(
                    archived = false
            )
    )

    @Test
    void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        final CategoryJson archivedCategory = user.testData().categories().getFirst();

        open(CFG.frontUrl(), LoginPage.class)
                .setUserName(user.username())
                .setPassword(user.testData().password())
                .submitLogin();

                new MainPage().openAvatarMenu()
                .goToProfilePage()
                .showArchivedCategories()
                .checkArchivedCategoryPresent(archivedCategory.name());
    }

    @User(
            username = "duck",
            categories = @Category(
                    archived = false
            )
    )

    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson[] categoryJson) {

        open(CFG.frontUrl(), LoginPage.class)
                .setUserName("duck")
                .setPassword("12345")
                .submitLogin();

                new MainPage().openAvatarMenu()
                .goToProfilePage();
        new ProfilePage().checkActiveCategoryPresent(categoryJson[0].name());

    }
}
