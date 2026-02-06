package com.example.devops.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the DevOps CI/CD Project!";
    }


    @GetMapping("/hello")
    public String hello(){
        return "Welcome to the DevOps CI/CD Project!";


    }





}

