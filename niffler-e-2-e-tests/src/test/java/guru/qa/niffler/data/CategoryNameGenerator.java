package guru.qa.niffler.data;

import java.util.List;
import java.util.Random;

public class CategoryNameGenerator {
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

    private static final Random RANDOM = new Random();

    public static String randomCategoryName() {
        return CATEGORIES.get(RANDOM.nextInt(CATEGORIES.size())) + "_" + RANDOM.nextInt(100);
    }
}
