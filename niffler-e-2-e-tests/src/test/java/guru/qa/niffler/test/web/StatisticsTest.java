package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.pages.LoginPage;
import guru.qa.niffler.page.pages.MainPage;
import guru.qa.niffler.page.usercontext.ExpectedUserContext;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;

@ExtendWith(BrowserExtension.class)
public class StatisticsTest {

    private static final Config CFG = Config.getInstance();

    @User(
            categories = {
                    @Category(name = "Обучение"),
                    @Category(name = "Ремонт"),
                    @Category(name = "Путешествие на Алтай"),
                    @Category(name = "Здоровье")
            },
            spendings = {
                    @Spend(category = "Обучение", description = "Дизайнер курс", amount = 95000),
                    @Spend(category = "Ремонт", description = "Модная ванная", amount = 5005700),
                    @Spend(category = "Путешествие на Алтай", description = "Корм для нерп", amount = 5000.11, currency = CurrencyValues.RUB)
            }
    )

    @ScreenShotTest("img/expected_stat_description_spend.png")
    void checkChartAndLegendsAfterUpdateDescriptionSpending(UserJson user, BufferedImage expected) throws IOException {
        System.out.println("Создали user: " + user.username());

        ExpectedUserContext userContext = new ExpectedUserContext();
        userContext.setInitialUser(user);

        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .checkLegendsNameAndSum(user);

        BufferedImage beforeUpdate = mainPage.getStatPieChart();

        mainPage
                .editSpend("Дизайнер курс")
                .editDescription("Курсы макраме")
                .save(userContext);

        mainPage.checkLegendsNameAndSum(userContext.getExpectedUser());
        BufferedImage afterUpdate = mainPage.checkStatPieChart(expected);
        mainPage.checkNoChangeStatPieChart(beforeUpdate, afterUpdate);
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
            }
    )

    @ScreenShotTest("img/expected_stat_change_amount.png")
    void checkChartAndLegendsAfterAmountChange(UserJson user, BufferedImage expected) throws IOException {
        System.out.println("Создали user: " + user.username());

        ExpectedUserContext userContext = new ExpectedUserContext();
        userContext.setInitialUser(user);

        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .checkLegendsNameAndSum(user);

        BufferedImage beforeUpdate = mainPage.getStatPieChart();

        mainPage.editSpend("Дизайнер курс")
                .editDescription("Дизайнер курс VIP")
                .editAmount(506000.11)
                .save(userContext);

        mainPage.checkLegendsNameAndSum(userContext.getExpectedUser());
        BufferedImage afterUpdate = mainPage.checkStatPieChart(expected);
        mainPage.checkThatPieChartUpdate(beforeUpdate, afterUpdate);
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
            }
    )

    @ScreenShotTest("img/expected_stat_change_category.png")
    void updateChartAndLegendsAfterCategoryChange(UserJson user, BufferedImage expected) throws InterruptedException, IOException {
        System.out.println("Создали user: " + user.username());

        ExpectedUserContext userContext = new ExpectedUserContext();
        userContext.setInitialUser(user);

        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .checkLegendsNameAndSum(user);

        BufferedImage beforeUpdate = mainPage.getStatPieChart();

        mainPage.editSpend("Бронь гостиницы")
                .editCategory("Обучение")
                .save(userContext);

        mainPage.checkLegendsNameAndSum(userContext.getExpectedUser());
        BufferedImage afterUpdate = mainPage.checkStatPieChart(expected);
        mainPage.checkThatPieChartUpdate(beforeUpdate, afterUpdate);
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
            }
    )

    @ScreenShotTest("img/expected_stat_archived_category.png")
    void updateChartAndLegendsAfterCategoryArchive(UserJson user, BufferedImage expected) throws InterruptedException, IOException {
        System.out.println("Создали user: " + user.username());

        ExpectedUserContext userContext = new ExpectedUserContext();
        userContext.setInitialUser(user);

        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .checkLegendsNameAndSum(user);

        BufferedImage beforeUpdate = mainPage.getStatPieChart();

        mainPage.openAvatarMenu()
                .goToProfilePage()
                .archiveCategory("Путешествие на Алтай", userContext)
                .goToMain();

        mainPage.checkLegendsNameAndSum(userContext.getExpectedUser());
        BufferedImage afterUpdate = mainPage.checkStatPieChart(expected);
        mainPage.checkThatPieChartUpdate(beforeUpdate, afterUpdate);
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
            }
    )

   // @ScreenShotTest("img/expected_stat_delete_two_spend.png")
    @ScreenShotTest("img/expected_stat_update_sum_delete_spend.png")
    void updateChartAndLegendsAfterDeleteTwoSpending(UserJson user, BufferedImage expected) throws InterruptedException, IOException {
        System.out.println("Создали user: " + user.username());

        ExpectedUserContext userContext = new ExpectedUserContext();
        userContext.setInitialUser(user);

        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .checkLegendsNameAndSum(user);

        BufferedImage beforeUpdate = mainPage.getStatPieChart();

        mainPage.deleteSpend("Путешествие на Алтай", "Билеты в Барнаул", userContext);
        mainPage.deleteSpend("Путешествие на Алтай", "Бронь гостиницы", userContext);

        mainPage.checkLegendsNameAndSum(userContext.getExpectedUser());
        BufferedImage afterUpdate = mainPage.checkStatPieChart(expected);

        mainPage.checkThatPieChartUpdate(beforeUpdate, afterUpdate);
    }


    @User(
            categories = {
                    @Category(name = "Обучение"),
                    @Category(name = "Ремонт"),
                    @Category(name = "Путешествие на Алтай")
            },
            spendings = {
                    @Spend(category = "Обучение", description = "Дизайнер курс", amount = 95000),
                    @Spend(category = "Ремонт", description = "Потолок", amount = 300700.45),
                    @Spend(category = "Путешествие на Алтай", description = "Билеты в Барнаул", amount = 108000, currency = CurrencyValues.RUB),
                    @Spend(category = "Путешествие на Алтай", description = "Бронь гостиницы", amount = 140000, currency = CurrencyValues.RUB),
                    @Spend(category = "Путешествие на Алтай", description = "Корм для нерп", amount = 5000, currency = CurrencyValues.RUB)
            }
    )

   // @ScreenShotTest("img/expected_stat_update_sum_delete_spend.png")
    @ScreenShotTest("img/expected_stat_delete_two_spend.png")
    void updateChartAndLegendAfterSpendingAmountChangeAndDeletion(UserJson user, BufferedImage expected) throws InterruptedException, IOException {

        System.out.println("Создали user: " + user.username());

        ExpectedUserContext userContext = new ExpectedUserContext();
        userContext.setInitialUser(user);

        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password())
                .checkLegendsNameAndSum(user);


        BufferedImage beforeUpdate = mainPage.getStatPieChart();

        mainPage.editSpend("Дизайнер курс")
                .editAmount(506000.11)
                .save(userContext);

        mainPage.deleteSpend("Путешествие на Алтай", "Билеты в Барнаул", userContext);

        mainPage.checkLegendsNameAndSum(userContext.getExpectedUser());
        BufferedImage afterUpdate = mainPage.checkStatPieChart(expected);
        mainPage.checkThatPieChartUpdate(beforeUpdate, afterUpdate);
    }
}
