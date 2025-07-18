package guru.qa.niffler.data.jpa;

import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.jdbc.JdbcConnectionHolder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManagers {

    private EntityManagers() {
    }

    private static final Map<String, EntityManagerFactory> emfs = new ConcurrentHashMap<>();

    public static EntityManager em(String jdbcUrl) {

        return new ThreadSafeEntityManager(
                emfs.computeIfAbsent(
                        jdbcUrl,
                        key -> {
                            DataSources.dataSource(jdbcUrl);
                            return Persistence.createEntityManagerFactory(jdbcUrl);
                        }
                ).createEntityManager()
        );
    }
    public static void closeAllEmfs() {
        emfs.values().forEach(EntityManagerFactory::close);
    }

}
