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
        Object value;

        try {
            switch (property.getType()) {
                case STRING:
                    if (rawValue == null) {
                        value = "";
                    } else {
                        value = String.valueOf(rawValue);
                    }
                    break;
                case INTEGER:
                    if (rawValue instanceof Number) {
                        value = ((Number) rawValue).intValue();
                    } else {
                        value = Integer.parseInt(rawValue.toString());
                    }
                    break;
                case BOOLEAN:
                    if (rawValue instanceof Boolean) {
                        value = rawValue;
                    } else {
                        value = Boolean.parseBoolean(rawValue.toString());
                    }
                    break;
                case DOUBLE:
                    if (rawValue instanceof Number) {
                        value = ((Number) rawValue).doubleValue();
                    } else {
                        value = Double.parseDouble(rawValue.toString());
                    }
                    break;
                case TIME:
                    if (rawValue instanceof java.time.LocalTime) {
                        value = rawValue;
                    } else {
                        value = java.time.LocalTime.parse(rawValue.toString());
                    }
                    break;
                default:
                    value = rawValue;
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Cannot parse property " + property.getName() + " with value " + rawValue + " to type " + property.getType(), e
            );
        }

        return Property.builder()
                .id(property.getId())
                .name(property.getName())
                .type(property.getType())
                .defaultValue(property.getDefaultValue())
                .value(value)
                .saveToFetch(property.isSaveToFetch())
                .build();
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
