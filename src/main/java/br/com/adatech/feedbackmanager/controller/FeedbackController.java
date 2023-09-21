package br.com.adatech.feedbackmanager.controller;

import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService){
        this.feedbackService = feedbackService;
    }
    @PostMapping("/send")
    public ResponseEntity<String> sendFeedback(@RequestBody CustomerFeedback customerFeedback){
       return this.feedbackService.sendFeedback(customerFeedback);
    }

    @GetMapping("/size")
    public int queueSize(){
        return this.feedbackService.queueSize();
    }

    @GetMapping("/info")
    public CustomerFeedback[] getInfo(){
        return this.feedbackService.getInfo();
    }
}