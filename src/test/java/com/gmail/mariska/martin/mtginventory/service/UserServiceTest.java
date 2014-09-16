package com.gmail.mariska.martin.mtginventory.service;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;

import org.junit.Test;

import com.gmail.mariska.martin.mtginventory.db.UserDao;


public class UserServiceTest {

    @Test
    public void test() {
        EntityManager em = mock(EntityManager.class);
        UserDao userDao = new UserDao(em);
        UserService userService = new UserService(em, userDao);



        fail("Not yet implemented");
    }

}
