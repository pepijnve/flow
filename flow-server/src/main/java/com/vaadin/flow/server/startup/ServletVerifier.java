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
package com.vaadin.flow.server.startup;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;
import java.util.Set;

import com.vaadin.flow.server.VaadinContext;

/**
 * Verify the servlet version on container initialization.
 * <p>
 * In cases of non compatible servlet version application deployment will fail.
 * <p>
 * For internal use only. May be renamed or removed in a future release.
 *
 * @since 1.0
 */
public class ServletVerifier implements VaadinServletContextStartupInitializer {

    @Override
    public void initialize(Set<Class<?>> classSet, VaadinContext context)
            throws VaadinInitializerException {
        try {
            verifyServletVersion();
        } catch (ServletException e) {
            throw new VaadinInitializerException(e.getMessage(), e);
        }
    }

    /**
     * Verify that the used servlet version is not too old.
     *
     * @throws ServletException
     *             thrown if the servlet version is not compatible
     */
    public static void verifyServletVersion() throws ServletException {
        try {
            Method m = HttpServletResponse.class
                    .getMethod("setContentLengthLong", long.class);
            if (m == null) {
                throw new ServletException("Servlet 3.1+ is required");
            }
        } catch (Exception e) {
            throw new ServletException("Servlet 3.1+ is required", e);
        }
    }
}
