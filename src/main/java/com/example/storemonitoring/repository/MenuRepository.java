package com.example.storemonitoring.repository;
import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.example.storemonitoring.models.Menu;

@Component
public interface MenuRepository extends CrudRepository<Menu, Integer> 
{
    ArrayList<Menu> findByStoreId(String restoId);

    ArrayList<Menu> findByStoreIdAndDay(String restoId, Integer day);
}
