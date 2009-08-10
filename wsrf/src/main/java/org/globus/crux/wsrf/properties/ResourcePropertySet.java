package org.globus.crux.wsrf.properties;

import org.oasis.wsrf.properties.InvalidResourcePropertyQNameFault;
import org.oasis.wsrf.properties.ResourceUnknownFault;

import javax.xml.namespace.QName;

/**
 * Exposes ResourceProperties.  This is used by most of the internal plumbing.
 *
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public interface ResourcePropertySet extends Iterable<QName> {
    /**
     * Determines if the specified resource property is present in this Resource Property Set.
     *
     * @param qname
     * @return True if the ResourceProperty exists, else false
     */
    public boolean containsResourceProperty(QName qname);

    /**
     * Get the value of the specified ResourceProperty.
     *
     * @param qname name of the ResourceProperty to retrieve.
     * @return The value of the ResourceProperty.
     * @throws InvalidResourcePropertyQNameFault
     *                              If named ResourceProperty does not exist.
     * @throws ResourceUnknownFault If the resource can not be found.
     */
    public Object getResourceProperty(QName qname) throws InvalidResourcePropertyQNameFault, ResourceUnknownFault;

    /**
     * Get the name of the resource described by this ResourcePropertySet.
     *
     * @return Resource QName.
     */
    public QName getResourceName();
}
