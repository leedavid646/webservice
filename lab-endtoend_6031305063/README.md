Lab - JPA and end-to-end web service development
==========

Before you start
----------
The purpose of this lab is to reinforce and build upon the course material, gaining more practice with JPA, Jackson, and JAX-RS in the end-to-end development of a simple Concert service.

Begin by cloning this repository onto your local machine.
:octocat: :octocat: Follows the same guideline as last lab to import subproject `lab-end2end-concert-domain-model` and `lab-end2end-concert-web-service`

Exercise - Develop a stateless Concert Web service that uses ORM
----------
This exercise develop rest-based services that connect database through JPA framework.
#### (a) Complete the domain model
In `lab-end2end-concert-domain-model`, the `Concert` and `Performer` class are included. This project serves database to the system using JPA. You need to add annotations to these classes, similar to `lab-concert-database` in the previous lab (week 4). You may follows the following guideline:
- add `@Entity` to the entity class.
- add `@Id` and `@GeneratedValue` to the attribute that will be used as primary key.
- add relationship annotation to an attribute that link to the other class such as `@ManyToOne` with proper `fetch` and `cascade` `(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})` so that when concert is saved, performer is also saved if not existed. When concert is removed, its performer is removed when it is no longer associated to any concert.
- add `@Enumerated` where the enum is used such as `Genre`.

The object of these classes should be used to exchange between client and rest service in JSON format. So, please remember to pay attention on some attribute that are date/datetime such `getDate()` as  `@JsonSerialize` and `@JsonDeserialize` may need to be added (Please see the lab `lab rest` at week no. 3).

If you have done it correctly, please run the testing for `lab-end2end-concert-domain-model` using maven's `verify` goal. All test should pass.

#### (b) Complete the rest service
The `lab-end2end-concert-web-service` contains the source code of rest services, smilar to what we have done in `lab rest` (week 3). You have to complete these services using JPA API to connect to database created by `lab-end2end-concert-domain-model` project.

The service is to provide a basic REST interface as follows:

- `GET /services/concerts/{id}`. Retrieves a representation of a `Concert`, identified by its unique ID. The HTTP response message should have a status code of either 200 or 404, depending on whether the specified concert is found.

- `POST /services/concerts`. Creates a `Concert`. The body of the HTTP request message contains a representation of the new concert (other than the unique ID) to create. The service generates the concert's ID via the database, and returns a HTTP response of 201 with a `Location` header storing the URI for the newly created concert.
  
- `PUT /services/concerts`. Updates an existing `Concert`. A representation of the modified concert is stored in the body of the HTTP request message. Being an existing concert that was earlier created by the Web service, it should include a unique ID value. The HTTP status code should be 204 on success, or 404 where the concert isn't known to the Web service.

- `DELETE /services/concerts/{id}`. Deletes a `Concert`, where the concert to delete is specified by a unique ID. This operation returns either 204 or 404, depending on whether the concert exists.

- `DELETE /services/concerts`. Deletes all `Concert`s, and returns a 204 status code.

:question: :exclamation: The persistent unit `lab.end2end.concert` is defined in `persistence.xml`. To integrate the rest service to database, we have `PersistenceManager.java` to link this persistence unit. Please follow the guideline below to complete coding:

##### In `ConcertApplication.java`, modify the following
:smiley_cat: **You may try to look at `lab-concert-rest` project in `lab rest` (week 3)**
- Add proper `@ApplicationPath` 
- In the constructor, add the lines below to register persistence manager and resource class

```java
singletons.add(PersistenceManager.instance());
classes.add(ConcertResource.class);
```
##### In `ConcertResource.java`, modify the following:
:smiley_cat: **You may try to look at `lab-concert-rest` project in `lab rest` (week 3) and how we manage data in database using entity manager in `lab jpa` at week 4 (Exercise 1c)** 
- add proper path `@Path` and produce/consumes `@Produce` and `@Consume` for JSON. 
- Please follow `TODO` in the source code of this class to complete the services. Also read the reference below for a guideline.

:grin::grin:**Running the maven's verify at `lab-end2end-concert-web-service` may result in failure, as the project need the database to run, please run at the parent project `lab-entdev-end2end`**

##### Reference: Use of an EntityManager	
In implementing the `Resource` class' handler methods, you'll need to use the `EntityManager`. The typical usage pattern for `EntityManager` is shown below. Particular `EntityManager` methods that will be useful for this task include:

- `find(Class, primary-key)`. `find()` looks up an object in the database based on the type of object and primary-key value arguments. If a match is found, this method returns an object of the specified type and with the given primary key. If there's no match the method returns `null`.

- `persist(Object)`. This persists a new object in the database when the enclosing transaction commits.

- `merge(Object)`. A `merge()` call updates an object in the database. When the enclosing transaction commits, the database is updated based on the state of the in-memory object to which the `merge()` call applies.

- `remove(Object)`. This deletes an object from the database when the transaction commits.

For this task, the above `EntityManager` methods are sufficient. The `EntityManager` interface will be discussed in more detail this week; for more information in the meantime consult the Javadoc for `javax.persistence.EntityManager` (<https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html>).

A simple JPQL query to return all `Concert`s might be useful for this task, and this can be expressed simply as:

```java
EntityManager em = PersistenceManager.instance().createEntityManager();
TypedQuery<Concert> concertQuery = em.createQuery("select c from Concert c", Concert.class);
List<Concert> concerts = concertQuery.getResultList();
```


###### EntityManager usage scenario

```java
// Acquire an EntityManager (creating a new persistence context).
EntityManager em = PersistenceManager.instance().createEntityManager();
try {
    
    // Start a new transaction.
    em.getTransaction().begin();
    
    // Use the EntityManager to retrieve, persist or delete object(s).
    // Use em.find(), em.persist(), em.merge(), etc...
    
    // Commit the transaction.
    em.getTransaction().commit();
    
} finally {
    // When you're done using the EntityManager, close it to free up resources.
    em.close();
}
```


#### (c) Build and Test the project
You can also build and run maven `verify` as usual at parent project `lab-entdev-end2end`

#### Do not forget to Commit and Push code to github