package com.thecheatschool.thecheatschool.server.repository.em;

import com.thecheatschool.thecheatschool.server.model.em.EMBusinessSetupSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EMBusinessSetupRepository extends JpaRepository<EMBusinessSetupSubmission, Long> {

    List<EMBusinessSetupSubmission> findByStatus(String status);

    List<EMBusinessSetupSubmission> findByExpiresAtBefore(LocalDateTime date);
}
