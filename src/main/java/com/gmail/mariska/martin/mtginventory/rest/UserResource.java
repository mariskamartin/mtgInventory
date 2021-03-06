package com.gmail.mariska.martin.mtginventory.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.auth.AuthenticationRequired;
import com.gmail.mariska.martin.mtginventory.db.model.AuthModel;
import com.gmail.mariska.martin.mtginventory.db.model.User;
import com.gmail.mariska.martin.mtginventory.service.AuthService;
import com.gmail.mariska.martin.mtginventory.service.ServiceFactory;
import com.gmail.mariska.martin.mtginventory.service.UserService;
import com.google.common.base.Preconditions;

@Path("/users")
public class UserResource {
    private static final Logger logger = Logger.getLogger(UserResource.class);

    @Context
    ServletContext context;

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/authenticate/")
    public User authenticateUser(AuthModel authModel) throws IllegalAccessException {
        UserService userService = ServiceFactory.createUserService(context);
        AuthService authService = new AuthService(userService);
        return authService.authenticate(authModel.getLoginEmail(), authModel.getPassword());
    }

    @AuthenticationRequired
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<User> getUsers() {
        return ServiceFactory.createUserService(context).getAll();
    }

    @AuthenticationRequired
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/{id}")
    public User getTodoById(@PathParam("id") String userId) {
        return ServiceFactory.createUserService(context).findById(userId);
    }

//    @AuthenticationRequired
    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public User insertUser(User newUser) throws IOException {
        logger.debug("new user: " + newUser);
        Preconditions.checkArgument(newUser.getIdEmail() != null, "Chybný uživatelský email.");
        Preconditions.checkArgument(newUser.getName() != null && newUser.getName().length() > 3, "Chybné uživatelské jméno. (Musí mít min. 3 znaky)");
        Preconditions.checkArgument(newUser.getPassword() != null && newUser.getPassword().length() > 3, "Chybné uživatelské heslo. (Musí mít min. 3 znaky)");
        return ServiceFactory.createUserService(context).insert(newUser);
    }

    @AuthenticationRequired
    @PUT
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public User updateUser(User user) throws IOException {
        logger.debug("update user: " + user);
        return ServiceFactory.createUserService(context).update(user);
    }

    @AuthenticationRequired
    @DELETE
    @Path("/{id}")
    public User deleteUser(@PathParam("id") String userId) {
        return ServiceFactory.createUserService(context).delete(userId);
    }
}
