package br.com.adatech.feedbackmanager.infra.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

/** Faz com que o spring faça a instanciação correta do nosso cliente sns. **/
@Configuration
public class AwsSnsConfig {

    private final String accessKeyId = "AKIAYGVI3ALTAEFZXPWW";


    private final String secretAccessKey = "aeBcnCWrxI7ouN6wC9L3jhBlxX2cUrlLizY1P8jT";


    private final String region = "us-east-1";
    @Bean
    public SnsClient snsClient() {
        System.out.println();
        return SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
    }

    public AwsSnsConfig(){
        System.out.println("AccessKey= " + this.accessKeyId);
        System.out.println("SecretKey= " + this.secretAccessKey);
        System.out.println("Region= " + this.region);
    }
}
//ativar a deduplicação na configuração da fila fifo.
//usar o pacote com.amazonaws