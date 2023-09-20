package br.com.adatech.feedbackmanager.infra.aws;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Faz com que o spring faça a instanciação correta do nosso cliente sns. **/
@Configuration
public class AwsConfig {
    //TO DO: crendtials vinculation here.
    @Bean
    public AmazonSNS amazonSNS(){
        //retorna um construtor de cliente SNS com as configurações padrão.
        return AmazonSNSClientBuilder.standard().build();
        // return AmazonSNSClientBuilder.standard().withRegion("us-east-1").build();
    }

}

