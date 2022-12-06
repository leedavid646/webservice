Lab - Java Persistence
==========

Before you start
----------
The purpose of this lab is to reinforce and build upon the lecture material concerning Hibernate.

Begin by cloning this repository, then import the project onto your Eclipse, as well as subprojects.

 :octocat: :octocat: Follows the same guideline as last lab to import subproject `lab-parolee-database`  and `lab-concert-database`

:sunglasses: **HINT: You may find where to complete code at TODO: in the source code**

Here is overview of what we will do. We will create a database using JPA. H2 will be used as a database. The test program will be created to manage data on the database through Entity Manager.

![system overview](overview.png)


Exercise One - Parolee Database
----------
This exercise guide you how to build up database using Java Persistence (JPA)/Hibernate. `lab-parolee-database` includes the skeleton of source code that include Plain Old Java Object (POJO) classes such as `Parolee`.

#### (a) Make Entity class 
In `Parolee.java`, we need to convert it into Entity class used in JPA by following this steps

- Add `@Entity` above the class declaration
- Define `id` field as an identification number (or primary key) by add the annotations above the field declaration:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
```

- As the `gender` is an `enum` called `Gender`, we need to persisted this on database as String so we added `@Enumerated(EnumType.STRING)` above the field declaration.

#### (b) Database Configuration
For JPA, we need a configuration in `persistence.xml` (Under project explorer, you can find this under JPA Content) to specify the persistence unit and which database we use to store the data. In this exercise, we will use  H2 as database.  H2 database could be configured to persist data to the local file system (For Maven run, the connection is configured such that the database exists only in memory, in which case its data would be lost once the JVM running the database shuts down). In `persistence.xml`, add the configuration below between `<persistence>`

```
    <persistence-unit name="entdev.parolee">
        <exclude-unlisted-classes>false</exclude-unlisted-classes>
        <properties>
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver" />
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:~/test;mv_store=false" />
            <property name="javax.persistence.jdbc.user" value="sa" />
            <property name="javax.persistence.jdbc.password" value="sa" />

            <property name="javax.persistence.schema-generation.database.action"
                      value = "none" />

            <property name="hibernate.show_sql" value="false" />
            <property name="hibernate.format_sql" value="true" />
            <property name="hibernate.use_sql_comments" value="true" />
        </properties>
    </persistence-unit>
```
This configuration define an persistence unit called `entdev.parolee`. It is configured to use H2 database. This database will automatically be created when we run maven for testing. 

#### (c) Complete Development of Unit Test
This step will guide you to create a unit test that read/update/delete data on the database through JPA. 

In Unit Test, There are 3 types of method 1) `Before` method, 2)`Test` method and 3)`After` method.
The `Before` method runs before each `Test` methods run. The `After` method run after each `Test` methods is complete. You may study more details at [Junit Tutorial](https://www.javatpoint.com/junit-tutorial)

Let's modify `ParoleeTest.java` (under `src/test/java`)

`initialiseDatabase()` is a `Before` method as you see it is annotated with `@Before`. This method helps to create schema and insert some sample data to database for testing. This will be done according to script `db-init.sql` (can be found under `/src/main/resources`). In addition to this, we need to initialise `EntityManagerFactory` in this method by inserting the code below in this method.

```java
entityManagerFactory = Persistence.createEntityManagerFactory("entdev.parolee");
```
It can be seen that we define the persistence unit `entdev.parolee` as defined in the previous step.

`closeDatabase()` is a `After` method. We need to close entity manager, which close the connection to the database after testing. Please insert the code below. 

```java
 entityManagerFactory.close();
```

Now let's query the data for testing by adding some code to `queryAllParolees()` method. This method is a test method for querying all parolees. Every time, we access database, the `EntityManager` will be created from the factory and closed at `finally`.

Please insert the code below between `try` to select all parolees ordered by their firstname.

```java
em.getTransaction().begin();
List<Parolee> parolees = em.createQuery("select c from Parolee c order by c.firstName", Parolee.class).getResultList();
em.getTransaction().commit();
```

For safety, this code creates a query within a transaction so the query (`em.createQuery()`) is performed between `em.getTransaction().begin()` and `em.getTransaction().commit()`.

Then we need to check if the results return correctly, please insert the code below after last code we added (within `try` block). This code check if we have 5 parolees returned, if the first parolee's name is *Danny* and his date of birth is on 1913-7-11.

```java
// Check that the List contains all  stored in the database.
assertEquals(5, parolees.size());

// Check that the first parolee is correct.
Parolee parolee = parolees.get(0);
assertEquals("Danny", parolee.getFirstName());
assertEquals(LocalDate.of(1913, 7, 11), parolee.getDateOfBirth());
```

Next step let's create a new parolee for testing in `addParolee()`. Please insert the code below within `try` block. This code create a new parolee and use `em.persist()` to save an object of parolee.

```java
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
```

:eyeglasses: :eyeglasses: :eyeglasses: You should study at the code in `deleteParolee()` and `updateParolee()`, which shows how we remove a record using `em.remove()` and update record using `em.merge()`. We may use these in other lab.

#### (d) Build and Run
You can simply run unit-testing `ParoleeTest` using `Run As> Junit Test` option in Eclipse. The unit test should be all green. :green_heart: :green_heart: :green_heart:

You can also run maven `verify` as usual, which will call `ParoleeTest` to run.



Exercise Two - Complete the Concert database application
----------
For this exercise, complete the `lab-jpa-database` project. This project helps to build the database to store concert information.

The project is a simple Maven project using JPA / Hibernate to enable the persistence of `Concert`s and `Performer`s. It contains the following classes:


The project is a simple Maven project that includes the following key artifacts:

- Domain classes `Concert` and `Performer`. `Concert` has a unique ID, a title, a date and one `Performer`. `Performer` has a unique ID, a name, image file and `Genre`. A `Performer` can feature in many `Concert`s, hence there's a one-to-many relationship between `Performer` and `Concert`. The relationship is unidirectional, from `Concert` to `Performer`.

- Class `ConcertTest`, a unit test that tests the Hibernate `EntityManagerFactory` and `EntityManager` classes to access the database.
 
- `db-init.sql`, a database initialisation script that includes SQL DDL and DML statements to create tables for `Concert` and `Performer`, and to populate the tables with data. This script is run automatically at the beginning of each unit test.

- `persistence.xml`, containing the definition of the Hibernate *persistence unit* (rules determining which database to connect to; how to connect; whether to automatically generate tables; and which classes to persist in that database). In this case, the persistence unit is set up **not** to automatically generate tables, as we are creating them manually in `db-init.sql`. The `<exclude-unlisted-classes>` tag will force Hibernate to scan your codebase and include all `@Entity`s when set to `false`.

#### (a) Annotate the domain classes
For this task, annotate the `Concert` and `Performer` classes using `@Entity` `@Id` and `@GeneratedValue`. 

Important things to consider:

- The relationship between the two entities is as follows. Each `Concert` only has one performer. Each `Performer` may perform in any number of concerts. The only field in the Java classes describing this relationship is the `performer` field in `Concert`. Please place the annotation (as shown below) above `performer` to define this relationship

```java
 @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
```

This annotation is defined to *cascade* the persistence. For example, when a new `Concert` is created, with a new `Performer`, one might with to persist both entities at once with only a single `persist` call.

- `Performer`'s `genre` field is an `enum` type. We want to persist this in the database as a `String`. Investigate how we can configure this using the `@Enumerated` annotation.

- The pre-defined database in `db-init.sql` uses `AUTO_INCREMENT` to automatically generate and assign valid IDs to newly persisted entities. To force Hibernate to utilize the `AUTO_INCREMENT` functionality rather than its own generation strategy, we can set the `strategy` property of the `@GeneratedValue` annotation to `GenerationType.IDENTITY`.

#### (b) Run the unit tests
Once you've annotated your classes, simply run the unit tests from your IDE (there is no need to run a Maven goal for this project, as we are not running integration tests which require an active server). 

The unit test `ConcertTest` is complete, so you do not need to modify it.:smile_cat::smile_cat::smile_cat:  

The unit tests should pass. If they do not, modify the annotations from task (a) until they do. You should not need to modify anything in the project, other than adding JPA annotations.

#### Do not forget to Commit and Push code to github

#### Resources

Useful resources for H2  include the H2 website:

<http://www.h2database.com/html/main.html>

From here, you can download the H2 Console for your own machines. The website also has useful information, e.g. the SQL grammar for H2.