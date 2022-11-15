package rest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.UserDTO;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import entities.*;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.glassfish.grizzly.http.server.HttpServer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeAll;

public class UserResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static User u1, u2;

    private static UserDTO udto1,udto2;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();

        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        Role userRole = new Role("user");
        User u1 = new User();
        User u2 = new User();
        u1.setUserName("Oscar");
        u1.setUserPass("test");
        u1.addRole(userRole);
        u2.setUserName("Mark");
        u2.setUserPass("test");
        u2.addRole(userRole);
        try {
            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.persist(userRole);
            em.persist(u1);
            em.persist(u2);
            em.getTransaction().commit();
        } finally {
            udto1 = new UserDTO(u1);
            udto2 = new UserDTO(u2);
            em.close();
        }
    }
    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/users/all").then().statusCode(200);
    }
    @Test
    public void testLogRequest() {
        System.out.println("Testing logging request details");
        given().log().all()
                .when().get("/users/all")
                .then().statusCode(200);
    }
    @Test
    public void testLogResponse() {
        System.out.println("Testing logging response details");
        given()
                .when().get("/users/all")
                .then().log().body().statusCode(200);
    }

    @Test
    public void testPrintResponse(){
        Response response = given().when().get("/users/"+udto1.getUserName());
        ResponseBody body = response.getBody();
        System.out.println(body.prettyPrint());

        response
                .then()
                .assertThat()
                .body("userName",equalTo("Oscar"));
    }


    @Test
    void allUsers() {
        List<UserDTO> usersDTOS;
        usersDTOS = given()
                .contentType("application/json")
                .when()
                .get("/users/all")
                .then()
                .extract().body().jsonPath().getList("", UserDTO.class);
                 assertThat(usersDTOS,containsInAnyOrder(udto1,udto2));
    }


    @Test
    void getUserByUserName() {
        given()
                .contentType(ContentType.JSON)
                .get("/users/"+ udto1.getUserName())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("userName", equalTo(udto1.getUserName()));
           // ADD ROLES    // .body("ro", equalTo(personDTO1.getEmail()))

    }

    @Test
    void createUser() {
       User user = new User();
       user.setUserName("Chris");
       user.setUserPass("PW");
       Role role = new Role("user");
       user.addRole(role);

       UserDTO udto = new UserDTO(user);
       String requestBody = GSON.toJson(udto);

        given()
                .header("Content-type", ContentType.JSON)
                .and()
                .body(requestBody)
                .when()
                .post("/users")
                .then()
                .assertThat()
                .statusCode(200)
                .body("userName", equalTo("Chris"));
               // ROLE //.body("email", equalTo("michael@mail.com"))

    }


    @Test
    void deleteUser() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("userName", udto1.getUserName())
                .delete("/users/{userName}")
                .then()
                .statusCode(200);
    }
}