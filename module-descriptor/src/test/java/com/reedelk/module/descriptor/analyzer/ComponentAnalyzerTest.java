package com.reedelk.module.descriptor.analyzer;

import com.reedelk.module.descriptor.analyzer.component.ComponentAnalyzer;
import com.reedelk.module.descriptor.analyzer.component.ComponentAnalyzerContext;
import com.reedelk.module.descriptor.analyzer.property.ComponentPropertyAnalyzer;
import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.module.descriptor.model.ComponentPropertyDescriptor;
import com.reedelk.module.descriptor.model.ComponentType;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComponentAnalyzerTest {

    @Mock
    private ComponentPropertyAnalyzer propertyAnalyzer;
    @Mock
    private ComponentPropertyDescriptor descriptor1;
    @Mock
    private ComponentPropertyDescriptor descriptor2;
    @Mock
    private ComponentPropertyDescriptor descriptor3;


    private ComponentAnalyzer analyzer;
    private ClassInfo componentClassInfo;


    @BeforeEach
    void setUp() {
        ScannerTestUtils.ScanContext scanContext = ScannerTestUtils.scan(TestComponent.class);

        componentClassInfo = scanContext.targetComponentClassInfo;

        ComponentAnalyzerContext context = spy(scanContext.context);

        doReturn(of(descriptor1), of(descriptor2), of(descriptor3), empty())
                .when(propertyAnalyzer).analyze(any(FieldInfo.class));

        analyzer = new ComponentAnalyzer(propertyAnalyzer);
    }

    @Test
    void shouldCorrectlyAnalyzeClassInfo() {
        // Given
        ClassInfo testComponentClassInfo = componentClassInfo;

        // When
        ComponentDescriptor descriptor = analyzer.analyze(testComponentClassInfo);

        // Then
        assertThat(descriptor.isHidden()).isFalse();
        assertThat(descriptor.getComponentPropertyDescriptors().size()).isEqualTo(3);
        assertThat(descriptor.getDisplayName()).isEqualTo("Test Component");
        assertThat(descriptor.getComponentType()).isEqualTo(ComponentType.PROCESSOR);
        assertThat(descriptor.getFullyQualifiedName()).isEqualTo(TestComponent.class.getName());
    }
}