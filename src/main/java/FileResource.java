import java.io.File;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/files")
public class FileResource {

    @Path("/download/{file}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getFile(@PathParam("file") String file) {

        File f = new File("downloads", file);
        if (!f.exists()) {
            throw new WebApplicationException(404);
        }

        String mt = new MimetypesFileTypeMap().getContentType(f);
        return Response.ok(f, mt).build();
    }

    @Path("/list")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String listFiles() {
        File f = new File("src/test/resources/download", "");
        return buildFileList(f);
    }

    private String buildFileList(File root) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("Files:\n");
        buildFileList(root, sBuilder);
        return sBuilder.toString();
    }

    private void buildFileList(File dir, StringBuilder sBuilder) {
        File[] files = dir.listFiles();
        if (files == null)
            return;
        for (File file : files) {
            if (file.isDirectory()) {
                buildFileList(file, sBuilder);
            } else {
                sBuilder.append(file.getPath().substring("src/test/resources/download/".length()));
                sBuilder.append('\n');
            }
        }
    }

}
