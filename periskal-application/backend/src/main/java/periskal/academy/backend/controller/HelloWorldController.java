package periskal.academy.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @GetMapping("/welcome")
    public String welcome() {
        return "testtest";
    }
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
