# Feedback Manager Backend

Este projeto consiste na implementação das especificações do desafio
técnico do Bootcamp Ada Tech em parceria com a Cielo.
Abaixo a documentação do serviço de feedback de clientes usando a Amazon Simple Notification Service (SNS) e Simple Queue Service (SQS).

## Índice

- [1. Desafio](#desafio)
- [2. Planejamento da solução](#planejamento)
- [3. Fluxo de desenvolvimento](#fluxo)
- [4. Instalação](#instalacao)
- [5. Arquitetura](#arquitetura)
- [6. Uso](#uso)
- [7. Demonstração](#demo)
- [8. Contribuição](#contribuição)
- [9. Licença](#licença)

<a id="desafio"></a>
## 1. Desafio

Implementar um serviço que gerencia filas de sugestões, elogios e críticas de
clientes para uma empresa, aproveitando os serviços da AWS, como SNS e SQS.

- O serviço deve
   permitir que os clientes enviem suas sugestões, elogios e críticas, que serão enfileirados e
   processados de acordo com a ordem em que foram recebidos.
- O serviço deve ser possível
   verificar o status da fila e obter informações sobre as mensagens recebidas.


### 1.2. Main Tasks:

1.2.1. Implemente uma classe CustomerFeedback que represente uma mensagem de
   feedback com os seguintes atributos: id, type (tipo de feedback, como "Sugestão",
   "Elogio" ou "Crítica"), message (mensagem do cliente) e status (status da mensagem,
   como "Recebido", "Em Processamento" ou "Finalizado").


1.2.2. Configure um tópico SNS para cada tipo de feedback (Sugestão, Elogio, Crítica) na
   AWS. Quando um cliente envia um feedback, o sistema deve publicar a mensagem no
   tópico SNS correspondente.


1.2.3. Configure uma fila SQS para cada tipo de feedback (Sugestão, Elogio, Crítica) na AWS.
   Configure as filas para seguir o princípio FIFO (First-In-First-Out).


1.2.4.  Implemente um consumidor de fila SQS para processar os feedbacks da fila. Este
    consumidor deve ser executado em segundo plano e processar os feedbacks de acordo
    com a ordem da fila. Quando um feedback for processado com sucesso, seu status
    deve ser atualizado para "Finalizado".


1.2.5. Crie um controlador REST em Java usando a biblioteca Spring Boot para expor
   endpoints para as seguintes operações:
- Enviar um feedback (sugestão, elogio ou crítica) para a fila correspondente no
      SQS.
- Obter o tamanho atual da fila de feedbacks para cada tipo (Sugestão, Elogio,
      Crítica).
- Obter informações sobre todos os feedbacks na fila de cada tipo.


1.2.6. Documente a API REST usando a especificação Swagger ou alguma outra ferramenta
   de documentação de API.


<a id="planejamento"></a>
## 2. Planejamento da solução

### 2.1. Entities

- Entity 1: Implementação Classe CustomerFeedback.
- Entity 2: Implementação Classe/Enum FeedbackType.
- Entity 3: Implementação Classe/Enum FeedbackStatus.

### 2.2. Docker

Subir database no docker e vincula-la à camada de persistência.
(alternativa: usar h2 in memory)

### 2.3. Camada de persistência

Mapear entidades com hibernate.

### 2.4. Endpoints

- __POST__: **/api/send** - para enviar um feedback.

- __GET__: **/api/size** - para obter o tamanho da fila.

- __GET__: **/api/info** - para obter informações sobre os feedbacks na fila.

### 2.5. Integração com AWS SNS e SQS

#### 2.5.1. Adicionar dependências Maven

``` bash
<!-- Dependência para AWS SNS -->
<dependency>
     <groupId>com.amazonaws</groupId>
     <artifactId>aws-java-sdk-sns</artifactId>
     <version>1.12.552</version>
</dependency>
```

``` bash
<!-- Dependência para AWS SQS -->
<dependency>
     <groupId>com.amazonaws</groupId>
     <artifactId>aws-java-sdk-sqs</artifactId>
     <version>1.12.552</version>
</dependency>
```

#### 2.5.2. Configurar as credenciais AWS no application.yml

``` bash
aws:
  accessKey: <ACCESS_KEY>
  secretKey: <SECRET_KEY>
  region: <AWS_REGION>
```

#### 2.5.3. Configurar o Cliente SNS e SQS usando as credenciais:

``` bash
@Configuration
public class AwsConfig {

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;
    
    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}
```

#### 2.5.4. Implementar a interação com SNS e SQS

Pub/Sub

PUB: Implementar Classe SnsService

SUB: Implementar Classe SqsService

**Pré-requisitos: configurar o gerenciamento de filas SQS e tópicos SNS na conta AWS e obter os ARNs (Amazon Resource Name) correspondentes para utilizá-los na aplicação.**

<a id="fluxo"></a>
## 3. Fluxo de desenvolvimento da aplicação

3.1. Iniciar a aplicação Spring Boot
3.2. Cliente envia feedback
   - Um cliente interage com a aplicação, por exemplo, através de uma interface web ou criando um topico direto pela AWS.
   - O cliente envia um feedback, fornecendo informações como FeedbackType (tipo) e mensagem. O id e o FeedbackStatus são atributos setados pelo sistema.
3.3. Enpoint REST para Enviar Feedback envia feedback do cliente.
   - O cliente utiliza o endpoint **POST: /feedback/send** para enviar o feedback.
   - O endpoint recebe as informações do feedback corretamente e faz as atribuições default para o id (uuid) e o FeedbackStatus.
   - FeedbackStatus inicia com ...
3.4. Publicação do payload no Tópico SNS correspondente.
   - A partir do endpoint **POST: /feedback/send**, já com os dados recebidos, o serviço principal (no caso, **SnsService**) é invocado.
   - O serviço **SnsService** utiliza o **Cliente SNS** (*ver seção 5.3*) para publicar a mensagem no **tópico SNS** correspondente ao tipo de feedback(Sugestão, Elogio, Crítica)
   - Significa que para cada FeedbackType teremos uma fila sendo gerada pelo nosso **Cliente SNS** que, em outras palavras, será o nosso produtor.
3.5. Nosso **Cliente SNS** encaminha a mensagem para as Filas SQS (que serão consumidas).
   - O tópico SNS notifica todas as filas SQS que estão subscritas a ele do feedback recém-chegado.
   - Cada fila SQS correspondente ao TIPO (FeedBackType) recebe a mensagem e a enfileira corretamente.
3.6. Nosso ***Consumidor de Fila SQS*** processa o feedback corretamente.
   - Em segundo plano, nosso ***Consumidor de Fila SQS*** (que foi previamente configurado na aplicação) está constamente verificando as filas SQS.
   - Quando um **CustomerFeedback** é encontrado na fila SQS, o nosso ***Consumidor de Fila SQS*** o retira da fila e o processa de acordo com a lógica definida, mudando seu **FeedbackStatus** apropriadamente
3.7. Atualização do **FeedbackStatus**
   - Após o processamento bem sucedido o status é atualizado para "Finalizado".


> ### Considerações adicionais
>
>- A Integração com o cliente pode ser iniciada pelo frontend da aplicação (pulga atrás da orelha mapeada com sucesso.)
>- Configuração AWS e Spring: antes do envio de feedbacks é necessário configurar previamente os tópicos SNS e as filas SQS na AWS, para que então, finalmente, possamos prosseguir com a integração desses serviços com o Spring Boot.
>- Para um sistema em produção, é importante monitorar as filas, tópicos e o consumo de mensagens (**CustomerFeedback**) para garantir que tudo esteja funcionando conforme esperado. isso pode ser feito usando ferramentas de monitoramente e logs.(a definir)

<a id="instalacao"></a>
## 4. Instalação e configurações iniciais

4.1. IDE: INTELIJ ULTIMATE.
4.2. Gerenciador de dependências: Maven.
4.3. Dependências da aplicação
```bash
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- Parser de arquivos yaml sem vulnerabilidades -->
<dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>2.2</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<!-- H2 -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
    <version>2.2.220</version>
</dependency>
<!-- AWS Java SDK para interagir SQS -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>sqs</artifactId>
    <version>2.20.151</version>
</dependency>
<!-- AWS Java SDK para interagir SNS -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>sns</artifactId>
    <version>2.20.151</version>
</dependency>
<!-- JPA  -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>3.1.3</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<!-- Swagger UI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
<!-- Jackson: Converte objeto java numa representação JSON -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>
````


<a id="arquitetura"></a>
## 5 - Arquitetura

- br.com.adatech.feedbackmanager
  - adapter\
    [FeedbackReceiverAdapter](src/main/java/br/com/adatech/feedbackmanager/adapter/FeedbackReceiverAdapter.java)\
    [FeedbackSenderAdapter](src/main/java/br/com/adatech/feedbackmanager/adapter/FeedbackSenderAdapter.java)
  - application\
    [FeedbackReceiverService](src/main/java/br/com/adatech/feedbackmanager/application/FeedbackReceiverService.java)\
    [FeedbackSenderService](src/main/java/br/com/adatech/feedbackmanager/application/FeedbackSenderService.java)
  - controller\
    [InfoEndpointController](src/main/java/br/com/adatech/feedbackmanager/controller/InfoEndpointController.java)\
  - [QueueController](src/main/java/br/com/adatech/feedbackmanager/controller/QueueController.java)\
    [SendFeedbackEndpointController](src/main/java/br/com/adatech/feedbackmanager/controller/SendFeedbackEndpointController.java)\
    [SizeEndpointController](src/main/java/br/com/adatech/feedbackmanager/controller/SizeEndpointController.java)
  - core
    - UseCase\
      [FeedbackReceiverUseCase](src/main/java/br/com/adatech/feedbackmanager/core/UseCase/FeedbackReceiverUseCase.java)\
      [FeedbackSenderUseCase](src/main/java/br/com/adatech/feedbackmanager/core/UseCase/FeedbackSenderUseCase.java)
    - config\
      [OpenApiConfig](src/main/java/br/com/adatech/feedbackmanager/core/config/OpenApiConfig.java)
    - entity\
      [CustomerFeedback](src/main/java/br/com/adatech/feedbackmanager/core/entity/CustomerFeedback.java)\
      [FeedbackStatus](src/main/java/br/com/adatech/feedbackmanager/core/entity/FeedbackStatus.java)\
      [FeedbackType](src/main/java/br/com/adatech/feedbackmanager/core/entity/FeedbackType.java)
    - exception\
      [CustomerFeedbackNotFoundException](src/main/java/br/com/adatech/feedbackmanager/core/exception/CustomerFeedbackNotFoundException.java)
    - util\
      [FeedbackTypeConverter](src/main/java/br/com/adatech/feedbackmanager/core/util/FeedbackTypeConverter.java)
    - dao
      - dto
        [CustomerFeedbackDTO](src/main/java/br/com/adatech/feedbackmanager/dao/dto/CustomerFeedbackDTO.java)
      - reposity
        [CustomerFeedbackRepository](src/main/java/br/com/adatech/feedbackmanager/dao/repository/CustomerFeedbackRepository.java)
      - service
        [CustomerFeedbackService](src/main/java/br/com/adatech/feedbackmanager/dao/service/CustomerFeedbackService.java)
    - infra
      - aws
        - util
          [QueueSizeInfo](src/main/java/br/com/adatech/feedbackmanager/infra/aws/util/QueueSizeInfo.java)
        [AwsSnsConfig](src/main/java/br/com/adatech/feedbackmanager/infra/aws/AwsSnsConfig.java)\
        [AwsSqsConfig](src/main/java/br/com/adatech/feedbackmanager/infra/aws/AwsSqsConfig.java)\
        [SnsPublisherService](src/main/java/br/com/adatech/feedbackmanager/infra/aws/SnsPublisherService.java)\
        [SnsTopicFactoryService](src/main/java/br/com/adatech/feedbackmanager/infra/aws/SnsTopicFactoryService.java)\
        [SqsService](src/main/java/br/com/adatech/feedbackmanager/infra/aws/SqsService.java)\
        [SqsSubscriberService](src/main/java/br/com/adatech/feedbackmanager/infra/aws/SqsSubscriberService.java)
  - service
    [InfoEndpointService](src/main/java/br/com/adatech/feedbackmanager/service/InfoEndpointService.java)\
    [QueueService](src/main/java/br/com/adatech/feedbackmanager/service/QueueService.java)\
    [SendFeedbackEnpointService](src/main/java/br/com/adatech/feedbackmanager/service/SendFeedbackEnpointService.java)\
    [SizeEndpointService](src/main/java/br/com/adatech/feedbackmanager/service/SizeEndpointService.java)
  [Startup](src/main/java/br/com/adatech/feedbackmanager/Startup.java)

<a id="uso"></a>
## 6 - INSTRUÇÕES DE USO
- Clone o repositório ou baixe o código fonte.
- Execute o código.
  - Crie manualmente os tópicos FIFO e as filas fifo com nomes significativos.
   - Incluia o TIPO de feedback na nomenclatura, em inglês, por exemplo:
     - Nome do tópico SNS: Feedback_Suggestion.fifo, Feedback_Compliment.fifo, Feedback_Criticism.fifo
     - Nome da fila SQS: FeedbackSuggestion.fifo, ...
     - Faça a FILA SQS se inscrever no TÓPICO SNS.
     - Certifique-se de usar a região Norte da Virgínia: us-east-1
     - Na criação do tópico SNS, marque a opção FIFO e ative a deduplicação.
     - Na criação da fila SQS, marque FIFO e habilite Desduplicação, alto throughput e desabilite a criptografia para um caso de uso mais simplificado.
     - Mantenha as demais configurções conforme padrão.
- Lembre de fazer manualmente a configuração da subscrição SQS/SNS, sempre inscrevendo a FILA SQS em um TÓPICO SNS, fluxos de teste em caso reverso ainda não estão considerados.
- Acesse GET api/info para Painel Administrativo do consumo de mensagens.
- Acesse GET api/size para Visão Geral.
- Envie feedbacks em POST api/send.
- Verifique se o seu feedback foi para a fila SQS da AWS adequadamente em GET api/size.
  - Consuma seus feedbacks em ordem de envio através RequestParams:
    - GET api/info?queue=Criticism: indique apenas o tipo.
    - GET api/info?queue=Compliment: indique apenas o tipo.
    - GET api/info?queue=Suggestion: indique apenas o tipo.
- Veja a mágica acontecendo através do frontend.

<a id="demo"></a>
## 7. Demonstração

- [Back-end swagger-ui](http://ec2-52-207-25-62.compute-1.amazonaws.com:8080/swagger-ui.html)
- [Front-end](http://ec2-52-207-25-62.compute-1.amazonaws.com:4200)



>## Considerações finais
>- Podem acontecer erros de processamento de informações no banco de dados EM MEMÓRIA se o programa for encerrado e depois for reiniciado para reprocessar a fila. Os erros de banco NÃO AFETAM o funcionamento do CONSUMO das informações antigas que porventura tenham ficado nas filas da AWS. Também NÃO AFETA novos envios ou novos consumos que possam decorrer do caso uso da aplicação restabelecida. 
>- Isso acontece porque os dados contidos no banco em memória serão perdidos e não poderão ser recuperados mais. Entretanto, o consumo dos dados e funcionamento GLOBAL da aplicação não é impactado por isso. Mantendo-se resiliente e tolerante a falhas de persistência em função do uso de um banco de dados em memória.
>- Solução: fazer a persistência num volume ou trocar o banco de dados.
>- Esses testes foram feitos para garantir a resiliência e a tolerância a falhas no backend no fluxo de configurações apresentados nas instruções de uso.
>- Este exemplo foi desenvolvido utilizando DDD e Clean Archtecture.
>- Existem várias branches de testes que devem ser feitos para garantir totalmente a resiliência da aplicação quanto à integração com a AWS.
>
>- À equipe de __avaliação ADA__: no meu usuário sandbox da Aws já existem filas sqs e sns configuradas e zeradas, prontas para serem testadas, mas ao critério de vocês pode ser feito novas configurações seguindo as especificações das instruções de uso. 