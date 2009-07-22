package org.globus.crux.stateful;

import org.testng.annotations.Test;
import org.globus.crux.stateful.internal.StatefulServiceDesc;

/**
 * @author turtlebender
 */
@Test
public class StatefulServiceDescTest {

    public void testCreateDesc() throws StatefulServiceException{
        StatefulServiceDesc desc = new StatefulServiceDesc(new CounterServiceImpl());
        CreateCounterResponse response = (CreateCounterResponse) desc.invoke(new CreateCounterRequest());
        System.out.println("response.id = " + response.id);
        AddRequest request = new AddRequest();
        request.setIncrement(5);
        AddResponse ar = (AddResponse) desc.invoke(request, response.id);
        System.out.println("ar.getValue() = " + ar.getValue());
        System.out.println("desc.getKeyQname() = " + desc.getKeyQname());
    }
}
