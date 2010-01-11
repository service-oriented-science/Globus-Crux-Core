package org.globus.crux;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

import org.mockito.Mock;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

/**
 * @author Doreen Seider
 */
@Test(groups = {"core"})
public class RPBeanConstructorTest {

	public RPBeanConstructor classUnderTest;
	
	@Mock Element element;
	@Mock BeanDefinitionBuilder builder;
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		initMocks(this);
		classUnderTest = new RPBeanConstructor();
	}
	
	@Test
	public void testGetBeanClassName() {
		assertEquals(classUnderTest.getBeanClassName(element), "org.globus.crux.wsrf.properties.AnnotationResourcePropertySet");
	}
	
	@Test
	public void testDoParse() {
		classUnderTest.doParse(element, builder);
	}

}
