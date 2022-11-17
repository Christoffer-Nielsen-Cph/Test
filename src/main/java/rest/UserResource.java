package rest;

import businessfacades.UserDTOFacade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.UserDTO;
import entities.User;

import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import errorhandling.API_Exception;
import errorhandling.NotFoundException;
import utils.EMF_Creator;

/**
 * @author lam@cphbusiness.dk
 */
@Path("users")
public class UserResource {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private UserDTOFacade facade = UserDTOFacade.getInstance(EMF);
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    @GET
    @Path("{userName}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getById(@PathParam("userName") String userName) throws EntityNotFoundException, NotFoundException {
        return Response.ok().entity(GSON.toJson(facade.getUserByUserName(userName))).type(MediaType.APPLICATION_JSON_TYPE.withCharset(StandardCharsets.UTF_8.name())).build();
    }

    @GET
    @Path("/all")
    public Response getAllUsers() throws NotFoundException {
        return Response.ok().entity(GSON.toJson(facade.getAllUsers())).type(MediaType.APPLICATION_JSON_TYPE.withCharset(StandardCharsets.UTF_8.name())).build();
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(String content) throws API_Exception {
        UserDTO userDTO = GSON.fromJson(content, UserDTO.class);
        UserDTO newUserDTO = facade.createUser(userDTO);
        return Response.ok().entity(GSON.toJson(newUserDTO)).type(MediaType.APPLICATION_JSON_TYPE.withCharset(StandardCharsets.UTF_8.name())).build();
    }

    @PUT
    @Path("/{username}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(@PathParam("username") String username, String content) throws EntityNotFoundException, API_Exception {
        UserDTO udto = GSON.fromJson(content, UserDTO.class);
        udto.setUserName(username);
        UserDTO updated = facade.updateUser(udto);
        return Response.ok().entity(GSON.toJson(updated)).type(MediaType.APPLICATION_JSON_TYPE.withCharset(StandardCharsets.UTF_8.name())).build();
    }

    @DELETE
    @Path("{userName}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response deleteUser(@PathParam("userName") String userName) throws API_Exception {
        UserDTO deletedUser = facade.deleteUser(userName);
        return Response.ok().entity(GSON.toJson(deletedUser)).type(MediaType.APPLICATION_JSON_TYPE.withCharset(StandardCharsets.UTF_8.name())).build();
    }
}