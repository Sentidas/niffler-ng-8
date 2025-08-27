package guru.qa.niffler.utils;

import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;

import java.util.Map;
import java.util.stream.Collectors;

public class UtilsStat {

    public static Map<String, Double> calculateCategorySums(UserJson user) {
        return user.testData().spends().stream()
                .collect(Collectors.groupingBy(
                        spend -> spend.category().archived() ? "Archived" : spend.category().name(),
                        Collectors.summingDouble(SpendJson::amount)
                ));
    }

    public static Map<String, Double> calculateCategorySumsByCurrency(UserJson user, CurrencyValues currency) {
        return user.testData().spends().stream()
                .filter(spend -> spend.currency().equals(currency))
                .collect(Collectors.groupingBy(
                        spend -> spend.category().archived() ? "Archived" : spend.category().name(),
                        Collectors.summingDouble(SpendJson::amount)
                ));
    }

    public static double calculateTotalByCurrency(UserJson user, CurrencyValues currency) {
        return calculateCategorySumsByCurrency(user, currency).values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }
}
