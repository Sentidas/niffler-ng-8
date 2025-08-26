package guru.qa.niffler.model.userdata;

public record UserJsonError(
        String type,
        String title,
        int status,
        String detail,
        String instance
){
}
