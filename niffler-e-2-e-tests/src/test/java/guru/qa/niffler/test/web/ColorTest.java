package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.pages.LoginPage;
import guru.qa.niffler.page.pages.MainPage;
import guru.qa.niffler.page.usercontext.ExpectedUserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

//@ExtendWith(BrowserExtension.class)
//@WebTest
//public class ColorTest {
//
//    private static final Config CFG = Config.getInstance();
//
//    @User(
//            spendings = {
//                    @Spend(category = "Обучение", description = "Дизайнер курс", amount = 95000),
//                    @Spend(category = "Ремонт", description = "Модная ванная", amount = 5005700),
//                    //  @Spend(category = "Путешествие на Алтай", description = "Корм для нерп", amount = 5000.11, currency = CurrencyValues.RUB)
//            }
//    )
//    @Test
//    @ApiLogin
//    void checkChartAndLegendsAfterUpdateDescriptionSpending(UserJson user) throws IOException {
//        System.out.println("Создали user: " + user.username());
//
//        ExpectedUserContext userContext = new ExpectedUserContext();
//        userContext.setInitialUser(user);
//
//        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class)
//                .checkLegendsNameAndSum(user);
//
//        mainPage
//                .editSpend("Дизайнер курс")
//                .setSpendDescription("Курсы макраме")
//                .save(userContext);
//
//        mainPage.checkLegendsNameAndSum(userContext.getExpectedUser());
//
//        mainPage.checkColorsLegends(Color.green, Color.yellow);
//    }
//}
@ExtendWith(BrowserExtension.class)
@WebTest
public class ColorTest {

    private static final Config CFG = Config.getInstance();

    @User(
            spendings = {
                    @Spend(category = "Обучение", description = "Дизайнер курс", amount = 95000),
                    @Spend(category = "Ремонт", description = "Модная ванная", amount = 5005700),
                    //    @Spend(category = "Путешествие на Алтай", description = "Корм для нерп", amount = 5000.11, currency = CurrencyValues.RUB)
            }
    )
    void checkChartAndLegendsAfterUpdateDescriptionSpending(UserJson user) throws IOException {
        System.out.println("Создали user: " + user.username());


        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLoginWithCredentials(user.username(), user.testData().password());
               // .checkLegendsNameAndSum(user);

        mainPage
                .editSpend("Дизайнер курс")
                .setSpendDescription("Курсы макраме")
                .save();

      //  mainPage.checkLegendsNameAndSum(userContext.getExpectedUser());

        mainPage.checkColorsLegends(Color.green, Color.yellow);
    }
}
