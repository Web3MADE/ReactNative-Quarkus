package org.acme.services;

import static org.mockito.Mockito.when;
import java.util.List;
import org.acme.repositories.UserRepository;
import org.acme.user.User;
import org.acme.user.UserDTO;
import org.junit.jupiter.api.Test;
import io.quarkus.test.InjectMock;
import io.quarkus.test.hibernate.reactive.panache.TransactionalUniAsserter;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

@QuarkusTest
public class UserServiceTest {

    @Inject
    UserService userService;

    @InjectMock
    UserRepository userRepo;

    @RunOnVertxContext
    @Test
    void testGetAllUsers(UniAsserter asserter) {
        // arrange
        User user = new User();
        user.id = 1L;
        user.name = "test";
        user.email = "test@test.com";
        user.password = "test";
        List<User> users = List.of((user));
        // act
        asserter.execute(() -> {
            // Mock the repository method inside the execute block
            when(userRepo.getAllUsers()).thenReturn(Uni.createFrom().item(users));
            // Return a Uni<Void> to indicate the test is done
            return Uni.createFrom().voidItem();
        });
        // assert
        asserter.assertThat(() -> userService.getAllUsers(), userDTOs -> {
            userDTOs.equals(List.of(new UserDTO(user)));
        });
    }

    @RunOnVertxContext
    @Test
    void testGetUserById(UniAsserter asserter) {
        // arrange
        Long id = 1L;
        User user = new User();
        user.id = id;
        user.name = "test";
        user.email = "test@test.com";
        user.password = "test";
        // act
        asserter.execute(() -> {
            when(userRepo.getUserById(id)).thenReturn(Uni.createFrom().item(user));
            return Uni.createFrom().voidItem();
        });

        asserter.assertThat(() -> userService.getUserById(id), userDTO -> {
            userDTO.equals(new UserDTO(user));
        });
    }

    @RunOnVertxContext
    @Test
    void testFindByEmail(UniAsserter asserter) {
        // arrange
        String email = "test@test.com";
        User user = new User();
        user.id = 1L;
        user.name = "test";
        user.email = email;
        user.password = "test";
        // act
        asserter.execute(() -> {
            when(userRepo.findByEmail(email)).thenReturn(Uni.createFrom().item(user));
            return Uni.createFrom().voidItem();
        });

        asserter.assertThat(() -> userService.findByEmail(email), userDTO -> {
            userDTO.equals(new UserDTO(user));
        });
    }

    @RunOnVertxContext
    @Test
    void testCreateUser(TransactionalUniAsserter asserter) {
        // arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setName("test");
        userDTO.setEmail("test@test.com");
        userDTO.setPassword("test");
        User createdUser = new User();
        createdUser.id = 1L;
        createdUser.name = userDTO.getName();
        createdUser.email = userDTO.getEmail();
        createdUser.password = userDTO.getPassword();
        // act
        asserter.execute(() -> {
            when(userRepo.createUser(userDTO)).thenReturn(Uni.createFrom().item(createdUser));
            return Uni.createFrom().voidItem();
        });

        asserter.assertThat(() -> userService.createUser(userDTO), res -> {
            res.equals(createdUser);
        });
    }



    // Will need TransactionalUniAsserter for createUser test



}
