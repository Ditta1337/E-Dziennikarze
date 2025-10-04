package com.edziennikarze.gradebook.property;

import com.edziennikarze.gradebook.exception.PropertyParseException;
import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalTime;

@Service
@AllArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public Flux<Property> getAllProperties() {
        return propertyRepository.findAll()
                .map(this::mapValue);
    }

    public Mono<Property> getPropertyByName(String name) {
        return propertyRepository.findByName(name)
                .map(this::mapValue);
    }

    public Mono<Property> updateProperty(Mono<Property> propertyMono) {
        return propertyMono.flatMap(property -> propertyRepository.findById(property.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Property with id " + property.getId() + " not found")))
                .flatMap(existingProperty -> {
                    existingProperty.setValue(property.getValue());
                    return propertyRepository.save(existingProperty);
                }))
                .map(this::mapValue);
    }

    private Property mapValue(Property property) {
        Object value;
        String valueToParse = property.getValue() == null ? property.getDefaultValue() : (String) property.getValue();
        try {
            value = switch (property.getType()) {
                case STRING -> valueToParse;
                case INTEGER -> Integer.parseInt(valueToParse);
                case BOOLEAN -> Boolean.parseBoolean(valueToParse);
                case DOUBLE -> Double.parseDouble(valueToParse);
                case TIME -> LocalTime.parse(valueToParse);
            };
        } catch (Exception e) {
            throw new PropertyParseException("Cannot parse property " + property.getName() + " with value " + valueToParse + " to type " + property.getType());
        }

        return Property.builder()
                .id(property.getId())
                .name(property.getName())
                .type(property.getType())
                .defaultValue(property.getDefaultValue())
                .value(value)
                .build();
    }
}
