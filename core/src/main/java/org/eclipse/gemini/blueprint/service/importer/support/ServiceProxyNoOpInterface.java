package org.eclipse.gemini.blueprint.service.importer.support;

import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.framework.ProxyFactory;

/**
 * It is a no-op interface simply to pass the issues described in https://github.com/spring-projects/spring-framework/issues/33985
 *
 * In short {@link ProxyFactory} will throw an exception if requesting proxy doesn't define `target`
 * and all defined interfaces are defined by registered through {@link IntroductionAdvisor}.
 *
 * By adding this interface directly we can be sure it will pass validation.
 */
public interface ServiceProxyNoOpInterface {
}
