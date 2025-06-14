package com.edziennikarze.gradebook;

import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockitoSetUpTests { //Test exists for showcase purpouses, delete later

    @Test
    void mockitoRunTest(){
        UserService service = mock(UserService.class);
        when(service.createUser(null)).thenReturn(null);
        Mono<User> result = service.createUser(null);
        Assertions.assertNull(result);
    }
}
