package org.anuran;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.anuran.model.Comment;
import org.anuran.model.Post;
import org.anuran.model.Tag;
import org.anuran.service.CommentService;
import org.anuran.service.TagService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Path("/api")
public class CommentResource {

    @Inject
    CommentService commentService;

    public static class NestedComment {
        Comment comment;
        List<NestedComment> comments;

        public NestedComment() {
        }

        public NestedComment(Comment comment, List<NestedComment> comments) {
            this.comment = comment;
            this.comments = comments;
        }

        public Comment getComment() {
            return comment;
        }

        public void setComment(Comment comment) {
            this.comment = comment;
        }

        public List<NestedComment> getComments() {
            return comments;
        }

        public void setComments(List<NestedComment> comments) {
            this.comments = comments;
        }
    }


    @GET
    @Path("/comments")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Comment> findAll(@QueryParam("sourceId") String sourceId) {
        if (sourceId == null) {
//        return commentService.findAll();
            return commentService.findBySourceApp("BLOG");
        } else {
            return commentService.findBySourceId(sourceId);
        }
    }

//    @GET
//    @Path("/comments/{sourceId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<Comment> findBySourceId(@PathParam("sourceId") String sourceId) {
//        assert sourceId != null;
//        return commentService.findBySourceId(sourceId);
//    }

    @GET
    @Path("/nested-comments")
    @Produces(MediaType.APPLICATION_JSON)
    public List<NestedComment> findAllNested(@QueryParam("sourceId") String sourceId) {
        List<Comment> comments;
        if (sourceId == null){
//            comments = commentService.findAll();
            comments = commentService.findBySourceApp("BLOG");
        } else {
            comments = commentService.findBySourceId(sourceId);
        }

        List<Comment> topLevelComments = comments.stream()
                .filter(Objects::nonNull)
                .filter(comment -> comment.getParentId() == null || comment.getParentId().isEmpty())
                .collect(Collectors.toList());
        return topLevelComments.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Comment::getDate).reversed())
                .map(comment -> new NestedComment(comment, getComments(comment.getId())))
                .collect(Collectors.toList());
    }

//    @GET
//    @Path("/nested-comments/{sourceId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<NestedComment> findNestedBySourceId(@PathParam("sourceId") String sourceId) {
//        assert sourceId != null;
//        List<Comment> comments = commentService.findBySourceId(sourceId);
//        List<Comment> topLevelComments = comments.stream()
//                .filter(Objects::nonNull)
//                .filter(comment -> comment.getParentId() == null || comment.getParentId().isEmpty())
//                .collect(Collectors.toList());
//        return topLevelComments.stream()
//                .filter(Objects::nonNull)
//                .filter(Objects::nonNull)
//                .map(comment -> new NestedComment(comment, getComments(comment.getId())))
//                .collect(Collectors.toList());
//    }

    List<NestedComment> getComments(String parentId) {
        List<Comment> comments = commentService.findByParentId(parentId);

        return comments.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Comment::getDate).reversed())
                .map(comment -> new NestedComment(comment, getComments(comment.getId())))
                .collect(Collectors.toList());

    }

    @GET
    @Path("/comments/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Comment findById(@PathParam("id") String id) {
        return commentService.get(id);
    }

    @GET
    @Path("/nested-comments/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public NestedComment findNestedById(@PathParam("id") String id) {
        return new NestedComment(commentService.get(id), getComments(id));
    }

    @POST
    @Path("/comments")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Comment add(Comment comment) {
//        return commentService.add(tag);
        return commentService.addWithSourceApp(comment, "BLOG");
    }

    @PUT
    @Path("/comments/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Comment update(@PathParam("id") String id, Comment comment) {
        return commentService.update(id, comment);
    }


    @DELETE()
    @Path("/comments/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deletePost(@PathParam("id") String id) {
        commentService.delete(id);
    }


}
