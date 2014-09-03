package com.gmail.mariska.martin.mtginventory.service;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;

import org.junit.Test;


public class UserServiceTest {

    @Test
    public void test() {
        EntityManager em = mock(EntityManager.class);
        UserService userService = new UserService(em);



        fail("Not yet implemented");
    }

}
