package com.edziennikarze.gradebook.property;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.config.TestSecurityConfig;
import com.edziennikarze.gradebook.exception.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.List;

import static com.edziennikarze.gradebook.property.PropertyType.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
@Import({TestSecurityConfig.class})
class PropertyControllerIntTest {

    @Autowired
    private PropertyController propertyController;

    @Autowired
    private PropertyRepository propertyRepository;


    @Test
    void shouldGetAllPropertiesAndParseAllTypesCorrectly() {
        // when
        List<Property> allProperties = propertyController.getAllProperties()
                .collectList()
                .block();

        // then
        assertNotNull(allProperties);
        assertEquals(11, allProperties.size());

        Property schoolName = allProperties.stream().filter(p -> p.getName().equals("schoolFullName")).findFirst().orElseThrow();
        assertEquals("Akademia Górniczo-Hutnicza im. Stanisława Staszica w Krakowie", schoolName.getValue());
        assertInstanceOf(String.class, schoolName.getValue());

        Property startTime = allProperties.stream().filter(p -> p.getName().equals("schoolDayStartTime")).findFirst().orElseThrow();
        assertEquals(LocalTime.of(7, 0), startTime.getValue());
        assertInstanceOf(LocalTime.class, startTime.getValue());

        Property lessonDuration = allProperties.stream().filter(p -> p.getName().equals("lessonDurationMinutes")).findFirst().orElseThrow();
        assertEquals(45, lessonDuration.getValue());
        assertInstanceOf(Integer.class, lessonDuration.getValue());

        Property preferences = allProperties.stream().filter(p -> p.getName().equals("allowTeacherPickPreferences")).findFirst().orElseThrow();
        assertEquals(false, preferences.getValue());
        assertInstanceOf(Boolean.class, preferences.getValue());
    }

    @Test
    void shouldThrowPropertyParseExceptionForMalformedValue() {
        // given
        Property badProperty = Property.builder()
                .name("malformedInteger")
                .type(INTEGER)
                .defaultValue("0")
                .value("this-is-not-an-integer")
                .build();

        Property savedBadProperty = propertyRepository.save(badProperty).block();
        assertNotNull(savedBadProperty);

        // when
        Mono<Property> resultMono = propertyController.getPropertyByName("malformedInteger");

        // then
        assertThrows(ParseException.class, resultMono::block);
    }

    @Test
    void shouldGetPropertyByName() {
        // given
        String propertyName = "schoolFullName";

        // when
        Property result = propertyController.getPropertyByName(propertyName).block();

        // then
        assertNotNull(result);
        assertEquals(propertyName, result.getName());
        assertEquals("Akademia Górniczo-Hutnicza im. Stanisława Staszica w Krakowie", result.getValue());
    }

    @Test
    void shouldUpdateProperty() {
        // given
        Property originalProperty = propertyRepository.findByName("maxLessonsPerDay").block();
        assertNotNull(originalProperty);
        assertEquals("8", originalProperty.getDefaultValue());

        Property updatedPropertyRequest = Property.builder()
                .id(originalProperty.getId())
                .name(originalProperty.getName())
                .type(originalProperty.getType())
                .defaultValue(originalProperty.getDefaultValue())
                .value("10")
                .build();

        // when
        Property result = propertyController.updateProperty(Mono.just(updatedPropertyRequest)).block();

        // then
        assertNotNull(result);
        assertEquals(originalProperty.getId(), result.getId());
        assertEquals(10, result.getValue());
    }
}

