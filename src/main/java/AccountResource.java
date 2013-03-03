import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import manager.AccountManager;

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

        MultivaluedMap<String, String> h = headers.getRequestHeaders();
        AccountManager aManager = AccountManager.getInstance();

        if (aManager.validateUser(h.getFirst("User"), h.getFirst("Password")))
            return Response.ok().build();
        else
            return Response.status(Status.UNAUTHORIZED).build();
    }
}
