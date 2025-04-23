package periskal.academy.backend.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import periskal.academy.backend.dto.SkippersDTO;

@RestController
@RequestMapping("/api/skippers")
public class SkipperController {


    @GetMapping()
    public ResponseEntity<List<SkippersDTO>> getSkippers() {

        List<SkippersDTO> skippers = new ArrayList<>();

        SkippersDTO skipper1 = new SkippersDTO();
        skipper1.setId("PER00181");
        skipper1.setUsername("Skipper_1");
        skipper1.setFirstname("Firstname");
        skipper1.setLastname("Lastname");
        skipper1.setShipname("Night of the Seas");
        skipper1.setRole("Skipper");
        skipper1.setProducts(Arrays.asList("Trackpilot", "AIS", "ECDIS"));
        skippers.add(skipper1);
        

        SkippersDTO skipper2 = new SkippersDTO();
        skipper2.setId("PER00182");
        skipper2.setUsername("Skipper_2");
        skipper2.setFirstname("John");
        skipper2.setLastname("Doe");
        skipper2.setShipname("Northern Star");
        skipper2.setRole("Skipper");
        skipper2.setProducts(Arrays.asList("Trackpilot", "ECDIS"));
        skippers.add(skipper2);
        

        SkippersDTO skipper3 = new SkippersDTO();
        skipper3.setId("PER00183");
        skipper3.setUsername("Skipper_3");
        skipper3.setFirstname("Jane");
        skipper3.setLastname("Smith");
        skipper3.setShipname("Ocean Explorer");
        skipper3.setRole("Skipper");
        skipper3.setProducts(Arrays.asList("AIS", "ECDIS"));
        skippers.add(skipper3);
        
        return ResponseEntity.ok(skippers);
    }
}
