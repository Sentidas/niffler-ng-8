package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.service.service.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendDbClient spendDbClient = new SpendDbClient();

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

                        CategoryJson createdCategory = spendDbClient.createCategory(categoryJson);

                        System.out.println("Категория '" + nameCategory + "' создана. " + "Статус архивности: " + category.archived());
                        context.getStore(NAMESPACE).put(context.getUniqueId(), createdCategory);
                    }
                });
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {

        CategoryJson categoryJson = context.getStore(NAMESPACE)
                .get(context.getUniqueId(), CategoryJson.class);

        if (categoryJson != null && !categoryJson.archived()) {

            CategoryJson archivedCategory = new CategoryJson(
                    categoryJson.id(),
                    categoryJson.name(),
                    categoryJson.username(),
                    true
            );
            spendDbClient.updateCategory(archivedCategory);
            System.out.println("После окончания теста у категории '" + archivedCategory.name() + "' изменен статус архивности на: " + archivedCategory.archived());


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