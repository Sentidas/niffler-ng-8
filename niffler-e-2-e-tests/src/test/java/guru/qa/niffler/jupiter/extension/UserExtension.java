package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.userdata.TestData;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class UserExtension implements BeforeEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);

    private static final String defaultPassword = "12345";
    private final UsersClient usersClient = new UsersDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(anno -> {
                    UserJson user;

                    if ("".equals(anno.username())) {
                        // Случай 1: username не задан — создаём нового
                        final String username = RandomDataUtils.randomUsername();

                        user = usersClient.createUser(
                                username,
                                defaultPassword
                        );
                        List<UserJson> friends = usersClient.addFriends(user, anno.friends());
                        List<UserJson> incomeInvitations = usersClient.createIncomeInvitations(user, anno.incomeInvitation());
                        List<UserJson> outcomeInvitations = usersClient.createOutcomeInvitations(user, anno.outcomeInvitation());

                        user = user.withTestData(
                                new TestData(
                                        defaultPassword,
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        friends,
                                        incomeInvitations,
                                        outcomeInvitations
                                )
                        );
                    } else {
                        // Случай 2: username задан — ищем, если не найден в БД - пока ошибка
                        user = usersClient.findUserByUsername(anno.username())
                                .orElseThrow(() -> new IllegalStateException("User " + anno.username() + " not found"))
                                .withPassword(defaultPassword);
                    }

                    context.getStore(NAMESPACE).put(
                            context.getUniqueId(),
                            user
                    );
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdUser();
    }

    public static @Nullable UserJson createdUser() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
    }
}