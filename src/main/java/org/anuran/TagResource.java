package org.anuran;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.anuran.model.Tag;
import org.anuran.service.TagService;

import java.util.List;

@Path("/api/tags")
public class TagResource {

    @Inject
    TagService tagService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Tag> findAll() {
        return tagService.findAll();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Tag add(Tag tag) {
        return tagService.add(tag);
    }
}
