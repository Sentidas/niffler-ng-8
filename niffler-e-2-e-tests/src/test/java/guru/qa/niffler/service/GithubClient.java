package guru.qa.niffler.service;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface GithubClient {
    @Nonnull
    String issueState(String issueNumber);
}
