package org.anuran.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.anuran.model.PostTag;
import org.anuran.model.Tag;
import org.anuran.util.DDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedResponse;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;

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

    public List<PostTag> findByPostId(Long postId) {
        return postTagTable.scan(s -> s
                        .consistentRead(true)
                        .filterExpression(Expression.builder()
                                .expression("postId = :postId")
                                .expressionValues(Map.of(":postId", AttributeValue.builder()
                                        .n(String.valueOf(postId))
                                        .build()))
                                .build()))
                .items().stream().collect(Collectors.toList());
    }

    public List<PostTag> findByTagId(Long tagId) {
        return postTagTable.scan(s -> s
                        .consistentRead(true)
                        .filterExpression(Expression.builder()
                                .expression("tagId = :tagId")
                                .expressionValues(Map.of(":tagId", AttributeValue.builder()
                                        .n(String.valueOf(tagId))
                                        .build()))
                                .build()))
                .items().stream().collect(Collectors.toList());
    }

    public List<PostTag> findByPostIdAndTagId(Long postId, Long tagId) {
        return postTagTable.scan(s -> s
                        .consistentRead(true)
                        .filterExpression(Expression.builder()
                                .expression("postId = :postId and tagId = :tagId")
                                .expressionValues(Map.of(":postId", AttributeValue.builder().n(String.valueOf(postId)).build(),
                                        ":tagId", AttributeValue.builder().n(String.valueOf(tagId)).build()))
                                .build()))
                .items().stream().collect(Collectors.toList());
    }

    public PostTag add(PostTag postTag) {
//        int pinx = postTag.getPostId().lastIndexOf("-");
//        String pidInclude = postTag.getPostId();
//        if(pinx > 0) {
//            pidInclude = postTag.getPostId().substring(0, pinx);
//        }
//        int tinx = postTag.getTagId().lastIndexOf("-");
//        String tidInclude = postTag.getTagId();
//        if(tinx > 0) {
//            tidInclude = postTag.getTagId().substring(0, tinx);
//        }
        Long did = new Date().getTime();
//        postTag.setId(pidInclude + "-" + tidInclude + "-" + did);
        postTag.setId(did);
        postTagTable.putItem(postTag);
        return postTag;
    }

//    public PostTag deleteByPostId(Long postId) {
//        return postTagTable.deleteItem(d -> d
//                .conditionExpression(Expression.builder()
//                        .expression("postId = :postId")
//                        .expressionValues(Map.of(":postId", AttributeValue.builder()
//                                .n(String.valueOf(postId)).build()))
//                        .build()));
//    }

    public List<PostTag> deleteByPostId(Long postId) {
        ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest.builder()
                .filterExpression(Expression.builder()
                        .expression("#pid = :pid")
                        .expressionNames(Map.of("#pid", "postId"))
                        .expressionValues(Map.of(":pid", AttributeValue.builder().n(String.valueOf(postId)).build()))
                        .build())
                .build();

        List<PostTag> postTags = postTagTable.scan(scanEnhancedRequest).items()
                .stream().collect(Collectors.toList());


        postTags.stream().forEachOrdered(pt -> postTagTable.deleteItem(pt));

        return postTags;
    }

    public List<PostTag> deleteByTagId(Long tagId) {

        ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest.builder()
                .filterExpression(Expression.builder()
                        .expression("#tid = :tid")
                        .expressionNames(Map.of("#tid", "tagId"))
                        .expressionValues(Map.of(":tid", AttributeValue.builder().n(String.valueOf(tagId)).build()))
                        .build())
                .build();

        List<PostTag> postTags = postTagTable.scan(scanEnhancedRequest).items()
                .stream().collect(Collectors.toList());


        postTags.stream().forEachOrdered(pt -> postTagTable.deleteItem(pt));

        return postTags;
    }

    public PostTag delete(Long id) {
        Key partitionKey = Key.builder().partitionValue(id).build();
        return postTagTable.deleteItem(partitionKey);
    }


}