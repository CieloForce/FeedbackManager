package br.com.adatech.feedbackmanager.core.entity.repository;

import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, String> {
}
