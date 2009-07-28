package com.images;

import org.globus.crux.service.StatefulService;
import org.globus.crux.service.StateKey;
import org.globus.crux.service.StateKeyParam;
import org.globus.crux.service.StatefulMethod;
import org.globus.crux.service.Payload;
import org.globus.crux.service.PayloadParam;
import org.globus.crux.wsrf.properties.GetResourceProperty;

import java.util.Map;

/**
 * @author turtlebender
 */
@StatefulService(@StateKey(namespace = "http://images.com", localpart = "ImageKey"))
public class ImageService {
    private Map<String, Image> imageMap;

    @StatefulMethod
    @Payload(namespace = "http://images.com", localpart = "RotateImage")
    public RotateImageResponse rotateImage(@StateKeyParam String key, @PayloadParam RotateImage request){
        Image image = imageMap.get(key);
        //do the actual rotation.
        return new RotateImageResponse();
    }

    @GetResourceProperty(namespace = "http://images.com", localpart = "Image")
    public Image getImage(@StateKeyParam String imageId){
        return imageMap.get(imageId);
    }

    public void setImageMap(Map<String, Image> imageMap) {
        this.imageMap = imageMap;
    }
}
