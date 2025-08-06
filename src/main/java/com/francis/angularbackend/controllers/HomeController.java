package com.francis.angularbackend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    public String index(Model model) {
        model.addAttribute("name", "Francis");
        return "index";
    }

}
