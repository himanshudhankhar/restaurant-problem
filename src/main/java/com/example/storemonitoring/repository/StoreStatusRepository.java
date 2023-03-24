package com.example.storemonitoring.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.example.storemonitoring.models.StoreStatus;

@Component
public interface StoreStatusRepository extends CrudRepository<StoreStatus, Integer> {
    ArrayList<StoreStatus> findFirstByStoreIdOrderByTimeStampUTCDesc(String Id);

    ArrayList<StoreStatus> findAllByTimeStampUTCBetweenAndStoreId(LocalDateTime startDay, LocalDateTime endDay, String storeId);
}
