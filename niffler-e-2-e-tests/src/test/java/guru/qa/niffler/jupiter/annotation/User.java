package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.CategoryExtension;
import guru.qa.niffler.jupiter.extension.SpendingExtension;
import guru.qa.niffler.jupiter.extension.UserExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({UserExtension.class, CategoryExtension.class, SpendingExtension.class})
@Test
public @interface User {

    String username() default "";

    Category[] categories() default {};

    Spend[] spendings() default {};

    int friends() default 0;

    int incomeInvitation() default 0;

    int outcomeInvitation() default 0;

}
