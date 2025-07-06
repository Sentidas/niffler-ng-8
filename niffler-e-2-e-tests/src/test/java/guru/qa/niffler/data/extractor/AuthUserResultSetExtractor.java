package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthUserResultSetExtractor implements ResultSetExtractor<AuthUserEntity> {


    public static final AuthUserResultSetExtractor instance = new AuthUserResultSetExtractor();

    @Override
    public AuthUserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, AuthUserEntity> userCache = new ConcurrentHashMap<>();
        UUID userId = null;

        while (rs.next()) {
            userId = rs.getObject("user_id", UUID.class);

            AuthUserEntity user = userCache.get(userId);
            if (user == null) {
                AuthUserEntity newUser = new AuthUserEntity();

                newUser.setId(userId);
                newUser.setUsername(rs.getString("username"));
                newUser.setPassword(rs.getString("password"));
                newUser.setEnabled(rs.getBoolean("enabled"));
                newUser.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                newUser.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                newUser.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));

                userCache.put(userId, newUser);
                user = newUser;
            }

            AuthorityEntity authority = new AuthorityEntity();
            authority.setId(rs.getObject("authority_id", UUID.class));
            authority.setAuthority(Authority.valueOf(rs.getString("authority")));

            user.addAuthorities(authority);
            //   authority.setUser(user);
            //  user.getAuthorities().add(authority);
        }
        return userId != null ? userCache.get(userId) : null;
    }
}
