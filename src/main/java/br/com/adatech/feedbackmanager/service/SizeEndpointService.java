package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.infra.aws.SqsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SizeEndpointService {

    private final SqsService sqsService;

    @Autowired
    public SizeEndpointService(SqsService sqsService){
        this.sqsService = sqsService;
    }

    public String getSize(String queue) {
        if(queue != null) {
            System.out.println("\nRecebendo dados da AWS para " + queue + "...\n");

        } else {
            System.out.println("\nRecebendo todos os dados da AWS...\n");
        }
        return sqsService.getGeneralQueueSizeInfoAsStringJSON();
    }
}
