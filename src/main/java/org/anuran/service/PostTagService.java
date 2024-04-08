package org.anuran.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.anuran.model.PostTag;
import org.anuran.model.Tag;
import org.anuran.util.DDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class PostTagService {

    private DynamoDbTable<PostTag> postTagTable;

    Logger logger = LoggerFactory.getLogger(PostTagService.class);


    @Inject
    public PostTagService(DDBUtil ddbUtil) {
        DynamoDbEnhancedClient enhancedClient = ddbUtil.getEnhancedDDBClient();
        postTagTable = enhancedClient.table("PostTag", TableSchema.fromBean(PostTag.class));
    }


    public List<PostTag> findAll() {
        return postTagTable.scan().items().stream().collect(Collectors.toList());
    }

    public List<PostTag> findByPostId(String postId) {
        return postTagTable.scan(s -> s
                        .consistentRead(true)
                        .filterExpression(Expression.builder()
                                .expression("postId = :postId")
                                .expressionValues(Map.of(":postId", AttributeValue.builder()
                                        .s(postId)
                                        .build()))
                                .build()))
                .items().stream().collect(Collectors.toList());
    }

    public PostTag add(PostTag postTag) {
        postTagTable.putItem(postTag);
        return postTag;
    }

    public PostTag deleteByPostId(String postId) {
        return postTagTable.deleteItem(d -> d
                .conditionExpression(Expression.builder()
                        .expression("postId = :postId")
                        .expressionValues(Map.of(":postId", AttributeValue.builder()
                                .s(postId).build()))
                        .build()));
    }


}