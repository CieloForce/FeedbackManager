package br.com.adatech.feedbackmanager;

import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.core.entity.FeedbackType;
import br.com.adatech.feedbackmanager.core.entity.repository.CustomerFeedbackRepository;
import br.com.adatech.feedbackmanager.core.entity.service.CustomerFeedbackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StartupTests {

    @Test
    void persistenceTeste() {
        CustomerFeedbackService service = new CustomerFeedbackService();

        CustomerFeedback model = new CustomerFeedback();
        model.setMessage("Mensagem de teste");
        model.setType(FeedbackType.Sugestoes);

        service.create(model);
    }

}