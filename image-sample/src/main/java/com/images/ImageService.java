package com.images;

import org.globus.crux.service.StatefulService;
import org.globus.crux.service.StateKey;
import org.globus.crux.service.StateKeyParam;
import org.globus.crux.service.StatefulMethod;
import org.globus.crux.service.Payload;
import org.globus.crux.service.PayloadParam;
import org.globus.crux.service.EPRFactory;
import org.globus.crux.service.DestroyState;
import org.globus.crux.wsrf.properties.GetResourceProperty;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author turtlebender
 */
@StatefulService(@StateKey(namespace = "http://images.com", localpart = "ImageKey"))
public class ImageService {
    private Map<ImageKey, Image> imageMap = new ConcurrentHashMap<ImageKey, Image>();
    private Map<ImageGalleryName, ImageKey> nameMap = new ConcurrentHashMap<ImageGalleryName, ImageKey>();

    @Resource
    private EPRFactory eprFac;
    private AtomicLong idGen = new AtomicLong(0);


    @StatefulMethod
    @Payload(namespace = "http://images.com", localpart = "RotateImage")
    public RotateImageResponse rotateImage(@StateKeyParam ImageKey key, @PayloadParam RotateImage request) {
        Image image = imageMap.get(key);
        int rot = request.getRotation();
        System.out.println("Image: " + image.getName() + " rotated: " + rot);
        //do the actual rotation.
        return new RotateImageResponse();
    }

    @GetResourceProperty(namespace = "http://images.com", localpart = "Image")
    public Image getImage(@StateKeyParam ImageKey imageId) {
        return imageMap.get(imageId);
    }

    @DestroyState
    public void destroyImage(@StateKeyParam ImageKey key) {
        imageMap.remove(key);
    }

    protected W3CEndpointReference createImageResource(long galleryId, String url, String name) {
        long id = idGen.getAndIncrement();
        ImageKey key = new ImageKey(galleryId, id);
        Image image = new Image().
                withFileUri(url).
                withName(name).
                withImageKey(key);
        imageMap.put(key, image);
        nameMap.put(new ImageGalleryName(name, galleryId), key);
        return eprFac.createEPRWithId(key);
    }

    protected W3CEndpointReference findImageResourceEndpoint(long galleryId, String imageName) {
        return eprFac.createEPRWithId(nameMap.get(new ImageGalleryName(imageName, galleryId)));
    }

    class ImageGalleryName {
        String name;
        long galleryId;

        ImageGalleryName(String name, long galleryId) {
            this.name = name;
            this.galleryId = galleryId;
        }
    }
}
