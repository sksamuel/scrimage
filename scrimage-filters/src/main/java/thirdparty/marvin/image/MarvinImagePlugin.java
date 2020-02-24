package thirdparty.marvin.image;

public interface MarvinImagePlugin extends MarvinPlugin {

   void process(
      MarvinImage imgIn,
      MarvinImage imgOut,
      MarvinAttributes attrOut,
      MarvinImageMask mask,
      boolean previewMode
   );

   void process(
      MarvinImage imgIn,
      MarvinImage imgOut,
      MarvinImageMask mask
   );

   void process(
      MarvinImage imgIn,
      MarvinImage imgOut,
      MarvinAttributes attrOut
   );

   void process(
      MarvinImage imgIn,
      MarvinImage imgOut
   );
}
