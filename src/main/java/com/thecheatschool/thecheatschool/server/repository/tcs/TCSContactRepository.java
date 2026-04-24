package com.thecheatschool.thecheatschool.server.repository.tcs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thecheatschool.thecheatschool.server.model.tcs.TCSContact;

import java.util.List;

@Repository
public interface TCSContactRepository extends JpaRepository<TCSContact, Long> {
    List<TCSContact> findByStatus(String status);
}