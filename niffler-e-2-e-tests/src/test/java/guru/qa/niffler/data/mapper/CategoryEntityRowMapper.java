package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryEntityRowMapper implements RowMapper<CategoryEntity> {

    public static final CategoryEntityRowMapper instance = new CategoryEntityRowMapper();

    private CategoryEntityRowMapper() {
    }

    @Override
    public CategoryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoryEntity result = new CategoryEntity();
        result.setId(rs.getObject("category_id", UUID.class));
        result.setName(rs.getString("category_name"));
        result.setUsername(rs.getString("category_username"));
        result.setArchived(rs.getBoolean("archived"));
        return result;
    }
}
