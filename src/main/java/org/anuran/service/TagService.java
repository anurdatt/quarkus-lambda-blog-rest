package org.anuran.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.anuran.model.Post;
import org.anuran.model.Tag;
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
public class TagService {

    private DynamoDbTable<Tag> tagTable;

    Logger logger = LoggerFactory.getLogger(TagService.class);


    @Inject
    public TagService(DDBUtil ddbUtil) {
        DynamoDbEnhancedClient enhancedClient = ddbUtil.getEnhancedDDBClient();
        tagTable = enhancedClient.table("Tag", TableSchema.fromBean(Tag.class));
    }


    public List<Tag> findAll() {
        return tagTable.scan().items().stream().collect(Collectors.toList());
    }

    public Tag get(String id) {
        Key partitionKey = Key.builder().partitionValue(id).build();
        return tagTable.getItem(partitionKey);
    }

    public Tag update(String id, Tag tag) {
        tag.setId(id);
        UpdateItemEnhancedRequest request = UpdateItemEnhancedRequest
                .builder(Tag.class)
                .ignoreNulls(true).item(tag).build();
        return tagTable.updateItem(request);
    }

    public Tag add(Tag tag) {
//        String id = UUID.randomUUID().toString();
        Long did = new Date().getTime();
        String tid = tag.getName()
//                .replaceAll("[!@#'$%^&*]", "")
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .replaceAll(" ", "-");
        tag.setId(tid + "-" + did);
        tagTable.putItem(tag);
        return tag;
    }

    public Tag delete(String id) {
        Key partitionKey = Key.builder().partitionValue(id).build();
        return tagTable.deleteItem(partitionKey);
    }
}