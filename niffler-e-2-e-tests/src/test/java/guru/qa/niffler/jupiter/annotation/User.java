package guru.qa.niffler.jupiter.annotation;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Test
public @interface User {

    String username() default "";

    Category[] categories() default {};

    Spend[] spendings() default {};

    int friends() default 0;

    int incomeInvitation() default 0;

    int outcomeInvitation() default 0;

}
