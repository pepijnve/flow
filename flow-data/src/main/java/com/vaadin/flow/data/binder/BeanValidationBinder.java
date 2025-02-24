/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.data.binder;

import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.BeanPropertySet.NestedBeanPropertyDefinition;
import com.vaadin.flow.data.validator.BeanValidator;
import com.vaadin.flow.internal.BeanUtil;

/**
 * Binder that uses reflection based on the provided bean type to resolve bean
 * properties. The Binder automatically adds BeanValidator which validates beans
 * using JSR-303 specification. It assumes that JSR-303 bean validation
 * implementation is present on the classpath.
 *
 * @author Vaadin Ltd
 * @since 1.0
 * @see Binder
 * @see BeanValidator
 * @see HasValue
 *
 * @param <BEAN>
 *            the bean type
 */
public class BeanValidationBinder<BEAN> extends Binder<BEAN> {

    private final Class<BEAN> beanType;

    private RequiredFieldConfigurator requiredConfigurator = RequiredFieldConfigurator.DEFAULT;

    /**
     * Creates a new binder that uses reflection based on the provided bean type
     * to resolve bean properties. It assumes that JSR-303 bean validation
     * implementation is present on the classpath. If there is no such
     * implementation available then {@link Binder} class should be used instead
     * (this constructor will throw an exception). Otherwise
     * {@link BeanValidator} is added to each binding that is defined using a
     * property name.
     *
     * @param beanType
     *            the bean type to use, not <code>null</code>
     */
    public BeanValidationBinder(Class<BEAN> beanType) {
        this(beanType, false);
    }

    /**
     * Creates a new binder that uses reflection based on the provided bean type
     * to resolve bean properties. It assumes that JSR-303 bean validation
     * implementation is present on the classpath. If there is no such
     * implementation available then {@link Binder} class should be used instead
     * (this constructor will throw an exception). Otherwise
     * {@link BeanValidator} is added to each binding that is defined using a
     * property name.
     *
     * @param beanType
     *            the bean type to use, not {@code null}
     * @param scanNestedDefinitions
     *            if {@code true}, scan for nested property definitions as well
     */
    public BeanValidationBinder(Class<BEAN> beanType,
            boolean scanNestedDefinitions) {
        super(beanType, scanNestedDefinitions);
        if (!BeanUtil.checkBeanValidationAvailable()) {
            throw new IllegalStateException(BeanValidationBinder.class
                    .getSimpleName()
                    + " cannot be used because a JSR-303 Bean Validation "
                    + "implementation not found on the classpath or could not be initialized. Use "
                    + Binder.class.getSimpleName() + " instead");
        }
        this.beanType = beanType;
    }

    /**
     * Sets a logic which allows to configure require indicator via
     * {@link HasValue#setRequiredIndicatorVisible(boolean)} based on property
     * descriptor.
     * <p>
     * Required indicator configuration will not be used at all if
     * {@code configurator} is null.
     * <p>
     * By default the {@link RequiredFieldConfigurator#DEFAULT} configurator is
     * used.
     *
     * @param configurator
     *            required indicator configurator, may be {@code null}
     */
    public void setRequiredConfigurator(
            RequiredFieldConfigurator configurator) {
        requiredConfigurator = configurator;
    }

    /**
     * Gets field required indicator configuration logic.
     *
     * @see #setRequiredConfigurator(RequiredFieldConfigurator)
     *
     * @return required indicator configurator, may be {@code null}
     */
    public RequiredFieldConfigurator getRequiredConfigurator() {
        return requiredConfigurator;
    }

    @Override
    protected BindingBuilder<BEAN, ?> configureBinding(
            BindingBuilder<BEAN, ?> binding,
            PropertyDefinition<BEAN, ?> definition) {
        Class<?> actualBeanType = findBeanType(beanType, definition);
        BeanValidator validator = new BeanValidator(actualBeanType,
                definition.getTopLevelName());
        if (requiredConfigurator != null) {
            configureRequired(binding, definition, validator);
        }
        return binding.withValidator(validator);
    }

    /**
     * Finds the bean type containing the property the given definition refers
     * to.
     *
     * @param beanType
     *            the root beanType
     * @param definition
     *            the definition for the property
     * @return the bean type containing the given property
     */
    @SuppressWarnings({ "rawtypes" })
    private Class<?> findBeanType(Class<BEAN> beanType,
            PropertyDefinition<BEAN, ?> definition) {
        if (definition instanceof NestedBeanPropertyDefinition) {
            return ((NestedBeanPropertyDefinition) definition).getParent()
                    .getType();
        } else {
            // Non nested properties must be defined in the main type
            return beanType;
        }
    }

    private void configureRequired(BindingBuilder<BEAN, ?> binding,
            PropertyDefinition<BEAN, ?> definition, BeanValidator validator) {
        assert requiredConfigurator != null;
        Class<?> propertyHolderType = definition.getPropertyHolderType();
        BeanDescriptor descriptor = validator.getJavaxBeanValidator()
                .getConstraintsForClass(propertyHolderType);
        PropertyDescriptor propertyDescriptor = descriptor
                .getConstraintsForProperty(definition.getTopLevelName());
        if (propertyDescriptor == null) {
            return;
        }
        if (propertyDescriptor.getConstraintDescriptors().stream()
                .map(ConstraintDescriptor::getAnnotation)
                .anyMatch(constraint -> requiredConfigurator.test(constraint,
                        binding))) {
            binding.getField().setRequiredIndicatorVisible(true);
        }
    }

}
