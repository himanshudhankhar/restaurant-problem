package com.example.storemonitoring.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.example.storemonitoring.models.Menu;

@Component
public interface MenuRepository extends CrudRepository<Menu, Integer> 
{
    
}
