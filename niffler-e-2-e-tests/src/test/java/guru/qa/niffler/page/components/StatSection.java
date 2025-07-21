package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.StatConditions;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.utils.ScreenDiffResult;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import guru.qa.niffler.condition.Color;
import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.image;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ParametersAreNonnullByDefault
public class StatSection {


    private final SelenideElement self = $("#stat"),
            chart = self.$("canvas[role='img']"),
            legendContainer = self.$("#legend-container"),
            statImage = self.$("canvas[role='img']");

    private final ElementsCollection bubbles = $("#legend-container").$$("li");

    @Step("Wait for Statistics Chart load")
    public void waitForStatChartLoad() {
        chart.is(image, Duration.ofSeconds(5));
    }

    @Step("Do Statistics Chart screenshot")
    public BufferedImage chartScreenshot() throws IOException {
        waitForStatChartLoad();
        return ImageIO.read(
                Objects.requireNonNull(
                        chart.shouldBe(visible, Duration.ofSeconds(5)).screenshot()
                )
        );
    }

    @Step("Check change Statistics Chart")
    public void checkChangeStatPieChart(BufferedImage beforeImage, BufferedImage afterImage) throws IOException {
        assertTrue(new ScreenDiffResult(
                beforeImage,
                afterImage
        ));
    }

    @Step("Check no change Statistics Chart")
    public void checkNoChangeStatPieChart(BufferedImage beforeImage, BufferedImage afterImage) throws IOException {
        assertFalse(new ScreenDiffResult(
                beforeImage,
                afterImage
        ));
    }

    @Step("Compare Statistics Chart with expected")
    public BufferedImage checkStatPieChart(BufferedImage expected) throws IOException {
        waitForStatChartLoad();
        BufferedImage actual = ImageIO.read(
                statImage.shouldBe(visible, Duration.ofSeconds(5)).screenshot()
        );
        assertFalse(new ScreenDiffResult(
                actual,
                expected), ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE);
        return actual;
    }

    @Step("Get legends")
    public List<String> getActualLegendsFromUI() {
        ElementsCollection legendList = legendContainer.$$("li")
                .shouldHave(sizeGreaterThan(0));

        return legendList.stream()
                .map(SelenideElement::getText)
                .toList();
    }

    @Step("Check color legends")
    public StatSection checkColorsLegends(Color... expectedColors) {
        bubbles.should(StatConditions.color(expectedColors));
        return this;
    }

    @Step("Compare legends with expected")
    public static void assertLegendsMatch(List<String> expectedLegends, List<String> actualLegends) {

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(actualLegends).hasSameSizeAs(expectedLegends);

        for (int i = 0; i < expectedLegends.size() && i < actualLegends.size(); i++) {
            softly.assertThat(actualLegends.get(i))
                    .isEqualTo(expectedLegends.get(i));

        }
        softly.assertAll();
    }
}
