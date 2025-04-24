package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.*;


@ExtendWith({UsersQueueExtension.class, BrowserExtension.class})
public class FriendsTest {

    public static Config CFG = Config.getInstance();

    @Test
    void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .loginWithCredentials(user.username(), user.password())
                .openAvatarMenu()
                .goToFriendsPage()
                .checkFriendIsInFriendsList(user.friend());

    }

    @Test
    void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .loginWithCredentials(user.username(), user.password())
                .openAvatarMenu()
                .goToFriendsPage()
                .checkFriendsListIsEmpty();
    }

    @Test
    void peopleTableShouldBeEmptyWithoutInvitation(@UserType(EMPTY) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .loginWithCredentials(user.username(), user.password())
                .openAvatarMenu()
                .goToPeoplePage()
                .checkNoOutgoingInvitationsPresent();
    }

    @Test
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .loginWithCredentials(user.username(), user.password())
                .openAvatarMenu()
                .goToFriendsPage()
                .checkIncomingInvitationVisible(user.income());
    }

    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .loginWithCredentials(user.username(), user.password())
                .openAvatarMenu()
                .goToPeoplePage()
                .checkOutgoingInvitationHasWaitingStatus(user.outcome());
    }
}
