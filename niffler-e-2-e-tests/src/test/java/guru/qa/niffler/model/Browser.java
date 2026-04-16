package guru.qa.niffler.model;

import com.codeborne.selenide.SelenideConfig;
import guru.qa.niffler.utils.SelenideUtils;
import lombok.Getter;

@Getter
public enum Browser {
    CHROME(SelenideUtils.chromeConfig),
    FIREFOX(SelenideUtils.firefoxConfig);

    private final SelenideConfig config;

    Browser(SelenideConfig config) {
        this.config = config;
    }

}
