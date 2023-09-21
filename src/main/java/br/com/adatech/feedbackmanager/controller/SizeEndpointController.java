package br.com.adatech.feedbackmanager.controller;

import br.com.adatech.feedbackmanager.service.SizeEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SizeEndpointController {

    private final SizeEndpointService sizeEndpointService;
    @Autowired
    public SizeEndpointController(SizeEndpointService sizeEndpointService){
        this.sizeEndpointService = sizeEndpointService;
    }
    @GetMapping("/size")
    public String getSize(@RequestParam(required = false) String queue) {
        return this.sizeEndpointService.getSize(queue);
    }
}
