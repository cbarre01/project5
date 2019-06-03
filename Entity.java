import java.util.List;
import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

abstract class Entity {

   private Point position;
   private List<PImage> images;
   private int imageIndex;



   public String toString()
   {
      return (this.getClass().getName() + "Pos: " + this.getPosition().toString());
   }
   protected Point getPosition() {
      return position;
   }

   protected void setPosition(Point position) {
      this.position = position;
   }


   protected List<PImage> getImages() {
      return images;
   }


   protected int getImageIndex() {
      return imageIndex;
   }

   protected PImage getCurrentImage()
   {

      return this.getImages().get(this.getImageIndex());

   }


   protected void setImages(List<PImage> images) {
      this.images = images;
   }

   protected void setImageIndex(int imageIndex) {
      this.imageIndex = imageIndex;
   }
}


