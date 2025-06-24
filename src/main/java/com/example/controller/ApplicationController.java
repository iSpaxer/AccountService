package com.example.controller;

import com.example.util.ApplicationDataComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    // todo Авторизация Для админов можно было бы показывать более крутое описание версий
    private final ApplicationDataComponent applicationDataComponent;

    @Autowired
    public ApplicationController(ApplicationDataComponent applicationDataComponent) {
        this.applicationDataComponent = applicationDataComponent;
    }

    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> versionAPI() {
        return ResponseEntity.ok(applicationDataComponent);
    }


}
