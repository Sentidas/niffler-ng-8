package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.spend.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SpendAndCategoryEntityRowMapper implements RowMapper<SpendEntity> {

    public static final SpendAndCategoryEntityRowMapper instance = new SpendAndCategoryEntityRowMapper();

    private SpendAndCategoryEntityRowMapper() {
    }

    @Override
    public SpendEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        SpendEntity result = new SpendEntity();
        result.setId(rs.getObject("spend_id", UUID.class));
        result.setUsername(rs.getString("spend_username"));
        result.setSpendDate(rs.getDate("spend_date"));
        result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        result.setAmount(rs.getDouble("amount"));
        result.setDescription(rs.getString("description"));
        UUID categoryId = rs.getObject("spend_category_id", UUID.class);

        CategoryEntity resultCategory = new CategoryEntity();
        resultCategory.setId(categoryId);
        result.setCategory(resultCategory);
        resultCategory.setUsername(rs.getString("category_username"));
        resultCategory.setName(rs.getString("category_name"));
        resultCategory.setArchived(rs.getBoolean("archived"));
        return result;
    }
}
