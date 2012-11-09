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

package org.eclipse.gemini.blueprint.extender.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.eclipse.gemini.blueprint.extender.internal.activator.ContextLoaderListener;
import org.eclipse.gemini.blueprint.extender.internal.support.ExtenderConfiguration;
import org.eclipse.gemini.blueprint.extender.internal.support.TestTaskExecutor;
import org.eclipse.gemini.blueprint.extender.support.internal.ConfigUtils;
import org.eclipse.gemini.blueprint.mock.EntryLookupControllingMockBundle;
import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.eclipse.gemini.blueprint.mock.MockBundleContext;
import org.eclipse.gemini.blueprint.mock.MockServiceRegistration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Adrian Colyer
 * 
 */
public abstract class ContextLoaderListenerTest extends TestCase {
	private ContextLoaderListener listener;
    // TODO: mock & train once there are any applications of this base class.
    private ExtenderConfiguration configuration;

    protected void setUp() throws Exception {
		super.setUp();
		this.listener = new ContextLoaderListener(this.configuration);
	}

	public void testStart() throws Exception {
		MockControl bundleContextControl = MockControl.createControl(BundleContext.class);
		BundleContext context = (BundleContext) bundleContextControl.getMock();
		// platform determination

		// extracting bundle id from bundle
		bundleContextControl.expectAndReturn(context.getBundle(), new MockBundle());

		// look for existing resolved bundles
		bundleContextControl.expectAndReturn(context.getBundles(), new Bundle[0], 2);

		// register namespace and entity resolving service
		// context.registerService((String[]) null, null, null);
		// bundleContextControl.setMatcher(MockControl.ALWAYS_MATCHER);
		// bundleContextControl.setReturnValue(null);

		// register context service
		context.registerService((String[]) null, null, null);
		bundleContextControl.setMatcher(MockControl.ALWAYS_MATCHER);
		bundleContextControl.setReturnValue(null, MockControl.ONE_OR_MORE);

		// create task executor
		EntryLookupControllingMockBundle aBundle = new EntryLookupControllingMockBundle(null);
		aBundle.setEntryReturnOnNextCallToGetEntry(null);
		bundleContextControl.expectAndReturn(context.getBundle(), aBundle, MockControl.ONE_OR_MORE);

		// listen for bundle events
		context.addBundleListener(null);
		bundleContextControl.setMatcher(MockControl.ALWAYS_MATCHER);
		bundleContextControl.setVoidCallable(2);

		bundleContextControl.expectAndReturn(context.registerService(new String[0], null, new Properties()),
			new MockServiceRegistration(), MockControl.ONE_OR_MORE);
		bundleContextControl.setMatcher(MockControl.ALWAYS_MATCHER);

		bundleContextControl.replay();

		this.listener.start(context);
		bundleContextControl.verify();
	}

	public void tstTaskExecutor() throws Exception {
		Dictionary headers = new Hashtable();
		headers.put(Constants.BUNDLE_NAME, "Extender mock bundle");
		final EntryLookupControllingMockBundle aBundle = new EntryLookupControllingMockBundle(headers);
		aBundle.setEntryReturnOnNextCallToGetEntry(new ClassPathResource("META-INF/spring/moved-extender.xml").getURL());

		MockBundleContext ctx = new MockBundleContext() {

			public Bundle getBundle() {
				return aBundle;
			}
		};

		this.listener.start(ctx);

		Dictionary hdrs = new Hashtable();
		hdrs.put(ConfigUtils.SPRING_CONTEXT_HEADER, "bla bla");
		MockBundle anotherBundle = new MockBundle(hdrs);
		anotherBundle.setBundleId(1);

		BundleEvent event = new BundleEvent(BundleEvent.STARTED, anotherBundle);

		BundleListener listener = (BundleListener) ctx.getBundleListeners().iterator().next();

		TestTaskExecutor.called = false;

		listener.bundleChanged(event);
		assertTrue("task executor should have been called if configured properly", TestTaskExecutor.called);
	}

}
