package guru.qa.niffler.test.web.archive;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.pages.FriendsPage;
import guru.qa.niffler.page.pages.LoginPage;
import guru.qa.niffler.page.pages.PeoplePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.*;


@ExtendWith({UsersQueueExtension.class, BrowserExtension.class})
public class PeopleTest {

    public static Config CFG = Config.getInstance();

    @Test
    void newPersonShouldBePresentInPeopleTable(UserJson user) throws InterruptedException {
        System.out.println(user.username() + " - ищем пользователя");

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .openAvatarMenu()
                .goToPeoplePage();

        PeoplePage peoplePage = new PeoplePage();
        Thread.sleep(9000);
        peoplePage.checkPersonIsInPeopleList(user.username());
    }

    @Test
    void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.password())
                .openAvatarMenu()
                .goToFriendsPage()
                .checkFriendsListIsEmpty();
    }

    @Test
    void peopleTableShouldBeEmptyWithoutInvitation(@UserType(EMPTY) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.password())
                .openAvatarMenu()
                .goToPeoplePage();
                new FriendsPage().checkNoOutgoingInvitationsPresent();
    }

    @Test
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.password())
                .openAvatarMenu()
                .goToFriendsPage()
                .checkIncomingInvitationVisible(user.income());
    }

    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.password())
                .openAvatarMenu()
                .goToPeoplePage();
               new FriendsPage().checkOutgoingInvitationHasWaitingStatus(user.outcome());
    }
}
