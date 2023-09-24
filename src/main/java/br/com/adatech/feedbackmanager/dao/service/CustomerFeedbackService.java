package br.com.adatech.feedbackmanager.dao.service;

import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.core.entity.FeedbackType;
import br.com.adatech.feedbackmanager.dao.repository.CustomerFeedbackRepository;
import br.com.adatech.feedbackmanager.core.exception.CustomerFeedbackNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;

@Service
public class CustomerFeedbackService {

    private final CustomerFeedbackRepository repository;
    @Autowired
    public CustomerFeedbackService(CustomerFeedbackRepository repository){
        this.repository = repository;
    }

    /**
     * Retorna todos os CustomerFeedback
     * @return Uma lista de CustomerFeedback
     */
    public List<CustomerFeedback> findAll(){
        return repository.findAll();
    }

    /**
     * Retorna um CustomerFeedback
     * @param id - id do CustomerFeedback
     * @return CustomerFeedback de acordo com o id fornecido
     */
    public CustomerFeedback findById(String id) {
        return repository.findById(id).orElseThrow( ()-> new CustomerFeedbackNotFoundException(id) );
    }

    /**
     *  Salva um novo CustomerFeedback
     * @param model - CustomerFeedback para ser salvo
     * @return CustomerFeedback salvo
     */
    public CustomerFeedback create(CustomerFeedback model) {
        repository.save(model);
        return model;
    }

    /**
     * Atualiza um CustomerFeedback existente
     * @param id - id do CustomerFeedback
     * @param model - CustomerFeedback para atualizar
     * @return CustomerFeedback atualizado
     */
    public CustomerFeedback update(String id, CustomerFeedback model) {
        CustomerFeedback modelUpdated = findById(id);
        modelUpdated.setType(model.getType());
        modelUpdated.setStatus(model.getStatus());
        modelUpdated.setMessage(model.getMessage());
        modelUpdated.setMessageId(model.getMessageId());
        repository.save(modelUpdated);
        return modelUpdated;
    }

    /**
     * Exclui um CustomerFeedback
     * @param id - Id do CustomerFeedback a ser excluido
     */
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }

    //JPA não consegue encontrar o messageID corretamente porque temos um objeto complexo Então precisa ser "na unha".
    /** Forçando o encontro de messageID no banco de dados corrente **/
    public CustomerFeedback findFeedbackByMessageId(String messageId) {

        try{
            Connection connection = DriverManager.getConnection("jdbc:h2:mem:feedbackdb", "sa", "p123");
            String sql = "SELECT * FROM CUSTOMER_FEEDBACK WHERE MESSAGE_ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, messageId);

            ResultSet rs = statement.executeQuery();

            CustomerFeedback customerFeedback = null;
            while(rs.next()){
                String uuid = rs.getString("uuid");
                String message = rs.getString("message");
                int feedbackType = rs.getInt("type");
                int feedbackStatus = rs.getInt("status");
                customerFeedback = new CustomerFeedback(uuid, messageId, message, feedbackType, feedbackStatus);
            }
            return customerFeedback;
        } catch (SQLException e){
            System.err.println("Ocorreu um erro ao buscar CustomerFeedback pelo messageID: " + e.getMessage());
            System.err.println("Banco em memória anterior pode ter sido deletado.");
            return null;
        }

    }
}
