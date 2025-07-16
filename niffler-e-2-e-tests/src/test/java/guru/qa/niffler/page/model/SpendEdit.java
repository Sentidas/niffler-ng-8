package guru.qa.niffler.page.model;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;

import java.time.LocalDate;

public record SpendEdit(

        String originalDescription,
        String newDescription,
        Double newAmount,
        CurrencyValues newCurrency,
        LocalDate newDate,
        CategoryJson newCategory
) { }
