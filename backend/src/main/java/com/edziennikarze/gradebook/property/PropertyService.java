package com.edziennikarze.gradebook.property;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.exception.AccessDenialException;
import com.edziennikarze.gradebook.exception.ParseException;
import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    private final LoggedInUserService loggedInUserService;

    public Flux<Property> getAllProperties() {
        return propertyRepository.findAll()
                .map(this::mapValue);
    }

    public Mono<Property> getPropertyByName(String name) {
        return propertyRepository.findByName(name)
                .flatMap(this::ensurePropertyIsFetchable)
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

    public Mono<Map<String, Object>> getPropertiesAsMap(List<String> propertyNames) {
        return propertyRepository.findAllByNameIn(propertyNames)
                .map(this::mapValue)
                .collectMap(Property::getName, Property::getValue);
    }

    private Property mapValue(Property property) {
        Object rawValue = property.getValue() != null ? property.getValue() : property.getDefaultValue();

        try {
            Object value = switch (property.getType()) {
                case STRING -> rawValue != null ? String.valueOf(rawValue) : "";
                case INTEGER -> rawValue instanceof Number n ? n.intValue() : Integer.parseInt(String.valueOf(rawValue));
                case DOUBLE -> rawValue instanceof Number n ? n.doubleValue() : Double.parseDouble(String.valueOf(rawValue));
                case BOOLEAN -> rawValue instanceof Boolean b ? b : Boolean.parseBoolean(String.valueOf(rawValue));
                case TIME -> rawValue instanceof LocalTime t ? t : LocalTime.parse(String.valueOf(rawValue));
            };

            return Property.builder()
                    .id(property.getId())
                    .name(property.getName())
                    .type(property.getType())
                    .defaultValue(property.getDefaultValue())
                    .value(value)
                    .build();

        } catch (Exception e) {
            throw new ParseException(
                    "Cannot parse property " + property.getName()
                            + " with value " + rawValue
                            + " to type " + property.getType()
            );
        }
    }


    private Mono<Property> ensurePropertyIsFetchable(Property property) {
        if (property.saveToFetch) {
            return Mono.just(property);
        }

        return loggedInUserService.getLoggedInUser()
                .then(Mono.just(property))
                .switchIfEmpty(Mono.error(new AccessDenialException("Access to property " + property.getName() + " is denied")));
    }
}
