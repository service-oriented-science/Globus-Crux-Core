package com.images;

import org.globus.crux.service.StateKeyParam;
import org.globus.crux.service.PayloadParam;
import org.globus.crux.service.EPRFactory;
import org.globus.crux.service.StatefulMethod;
import org.globus.crux.service.Payload;
import org.globus.crux.wsrf.properties.GetResourceProperty;

import javax.annotation.Resource;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.util.Map;

/**
 * @author turtlebender
 */
public class GalleryService {

    private Map<String, Gallery> galleryMap;
    private ImageService imageService;
    
    //this factory generates eprs for galleries
    @Resource
    private EPRFactory eprFactory;

    public W3CEndpointReference createGallery(String galleryId){
        Gallery gallery = new Gallery().withGalleryName(galleryId);
        galleryMap.put(galleryId, gallery);
        return eprFactory.createEPRWithId(gallery.getGalleryName());
    }

    public W3CEndpointReference findGallery(String galleryID){
        if(galleryMap.get(galleryID) != null){
            return eprFactory.createEPRWithId(galleryMap.get(galleryID).getGalleryName());
        }
        return null;
    }

    @StatefulMethod
    @Payload(namespace = "http://images.com", localpart = "FindImage")
    public FindImageResponse findImage(@StateKeyParam String galleryId, @PayloadParam FindImage request) {
        return new FindImageResponse().withEndpointReference(imageService.findImageResource(request.getImageName()));
    }

    @GetResourceProperty(namespace = "http://images.com", localpart = "Gallery")
    public Gallery getGallery(@StateKeyParam String galleryId) {
        return galleryMap.get(galleryId);
    }

    public ImageService getImageService() {
        return imageService;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    public Map<String, Gallery> getGalleryMap() {
        return galleryMap;
    }

    public void setGalleryMap(Map<String, Gallery> galleryMap) {
        this.galleryMap = galleryMap;
    }
}
