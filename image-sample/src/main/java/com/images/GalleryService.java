package com.images;

import org.globus.crux.service.StateKeyParam;
import org.globus.crux.service.PayloadParam;
import org.globus.crux.service.EPRFactory;
import org.globus.crux.service.StatefulMethod;
import org.globus.crux.service.Payload;
import org.globus.crux.service.DestroyState;
import org.globus.crux.wsrf.properties.GetResourceProperty;

import javax.annotation.Resource;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.persistence.EntityManagerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author turtlebender
 */
public class GalleryService {

    private ImageService imageService;
    private Map<Long, Gallery> galleryMap = new ConcurrentHashMap<Long, Gallery>();
    private Map<String, Long> nameMap = new ConcurrentHashMap<String, Long>();
    //this factory generates eprs for galleries
    @Resource
    private EPRFactory eprFactory;
    private AtomicLong idGen = new AtomicLong();

    @StatefulMethod
    @Payload(namespace = "http://images.com", localpart = "FindImage")
    public FindImageResponse findImage(@StateKeyParam long galleryId, @PayloadParam FindImage request) {
        return new FindImageResponse().
                withEndpointReference(imageService.findImageResourceEndpoint(galleryId, request.getImageName()));
    }

    @GetResourceProperty(namespace = "http://images.com", localpart = "Gallery")
    public Gallery getGallery(@StateKeyParam long galleryId) {
        return galleryMap.get(galleryId);
    }


    @DestroyState
    public void destroyGallery(@StateKeyParam long galleryId) {
        Gallery gallery = galleryMap.get(galleryId);
        for(Gallery.ImageRef ref: gallery.getImageRef()){
            imageService.destroyImage(new ImageKey(galleryId, ref.getId()));
        }
        this.galleryMap.remove(galleryId);
    }

    protected W3CEndpointReference createGallery(String galleryName) {
        long galleryId = idGen.getAndIncrement();
        Gallery gallery = new Gallery().withGalleryName(galleryName);
        galleryMap.put(galleryId, gallery);
        nameMap.put(galleryName, galleryId);
        return eprFactory.createEPRWithId(gallery.getGalleryName());
    }

    protected W3CEndpointReference findGallery(String galleryName) {
        if (nameMap.get(galleryName) != null) {
            return eprFactory.createEPRWithId(nameMap.get(galleryName));
        }
        return null;
    }

    public ImageService getImageService() {
        return imageService;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }
}
