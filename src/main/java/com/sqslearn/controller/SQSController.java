package com.sqslearn.controller;

import com.sqslearn.queue.SQSQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SQSController {

    @Autowired
    private SQSQueueService sqsQueueService;

    @GetMapping("/generate/{num}")
    public String generateMMessages(@PathVariable String num){
        try{
            Integer val = Math.min(Integer.parseInt(num),20);
            sqsQueueService.generateMessages(val);
            return "Created " + num + " messages in queue successfully";
        }
        catch (Exception e){
            e.printStackTrace();
            return "Parameter should be a number";
        }
    }

    @GetMapping("/consume")
    public String consumeMessages(){
        sqsQueueService.consumeMessages();
        return "Consuming messages...";
    }

}
