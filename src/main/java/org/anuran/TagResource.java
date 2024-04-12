package org.anuran;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.anuran.model.Tag;
import org.anuran.service.TagService;

import java.util.List;

@Path("/api")
public class TagResource {

    @Inject
    TagService tagService;

    @GET
    @Path("/tags")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tag> findAll() {
//        return tagService.findAll();
        return tagService.findBySourceApp("BLOG");
    }


    @POST
    @Path("/tags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Tag add(Tag tag) {
//        return tagService.add(tag);
        return tagService.addWithSourceApp(tag, "BLOG");
    }
}
