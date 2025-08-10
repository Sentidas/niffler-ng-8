package guru.qa.niffler.model.friend;

public record UserJsonError(
        String type,
        String title,
        int status,
        String detail,
        String instance
){
}
