# Feedback Manager Backend

Este projeto consiste na implementação das especificações do desafio
técnico do Bootcamp Ada Tech em parceria com a Cielo.
Abaixo a documentação do serviço de feedback de clientes usando a Amazon Simple Notification Service (SNS) e Simple Queue Service (SQS).

## Índice

- [Desafio](#desafio)
- [Planejamento da solução](#planejamento)
- [Fluxo de desenvolvimento](#fluxo)
- [Instalação](#instalação)
- [Uso](#uso)
- [Contribuição](#contribuição)
- [Licença](#licença)


## Desafio

Implementar um serviço que gerencia filas de sugestões, elogios e críticas de
clientes para uma empresa, aproveitando os serviços da AWS, como SNS e SQS.

1. O serviço deve
   permitir que os clientes enviem suas sugestões, elogios e críticas, que serão enfileirados e
   processados de acordo com a ordem em que foram recebidos.


2. O serviço deve ser possível
   verificar o status da fila e obter informações sobre as mensagens recebidas.


### Main Tasks:

1. Implemente uma classe CustomerFeedback que represente uma mensagem de
   feedback com os seguintes atributos: id, type (tipo de feedback, como "Sugestão",
   "Elogio" ou "Crítica"), message (mensagem do cliente) e status (status da mensagem,
   como "Recebido", "Em Processamento" ou "Finalizado").


2. Configure um tópico SNS para cada tipo de feedback (Sugestão, Elogio, Crítica) na
   AWS. Quando um cliente envia um feedback, o sistema deve publicar a mensagem no
   tópico SNS correspondente.


3. Configure uma fila SQS para cada tipo de feedback (Sugestão, Elogio, Crítica) na AWS.
   Configure as filas para seguir o princípio FIFO (First-In-First-Out).


4.  Implemente um consumidor de fila SQS para processar os feedbacks da fila. Este
    consumidor deve ser executado em segundo plano e processar os feedbacks de acordo
    com a ordem da fila. Quando um feedback for processado com sucesso, seu status
    deve ser atualizado para "Finalizado".


5. Crie um controlador REST em Java usando a biblioteca Spring Boot para expor
   endpoints para as seguintes operações:
    1. Enviar um feedback (sugestão, elogio ou crítica) para a fila correspondente no
       SQS.
    2. Obter o tamanho atual da fila de feedbacks para cada tipo (Sugestão, Elogio,
       Crítica).
    3. Obter informações sobre todos os feedbacks na fila de cada tipo.


6. Documente a API REST usando a especificação Swagger ou alguma outra ferramenta
   de documentação de API.



## Planejamento da solução

### 1. Entities

1. Entity 1: Implementação Classe CustomerFeedback.
2. Entity 2: Implementação Classe/Enum FeedbackType.
3. Entity 3: Implementação Classe/Enum FeedbackStatus.

### 2. Docker

Subir database no docker e vincula-la à camada de persistência.
(alternativa: usar h2 in memory)

### 3. Camada de persistência

Mapear entidades com hibernate.

### 4. Endpoints

1. __POST__: **/feedback/send** - para enviar um feedback.

2. __GET__: **/feedback/size** -fila para obter o tamanho da fila.

3. __GET__: **/feedback/info** -fila para obter informações sobre os feedbacks na fila.

### 5. Integração com AWS SNS e SQS

#### 5.1. Adicionar dependências Maven

``` bash
<!-- Dependência para AWS SNS -->
<dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>sns</artifactId>
        <version>2.x.x</version> <!-- A versão mais recente disponível -->
 </dependency>
```

``` bash
<!-- Dependência para AWS SQS -->
<dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>sqs</artifactId>
        <version>2.x.x</version> <!-- A versão mais recente disponível -->
</dependency>
```

#### 5.2. Configurar as credenciais AWS no application.yml

``` bash
aws:
  accessKey: <ACCESS_KEY>
  secretKey: <SECRET_KEY>
  region: <AWS_REGION>
```

#### 5.3. Configurar o Cliente SNS e SQS usando as credenciais:

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

#### 5.4. Implementar a interação com SNS e SQS

Pub/Sub

PUB: Implementar Classe SnsService

SUB: Implementar Classe SqsService

**Pré-requisitos: configurar o gerenciamento de filas SQS e tópicos SNS na conta AWS e obter os ARNs (Amazon Resource Name) correspondentes para utilizá-los na aplicação.**


## Fluxo de desenvolvimento da aplicação

1. Iniciar a aplicação Spring Boot
2. Cliente envia feedback
    1. Um cliente interage com a aplicação, por exemplo, através de uma interface web ou criando um topico direto pela AWS.
    2. O cliente envia um feedback, fornecendo informações como FeedbackType (tipo) e mensagem. O id e o FeedbackStatus são atributos setados pelo sistema.
3. Enpoint REST para Enviar Feedback envia feedback do cliente.
    1. O cliente utiliza o endpoint **POST: /feedback/send** para enviar o feedback.
    2. O endpoint recebe as informações do feedback corretamente e faz as atribuições default para o id (uuid) e o FeedbackStatus.
    3. FeedbackStatus inicia com ...
4. Publicação do payload no Tópico SNS correspondente.
    1. A partir do endpoint **POST: /feedback/send**, já com os dados recebidos, o serviço principal (no caso, **SnsService**) é invocado.
    2. O serviço **SnsService** utiliza o **Cliente SNS** (*ver seção 5.3*) para publicar a mensagem no **tópico SNS** correspondente ao tipo de feedback(Sugestão, Elogio, Crítica)
        1. Significa que para cada FeedbackType teremos uma fila sendo gerada pelo nosso **Cliente SNS** que, em outras palavras, será o nosso produtor.
5. Nosso **Cliente SNS** encaminha a mensagem para as Filas SQS (que serão consumidas).
    1. O tópico SNS notifica todas as filas SQS que estão subscritas a ele do feedback recém-chegado.
    2. Cada fila SQS correspondente ao TIPO (FeedBackType) recebe a mensagem e a enfileira corretamente.
6. Nosso ***Consumidor de Fila SQS*** (a definir) processa o feedback corretamente.
    1. Em segundo plano, nosso ***Consumidor de Fila SQS*** (que foi previamente configurado na aplicação) está constamente verificando as filas SQS.
    2. Quando um **CustomerFeedback** é encontrado na fila SQS, o nosso ***Consumidor de Fila SQS*** o retira da fila e o processa de acordo com a lógica definida, mudando seu **FeedbackStatus** apropriadamente
7. Atualização do **FeedbackStatus**
    1. Após o processamento bem sucedido o status é atualizado para "Finalizado".


### Considerações adicionais

1. A Integração com o cliente pode ser iniciada pelo frontend da aplicação (pulga atrás da orelha mapeada com sucesso.)
2. Configuração AWS e Spring: antes do envio de feedbacks é necessário configurar previamente os tópicos SNS e as filas SQS na AWS, para que então, finalmente, possamos prosseguir com a integração desses serviços com o Spring Boot.
3. Para um sistema em produção, é importante monitorar as filas, tópicos e o consumo de mensagens (**CustomerFeedback**) para garantir que tudo esteja funcionando conforme esperado. isso pode ser feito usando ferramentas de monitoramente e logs.(a definir)

## Instalação e configurações iniciais

1. IDE: INTELIJ ULTIMATE.
2. Gerenciador de dependências: Maven.
3. Dependências da aplicação
```bash
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>2.2</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
````

