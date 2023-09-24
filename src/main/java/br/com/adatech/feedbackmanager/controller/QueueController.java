package br.com.adatech.feedbackmanager.controller;

import br.com.adatech.feedbackmanager.dao.dto.CustomerFeedbackDTO;
import br.com.adatech.feedbackmanager.dao.dto.MessageDTO;
import br.com.adatech.feedbackmanager.service.InfoEndpointService;
import br.com.adatech.feedbackmanager.service.SendFeedbackEnpointService;
import br.com.adatech.feedbackmanager.service.SizeEndpointService;
import br.com.adatech.feedbackmanager.service.QueueService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
public class QueueController {

    private final InfoEndpointService infoEndpointService;
    private final SendFeedbackEnpointService sendFeedbackEnpointService;
    private final SizeEndpointService sizeEndpointService;
    private final QueueService queueService;

    @Autowired
    public QueueController(InfoEndpointService infoEndpointService,
                           SendFeedbackEnpointService sendFeedbackEnpointService,
                           SizeEndpointService sizeEndpointService,
                           QueueService queueService){

        this.sizeEndpointService = sizeEndpointService;
        this.sendFeedbackEnpointService = sendFeedbackEnpointService;
        this.infoEndpointService = infoEndpointService;
        this.queueService = queueService;
    }

    @GetMapping("/info")
    public String getInfo(@RequestParam(required = false) String queue) {
        return this.infoEndpointService.getInfo(queue);
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendFeedback(@RequestBody CustomerFeedbackDTO customerFeedbackDTO){
        return this.sendFeedbackEnpointService.sendFeedback(customerFeedbackDTO);
    }

    @GetMapping("/size")
    public String getSize(@RequestParam(required = false) String queue) {
        return this.sizeEndpointService.getSize(queue);
    }

    @PostMapping("/remove")
    public ResponseEntity<String> removeMessage(@RequestBody MessageDTO messageBody, @RequestParam(required = true) String queue) {
        return this.queueService.removeMessage(queue, messageBody.getReceiptHandle());
    }

    @GetMapping("/messages")
    public ResponseEntity<List<MessageDTO>> getMessages(@RequestParam(required = true) String queue) {
        return this.queueService.getMessages(queue);
    }
}
