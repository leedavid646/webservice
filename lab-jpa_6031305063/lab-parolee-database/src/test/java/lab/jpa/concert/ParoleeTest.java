package lab.jpa.concert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.h2.tools.RunScript;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lab.parolee.domain.Gender;
import lab.parolee.domain.Parolee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ParoleeTest {

    private static final String DB_INIT_SCRIPT_DIRECTORY = "src/test/resources";
    private static final String DB_INIT_SCRIPT = "db-init.sql";

    private static final String DATABASE_DRIVER_NAME = "org.h2.Driver";
    private static final String DATABASE_URL = "jdbc:h2:~/test;mv_store=false";
    private static final String DATABASE_USERNAME = "sa";
    private static final String DATABASE_PASSWORD = "sa";

    private EntityManagerFactory entityManagerFactory;

    @Before
    public void initialiseDatabase() throws ClassNotFoundException, SQLException, IOException {
        File file = new File(DB_INIT_SCRIPT_DIRECTORY + "/" + DB_INIT_SCRIPT);

        Class.forName(DATABASE_DRIVER_NAME);

        // Create test data
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
            FileReader reader = new FileReader(file);
            RunScript.execute(conn, reader);
            reader.close();
        }

        // TODO: Create EMF.
        entityManagerFactory = Persistence.createEntityManagerFactory("entdev.parolee");

    }

    @After
    public void closeDatabase() {

        // TODO: Close EMF
        entityManagerFactory.close();

    }

    @Test
    public void queryAllParolees() {

        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            // TODO: query all parolees
            em.getTransaction().begin();
            List<Parolee> parolees = em.createQuery("select c from Parolee c order by c.firstName", Parolee.class)
                    .getResultList();
            em.getTransaction().commit();

            // TODO: Check that the List contains all stored in the database.
            // Check that the List contains all stored in the database.
            assertEquals(5, parolees.size());

            // Check that the first parolee is correct.
            Parolee parolee = parolees.get(0);
            assertEquals("Danny", parolee.getFirstName());
            assertEquals(LocalDate.of(1913, 7, 11), parolee.getDateOfBirth());

        } finally {
            em.close();
        }
    }

    @Test
    public void queryParolee() {

        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            Parolee parolee1 = em.find(Parolee.class, 1L);
            Parolee parolee2 = em.find(Parolee.class, 1L);
            em.getTransaction().commit();

            // The DAO is expected to return 2 Concert objects with the same
            // value.
            assertEquals(parolee1, parolee2);

            // The DAO is expected to return the same Concert.
            assertSame(parolee1, parolee2);
        } finally {
            em.close();
        }
    }

    @Test
    public void addParolee() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            // TODO: Create a new parolee
            em.getTransaction().begin();
            LocalDate date = LocalDate.of(2005, 12, 1);
            Parolee parolee = new Parolee();

            parolee.setFirstName("TestFirstName");
            parolee.setLastName("TestLastName");
            parolee.setGender(Gender.Male);
            parolee.setDateOfBirth(date);

            // Save the new Concert.
            em.persist(parolee);
            em.getTransaction().commit();

            // Query all Concerts and pick out the new Concert.
            em.getTransaction().begin();
            Parolee result = em
                    .createQuery("select c from Parolee c where c.firstName = :fn", Parolee.class)
                    .setParameter("fn", "TestFirstName")
                    .getSingleResult();
            em.getTransaction().commit();

            // Check that the new Concert's ID has been assigned.
            assertNotNull(result.getId());

        } finally {
            em.close();
        }
    }

    @Test
    public void deleteParolee() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            Parolee result = em
                    .createQuery("select c from Parolee c where c.firstName = :fn", Parolee.class)
                    .setParameter("fn", "Michael")
                    .getSingleResult();

            // Delete the Concert.
            long id = result.getId();
            em.remove(result);
            em.getTransaction().commit();

            // Requery the Concert to check it's been deleted.
            em.getTransaction().begin();
            Parolee g = em.find(Parolee.class, id);
            em.getTransaction().commit();
            assertNull(g);

        } finally {
            em.close();
        }
    }

    @Test
    public void updateParolee() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            // Query Concert with the ID 11 (this represents "Dangerous Woman").
            Parolee dangerousWoman = em.find(Parolee.class, 5L);

            // Update the Concert's date, postponing it by one week.
            LocalDate newDate = LocalDate.of(2017, 8, 17);
            dangerousWoman.setDateOfBirth(newDate);

            // Save the updated Concert (and Performer).
            em.merge(dangerousWoman);
            em.getTransaction().commit();

            // Requery the Concert.
            em.getTransaction().begin();
            Parolee parolee = em.find(Parolee.class, 5L);

            // Check that the Concert's date has been updated.
            assertEquals(newDate, parolee.getDateOfBirth());

        } finally {
            em.close();
        }
    }
}
