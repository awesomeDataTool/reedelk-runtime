package com.reedelk.module.descriptor.analyzer.property.type;

import com.reedelk.module.descriptor.analyzer.component.ComponentAnalyzerContext;
import com.reedelk.module.descriptor.analyzer.component.UnsupportedType;
import com.reedelk.module.descriptor.analyzer.property.PropertyAnalyzer;
import com.reedelk.module.descriptor.model.*;
import com.reedelk.runtime.api.commons.PlatformTypes;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;

import java.util.List;
import java.util.Optional;

import static com.reedelk.module.descriptor.analyzer.commons.ScannerUtils.isCollapsible;
import static com.reedelk.module.descriptor.analyzer.commons.ScannerUtils.isShareable;
import static java.util.stream.Collectors.toList;

public class TypeObjectFactory implements TypeDescriptorFactory {

    @Override
    public boolean test(String fullyQualifiedClassName, FieldInfo fieldInfo, ComponentAnalyzerContext context) {
        // If it is not supported it must be a custom defined type (implementing Implementor interface).
        return !PlatformTypes.isSupported(fullyQualifiedClassName);
    }

    @Override
    public TypeDescriptor create(String fullyQualifiedClassName, FieldInfo fieldInfo, ComponentAnalyzerContext context) {
        // We check that it is a user defined object type (with Implementor).
        // We check that we can resolve class info. If we can, then ..
        ClassInfo classInfo = context.getClassInfo(fullyQualifiedClassName);
        if (classInfo == null) {
            throw new UnsupportedType(fullyQualifiedClassName);
        }

        Shared shared = isShareable(classInfo);
        Collapsible collapsible = isCollapsible(classInfo);
        PropertyAnalyzer propertyAnalyzer = new PropertyAnalyzer(context);

        List<PropertyDescriptor> allProperties = classInfo
                .getFieldInfo()
                .stream()
                .map(propertyAnalyzer::analyze)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        TypeObjectDescriptor descriptor = new TypeObjectDescriptor();
        descriptor.setTypeFullyQualifiedName(fullyQualifiedClassName);
        descriptor.setObjectProperties(allProperties);
        descriptor.setCollapsible(collapsible);
        descriptor.setShared(shared);
        return descriptor;
    }
}