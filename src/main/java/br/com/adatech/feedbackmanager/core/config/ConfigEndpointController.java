//package br.com.adatech.feedbackmanager.controller;
//
//import br.com.adatech.feedbackmanager.service.ConfigEndpointService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/config")
//public class ConfigEndpointController {
//
//    private final ConfigEndpointService configEndpointService;
//
//    @Autowired
//    public ConfigEndpointController(ConfigEndpointService configEndpointService){
//        this.configEndpointService = configEndpointService;
//    }
//
//    @GetMapping
//    public String config(){
//        return this.configEndpointService.config();
//    }
//}
