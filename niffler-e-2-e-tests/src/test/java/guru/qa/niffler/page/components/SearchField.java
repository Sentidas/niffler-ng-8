package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField {
    private final SelenideElement self = $("form.MuiBox-root");
    private final SelenideElement searchInput = self.$("input[placeholder=Search]");
    private final SelenideElement clearBtn = self.$("#input-clear");

    @Step("Search by query {0}")
    public SearchField search(String query) {

        searchInput.setValue(query).pressEnter();
        return this;
    }

    @Step("Clear search input if not empty")
    public SearchField clear() {
        clearBtn.click();
        return this;
    }
}
