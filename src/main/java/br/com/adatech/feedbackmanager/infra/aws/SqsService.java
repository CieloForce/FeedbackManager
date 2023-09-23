package br.com.adatech.feedbackmanager.infra.aws;

import br.com.adatech.feedbackmanager.infra.aws.util.QueueSizeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class SqsService {
    private final SqsClient sqsClient;

    @Autowired
    public SqsService(SqsClient sqsClient){
        this.sqsClient = sqsClient;
    }

    /** Obtém os atributos especificados para servir corretamente o payload requisitado pelo frontend **/
    public Map<QueueAttributeName, String> getQueueAttributes(String queueUrl){ //Funcionando
        Set<QueueAttributeName> queueAttributeNameList = new HashSet<>();
        queueAttributeNameList.add(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES);
        queueAttributeNameList.add(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE);
        queueAttributeNameList.add(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES_DELAYED);

        GetQueueAttributesResponse response = sqsClient.getQueueAttributes(GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(queueAttributeNameList)
                .build());

        return response.attributes();
    }

    /** Formata integralmente a saída das informações necessárias para subir o painel administrativo no frontend pelo endpoint
     * GET /api/size abstraindo toda a complexidade do payload da AWS, deixando o endpoint do back pronto para ser usado no front. **/
    private QueueSizeInfo getGeneralQueueSizeInfo(){ //funcionando
        Map<String, Map<String, Map<String, Integer>>> topics = new HashMap<>();
        Map<String, Map<String, Integer>> topicsPayloadGlobal = new HashMap<>();


        // Adicione os nomes das filas que você deseja obter informações
        Set<String> queueNames = new HashSet<>();
        queueNames.add("elogio");
        queueNames.add("sugestao");
        queueNames.add("critica");

        int totalSize = 0;

        for (String queueName : queueNames) {
            String queueUrl = this.getQueueUrlByFeedbackTypeWithPortugueseName(queueName);

            Map<QueueAttributeName, String> attributes = getQueueAttributes(queueUrl);

            Map<String, Integer> queueData = new HashMap<>();

            queueData.put("ApproximateNumberOfMessages", Integer.parseInt(attributes.get(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)));
            queueData.put("ApproximateNumberOfMessagesNotVisible", Integer.parseInt(attributes.get(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE)));
            queueData.put("ApproximateNumberOfMessagesDelayed", Integer.parseInt(attributes.get(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES_DELAYED)));
            int queueTotalSize = queueData.values().stream().mapToInt(Integer::intValue).sum(); // Soma os valores das métricas da fila
            queueData.put("totalSize", queueTotalSize); // Tamanho total relativo à fila específica da iteração.

            topicsPayloadGlobal.put(queueName, queueData);

            totalSize += queueTotalSize; //Adiciona o tamanho total desta fila ao total global
        }

        topics.put("topics", topicsPayloadGlobal);

        Map<String, Integer> globalSize = new HashMap<>();
        globalSize.put("sizeInfo", totalSize);

        return  new QueueSizeInfo(topics, globalSize);
    }

    public String getGeneralQueueSizeInfoAsStringJSON() { //funcionando!!
        QueueSizeInfo result = this.getGeneralQueueSizeInfo();
        Map<String, Map<String, Map<String, Integer>>> topics = result.getGlobalTopics();
        Map<String, Integer> globalSize = result.getGlobalSize();

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String preparedTopics = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(topics.get("topics"));
            String preparedGlobalSize = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(globalSize.get("sizeInfo"));

            return "{ \"topics\": " + preparedTopics + ", \"totalSize\": " + preparedGlobalSize + "}";
        }catch(JsonProcessingException e){
            System.err.println("Ocorreu um erro ao converter as informações gerais sobre os tamanhos das filas: " + e.getMessage());
            return null;
        }
    }

    public String getQueueName(String targetType){ //funcionando
        ListQueuesResponse response = sqsClient.listQueues();

        for (String queueUrl : response.queueUrls()) {
            String[] parts = queueUrl.split("/");
            String queueName = parts[parts.length - 1];
            if (queueName.contains(targetType)) {
                return queueName;
            }
        }
        return null; // Se não encontrar nenhuma fila
    }

    public String getQueueUrlByFeedbackType(String queueType) { //funcionando
        String queueName = this.getQueueName(queueType); // Faz o fetch do nome da fila automáticamente direto da AWS a partir de todas as filas existentes.
        if (queueName == null)
            throw new RuntimeException("Não foi encontrado nenhuma fila com nome referente ao FeedbackType informado, por favor, nomeie suas filas adequadamente na AWS.");
        GetQueueUrlRequest queueUrlRequest = GetQueueUrlRequest.builder().queueName(queueName).build();
        GetQueueUrlResponse queueUrlResponse = sqsClient.getQueueUrl(queueUrlRequest);
        return queueUrlResponse.queueUrl();
    }

    /** Obtém o queueUrl da aws correspondente ao FeedbackType em português. **/
    public String getQueueUrlByFeedbackTypeWithPortugueseName(String feedbackTypePortugueseName){
        return switch (feedbackTypePortugueseName.toLowerCase()) {
            case "elogio" -> this.getQueueUrlByFeedbackType("Compliment");
            case "sugestao" -> this.getQueueUrlByFeedbackType("Suggestion");
            case "critica" -> this.getQueueUrlByFeedbackType("Criticism");
            default -> throw new RuntimeException("Invalid Portuguese Semantic of Feedback type!");
        };
    }
}
