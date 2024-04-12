package org.anuran;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.amazon.lambda.http.model.AwsProxyRequestContext;
import io.quarkus.runtime.util.StringUtil;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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
import java.util.Objects;
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


    public static class PostWithTags {
        Post post;
        List<Tag> tags;

        public PostWithTags() {
        }

        public PostWithTags(Post post, List<Tag> tags) {
            this.post = post;
            this.tags = tags;
        }

        public Post getPost() {
            return post;
        }

        public void setPost(Post post) {
            this.post = post;
        }

        public List<Tag> getTags() {
            return tags;
        }

        public void setTags(List<Tag> tags) {
            this.tags = tags;
        }
    }

    @GET()
    @Path("/posts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PostWithTags> posts() {
        List<Post> posts = postService.findAll();
        return posts
                .stream()
                .map(p -> new PostWithTags(p, postTagService.findByPostId(p.getId())
                        .stream()
                        .map(PostTag::getTagId)
                        .map(tid -> tagService.get(tid))
                        .collect(Collectors.toList()))
                ).collect(Collectors.toList());
    }

//    @GET()
//    @Path("/posts/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Post getPost(@PathParam("id") String id) {
//        return postService.get(id);
//
//    }


    @GET()
    @Path("/posts/{id}/detail")
    @Produces(MediaType.APPLICATION_JSON)
    public PostWithTags getPost(@PathParam("id") String id) {
        Post post = postService.get(id);

        List<PostTag> postTags = postTagService.findByPostId(id);

        List<String> tagIds = postTags.stream().map(PostTag::getTagId).collect(Collectors.toList());

        List<Tag> tags = tagIds.stream().map(tagId -> tagService.get(tagId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new PostWithTags(post, tags);
    }


    @PUT()
    @Path("/posts/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional()
    public PostWithTags updatePost(@PathParam("id") String id, PostWithTags postWithTags) {
        Post post = postService.update(id, postWithTags.post);
        postWithTags.tags.stream().forEachOrdered(tag -> {
            if (postTagService.findByPostIdAndTagId(post.getId(), tag.getId()).size() == 0) {
                postTagService.add(new PostTag(post.getId(), tag.getId()));
            }
        });
        postTagService.findByPostId(post.getId()).stream().forEachOrdered(postTag -> {
            if(!postWithTags.tags.stream().anyMatch(tag -> tag.getId().equals(postTag.getTagId()))) {
                logger.info("Going to DELETE <" + postTag.getTagId() + ">");
                postTagService.delete(postTag.getId());
            }
        });
        return new PostWithTags(postWithTags.post, postWithTags.tags);
    }



    @POST()
    @Path("/posts")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional()
    public PostWithTags addPost(PostWithTags postWithTags, @Context AwsProxyRequestContext req) {
        String userId = "";
        if (req != null) {
            try {
                logger.info("Proxy request context = {}", new ObjectMapper().writeValueAsString(req));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
//            userId = req.getAuthorizer().getContextValue("userId");
        }
        logger.info("Received in request context, userId = {}", userId);
//        String username = "anuran.datta@hotmail.com"; //Hardcoded dummy
//        if (!StringUtil.isNullOrEmpty(userId)) {
//            username = userId;
//        }
//        note.setUsername(username);
        Post post = postService.add(postWithTags.post);
        postWithTags.tags
                .stream()
                .forEachOrdered(tag ->
                        postTagService.add(new PostTag(post.getId(), tag.getId())));

        return new PostWithTags(post, postWithTags.tags);
    }


    @DELETE()
    @Path("/posts/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deletePost(@PathParam("id") String id) {
        postService.delete(id);
    }
}
