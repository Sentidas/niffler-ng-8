package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Calendar {

    private final SelenideElement self = $("div.MuiDateCalendar-root"),
            yearSelectBtn = self.$("button svg[data-testid=ArrowDropDownIcon]"),
            leftSelectMonth = self.$("button svg[data-testid=ArrowLeftIcon]"),
            rightSelectMonth = self.$("button svg[data-testid=ArrowRightIcon]"),
            inputMonth = self.$("div.MuiPickersCalendarHeader-label");

    private final
    ElementsCollection years = self.$$("button.MuiPickersYear-yearButton"),
                       days = self.$$("button.MuiPickersDay-root");

    @Step("Select date in calendar")
    public void selectDateInCalendar(LocalDate date) {
        String year = String.valueOf(date.getYear());
        String monthName = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String day = String.valueOf(date.getDayOfMonth());

        selectYear(year);
        selectMonth(monthName);
        selectDay(day);
    }


    private void selectYear(String year) {
        yearSelectBtn.click();

        years.findBy(text(year))
                .scrollIntoView(true)
                .shouldBe(visible)
                .click();
    }

    private void selectMonth(String month) {
        for (int i = 0; i < 12; i++) {
            String currentMonth = inputMonth.shouldBe(visible).getText().split(" ")[0];
            if (!currentMonth.equals(month)) {
                leftSelectMonth.shouldBe(visible).click();
            } else {
                System.out.println("выбран месяц " + currentMonth);
                break;
            }
        }
    }

    private void selectDay(String day) {
        days.findBy(text(day))
                .click();
    }
}
