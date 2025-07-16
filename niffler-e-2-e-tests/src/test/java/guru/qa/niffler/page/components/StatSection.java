package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
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

public class StatSection {


    private final SelenideElement statComponent = $("canvas[role='img']"),
            legendContainer = $("#legend-container"),
            statImage = $("canvas[role='img']");

    public void waitForStatChartLoad() {
        statComponent.is(image, Duration.ofSeconds(5));
    }

    public BufferedImage getStatPieChart() throws IOException {
        waitForStatChartLoad();
        return ImageIO.read(
                Objects.requireNonNull(
                        statComponent.shouldBe(visible, Duration.ofSeconds(5)).screenshot()
                )
        );
    }


    public void checkChangeStatPieChart(BufferedImage beforeImage, BufferedImage afterImage) throws IOException {
        assertTrue(new ScreenDiffResult(
                beforeImage,
                afterImage
        ));
    }

    public void checkNoChangeStatPieChart(BufferedImage beforeImage, BufferedImage afterImage) throws IOException {
        assertFalse(new ScreenDiffResult(
                beforeImage,
                afterImage
        ));
    }

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


    public List<String> getActualLegendsFromUI() {
        ElementsCollection legendList = legendContainer.$$("li")
                .shouldHave(sizeGreaterThan(0));

        return legendList.stream()
                .map(SelenideElement::getText)
                .toList();
    }

}
