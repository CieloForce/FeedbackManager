package br.com.adatech.feedbackmanager.controller;

import br.com.adatech.feedbackmanager.application.FeedbackSenderService;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FeedbackController {
    private final FeedbackSenderService feedbackSenderService;

    @Autowired
    public FeedbackController(FeedbackSenderService feedbackSenderService){
        this.feedbackSenderService = feedbackSenderService;
    }
    @PostMapping("/send")
    public ResponseEntity<String> sendFeedback(@RequestBody CustomerFeedback customerFeedback){
        try {
            this.feedbackSenderService.sendCustomerFeedback(customerFeedback);
            //TO DO : publish feedback accordingly with its type.
            return ResponseEntity.ok("Customer Feedback published sucessfully");
        }catch (Exception e) {
            // TO DO: Discover the "real" exception type triggered here and personalize it properly.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in send customer feedback to topic SNS");
        }
    }

    /** Considerando todas as filas possíveis conforme o FeedbackType. **/
    @GetMapping("/size")
    public int queueSize(){
        //TO DO: Dicover how to get size of each queue from sqs.
        return 0;
    }

    /** Considerando que a informação seja relacionada a todos os campos que compõe CustomerFeedback
     * e que sejam referentes a todas as filas **/
    @GetMapping("/info")
    public CustomerFeedback[] getInfo(){
        //TO DO: Discover how to recover the published CustomerFeedback from each possible queue from sqs.
        return null;
    }
}