package guru.qa.niffler.page.assertions;

import org.assertj.core.api.SoftAssertions;

import java.util.List;

public class LegendAssert {
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
