package com.newgen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newgen.model.LogEntity;

@Repository
public interface LoggingRepository extends JpaRepository<LogEntity, Long> {

}
