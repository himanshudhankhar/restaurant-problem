package com.example.storemonitoring.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.example.storemonitoring.models.Report;

@Component
public interface ReportRepository extends CrudRepository<Report, Integer> {
    
}
