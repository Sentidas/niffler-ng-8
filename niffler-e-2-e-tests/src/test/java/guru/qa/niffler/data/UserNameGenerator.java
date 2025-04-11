package guru.qa.niffler.data;

import com.github.javafaker.Faker;

public class UserNameGenerator {

    private static final Faker faker = new Faker();

    public static String generateLogin() {
        String username;
        do {
            username = faker.animal().name();
        } while (username.length() < 3);
        return username;
    }
}

