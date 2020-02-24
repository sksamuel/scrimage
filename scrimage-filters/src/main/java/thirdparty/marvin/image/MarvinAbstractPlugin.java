package thirdparty.marvin.image;

public abstract class MarvinAbstractPlugin implements MarvinPlugin {

   private MarvinAttributes marvinAttributes;

   private boolean valid;

   protected MarvinAbstractPlugin() {
      marvinAttributes = new MarvinAttributes(this);
   }

   public void validate() {
      valid = true;
   }

   public void invalidate() {
      valid = false;
   }

   public boolean isValid() {
      return valid;
   }

   public MarvinAttributes getAttributes() {
      return marvinAttributes;
   }

   public void setAttribute(String label, Object value) {
      marvinAttributes.set(label, value);
   }

   public void setAttributes(Object... params) {
      marvinAttributes.set(params);
   }

   public Object getAttribute(String label) {
      return marvinAttributes.get(label);
   }
}
