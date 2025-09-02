package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;


public class CurrencyGrpcTest extends BaseGrpcTest {

    private static final double EPS = 1e-9;

    @Test
    void allCurrenciesShouldReturnedWithRates() {
        final CurrencyResponse response = currencyBlockingStub.getAllCurrencies(Empty.getDefaultInstance());

        Map<CurrencyValues, Double> actual = response.getAllCurrenciesList()
                .stream()
                .collect(Collectors.toMap(Currency::getCurrency, Currency::getCurrencyRate));

        Map<CurrencyValues, Double> expected = Map.of(
                CurrencyValues.USD, 1.0,
                CurrencyValues.EUR, 1.08,
                CurrencyValues.RUB, 0.015,
                CurrencyValues.KZT, 0.0021
        );

        Assertions.assertEquals(expected.keySet(), actual.keySet(), "Набор валют отличается");
        expected.forEach((k, v) ->
                Assertions.assertEquals(v, actual.get(k), EPS, "Неверный курс для " + k));
    }

    static Stream<Arguments> calculatedRateDataProvider() {
        return Stream.of(
                Arguments.of(named("USD", CurrencyValues.USD), 100.10, 1.5),
                Arguments.of(named("KZT", CurrencyValues.KZT), 20.10, 143.57),
                Arguments.of(named("EUR", CurrencyValues.EUR), 505.50, 7.02),
                Arguments.of(named("RUB", CurrencyValues.RUB), 333.25, 333.25)
        );
    }

    @ParameterizedTest(name = "[{index}] RUB {1} -> {0} = {2}")
    @MethodSource("calculatedRateDataProvider")
    void shouldCalculateAmountForSpendsInRUB(CurrencyValues desiredCurrency,
                                             double amount,
                                             double expectedValue) {
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(desiredCurrency)
                .build();

        final CalculateResponse response = currencyBlockingStub.calculateRate(request);
        Assertions.assertEquals(expectedValue, response.getCalculatedAmount(), EPS);
    }

    static Stream<Arguments> calculatedRateDataProviderInUSD() {
        return Stream.of(
                Arguments.of(named("USD", CurrencyValues.USD), 100.10, 100.1),
                Arguments.of(named("KZT", CurrencyValues.KZT), 20.10, 9571.43),
                Arguments.of(named("EUR", CurrencyValues.EUR), 505.50, 468.06),
                Arguments.of(named("RUB", CurrencyValues.RUB), 333.25, 22216.67)
        );
    }

    @ParameterizedTest(name = "[{index}] USD {1} -> {0} = {2}")
    @MethodSource("calculatedRateDataProviderInUSD")
    void shouldCalculateAmountForSpendsInUSD(CurrencyValues desiredCurrency,
                                             double amount,
                                             double expectedValue) {
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(CurrencyValues.USD)
                .setDesiredCurrency(desiredCurrency)
                .build();

        final CalculateResponse response = currencyBlockingStub.calculateRate(request);
        Assertions.assertEquals(expectedValue, response.getCalculatedAmount(), EPS);
    }

    static Stream<Arguments> calculatedRateDataProviderInEUR() {
        return Stream.of(
                Arguments.of(named("USD", CurrencyValues.USD), 100.10, 108.11),
                Arguments.of(named("KZT", CurrencyValues.KZT), 20.10, 10337.14),
                Arguments.of(named("EUR", CurrencyValues.EUR), 505.50, 505.5),
                Arguments.of(named("RUB", CurrencyValues.RUB), 333.25, 23994.0)
        );
    }

    @ParameterizedTest(name = "[{index}] EUR {1} -> {0} = {2}")
    @MethodSource("calculatedRateDataProviderInEUR")
    void shouldCalculateAmountForSpendsInEUR(CurrencyValues desiredCurrency,
                                             double amount,
                                             double expectedValue) {
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(CurrencyValues.EUR)
                .setDesiredCurrency(desiredCurrency)
                .build();

        final CalculateResponse response = currencyBlockingStub.calculateRate(request);
        Assertions.assertEquals(expectedValue, response.getCalculatedAmount(), EPS);
    }

    static Stream<Arguments> calculatedRateDataProviderInKZT() {
        return Stream.of(
                Arguments.of(named("USD", CurrencyValues.USD), 100.10, 0.21),
                Arguments.of(named("KZT", CurrencyValues.KZT), 20.10, 20.1),
                Arguments.of(named("EUR", CurrencyValues.EUR), 505.50, 0.98),
                Arguments.of(named("RUB", CurrencyValues.RUB), 333.25, 46.66)
        );
    }
    @ParameterizedTest(name = "[{index}] KZT {1} -> {0} = {2}")
    @MethodSource("calculatedRateDataProviderInKZT")
    void shouldCalculateAmountForSpendsInKZT(CurrencyValues desiredCurrency,
                                             double amount,
                                             double expectedValue) {
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(CurrencyValues.KZT)
                .setDesiredCurrency(desiredCurrency)
                .build();

        final CalculateResponse response = currencyBlockingStub.calculateRate(request);
        Assertions.assertEquals(expectedValue, response.getCalculatedAmount(), EPS);
    }

    @Test
    void shouldFailIfSpendIsUnspecified() {
        var req = CalculateRequest.newBuilder()
                .setAmount(100.0)
                .setSpendCurrency(CurrencyValues.UNSPECIFIED)
                .setDesiredCurrency(CurrencyValues.RUB)
                .build();
        Assertions.assertThrows(io.grpc.StatusRuntimeException.class,
                () -> currencyBlockingStub.calculateRate(req));
    }

    @Test
    void shouldFailIfDesiredIsUnspecified() {
        var req = CalculateRequest.newBuilder()
                .setAmount(100.0)
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(CurrencyValues.UNSPECIFIED)
                .build();
        Assertions.assertThrows(io.grpc.StatusRuntimeException.class,
                () -> currencyBlockingStub.calculateRate(req));
    }
}