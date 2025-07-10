package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(
            String username,
            String password,
            String friend,
            String income,
            String outcome
    ) {
    }

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUser("fox", "12345", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("duck", "12345", "panda", null, null));
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("grasshopper", "12345", null, "Catty", null));
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("Catty", "12345", null, null, "grasshopper"));
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserType {
        Type value() default Type.EMPTY;

        enum Type {
            EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {

        Map<UserType, StaticUser> users = new HashMap<>();

        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
                .map(p -> p.getAnnotation(UserType.class))
                .forEach(userType -> {

                    Optional<StaticUser> user = Optional.empty();
                    StopWatch sw = StopWatch.createStarted();

                    while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 7) {

                        user = switch (userType.value()) {
                            case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
                            case WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
                            case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
                            case WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
                        };
                    }

                    user.ifPresentOrElse(
                            u -> users.put(userType, u),
                            () -> {
                                throw new IllegalStateException("Can`t obtain user for empty=" + userType.value() + " after 30s.");
                            }
                    );

                });

        Allure.getLifecycle().updateTestCase(testCase ->
                testCase.setStart(new Date().getTime())
        );


        @SuppressWarnings("unchecked")
        Map<UserType, StaticUser> storeMap = ((Map<UserType, StaticUser>) context.getStore(NAMESPACE)
                .getOrComputeIfAbsent(
                        context.getUniqueId(),
                        key -> new HashMap<>()
                ));

        storeMap.putAll(users);
    }


    @Override
    public void afterTestExecution(ExtensionContext context) {

        @SuppressWarnings("unchecked")
        Map<UserType, StaticUser> mapUser = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);

        for (Map.Entry<UserType, StaticUser> e : mapUser.entrySet()) {
            UserType.Type type = e.getKey().value();
            StaticUser user = e.getValue();

            switch (type) {
                case EMPTY -> EMPTY_USERS.add(user);
                case WITH_FRIEND -> WITH_FRIEND_USERS.add(user);
                case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.add(user);
                case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.add(user);
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {

        @SuppressWarnings("unchecked")
        Map<UserType, StaticUser> storeMap = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        UserType ut = parameterContext.findAnnotation(UserType.class).orElseThrow();

        return storeMap.get(ut);
    }
}
