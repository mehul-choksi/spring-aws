package com.sqslearn.queue;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;
import java.util.Random;

@Configuration
public class SQSQueue {

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.endpoint.uri}")
    private String endpoint;

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(){
        System.out.println("ACCESS KEY: " + accessKey);
        System.out.println("SECRET KEY: " + secretKey);
        return new QueueMessagingTemplate(amazonSQSAsync());
    }

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsync(){

        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKey, secretKey)
        );

       return AmazonSQSAsyncClientBuilder.standard()
               .withRegion(region)
               .withCredentials(awsCredentialsProvider)
               .build();
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public QueueMessagingTemplate getQueueMessagingTemplate() {
        return queueMessagingTemplate;
    }

    public void setQueueMessagingTemplate(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }
}
