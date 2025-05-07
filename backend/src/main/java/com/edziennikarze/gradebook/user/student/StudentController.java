package com.edziennikarze.gradebook.user.student;

import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.student.dto.StudentsGuardianDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/student")
@AllArgsConstructor
public class StudentController {
    private StudentService studentService;

    @PostMapping("/create")
    public Mono<User> createStudent(@RequestBody Mono<User> userMono){
        return studentService.createStudent(userMono);
    }

    @GetMapping("/get/{uuid}")
    public Mono<User> getStudent(@PathVariable("uuid") UUID uuid){
        return studentService.getStudent(uuid);
    }

    @GetMapping("/get/all")
    public Flux<User> getAllStudents(){
        return studentService.getAllStudents();
    }

    @DeleteMapping("/delete/{uuid}")
    public Mono<User> deleteStudent(@PathVariable("uuid") UUID uuid){
        return studentService.deleteStudent(uuid);
    }

    @PatchMapping("/add/guardian")
    public Mono<Student> setStudentGuardian(@RequestBody Mono<StudentsGuardianDTO> studentsGuardianMono){
        return studentService.setStudentsGuardian(studentsGuardianMono);
    }

    @PatchMapping("/preferences/activate/{uuid}")
    public Mono<Student> activateStudentPreferences(@PathVariable("uuid") UUID uuid){
        return studentService.setStudentPreferencesCapabilities(uuid, true);
    }

    @PatchMapping("/preferences/deactivate/{uuid}")
    public Mono<Student> deactivateStudentPreferences(@PathVariable("uuid") UUID uuid){
        return studentService.setStudentPreferencesCapabilities(uuid, false);
    }
}
