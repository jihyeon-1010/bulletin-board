package com.example.bulletinboardapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

   @GetMapping("/signup")
   public String signup() {
        return "auth/signup";
   }
}
