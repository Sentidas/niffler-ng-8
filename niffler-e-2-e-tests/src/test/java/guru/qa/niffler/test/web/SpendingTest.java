package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.pages.LoginPage;
import guru.qa.niffler.page.pages.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;


@ExtendWith(BrowserExtension.class)
public class SpendingTest {

    private static final Config CFG = Config.getInstance();

    @User(
            username = "duck",
            spendings = @Spend(
                    category = "Обучение",
                    description = "Обучение 2.0",
                    amount = 89000.00,
                    currency = CurrencyValues.RUB
            ))

    @Test
    void spendingDescriptionShouldBeUpdatedByTableAction(SpendJson[] spend) {

        final String newDescription = "For me";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials("duck", "12345")
                .editSpend(spend[0].description())
                .editDescription(newDescription)
                .save();

        new MainPage().checkThatSpendTableContains(newDescription);
    }

    @User(
            categories = {
                    @Category(name = "Путешествие"),},
            spendings = {
                    @Spend(category = "Путешествие", description = "Билеты на Кубу", amount = 95000),
                    @Spend(category = "Путешествие", description = "Аренда отеля", amount = 47300.555),
                    @Spend(category = "Путешествие", description = "Прогулка на яхте", amount = 10600),
                    @Spend(category = "Путешествие", description = "Гид по городу", amount = 3000),
                    @Spend(category = "Путешествие", description = "Дайвинг", amount = 10000),
                    @Spend(category = "Путешествие", description = "Ресторан", amount = 3000),
                    @Spend(category = "Путешествие", description = "Прогулка в городе", amount = 4800),
                    @Spend(category = "Путешествие", description = "Чаевые общие", amount = 2090.99),
                    @Spend(category = "Путешествие", description = "Сувениры", amount = 5876),
                    @Spend(category = "Путешествие", description = "Еда", amount = 39000),
                    @Spend(category = "Путешествие", description = "Покупка одежды", amount = 7090.22),
                    @Spend(category = "Путешествие", description = "Кафе", amount = 5400)}
    )

    @Test
    void searchCategoryNameInTableAction(UserJson user) {
        System.out.println("Создали user: " + user.username());

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), "12345");

        new MainPage().checkThatSpendTableContains("Билеты на Кубу");
    }

    @User(
            categories = {
                    @Category(name = "Обучение"),
            },
            spendings = {
                    @Spend(category = "Обучение", description = "Дизайнер курс", amount = 95000)
            }
    )

    @ScreenShotTest(value = "img/7.png", rewriteExpected = true)
    void checkRewriteExpectedImage(UserJson user, BufferedImage expectedStatPieChart) throws IOException {
        System.out.println("Создали user: " + user.username());

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .checkStatPieChart(expectedStatPieChart);
    }
}
