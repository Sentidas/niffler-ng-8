package guru.qa.niffler.page.pages;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.components.Calendar;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.components.SpendingTable;
import guru.qa.niffler.page.components.StatSection;
import guru.qa.niffler.page.usercontext.ExpectedUserContext;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.page.utils.ExpectedLegendGenerator.getSortedExpectedLegends;

@ParametersAreNonnullByDefault
public class MainPage {
    private final SpendingTable spendingTable = new SpendingTable();
    private final StatSection statSection = new StatSection();
    private final Header header = new Header();
    private final Calendar calendar = new Calendar();

    private final SelenideElement statisticsSection = $("#stat"),
            spendingSection = $("#spendings"),
            newSpendingButton = $(byText("New spending"));


    public MainPage openAvatarMenu() {
        header.openAvatarMenu();
        return this;
    }

    public ProfilePage goToProfilePage() {
        header.toProfilePage();
        return new ProfilePage();
    }

    public FriendsPage goToFriendsPage() {
        header.toFriendsPage();
        return new FriendsPage();
    }

    public void goToPeoplePage() {
        header.toPeoplePage();
    }

    public MainPage checkStatisticsIsVisible() {
        statisticsSection.shouldBe(visible);
        return this;
    }

    public MainPage checkHistoryOfSpendingIsVisible() {
        spendingSection.should(visible);
        return this;
    }

    public MainPage checkToolBarIsVisible() {
        header.checkToolBarIsVisible();
        return this;
    }


    @Step("Create new spending")
    public EditSpendingPage createSpend() {
        newSpendingButton.click();
        return new EditSpendingPage();
    }

    public EditSpendingPage editSpend(String description) {
        spendingTable.editSpend(description);
        return new EditSpendingPage(description);
    }

    public MainPage deleteSpend(String category, String description) {
        spendingTable.deleteSpend(category, description);
        return this;
    }

    public MainPage deleteSpend(String category, String description, ExpectedUserContext userContext) {
        spendingTable.deleteSpend(category, description);

        if (userContext != null) {
            userContext.applySpendDelete(category, description);
        }
        return this;
    }

    public void checkThatSpendTableContains(String spendingDescription) {
        spendingTable.checkTableContains(spendingDescription);
    }

    public void checkThatSpendTableContainsWithData(String spendingDescription, LocalDate date) {
        spendingTable.checkTableContainsWithData(spendingDescription, date);
    }

    public BufferedImage chartScreenshot() throws IOException {
        return statSection.chartScreenshot();
    }

    public void checkColorsLegends(Color... color) {
        statSection.checkColorsLegends(color);
    }

    public MainPage checkThatPieChartUpdate(BufferedImage beforeImage, BufferedImage afterImage) throws IOException {
        statSection.checkChangeStatPieChart(beforeImage, afterImage);
        return this;

    }

    public MainPage checkNoChangeStatPieChart(BufferedImage beforeImage, BufferedImage afterImage) throws IOException {
        statSection.checkNoChangeStatPieChart(beforeImage, afterImage);
        return this;

    }

    public BufferedImage checkStatPieChart(BufferedImage expectedStatChar) throws IOException {
        return statSection.checkStatPieChart(expectedStatChar);

    }

    public MainPage checkLegendsNameAndSum(UserJson user) {

        List<String> expectedLegends = getSortedExpectedLegends(user.testData().spends());
        System.out.println("ожидаемые траты" + expectedLegends);
        List<String> actualLegends = statSection.getActualLegendsFromUI();
        System.out.println("реальные траты" + actualLegends);

        StatSection.assertLegendsMatch(expectedLegends, actualLegends);
        return this;
    }
}
