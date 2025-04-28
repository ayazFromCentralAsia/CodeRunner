package com.example.language_runtime_service.repository;

import com.example.language_runtime_service.entity.CodeResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeResultRepository extends JpaRepository<CodeResult, Integer> {
}
