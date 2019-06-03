import processing.core.PImage;

import java.util.List;

public class Gas extends Entity {



    public Gas(String id, Point position,
                    List<PImage> images) {
        this.setPosition(position);
        this.setImages(images);
        this.setImageIndex(0);
    }



    public static Obstacle createGas(String id, Point position,
                                          List<PImage> images)
    {
        return new Obstacle(id, position, images
        );
    }





}
