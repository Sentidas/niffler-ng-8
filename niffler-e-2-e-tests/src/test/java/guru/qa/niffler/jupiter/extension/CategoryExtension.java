package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(anno -> {

                    if (anno.categories().length > 0) {
                        Category category = anno.categories()[0];

                        String nameCategory = RandomDataUtils.randomCategoryName();

                        CategoryJson categoryJson = new CategoryJson(
                                null,
                                nameCategory,
                                anno.username(),
                                false
                        );

                        CategoryJson createdCategory = spendApiClient.createCategory(categoryJson);

                        if (category.archived()) {
                            CategoryJson archivedCategory = new CategoryJson(
                                    createdCategory.id(),
                                    createdCategory.name(),
                                    createdCategory.username(),
                                    true
                            );
                            createdCategory = spendApiClient.editCategory(archivedCategory);
                        }
                        System.out.println("Категория создана: " + nameCategory);
                        context.getStore(NAMESPACE).put(context.getUniqueId(), createdCategory);
                    }
                });
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {

        Optional<User> user = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class);

        if (user.isPresent()) {
            if (user.get().categories().length > 0) {
                CategoryJson category = context.getStore(NAMESPACE)
                        .get(context.getUniqueId(), CategoryJson.class);

                if (!category.archived()) {
                    CategoryJson archivedCategory = new CategoryJson(
                            category.id(),
                            category.name(),
                            category.username(),
                            true
                    );
                    new SpendApiClient().editCategory(archivedCategory);
                }
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}