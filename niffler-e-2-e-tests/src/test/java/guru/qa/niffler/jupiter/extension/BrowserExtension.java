package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class BrowserExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    private final ThreadLocal<List<SelenideDriver>> drivers = ThreadLocal.withInitial(ArrayList::new);

    public List<SelenideDriver> drivers() {
        return drivers.get();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        for (SelenideDriver driver : drivers.get()) {
            if (driver.hasWebDriverStarted()) {
                driver.close();
            }
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        // Configuration.browserSize = "1920x1080";
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
                .screenshots(false)
        );
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    private void doScreenshot() {
        for (SelenideDriver driver : drivers.get()) {
            if (driver.hasWebDriverStarted()) {
                Allure.addAttachment(
                        "Screen on fail ",
                        new ByteArrayInputStream(
                                ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                        )
                );
            }
        }
    }
}
