package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.PeoplePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.*;
import static java.lang.Thread.sleep;


@ExtendWith({UsersQueueExtension.class, BrowserExtension.class})
public class FriendsTest {

    public static Config CFG = Config.getInstance();

    @Test
    @User(username = "yak64")
    void friendShouldBePresentInFriendsTable(UserJson user) {
        System.out.println(user.username() + " - ищем пользователя");
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .loginWithCredentials("duck", user.testData().password())
                .openAvatarMenu()
                .goToFriendsPage();

        FriendsPage friend = new FriendsPage();
        friend.checkFriendIsInFriendsList(user.username());

    }


    @Test
    @User
    void personShouldBePresentInPeopleTable(UserJson user) {
        System.out.println(user.username() + " - ищем пользователя");
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .loginWithCredentials("duck", user.testData().password())
                .openAvatarMenu()
                .goToPeoplePage();

        PeoplePage person = new PeoplePage();
        person.checkPersonIsInPeopleList(user.username());

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
                .goToPeoplePage();

               new FriendsPage().checkNoOutgoingInvitationsPresent();
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
                .goToPeoplePage();
                new FriendsPage().checkOutgoingInvitationHasWaitingStatus(user.outcome());
    }
}
