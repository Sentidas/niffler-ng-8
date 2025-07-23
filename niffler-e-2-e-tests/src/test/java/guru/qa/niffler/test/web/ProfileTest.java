package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.pages.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.open;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

    private static final Config CFG = Config.getInstance();


    @Test
    void updateNameInProfile() {

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials("duck", "12345")
                .openAvatarMenu()
                .goToProfilePage()
                .setName("duccY")
                .saveChanges();
    }



    @ScreenShotTest("img/avatar_expected.png")
    @User
    void checkUploadAvatarInProfile(UserJson user, BufferedImage expectedAvatar) throws IOException {
        System.out.println("Создали пользователя:" + user.username());

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .openAvatarMenu()
                .goToProfilePage()
                .uploadAvatarPhoto("img/avatar.png")
                .saveChanges()
                .checkAvatar(expectedAvatar);
    }

    @User
    void newPersonShouldBePresentInPeopleTable(UserJson user) {
        System.out.println(user.username() + " - создали пользователя");

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials("duck", user.testData().password())
                .openAvatarMenu()
                .goToPeoplePage();

        PeoplePage peoplePage = new PeoplePage();
        peoplePage.checkPersonIsInPeopleList(user.username());
    }

    @User
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        System.out.println(user.username() + " - создали пользователя");

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .openAvatarMenu()
                .goToFriendsPage()
                .checkFriendsListIsEmpty();
    }

    @User
    void peopleTableShouldBeEmptyWithoutInvitation(UserJson user) {
        System.out.println(user.username() + " - создали пользователя");

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .openAvatarMenu()
                .goToPeoplePage();

        new FriendsPage().checkNoOutgoingInvitationsPresent();
    }

    @User(
            username = "duck",
            categories = @Category(
                    archived = false
            )
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(UserJson user) {

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .openAvatarMenu()
                .goToProfilePage()
                .checkActiveCategoryPresent(user.testData().categories().get(0).name());

    }

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        // final CategoryJson archivedCategory = user.testData().categories().getFirst();
        System.out.println("создан пользователь:" + user.username());

        open(CFG.frontUrl(), LoginPage.class)
                .setUserName(user.username())
                .setPassword(user.testData().password())
                .submitLogin();

        new MainPage().openAvatarMenu()
                .goToProfilePage()
                .showArchivedCategories()
                .checkArchivedCategoryPresent(user.testData().categories().get(0).name());
    }
}
