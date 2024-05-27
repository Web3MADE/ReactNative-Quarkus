package org.acme.controllers;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;
import java.util.List;
import org.acme.services.JwtTokenService;
import org.acme.services.UserService;
import org.acme.user.User;
import org.acme.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import io.smallrye.mutiny.Uni;

@QuarkusTest
@TestHTTPEndpoint(UserController.class)
public class UserControllerTest {

    @InjectMock
    UserService userService;

    @InjectMock
    JwtTokenService jwtTokenService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    public void setUp() {
        // Set up mock user
        user = new User();
        user.id = 1L;
        user.name = "test";
        user.email = "test@example.com";
        user.password = "test";
        // Set up mock userDTO
        userDTO = new UserDTO();
        userDTO.setName("test");
        userDTO.setEmail("test@test.com");
        userDTO.setPassword("test");

    }

    @RunOnVertxContext
    @Test
    void testGetAllUsers(UniAsserter asserter) {
        asserter.execute(() -> {
            when(userService.getAllUsers()).thenReturn(Uni.createFrom().item(List.of(userDTO)));
            return Uni.createFrom().voidItem();
        });
        given().when().get().then().statusCode(200);
        Mockito.verify(userService, Mockito.times(1)).getAllUsers();
    }
    // TODO: refactor createUser and Login UserController endpoints
    // @RunOnVertxContext
    // @Test
    // void testCreateUser(TransactionalUniAsserter asserter) {
    // String mockIssuer = "mockIssuer";
    // Role mockRole = Constants.Role.USER;
    // String mockUpn = "mockUpn";
    // String mockBirthdate = "mockBirthdate";

    // System.out.println("User role name " + mockRole.name());

    // asserter.execute(() -> {
    // when(userService.createUser(userDTO)).thenReturn(Uni.createFrom().item(user));
    // when(jwtTokenService.generateJwtToken(mockIssuer, mockUpn, mockRole, mockBirthdate))
    // .thenReturn("token");
    // return Uni.createFrom().voidItem();
    // });
    // given().contentType("application/json").body(userDTO).when().post().then().statusCode(201);
    // Mockito.verify(userService, Mockito.times(1)).createUser(userDTO);
    // Mockito.verify(jwtTokenService, Mockito.times(1)).generateJwtToken(mockIssuer, mockUpn,
    // mockRole, mockBirthdate);
    // }



}
