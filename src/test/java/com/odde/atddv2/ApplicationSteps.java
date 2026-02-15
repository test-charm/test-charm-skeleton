package com.odde.atddv2;

import com.github.leeonky.cucumber.restful.RestfulStep;
import com.github.leeonky.dal.Assertions;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.util.Sneaky;
import com.odde.atddv2.entity.User;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.SneakyThrows;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;
import java.sql.Statement;
import java.util.*;
import java.util.function.Consumer;

@ContextConfiguration(classes = {CucumberConfiguration.class}, loader = SpringBootContextLoader.class)
@CucumberContextConfiguration
public class ApplicationSteps {

    @Autowired
    private JFactory jFactory;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Value("${testcharm.dal.dumpinput:true}")
    private boolean dalDumpInput;

    @Before
    public void disableDALDump() {
        Assertions.dumpInput(dalDumpInput);
    }

    private Set<String> allTableNames() {
        return new HashSet<>(Arrays.asList(
                "users",
                "orders",
                "order_lines"
        ));
    }

    private void cleanTables(Collection<String> tables) {
        executeDB(entityManager -> entityManager.unwrap(Session.class).doWork(connection -> Sneaky.run(() -> {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=0");

                for (String table : tables) {
                    int deleted = stmt.executeUpdate("DELETE FROM `" + table + "`");
                    if (deleted > 0) stmt.execute("ALTER TABLE `" + table + "` AUTO_INCREMENT = 1");
                }

                stmt.execute("SET FOREIGN_KEY_CHECKS=1");
            }
        })));
    }

    private void executeDB(Consumer<EntityManager> consumer) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        consumer.accept(manager);
        transaction.commit();
        manager.close();
    }

    @Before(order = 0)
    public void clearDB() {
        jFactory.getDataRepository().clear();
        cleanTables(allTableNames());
    }

    @Autowired
    private RestfulStep restfulStep;

    @PostConstruct
    public void setBaseUrl() {
        restfulStep.setBaseUrl("http://127.0.0.1:10082/api/");
    }

    @SneakyThrows
    @Before("@api-login")
    public void apiLogin() {
        User defaultUser = new User().setUserName("j").setPassword("j");
        jFactory.spec("用户").property("userName", "j").property("password", "j").create();
        RestfulStep loginRestfulStep = new RestfulStep();
        loginRestfulStep.setBaseUrl("http://localhost:10082");
        loginRestfulStep.post("/users/login", defaultUser);
        restfulStep.header("token", (String)loginRestfulStep.response("headers.Token"));
    }

}
