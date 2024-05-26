package org.acme.services;

import static org.mockito.Mockito.when;
import java.util.List;
import org.acme.repositories.UserRepository;
import org.acme.user.User;
import org.acme.user.UserDTO;
import org.junit.jupiter.api.Test;
import io.quarkus.test.InjectMock;
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
            return Uni.createFrom().voidItem();
        });
        // assert
        asserter.assertThat(() -> userService.getAllUsers(), userDTOs -> {
            userDTOs.equals(List.of(new UserDTO(user)));
        });
    }

    // Will need TransactionalUniAsserter for createUser test



}