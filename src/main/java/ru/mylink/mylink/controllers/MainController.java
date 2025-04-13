package ru.mylink.mylink.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import ru.mylink.mylink.model.entity.Link;

@Controller
public class MainController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home() {
        return new ModelAndView("redirect:/pages/index.html");
    }

    @GetMapping(value = "{shortUrl}")
    public RedirectView get(@PathVariable("shortUrl") Link link) {
        return new RedirectView(link.getUrl());
    }

}
