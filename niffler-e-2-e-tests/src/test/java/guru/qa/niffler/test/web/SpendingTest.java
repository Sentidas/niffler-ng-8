package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
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
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение 2.0",
                    amount = 89000.00,
                    currency = CurrencyValues.RUB
            ))

    @Test
    void spendingDescriptionShouldBeUpdatedByTableAction(SpendJson spend) {

        final String newDescription = "For me";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .loginWithCredentials("duck", "12345")
                .editSpending(spend.description())
                .editDescription(newDescription)
                .save();

        new MainPage().checkThatTableContains(newDescription);
    }
}
