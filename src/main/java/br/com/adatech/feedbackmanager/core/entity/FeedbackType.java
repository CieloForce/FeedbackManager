package br.com.adatech.feedbackmanager.core.entity;

public enum FeedbackType {

    suggestion("Suggestion"), //Duplicação sns ativada na aws, com desduplicação sqs, sem criptografia, com alto throughput --> limitação da transferência com base no GroupId
    compliment("Compliment"), //Duplicação sns desativada na aws, com desduplicação sqs, sem criptografia, sem alto throughput --> realmente é necessário a desduplicação.
    criticism("Criticism"); //Duplicação sns desativada na aws, com desduplicação, com criptografia, sem throughput

    private final String description;
    FeedbackType(String description){
        this.description = description;
    }
    public String getDescription(){
        return description;
    }

}

//Deve-se criar um FIFO SNS topic para uma FIFO SQS fila e passar MessageGroupId para garantir a ordem de entrega conforme a ordem de chegada.
//Deve-se ativar eliminação de duplicação de mensagens nos tópicos sns e desduplicação nas filas sqs.
//Alto throughtput pode limitar a taxa de tranferência de mensagens para uma mesma fila.
//Sem alto thoughtput todas as mensagens foram enviadas, mesmo atualizando a página da aws depois de muitas mensagens publicadas.