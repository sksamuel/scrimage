package com.sksamuel.scrimage;

public interface Position {

    class _TopCenter implements Top, CenterX {}
    class _TopLeft implements Top, Left {}
    class _TopRight implements Top, Right {}
    class _CenterLeft implements CenterY, Left {}
    class _Center implements CenterY, CenterX {}
    class _CenterRight implements CenterY, Right {}
    class _BottomLeft implements Bottom, Left {}
    class _BottomCenter implements Bottom, CenterX {}
    class _BottomRight implements Bottom, Right {}

    Position TopCenter = new _TopCenter();
    Position TopLeft = new _TopLeft();
    Position TopRight = new _TopRight();
    Position CenterLeft = new _CenterLeft();
    Position Center = new _Center();
    Position CenterRight = new _CenterRight();
    Position BottomCenter = new _BottomCenter();
    Position BottomRight = new _BottomRight();
    Position BottomLeft = new _BottomLeft();

    /**
     * Returns the x coordinate for where a target should be placed inside the source.
     */
    int calculateX(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight);

    /**
     * Returns the y coordinate for where an image should be placed inside the canvas.
     */
    int calculateY(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight);

    default Dimension calculateXY(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
        return new Dimension(
                calculateX(sourceWidth, sourceHeight, targetWidth, targetHeight),
                calculateY(sourceWidth, sourceHeight, targetWidth, targetHeight)
        );
    }
}

interface Left extends Position {
    default int calculateX(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
        return 0;
    }
}

interface CenterX extends Position {
    default int calculateX(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
        return (int) ((sourceWidth - targetWidth) / 2.0);
    }
}

interface Right extends Position {
    default int calculateX(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
        return sourceWidth - targetWidth;
    }
}

interface Top extends Position {
    default int calculateY(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
        return 0;
    }
}

interface CenterY extends Position {
    default int calculateY(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
        return (int) ((sourceHeight - targetHeight) / 2.0);
    }
}

interface Bottom extends Position {
    default int calculateY(int sourceWidth,
                           int sourceHeight,
                           int targetWidth,
                           int targetHeight) {
        return sourceHeight - targetHeight;
    }
}

