package com.example.storemonitoring.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.example.storemonitoring.models.StoreStatus;

@Component
public interface StoreStatusRepository extends CrudRepository<StoreStatus, Integer> {
}
