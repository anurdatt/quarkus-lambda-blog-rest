package org.anuran;

import io.quarkus.runtime.util.StringUtil;
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
    public List<Tag> findAll(@QueryParam("tagUrl") String tagUrl,
                             @QueryParam("sourceApp") String sourceApp) {
//        return tagService.findAll();
        if (!StringUtil.isNullOrEmpty(tagUrl)) {
            return tagService.findTagsByTagUrl(tagUrl);
        }
        if (!StringUtil.isNullOrEmpty(sourceApp)) {
            return tagService.findBySourceApp(sourceApp);
        }
        return tagService.findAll();
    }


    @POST
    @Path("/tags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Tag add(Tag tag) {
//        return tagService.add(tag);
        if(!StringUtil.isNullOrEmpty(tag.getSourceApp()))
            return tagService.add(tag);
        return tagService.addWithSourceApp(tag, "BLOG");
    }
}
