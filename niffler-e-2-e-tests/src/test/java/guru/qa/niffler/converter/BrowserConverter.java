package guru.qa.niffler.converter;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.model.Browser;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserConverter implements ArgumentConverter {
    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {

        if (source == null) {
            throw new ArgumentConversionException("Source browser value is null");
        }

        if (source instanceof Browser browser) {
            return new SelenideDriver(browser.getConfig());

        } else {
            throw new ArgumentConversionException("Cannot convert " + source.getClass().getName() + " to SelenideDriver");
        }
    }
}
