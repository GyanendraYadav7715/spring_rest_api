package com.codewithmosh.store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String index() {
        return "index.html";
    }

    @RequestMapping("/about")
    public String sayHello(Model model) {
        model.addAttribute("message", "Gyanendra Yadav");
        return "index";

    }
}
