package br.com.adatech.feedbackmanager.controller;

import br.com.adatech.feedbackmanager.dao.dto.CustomerFeedbackDTO;
import br.com.adatech.feedbackmanager.service.SendFeedbackEnpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SendFeedbackEndpointController {

    private final SendFeedbackEnpointService sendFeedbackEnpointService;

    @Autowired
    public SendFeedbackEndpointController(SendFeedbackEnpointService sendFeedbackEnpointService){
        this.sendFeedbackEnpointService = sendFeedbackEnpointService;
    }
    @PostMapping("/send")
    public ResponseEntity<String>sendFeedback(@RequestBody CustomerFeedbackDTO customerFeedbackDTO){
       return this.sendFeedbackEnpointService.sendFeedback(customerFeedbackDTO);
    }
}