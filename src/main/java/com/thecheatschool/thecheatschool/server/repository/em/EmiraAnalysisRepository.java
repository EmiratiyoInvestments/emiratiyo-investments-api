package com.thecheatschool.thecheatschool.server.repository.em;

import com.thecheatschool.thecheatschool.server.model.em.EmiraAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmiraAnalysisRepository extends JpaRepository<EmiraAnalysis, Long> {
    List<EmiraAnalysis> findAllByOrderByCreatedAtDesc();
}
