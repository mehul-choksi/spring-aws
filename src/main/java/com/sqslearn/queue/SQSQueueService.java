package com.sqslearn.queue;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class SQSQueueService {

    private final SQSQueue sqsQueue;

    private AmazonSQSAsync amazonSQSAsync;

    public SQSQueueService(SQSQueue sqsQueue){
        this.sqsQueue = sqsQueue;

        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(sqsQueue.getAccessKey(), sqsQueue.getSecretKey())
        );

        amazonSQSAsync=  AmazonSQSAsyncClientBuilder.standard()
                .withRegion(sqsQueue.getRegion())
                .withCredentials(awsCredentialsProvider)
                .build();
    }

    public void produceMessage(){
        Random random = new Random();
        int range = 10;
        int a = random.nextInt(range), b = random.nextInt(range);
        String message = Integer.toString(a) + " " + Integer.toString(b);

        sqsQueue.getQueueMessagingTemplate().send(sqsQueue.getEndpoint(), MessageBuilder.withPayload(message).build());
    }

    public void generateMessages(int n){
        for(int i = 0; i < n; i++){
            produceMessage();
        }
    }

    public void consumeMessages(){
        final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsQueue.getEndpoint())
                .withMaxNumberOfMessages(3)
                .withWaitTimeSeconds(5);

        while (true) {
            final List<Message> messages = amazonSQSAsync.receiveMessage(receiveMessageRequest).getMessages();

            for (Message message : messages) {
                String body = message.getBody();

                System.out.println("Processing message...");
                String result = processMessage(body);
                System.out.println("Result: " + result);

                if(result != "ERROR"){
                    deleteMessage(message);
                }
                else{
                    System.out.println("Error while processing message");
                }
            }
        }
    }

    public String processMessage(String message){
        String arr[] = message.split("\\s+");
        try{
            System.out.println("Num 1: " + arr[0] + " , Num2: " + arr[1] );
            return Integer.toString(Integer.parseInt(arr[0]) + Integer.parseInt(arr[1]));
        }
        catch (Exception e){
            e.printStackTrace();
            return "ERROR";
        }
    }

    private void deleteMessage(Message message){
        final String messageReceiptHandle = message.getReceiptHandle();
        amazonSQSAsync.deleteMessage(new DeleteMessageRequest(sqsQueue.getEndpoint(), messageReceiptHandle));
        System.out.println("Deleted message successfully.");
    }

}