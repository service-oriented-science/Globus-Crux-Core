package org.globus.crux.wsrf.properties;



import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.GetResourceProperty;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.GetResourcePropertyResponse;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFault;

import javax.xml.namespace.QName;

class RPProviderImpl implements GetResourceProperty {
        private ResourcePropertySet rpSet;

        public void setRpSet(ResourcePropertySet rpSet) {
            this.rpSet = rpSet;
        }

        public GetResourcePropertyResponse getResourceProperty(QName getResourcePropertyRequest) throws InvalidResourcePropertyQNameFault, ResourceUnknownFault {
            GetResourcePropertyResponse response = new GetResourcePropertyResponse();
            response.getAny().add(this.rpSet.getResourceProperty(getResourcePropertyRequest));
            return response;
        }
    }