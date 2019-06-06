import processing.core.PImage;

import java.util.List;
public class PowerUp extends Entity
{
    public PowerUp(Point position, List<PImage> images)
    {
        this.setPosition(position);
        this.setImages(images);
        this.setImageIndex(0);
    }



}