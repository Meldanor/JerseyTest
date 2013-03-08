import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import manager.AccountManager;
import security.Token;

@Path("/account")
public class AccountResource {

    @PUT
    @Path("/create/{username}/{password}")
    public Response createUser(@PathParam("username") String username, @PathParam("password") String password) {
        AccountManager aManager = AccountManager.getInstance();
        boolean result = aManager.addUser(username, password);
        if (result)
            return Response.ok().build();
        else
            return Response.serverError().build();
    }

    @GET
    @Path("/verify/{username}/{password}")
    public Response verify(@PathParam("username") String username, @PathParam("password") String password) {
        AccountManager aManager = AccountManager.getInstance();
        boolean result = aManager.validateUser(username, password);
        if (result)
            return Response.ok().build();
        else
            return Response.status(Status.UNAUTHORIZED).build();
    }

    @GET
    @Path("/verifyfilter")
    public Response verifyfilter(@Context HttpHeaders headers) {

        AccountManager aManager = AccountManager.getInstance();

        if (aManager.validateUser(headers))
            return Response.ok().build();
        else
            return Response.status(Status.UNAUTHORIZED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/recToken")
    public Response getToken(@Context HttpHeaders headers) {

        AccountManager aManager = AccountManager.getInstance();
        if (!aManager.validateUser(headers))
            return Response.status(Status.UNAUTHORIZED).build();

        Token token = aManager.generateToken(aManager.extractUser(headers));
        System.out.println(token);
        if (token != null)
            return Response.ok().entity(token).build();
        else
            return Response.serverError().build();
    }
}
