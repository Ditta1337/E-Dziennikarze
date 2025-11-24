package com.edziennikarze.gradebook.property;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/property")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping("/all")
    public Flux<Property> getAllProperties() {
        return propertyService.getAllProperties();
    }

    @GetMapping("/name/{name}")
    public Mono<Property> getPropertyByName(@PathVariable String name) {
        return propertyService.getPropertyByName(name);
    }

    @PutMapping
    public Mono<Property> updateProperty(@RequestBody Mono<Property> propertyMono) {
        return propertyService.updateProperty(propertyMono);
    }
}
