package com.images;

import org.globus.crux.service.EPRFactory;
import org.globus.crux.service.CreateState;
import org.globus.crux.service.Payload;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author turtlebender
 */
public class PhotoSharingService {
    //This factory generates eprs for gallery services
    @Resource
    EPRFactory eprFactory;
    Map<String, Gallery> galleryMap = new ConcurrentHashMap<String, Gallery>();

    @CreateState
    @Payload(namespace = "http://images.com", localpart = "CreateGallery")
    public CreateGalleryResponse createGallery(CreateGallery request) {
        CreateGalleryResponse response = new CreateGalleryResponse(eprFactory.createEPRWithId(request.getName()));
        Gallery gallery = new Gallery().withGalleryName(request.getName());
        galleryMap.put(request.getName(), gallery);
        return response;
    }

    public FindGalleryResponse findGallery(FindGallery request) {
        Gallery gallery = galleryMap.get(request.getGalleryName());
        if (gallery == null) {
            //handle null resource
        }
        return new FindGalleryResponse().
                withEndpointReference(eprFactory.createEPRWithId(request.getGalleryName()));
    }

    public Map<String, Gallery> getGalleryMap() {
        return galleryMap;
    }

    public void setGalleryMap(Map<String, Gallery> galleryMap) {
        this.galleryMap = galleryMap;
    }
}
