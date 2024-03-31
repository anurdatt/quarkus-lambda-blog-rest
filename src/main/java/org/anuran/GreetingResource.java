package org.anuran;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.amazon.lambda.http.model.AwsProxyRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.anuran.model.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Path("/hello")
public class GreetingResource {

    Logger logger = LoggerFactory.getLogger(GreetingResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Test> hello() {
        Test t = new Test();
        t.setData("hello jaxrs");
        return List.of(t);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Test helloPost(Test test) {
        Test t = new Test();
        t.setData(test.getData());
        return t;
    }

//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Test helloPost(Test test, AwsProxyRequest request) {
////        try {
////            // Extract the JSON string from the body
////            String jsonString = request.getBody();
////            logger.info("Json String: {}", jsonString);
////            // Manually deserialize the JSON string
////            ObjectMapper objectMapper = new ObjectMapper();
////            Test test = objectMapper.readValue(jsonString, Test.class);
//
//            // Your existing logic
//            Test t = new Test();
//            t.setData(test.getData());
//            return t;
////        } catch (IOException e) {
////            // Handle deserialization exception
////            e.printStackTrace();
////            return null;
////        }
//    }
}
