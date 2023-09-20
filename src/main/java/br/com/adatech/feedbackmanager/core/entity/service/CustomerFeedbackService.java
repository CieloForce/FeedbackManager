package br.com.adatech.feedbackmanager.core.entity.service;

import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.core.entity.repository.CustomerFeedbackRepository;
import br.com.adatech.feedbackmanager.core.exception.CustomerFeedbackNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerFeedbackService {

    @Autowired
    private CustomerFeedbackRepository repository;

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


    public CustomerFeedback create(CustomerFeedback model) {
        model.setId(getUUID());
        repository.save(model);
        return model;
    }

    public CustomerFeedback update(String id, CustomerFeedback model) {
        CustomerFeedback modelUpdated = findById(id);
        modelUpdated.setType(model.getType());
        modelUpdated.setStatus(model.getStatus());
        modelUpdated.setMessage(model.getMessage());
        repository.save(modelUpdated);
        return modelUpdated;
    }

    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }

    private static String getUUID(){
        return UUID.randomUUID().toString();
    }
}
