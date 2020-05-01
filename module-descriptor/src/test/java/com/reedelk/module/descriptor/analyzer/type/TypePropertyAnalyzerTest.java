package com.reedelk.module.descriptor.analyzer.type;

import com.reedelk.module.descriptor.model.type.TypePropertyDescriptor;
import io.github.classgraph.ClassInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.reedelk.module.descriptor.analyzer.AnalyzerTestUtils.classInfoOf;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.assertj.core.api.Assertions.assertThat;

class TypePropertyAnalyzerTest {

    @Test
    void shouldReturnCorrectPropertyDescriptors() {
        // Given
        ClassInfo classInfo = classInfoOf(TypePropertyComponent.class);
        TypePropertyAnalyzer analyzer = new TypePropertyAnalyzer(classInfo);

        // When
        List<TypePropertyDescriptor> properties = analyzer.analyze();

        // Then
        assertThat(properties).hasSize(3);
        assertExists(properties, "property1", "Util.property1", String.class.getName(), "Property1 description");
        assertExists(properties, "property2", "Util.property2", long.class.getName(), "Property2 description");
        assertExists(properties, "correlationId", "context.correlationId", String.class.getName(), "Returns the current flow correlation id.");
    }

    private void assertExists(List<TypePropertyDescriptor> collection,
                              String name,
                              String example,
                              String type,
                              String description) {
        TypePropertyDescriptor expected = create(name, example, type, description);
        assertExists(collection, expected);
    }

    private void assertExists(List<TypePropertyDescriptor> collection, TypePropertyDescriptor expected) {
        boolean found = collection.stream().anyMatch(actual -> reflectionEquals(expected, actual));
        assertThat(found).withFailMessage("Could not find expected=" + expected.toString()).isTrue();
    }

    private TypePropertyDescriptor create(String name,
                                          String example,
                                          String type,
                                          String description) {
        TypePropertyDescriptor descriptor = new TypePropertyDescriptor();
        descriptor.setDescription(description);
        descriptor.setExample(example);
        descriptor.setType(type);
        descriptor.setName(name);
        return descriptor;
    }
}
