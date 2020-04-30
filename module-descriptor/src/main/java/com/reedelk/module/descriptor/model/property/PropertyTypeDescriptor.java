package com.reedelk.module.descriptor.model.property;

import java.io.Serializable;

// TODO: This is a Component property type descriptor.
public interface PropertyTypeDescriptor extends Serializable {

    Class<?> getType();

    default void setType(Class<?> type) {}

}