package com.images;

import org.globus.crux.service.StateKeyParam;
import org.globus.crux.service.PayloadParam;
import org.globus.crux.service.EPRFactory;
import org.globus.crux.service.StatefulMethod;
import org.globus.crux.service.Payload;
import org.globus.crux.wsrf.properties.GetResourceProperty;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author turtlebender
 */
public class GalleryService {

    private Map<String, Gallery> galleryMap;

    //this factory generates eprs for images
    @Resource
    private EPRFactory eprFactory;

    @StatefulMethod
    @Payload(namespace = "http://images.com", localpart = "FindImage")
    public FindImageResponse findImage(@StateKeyParam String galleryId, @PayloadParam FindImage request) {
        Gallery gallery = galleryMap.get(galleryId);
        boolean found = false;
        //Obviously this is not a good way to do this, but it works for this purpose
        for (String image : gallery.getImages()) {
            if (image.equals(request.getImageName())) {
                found = true;
                break;
            }
        }
        FindImageResponse response = new FindImageResponse();
        if (found) {
            response.setEndpointReference(eprFactory.createEPRWithId(request.getImageName()));
        }
        return response;
    }

    @GetResourceProperty(namespace = "http://images.com", localpart = "Gallery")
    public Gallery getGallery(@StateKeyParam String galleryId) {
        return galleryMap.get(galleryId);
    }

    public Map<String, Gallery> getGalleryMap() {
        return galleryMap;
    }

    public void setGalleryMap(Map<String, Gallery> galleryMap) {
        this.galleryMap = galleryMap;
    }
}
