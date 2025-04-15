package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

import java.util.List;
import java.util.Random;

public class RandomDataUtils {

    private static final Faker faker = new Faker();
    private static final Random RANDOM = new Random();
    private static final List<String> CATEGORIES = List.of(
            // Еда и напитки
            "Продукты", "Рестораны", "Кофейни", "Доставка еды", "Алкоголь",
            //  Дом
            "Аренда", "Коммунальные услуги", "Ремонт", "Мебель", "Бытовая техника",
            //  Транспорт
            "Такси", "Общественный транспорт", "Авто", "Бензин", "Парковка",
            // Хобби
            "Книги", "Фильмы", "Музыка", "Игры", "Фотография",
            //  Здоровье
            "Фитнес", "Аптека", "Врачи", "Массаж", "Йога",
            //  Образование
            "Курсы", "Подписки", "Образование", "Школа", "Университет",
            // Путешествия
            "Авиабилеты", "Отели", "Экскурсии", "Сувениры", "Пляж"
    );

    public static String randomUsername() {
        String username;
        do {
            username = faker.animal().name();
        } while (username.length() < 3);
        return username;
    }

    public static String randomName() {
        return faker.name().firstName();
    }

    public static String randomSurname() {
        return faker.name().lastName();
    }

    public static String randomSentence(int wordsCount) {
        StringBuilder sentence = new StringBuilder();

        for (int i = 0; i < wordsCount; i++) {
            sentence.append(faker.lorem().word() + " ");
        }
        return sentence.toString();
    }

    public static String randomCategoryName() {
        return CATEGORIES.get(RANDOM.nextInt(CATEGORIES.size())) + "_" + RANDOM.nextInt(100);
    }

}
