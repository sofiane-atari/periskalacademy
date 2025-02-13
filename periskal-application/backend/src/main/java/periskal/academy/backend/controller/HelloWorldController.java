package periskal.academy.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import periskal.academy.backend.jpa.TestRepository;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class HelloWorldController {

    private final TestRepository repository;

    public HelloWorldController(TestRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "testtest";
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/test")
    public String test() {
        return repository.findAll().toString();
    }

}
