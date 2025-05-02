package com.edziennikarze.gradebook;

import com.edziennikarze.gradebook.user.admin.AdminService;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

public class MockitoSetUpTests { //Test exists for showcase purpouses, delete later
    private static AdminService service;

//    @BeforeAll
//    static void mockitoSetUpTest(){
//        service = mock(AdminService.class);
//        when(service.createUser(null)).thenReturn(null);
//    }
//
//    @Test
//    void mockitoRunTest(){
//        Mono<User> createdUser =
//        assertNull(createdUser);
//    }
}
