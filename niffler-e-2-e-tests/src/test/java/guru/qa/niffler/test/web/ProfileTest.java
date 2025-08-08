package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
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


    @ApiLogin(username = "duck", password = "12345")
    void updateNameInProfile() {

        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .setName("duccY")
                .saveChanges();
    }


    @User
    @ApiLogin
    @ScreenShotTest("img/avatar_expected.png")
    void checkUploadAvatarInProfile(UserJson user, BufferedImage expectedAvatar) throws IOException {
        System.out.println("Создали пользователя:" + user.username());

        Selenide.open(ProfilePage.URL, ProfilePage.class)
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
                .goToPeoplePage()
                .checkPersonIsInPeopleList(user.username());
    }

    @User
    @ApiLogin
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        System.out.println(user.username() + " - создали пользователя");

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkFriendsListIsEmpty();
    }

    @User
    @ApiLogin
    void friendsTableShouldBeEmptyWithoutInvitation(UserJson user) {
        System.out.println(user.username() + " - создали пользователя");

        Selenide.open(FriendsPage.URL, FriendsPage.class)
            .checkNoOutgoingInvitationsPresent();
    }

    @User(
            username = "duck",
            categories = @Category(
                    archived = false
            )
    )
    @ApiLogin
    void activeCategoryShouldPresentInCategoriesList(UserJson user) {

        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .checkActiveCategoryPresent(user.testData().categories().get(0).name());

    }

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
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
