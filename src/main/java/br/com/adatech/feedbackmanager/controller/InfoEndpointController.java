package br.com.adatech.feedbackmanager.controller;

import br.com.adatech.feedbackmanager.service.InfoEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class InfoEndpointController {

    private final InfoEndpointService infoEndpointService;

    @Autowired
    public InfoEndpointController(InfoEndpointService infoEndpointService){
        this.infoEndpointService = infoEndpointService;
    }

    @GetMapping("/info")
    public String getInfo(@RequestParam(required = false) String queue) {
        return this.infoEndpointService.getInfo(queue);
    }
}
