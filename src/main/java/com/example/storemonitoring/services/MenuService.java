package com.example.storemonitoring.services;

import java.sql.Time;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.storemonitoring.models.Menu;
import com.example.storemonitoring.repository.MenuRepository;

@Component
public class MenuService {
    @Autowired
    private MenuRepository menuRepository;

    public Menu addMenu(Menu menu) {
       return menuRepository.save(menu);
    }

    public Menu parseMenu(List<String> menu) throws Exception{
        if (menu.size() < 4) {
            throw new Exception("Invalid entry as " + menu);
        }
        return new Menu(0, menu.get(0), Integer.parseInt(menu.get(1)), Time.valueOf(menu.get(2)), Time.valueOf(menu.get(3)));
    }

    public void saveAllMenus(List<Menu> records) {
        menuRepository.saveAll(records);
    }
}
