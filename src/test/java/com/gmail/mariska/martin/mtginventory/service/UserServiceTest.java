package com.gmail.mariska.martin.mtginventory.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.gmail.mariska.martin.mtginventory.db.UserDao;
import com.gmail.mariska.martin.mtginventory.db.model.User;

public class UserServiceTest {

    @Test
    public void test() {
        EntityManager em = mock(EntityManager.class);
        UserDao mockUserDao = mock(UserDao.class);
        UserService userService = new UserService(em, mockUserDao);

        User user = new User();
        when(mockUserDao.getAll()).thenReturn(Arrays.asList(user, user));

        List<User> all = userService.getAll();
        Assert.assertEquals(user, all.get(0));

        verify(em, Mockito.times(0)).getTransaction();
//        Mockito.verify(em).getTransaction().begin();
//        Mockito.verify(em).getTransaction().commit();
    }

}
