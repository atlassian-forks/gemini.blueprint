/******************************************************************************
 * Copyright (c) 2006, 2010 VMware Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html and the Apache License v2.0
 * is available at http://www.opensource.org/licenses/apache2.0.php.
 * You may elect to redistribute this code under either of these licenses.
 * <p/>
 * Contributors:
 * VMware Inc.
 *****************************************************************************/

package org.eclipse.gemini.blueprint;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.Dictionary;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@ContextConfiguration(locations = "dict-editor.xml", initializers = DictionaryEditorTest.class)
public class DictionaryEditorTest extends AbstractJUnit4SpringContextTests implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private Dictionary dictionary;

    @Autowired
    // Spring detects 2 matching beans:
    // - dictionary (defined in `dict-editor.xml`)
    // - systemProperties - one of the default beans
    //
    // Spring 6.1 stopped using previous "name discoverer"
    // Use of `Qualifier` annotation explicitly defines the bean we want to inject
    // Another option would be to compile code with `-parameters` flag
    //
    // see https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x/b05067858f10a9b3a42a09556c07fbaa1dfdf8d3#parameter-name-retention
    @Qualifier("dictionary")
    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Test
    public void testInjection() {
        assertNotNull(dictionary);
    }

    @Test
    public void testInjectedValue() {
        assertSame(applicationContext.getBean("dictionary"), dictionary);
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.getBeanFactory().registerCustomEditor(Dictionary.class, PropertiesEditor.class);
    }
}