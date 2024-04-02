package org.anuran.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.anuran.model.Post;
import org.anuran.util.DDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PostService {

    private DynamoDbTable<Post> postTable;

    Logger logger = LoggerFactory.getLogger(PostService.class);


    @Inject
    public PostService(DDBUtil ddbUtil) {
        DynamoDbEnhancedClient enhancedClient = ddbUtil.getEnhancedDDBClient();
        postTable = enhancedClient.table("Post", TableSchema.fromBean(Post.class));
    }


    public List<Post> findAll() {
        return postTable.scan().items().stream().collect(Collectors.toList());
    }

    public Post get(String id) {
        Key partitionKey = Key.builder().partitionValue(id).build();
        return postTable.getItem(partitionKey);
    }

    public Post update(String id, Post post) {
        post.setId(id);
        UpdateItemEnhancedRequest request = UpdateItemEnhancedRequest
                .builder(Post.class)
                .ignoreNulls(true).item(post).build();
        return postTable.updateItem(request);
    }

    public Post add(Post post) {
//        String id = UUID.randomUUID().toString();
        Long did = new Date().getTime();
        String tid = post.getTitle()
//                .replaceAll("[!@#'$%^&*]", "")
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .replaceAll(" ", "-");
        post.setId(tid + "-" + did);
        postTable.putItem(post);
        return post;
    }

    public Post delete(String id) {
        Key partitionKey = Key.builder().partitionValue(id).build();
        return postTable.deleteItem(partitionKey);
    }
}