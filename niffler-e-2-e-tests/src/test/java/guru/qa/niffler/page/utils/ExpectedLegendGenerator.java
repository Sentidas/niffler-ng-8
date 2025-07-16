package guru.qa.niffler.page.utils;

import guru.qa.niffler.model.spend.SpendJson;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class ExpectedLegendGenerator {
    @NotNull
    public static List<String> getSortedExpectedLegends(List<SpendJson> spendsList) {
        Map<String, Double> totalByCategory = new HashMap<>();
        for (SpendJson spend : spendsList) {
            String categoryName = spend.category().name();
            if (spend.category().archived().equals(true)) {
                categoryName = "Archived";
            }
            double amount = spend.amount();

            totalByCategory.merge(categoryName, amount, Double::sum);
        }

        List<Map.Entry<String, Double>> sortedTotals = totalByCategory.entrySet().stream()
                .sorted(
                        Comparator
                                // ставим архивные в конце
                                .comparing((Map.Entry<String, Double> e) -> e.getKey().equals("Archived") ? 1 : 0)
                                // сортируем остальное по убыванию
                                .thenComparing(Map.Entry.<String, Double>comparingByValue().reversed()))
                .toList();

        // преобразуем Map в отформатированный list
        List<String> expectedLegends = sortedTotals.stream()
                .map(entry -> formatLegend(entry.getKey(), entry.getValue()))
                .toList();

        return expectedLegends;
    }

    private static String formatLegend(String category, double amount) {
        DecimalFormat formatted = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.US));
        return String.format("%s %s ₽", category, formatted.format(amount));
    }
}
