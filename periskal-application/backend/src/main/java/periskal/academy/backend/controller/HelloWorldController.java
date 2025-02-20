package periskal.academy.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import periskal.academy.backend.jpa.Test;
import periskal.academy.backend.jpa.TestRepository;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class HelloWorldController {

    private final TestRepository repository;

    public HelloWorldController(TestRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/welcome")
    public String welcome() {
        return "testtest";
    }

    @GetMapping("/api/")
    public String home() {
        return "home";
    }

    @GetMapping("/api/test")
    public List<Test> getAllTests() {
        Iterable<Test> iterable = repository.findAll();
        List<Test> testList = new ArrayList<>();
        iterable.forEach(testList::add);
        return testList;
    }

}
