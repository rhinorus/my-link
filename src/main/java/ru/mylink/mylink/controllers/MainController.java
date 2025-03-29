package ru.mylink.mylink.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import ru.mylink.mylink.services.LinkService;

@Controller
public class MainController {

    LinkService linkService;

    MainController(LinkService linkService) {
        this.linkService = linkService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home() {
        return new ModelAndView("redirect:/pages/index.html");
    }

    @GetMapping(value = "{shortUrl}")
    RedirectView get(@PathVariable("shortUrl") String shortUrl) {
        var optionalLink = linkService.find(shortUrl);

        if (optionalLink.isPresent())
            return new RedirectView(optionalLink.get().getUrl());
        return new RedirectView("/pages/404");
    }

}
