/******************************************************************************
 * Copyright (c) 2006, 2010 VMware Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution. 
 * The Eclipse Public License is available at 
 * http://www.eclipse.org/legal/epl-v10.html and the Apache License v2.0
 * is available at http://www.opensource.org/licenses/apache2.0.php.
 * You may elect to redistribute this code under either of these licenses. 
 * 
 * Contributors:
 *   VMware Inc.
 *****************************************************************************/

package org.eclipse.gemini.blueprint.iandt.jdk5.io;

import org.eclipse.gemini.blueprint.iandt.BaseIntegrationTest;
import org.eclipse.gemini.blueprint.iandt.io.component.ComponentBean;
import org.springframework.stereotype.Component;

/**
 * Integration test for Spring 2.5 component scan.
 * 
 * @author Costin Leau
 * 
 */
public class ComponentScanTest extends BaseIntegrationTest {

	static {
		// Modern Spring versions are trying to load and compare annotation by its runtime type.
		// Older versions were simply comparing names of the class.
		// That is an important change in OSGi world,
		// where some bundles may not have access to specific classes or have different versions of them.
		//
		// Those tests create OSGi bundles on the fly. Code below ensures that created bundle will import Spring package.
		// With that configuration Spring used by Gemini will be able to properly load an XML configuration.
		//
		// For Spring change see:
		// https://github.com/spring-projects/spring-framework/issues/22884
		// https://github.com/spring-projects/spring-framework/commit/7fbf3f97cdeffd7e60a7dc9b19ca59ce73cd1cea#diff-8edf0405eb9b93018d302aaa928ec58c231a58e34d92bcd7c3241b42c99be99cR99
		Class<Component> componentClass = Component.class;
	}

	protected String[] getConfigLocations() {
		return new String[] { "/org/eclipse/gemini/blueprint/iandt/jdk5/io/component-scan.xml" };
	}

	protected String[] getTestBundlesNames() {
		return new String[] { "org.eclipse.gemini.blueprint.iandt,component.scan.bundle," + getSpringDMVersion() };
	}

	public void testComponentScan() throws Exception {
		// force an import on component bean
		assertNotNull(ComponentBean.class);
		assertTrue("component scan did not pick up all classes", applicationContext.containsBean("bean"));
		assertTrue("component scan did not pick up all classes", applicationContext.containsBean("componentBean"));
	}
}
