package org.anuran.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.anuran.converter.LocalDateToStringTypeConverter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDate;


@RegisterForReflection
@DynamoDbBean
public class Comment {

    private String id;
    private String text;
    private String author;
    private String sourceId;
    private String sourceApp;
    private String parentId;
    private LocalDate date;


    public Comment() {

    }

    public Comment(String id, String text, String author, LocalDate date) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.date = date;
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceApp() {
        return sourceApp;
    }

    public void setSourceApp(String sourceApp) {
        this.sourceApp = sourceApp;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @DynamoDbConvertedBy(LocalDateToStringTypeConverter.class)
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


//    public Map<String, AttributeValue> post() {
//        return Map.of("id", AttributeValue.builder().n(String.valueOf(this.id)).build(),
//                "title", AttributeValue.builder().s(this.title).build(),
//                "text", AttributeValue.builder().s(this.text).build(),
//                "username", AttributeValue.builder().s(this.username).build());
//    }
}
