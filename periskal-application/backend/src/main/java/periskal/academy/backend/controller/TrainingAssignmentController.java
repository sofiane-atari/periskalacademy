package periskal.academy.backend.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import periskal.academy.backend.jpa.Assignment.TrainingAssignment;
import periskal.academy.backend.service.TrainingAssignmentService;

@RestController
@RequestMapping("/api/assignments")
public class TrainingAssignmentController {
    
    private final TrainingAssignmentService assignmentService;

    public TrainingAssignmentController(TrainingAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    public ResponseEntity<List<TrainingAssignment>> createOrUpdateAssignments(@RequestBody List<TrainingAssignment> assignments) {

        return ResponseEntity.ok(assignmentService.createOrUpdateAssignments(assignments));
    }

    @GetMapping("/{skipperId}")
    public ResponseEntity<List<TrainingAssignment>> getAssignmentsForSkipper(@PathVariable String skipperId) {

        return ResponseEntity.ok(assignmentService.getAssignmentsForSkipper(skipperId));
    }
    
    @GetMapping("/fleetmanager/{fleetManagerId}")
    public ResponseEntity<List<TrainingAssignment>> getAssignmentsByFleetManager(@PathVariable String fleetManagerId) {

        return ResponseEntity.ok(assignmentService.getAssignmentsByFleetManager(fleetManagerId));
    }
    
    @GetMapping("/{skipperId}/{trainingId}")
    public ResponseEntity<Boolean> checkAssignment(@PathVariable String skipperId, @PathVariable Long trainingId) {

        return ResponseEntity.ok(assignmentService.checkAssignment(skipperId, trainingId));
    }

    @DeleteMapping("/{skipperId}/{trainingId}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable String skipperId, @PathVariable Long trainingId) {

        assignmentService.deleteAssignment(skipperId, trainingId);
        return ResponseEntity.ok().build();
    }

}
