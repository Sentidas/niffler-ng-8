package guru.qa.niffler.page.model;

import lombok.Getter;

@Getter
public enum DataFilterValues {

    All_TIME("All time"),
    lAST_MONTH("Last month"),
    LAST_WEEK("Last week"),
    TODAY("Today");

    private final String uiText;

    DataFilterValues(String uiText) {
        this.uiText = uiText;
    }
}
