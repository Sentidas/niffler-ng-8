package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.spend.CurrencyValues;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpendResultSetExtractor implements ResultSetExtractor<CategoryEntity> {


    public static final SpendResultSetExtractor instance = new SpendResultSetExtractor();

    @Override
    public CategoryEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, CategoryEntity> categoryMap = new ConcurrentHashMap<>();
        UUID categoryId = null;

        while (rs.next()) {
            categoryId = rs.getObject("category_id", UUID.class);
            System.out.println("categoryId = " +  categoryId);

            CategoryEntity category = categoryMap.computeIfAbsent(categoryId, id -> {
                CategoryEntity result = new CategoryEntity();
                try {
                    result.setId(id);
                    result.setUsername(rs.getString("category_username"));
                    result.setName(rs.getString("category_name"));
                    result.setArchived(rs.getBoolean("archived"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return result;
            });

            UUID spendId = rs.getObject("spend_id", UUID.class);
            if (spendId == null) {
                continue;
            }
            SpendEntity spend = new SpendEntity();
            spend.setId(spendId);
            spend.setUsername(rs.getString("spend_username"));
            spend.setSpendDate(rs.getDate("spend_date"));
            spend.setDescription(rs.getString("description"));
            spend.setAmount(rs.getDouble("amount"));

            String currencyStr = rs.getString("currency");
            if (currencyStr != null) {
                spend.setCurrency(CurrencyValues.valueOf(currencyStr));
            }
            spend.setCategory(category);
            category.getSpends().add(spend);
        }
        return categoryMap.get(categoryId);
    }
}
