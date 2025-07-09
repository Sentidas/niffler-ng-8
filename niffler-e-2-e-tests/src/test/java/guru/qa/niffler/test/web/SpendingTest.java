package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
    void spendingDescriptionShouldBeUpdatedByTableAction(SpendJson[] spend) throws InterruptedException {

        final String newDescription = "For me";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .loginWithCredentials("duck", "12345")
                .editSpending(spend[0].description())
                .editDescription(newDescription)
                .save();

        new MainPage().checkThatTableContains(newDescription);
    }

    @Test
    void spendingShouldBeVisibleInTableActionAfterSearch() {

        final String description = "new spend111";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .loginWithCredentials("duck", "12345");

        new MainPage().checkThatTableContains(description);
    }

    @Test
    void categoryNameShouldBeVisibleInTableActionAfterSearch() {

        final String categoryName = "Йога0023a";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .loginWithCredentials("duck", "12345");

        new MainPage().checkThatTableContains(categoryName);
    }
}
