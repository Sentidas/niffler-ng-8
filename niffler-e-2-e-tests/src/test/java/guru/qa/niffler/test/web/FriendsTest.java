package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.pages.FriendsPage;
import guru.qa.niffler.page.pages.LoginPage;
import guru.qa.niffler.page.pages.MainPage;
import guru.qa.niffler.page.pages.PeoplePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(BrowserExtension.class)
public class FriendsTest {

    public static Config CFG = Config.getInstance();

    @Test
    @User
    @DisplayName("У нового пользователя нет друзей в списке")
    void friendShouldNotBePresentInFriendsTable(UserJson user) {
        System.out.println(user.username() + " - ищем пользователя");
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .openAvatarMenu()
                .goToFriendsPage();

        new FriendsPage().checkFriendsListIsEmpty();
    }

    @Test
    @User(friends = 3)
    @DisplayName("При добавлении друзей новому пользователю они отображаются в списке друзей")
    void friendShouldBePresentInFriendsTable(UserJson user) {

        System.out.println(user.username() + " - ищем пользователя");
        System.out.println("Друзья пользователя: " + user.testData().friends());
        FriendsPage friendsPage = new FriendsPage();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .openAvatarMenu()
                .goToFriendsPage();

        for (UserJson friend : user.testData().friends()) {
            friendsPage.checkFriendIsInFriendsList(friend.username());
        }
    }

    @Test
    @User(
            incomeInvitation = 4
    )
    @DisplayName("При отправке приглашений новому пользователю они отображаются на странице друзей")
    void incomeInvitationsShouldBePresentInFriendsTable(UserJson user) {
        System.out.println("добавили пользователя:" + user.username());

        FriendsPage friendsPage = new FriendsPage();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .openAvatarMenu()
                .goToFriendsPage();

        for (UserJson invitation : user.testData().incomeInvitations()) {
            friendsPage.checkIncomingInvitationVisible(invitation.username());
        }

    }

    @Test
    @User(
            outcomeInvitation = 2
    )
    @DisplayName("При отправке приглашений от нового пользователя они отображаются в списке пользователей")
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        System.out.println("добавили пользователя: " + user.username());
        PeoplePage peoplePage = new PeoplePage();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .openAvatarMenu()
                .goToPeoplePage();

        for (UserJson invitation : user.testData().outcomeInvitations()) {
            peoplePage.checkInvitationSentToUser(invitation.username());
        }
    }

    @Test
    @User(
            categories = @Category(
                    archived = false
            ),
            spendings = @Spend(
                    category = "test category",
                    description = "test description",
                    amount = 89000.00,
                    currency = CurrencyValues.RUB
            ),
            friends = 4,
            incomeInvitation = 3,
            outcomeInvitation = 2
    )
    @DisplayName("Друзья и входящие/исходящие приглашения должны отображаться в профиле пользователя")
    void outcomeIncomeInvitationsAndFriendsBePresentProfileUser(UserJson user) {
        System.out.println("добавили пользователя: " + user.username());
        PeoplePage peoplePage = new PeoplePage();
        FriendsPage friendsPage = new FriendsPage();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .openAvatarMenu()
                .goToPeoplePage();

        for (UserJson outcomeInvitation : user.testData().outcomeInvitations()) {
            peoplePage.checkInvitationSentToUser(outcomeInvitation.username());
        }

        new MainPage().goToFriendsPage();

        for (UserJson friend : user.testData().friends()) {
            friendsPage.checkFriendIsInFriendsList(friend.username());
        }

        for (UserJson incomeInvitation : user.testData().incomeInvitations()) {
            friendsPage.checkIncomingInvitationVisible(incomeInvitation.username());
        }
    }
}
