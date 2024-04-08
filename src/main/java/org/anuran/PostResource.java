package org.anuran;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.amazon.lambda.http.model.AwsProxyRequestContext;
import io.quarkus.runtime.util.StringUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.anuran.model.Post;
import org.anuran.model.PostTag;
import org.anuran.model.Tag;
import org.anuran.model.Test;
import org.anuran.service.PostService;
import org.anuran.service.PostTagService;
import org.anuran.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Path("/api")
public class PostResource {

    Logger logger = LoggerFactory.getLogger(PostResource.class);

    @Inject
    PostService postService;

    @Inject
    TagService tagService;

    @Inject
    PostTagService postTagService;

    @GET()
    @Path("/posts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Post> posts() {
        return postService.findAll();
    }

//    @GET()
//    @Path("/posts/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Post getPost(@PathParam("id") String id) {
//        return postService.get(id);
//
//    }

    class PostWithTags {
        Post post;
        List<Tag> tags;

        public PostWithTags(Post post, List<Tag> tags) {
            this.post = post;
            this.tags = tags;
        }
    }

    @GET()
    @Path("/posts/{id}/detail")
    @Produces(MediaType.APPLICATION_JSON)
    public PostWithTags getPost(@PathParam("id") String id) {
        Post post = postService.get(id);

        List<PostTag> postTags = postTagService.findByPostId(id);

        List<String> tagIds = postTags.stream().map(PostTag::getTagId).collect(Collectors.toList());

        List<Tag> tags = tagIds.stream().map(tagId -> tagService.get(id))
                .filter(item -> item != null)
                .collect(Collectors.toList());

        return new PostWithTags(post, tags);
    }


    @PUT()
    @Path("/posts/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Post updatePost(@PathParam("id") String id, Post post) {
        return postService.update(id, post);
    }



    @POST()
    @Path("/posts")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Post addPost(Post post, @Context AwsProxyRequestContext req) {
        String userId = "";
        if (req != null) {
            try {
                logger.info("Proxy request context = {}", new ObjectMapper().writeValueAsString(req));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            userId = req.getAuthorizer().getContextValue("userId");
        }
        logger.info("Received in request context, userId = {}", userId);
//        String username = "anuran.datta@hotmail.com"; //Hardcoded dummy
//        if (!StringUtil.isNullOrEmpty(userId)) {
//            username = userId;
//        }
//        note.setUsername(username);
        return postService.add(post);
    }


    @DELETE()
    @Path("/posts/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deletePost(@PathParam("id") String id) {
        postService.delete(id);
    }
}
