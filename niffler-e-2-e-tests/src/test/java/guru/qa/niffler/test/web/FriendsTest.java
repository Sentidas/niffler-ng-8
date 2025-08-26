package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.pages.FriendsPage;
import guru.qa.niffler.page.pages.LoginPage;
import guru.qa.niffler.page.pages.PeoplePage;
import org.junit.jupiter.api.DisplayName;


@WebTest
public class FriendsTest {

    public static Config CFG = Config.getInstance();


    @User(
            incomeInvitation = 2
    )
    @ApiLogin
    @DisplayName("Принять приглашение")
    void incomeInvitationsShouldBePresentInFriendsTable2(UserJson user) throws InterruptedException {
        System.out.println("добавили пользователя:" + user.username());

        String friendName = user.testData().incomeInvitations().get(0).username();

        System.out.println("приглашение отправил:" + friendName);

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .openAvatarMenu()
                .goToFriendsPage()
                .acceptIncomingInvitation(friendName)
                .checkAlertMessage("Invitation of " + friendName + " accepted")
                .checkFriendIsInFriendsList(friendName);
    }

    @User(
            incomeInvitation = 2
    )
    @ApiLogin
    @DisplayName("Отклонить приглашение")
    void incomeInvitationsShouldBePresentInFriendsTable3(UserJson user) throws InterruptedException {
        System.out.println("добавили пользователя:" + user);

        String friendName = user.testData().incomeInvitations().get(0).username();

        System.out.println("приглашение получил:" + friendName);

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .declineIncomingInvitation(friendName)
                .checkAlertMessage("Invitation of " + friendName + " is declined")
                .checkNoPresentFriendIsInFriendsList(friendName);
    }


    @User
    @ApiLogin
    @DisplayName("У нового пользователя нет друзей в списке")
    void friendShouldNotBePresentInFriendsTable(UserJson user) {
        System.out.println("добавили пользователя:" + user.username());

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkFriendsListIsEmpty();
    }


    @User(friends = 2)
    @ApiLogin
    @DisplayName("При добавлении друзей новому пользователю они отображаются в списке друзей")
    void friendShouldBePresentInFriendsTable(UserJson user) {

        System.out.println("добавили пользователя:" + user);
        System.out.println("Друзья пользователя: " + user.testData().friends());
        FriendsPage friendsPage = new FriendsPage();

        Selenide.open(FriendsPage.URL, FriendsPage.class);

        for (UserJson friend : user.testData().friends()) {
            friendsPage.checkFriendIsInFriendsList(friend.username());
        }
    }


    @User(
            incomeInvitation = 4
    )
    @ApiLogin
    @DisplayName("При отправке приглашений новому пользователю они отображаются на странице друзей")
    void incomeInvitationsShouldBePresentInFriendsTable(UserJson user) {
        System.out.println("добавили пользователя:" + user);

        FriendsPage friendsPage = Selenide.open(FriendsPage.URL, FriendsPage.class);

        for (UserJson invitation : user.testData().incomeInvitations()) {
            friendsPage.checkIncomingInvitationVisible(invitation.username());
        }
    }


    @User(
            outcomeInvitation = 2
    )
    @ApiLogin
    @DisplayName("При отправке приглашений от нового пользователя они отображаются в списке пользователей")
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        System.out.println(user);
        System.out.println("добавили пользователя: " + user.username());
        System.out.println("отправили приглашение: " + user.testData().outcomeInvitations().get(0).username());
        PeoplePage peoplePage = Selenide.open(PeoplePage.URL, PeoplePage.class);

        for (UserJson invitation : user.testData().outcomeInvitations()) {
            peoplePage.checkInvitationSentToUser(invitation.username());
        }
    }

    @User(
            categories = {
                    @Category(name = "Обучение"),
                    @Category(name = "Ремонт"),
                    @Category(name = "Путешествие на Алтай")
            },
            spendings = {
                    @Spend(category = "Обучение", description = "Дизайнер курс", amount = 95000),
                    @Spend(category = "Ремонт", description = "Потолок", amount = 50700.45),
                    @Spend(category = "Путешествие на Алтай", description = "Билеты в Барнаул", amount = 108000, currency = CurrencyValues.RUB),
                    @Spend(category = "Путешествие на Алтай", description = "Бронь гостиницы", amount = 140000, currency = CurrencyValues.RUB),
                    @Spend(category = "Путешествие на Алтай", description = "Корм для нерп", amount = 5000, currency = CurrencyValues.RUB)
            },

            friends = 14,
            incomeInvitation = 4,
            outcomeInvitation = 2
    )
    @ApiLogin
    @DisplayName("Друзья и входящие/исходящие приглашения должны отображаться в профиле пользователя")
    void outcomeIncomeInvitationsAndFriendsBePresentProfileUser(UserJson user) {
        System.out.println("добавили пользователя: " + user.username());

        PeoplePage peoplePage = Selenide.open(PeoplePage.URL, PeoplePage.class);

        for (UserJson outcomeInvitation : user.testData().outcomeInvitations()) {
            peoplePage.checkInvitationSentToUser(outcomeInvitation.username());
        }

        FriendsPage friendsPage = Selenide.open(FriendsPage.URL, FriendsPage.class);

        for (UserJson friend : user.testData().friends()) {
            friendsPage.checkFriendIsInFriendsList(friend.username());
        }

        for (UserJson incomeInvitation : user.testData().incomeInvitations()) {
            friendsPage.checkIncomingInvitationVisible(incomeInvitation.username());
        }
    }
}
