package org.globus.crux.cxf;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.apache.cxf.ws.addressing.AddressingPropertiesImpl;
import org.apache.cxf.ws.addressing.ReferenceParametersType;
import org.apache.cxf.ws.addressing.soap.MAPCodec;
import org.globus.crux.stateful.StateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author turtlebender
 */
public class AddressingIdInterceptor<T> extends AbstractPhaseInterceptor<Message> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ResourceIdExtractor<T> idExtractor;
    private StateAdapter<T> stateAdapter;


    public AddressingIdInterceptor() {
        super(Phase.PRE_PROTOCOL);
        addAfter(MAPCodec.class.getName());
    }

    @SuppressWarnings("unchecked")
    public void handleMessage(Message message) throws Fault {
        AddressingPropertiesImpl add = ContextUtils.retrieveMAPs(message, false, false);
        if (add != null) {
            try {
                if (add.getToEndpointReference() != null) {
                    ReferenceParametersType rpt = add.getToEndpointReference().getReferenceParameters();
                    if (rpt != null) {
                        T id = idExtractor.extractId(rpt);
                        stateAdapter.setState(id);
                    }
                }
            } catch (CXFAddressingException e) {
                throw new Fault(e);
            }
        }
    }

    public ResourceIdExtractor getIdExtractor() {
        return idExtractor;
    }

    public void setIdExtractor(ResourceIdExtractor idExtractor) {
        this.idExtractor = idExtractor;
    }

    public StateAdapter<T> getStateAdapter() {
        return stateAdapter;
    }

    public void setStateAdapter(StateAdapter<T> stateAdapter) {
        this.stateAdapter = stateAdapter;
    }
}
