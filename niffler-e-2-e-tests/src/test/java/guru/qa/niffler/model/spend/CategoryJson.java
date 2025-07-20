package guru.qa.niffler.model.spend;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public record CategoryJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("name")
    String name,
    @JsonProperty("username")
    String username,
    @JsonProperty("archived")
    Boolean archived,
    List<String> spends) {

    public static @Nonnull CategoryJson fromEntity(@Nonnull CategoryEntity entity) {
        List<String> spends = entity.getSpends().stream()
                .map(a -> a.getDescription())
                .toList();

        return new CategoryJson(
            entity.getId(),
            entity.getName(),
            entity.getUsername(),
            entity.isArchived(),
                spends
        );
    }

    public CategoryJson withArchived(boolean archived) {
        return new CategoryJson(
                this.id(),
                this.name(),
                this.username(),
                archived,
                null
        );
    }
}
