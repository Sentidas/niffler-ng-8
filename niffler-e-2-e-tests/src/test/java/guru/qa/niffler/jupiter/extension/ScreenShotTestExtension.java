package guru.qa.niffler.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.model.allure.ScreenDiff;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

@ParametersAreNonnullByDefault
public class ScreenShotTestExtension implements ParameterResolver, TestExecutionExceptionHandler {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenShotTestExtension.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Base64.Encoder encoder = Base64.getEncoder();
    public static final String ASSERT_SCREEN_MESSAGE = "Image comparison failed: charts are not equal";


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenShotTest.class) &&
                parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
    }

    @SneakyThrows
    @Nonnull
    @Override
    public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ScreenShotTest screenShotTest = extensionContext.getRequiredTestMethod().getAnnotation(ScreenShotTest.class);
        return ImageIO.read(new ClassPathResource(screenShotTest.value()).getInputStream());
    }


    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        final ScreenShotTest screenShotTest = context.getRequiredTestMethod().getAnnotation(ScreenShotTest.class);

        File screenshotsDir = new File("build/screenshots");
        if (!screenshotsDir.exists() && !screenshotsDir.mkdirs()) {
            System.err.println("Failed to create 'build/screenshots'");
        }

        if (getExpected() != null) {
            File expectedFile = new File(screenshotsDir, "expected.png");
            ImageIO.write(getExpected(), "png", expectedFile);
        }

        if (getActual() != null) {
            File actualFile = new File(screenshotsDir, "actual.png");
            ImageIO.write(getActual(), "png", actualFile);
        }

        if (getDiff() != null) {
            File diffFile = new File(screenshotsDir, "diff.png");
            ImageIO.write(getDiff(), "png", diffFile);
        }

        if (throwable.getMessage() != null && throwable.getMessage().contains(ASSERT_SCREEN_MESSAGE)) {

            ScreenDiff screenDif = new ScreenDiff(
                    "data:image/png;base64," + encoder.encodeToString(imageToBytes(getExpected())),
                    "data:image/png;base64," + encoder.encodeToString(imageToBytes(getActual())),
                    "data:image/png;base64," + encoder.encodeToString(imageToBytes(getDiff()))
            );

            Allure.addAttachment(
                    "Screenshot diff",
                    "application/vnd.allure.image.diff",
                    objectMapper.writeValueAsString(screenDif)
            );
        }

        if (screenShotTest.rewriteExpected()) {
            BufferedImage actual = getActual();
            if (actual != null) {
                ImageIO.write(actual, "png", new File("src/test/resources/" + screenShotTest.value()));
            }
        }

        throw throwable;
    }

    public static void setExpected(BufferedImage expected) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("expected", expected);
    }

    public static BufferedImage getExpected() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("expected", BufferedImage.class);
    }

    public static void setActual(BufferedImage actual) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("actual", actual);
    }

    @Nullable
    public static BufferedImage getActual() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("actual", BufferedImage.class);
    }

    public static void setDiff(BufferedImage diff) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("diff", diff);
    }

    public static BufferedImage getDiff() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("diff", BufferedImage.class);
    }

    private static byte[] imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
