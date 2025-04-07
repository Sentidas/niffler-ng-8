package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.data.CategoryNameGenerator;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {

                    System.out.println("Из аннотации: archived = " + anno.archived());

                    for (int i = 0; i < 10; i++) {
                        String nameCategory = CategoryNameGenerator.randomCategoryName();

                        CategoryJson categoryJson = new CategoryJson(
                                null,
                                nameCategory,
                                anno.username(),
                                false
                                // anno.archived()
                        );

                        CategoryJson createdCategory = spendApiClient.createCategory(categoryJson);

                        if (createdCategory != null) {
                            if (anno.archived()) {
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
                            return;
                        }
                    }
                    throw new RuntimeException("Не удалось создать уникальную категорию за 10 попыток");
                });
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
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

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}
