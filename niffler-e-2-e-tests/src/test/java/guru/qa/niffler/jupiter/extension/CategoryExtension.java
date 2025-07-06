package guru.qa.niffler.jupiter.extension;

import com.github.javafaker.Cat;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendDbClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(anno -> {

                    if (ArrayUtils.isNotEmpty(anno.categories())) {
                        UserJson createdUser = UserExtension.createdUser();
                        final String username = createdUser != null
                                ? createdUser.username()
                                : anno.username();

                        final List<CategoryJson> createdCategories = new ArrayList<>();

                        for (Category categoryAnno : anno.categories()) {
                            final String categoryName = "".equals(categoryAnno.name())
                                    ? RandomDataUtils.randomCategoryName()
                                    : categoryAnno.name();

                            CategoryJson categoryJson = new CategoryJson(
                                    null,
                                    categoryName,
                                    username,
                                    categoryAnno.archived(),
                                    null
                            );
                            createdCategories.add(
                                    spendClient.createCategory(categoryJson));
                        }

                        if (createdUser != null) {
                            createdUser.testData().categories().addAll(
                                    createdCategories
                            );
                        } else {
                            context.getStore(NAMESPACE).put(
                                    context.getUniqueId(),
                                    createdCategories);
                        }
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (CategoryJson[]) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(),List.class)
                .stream()
                .toArray(CategoryJson[] :: new);
   }
}