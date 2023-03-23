package com.example.storemonitoring.models.responses;

import com.example.storemonitoring.models.Menu;

public class AddMenuCSVResponse {
    Iterable<Menu> menus;
    String error;
    Boolean success;


    public AddMenuCSVResponse(Iterable<Menu> menus, String error, Boolean success) {
        this.menus = menus;
        this.error = error;
        this.success = success;
    }


    public Iterable<Menu> getMenus() {
        return this.menus;
    }

    public void setMenus(Iterable<Menu> menus) {
        this.menus = menus;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean isSuccess() {
        return this.success;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public AddMenuCSVResponse() {
    }

}

  