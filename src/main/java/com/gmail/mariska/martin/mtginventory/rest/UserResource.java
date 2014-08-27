package com.gmail.mariska.martin.mtginventory.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.gmail.mariska.martin.mtginventory.DatabaseManager;
import com.gmail.mariska.martin.mtginventory.db.model.User;
import com.gmail.mariska.martin.mtginventory.service.UserService;

@Path("/users")
public class UserResource {
    @Context
    ServletContext context;

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<User> getUsers() {
        UserService userService = new UserService(DatabaseManager.getEM(context));
        return userService.getAll();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/{id}")
    public User getTodoById(@PathParam("id") String userId) {
        UserService userService = new UserService(DatabaseManager.getEM(context));
        return userService.findById(userId);
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public User insertUser(User newUser) throws IOException {
        UserService userService = new UserService(DatabaseManager.getEM(context));
        return userService.insert(newUser);
    }

    @DELETE
    @Path("/{id}")
    public User deleteUser(@PathParam("id") String userId) {
        UserService userService = new UserService(DatabaseManager.getEM(context));
        return userService.delete(userId);
    }
}
