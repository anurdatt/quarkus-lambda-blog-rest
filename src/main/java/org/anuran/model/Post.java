package org.anuran.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.anuran.converter.LocalDateToStringTypeConverter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDate;


@RegisterForReflection
@DynamoDbBean
public class Post {

    private Long id;
    private String title;

    private String author;
    private String description;
    private String avatarImageUrl;
    private String blogUrl;

    private String content;
    private LocalDate date;


    public Post() {

    }

    public Post(Long id, String title, String author, String description,
                String avatarImageUrl, String blogUrl, String content,
                LocalDate date) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.avatarImageUrl = avatarImageUrl;
        this.blogUrl = blogUrl;
        this.content = content;
        this.date = date;
    }

    @DynamoDbPartitionKey
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatarImageUrl() {
        return avatarImageUrl;
    }

    public void setAvatarImageUrl(String avatarImageUrl) {
        this.avatarImageUrl = avatarImageUrl;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
