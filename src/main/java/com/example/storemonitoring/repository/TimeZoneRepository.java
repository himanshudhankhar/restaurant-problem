package com.example.storemonitoring.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.example.storemonitoring.models.TimeZone;

@Component
public interface TimeZoneRepository extends CrudRepository<TimeZone, Integer>{
    TimeZone findByStoreId(String storeId);
}
