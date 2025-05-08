package com.edziennikarze.gradebook;

import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.admin.AdminService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockitoSetUpTests { //Test exists for showcase purpouses, delete later

    @Test
    void mockitoRunTest(){
        AdminService service = mock(AdminService.class);
        when(service.createAdmin(null)).thenReturn(null);
        Mono<User> result = service.createAdmin(null);
        Assertions.assertNull(result);
    }
}
