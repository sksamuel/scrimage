package com.sksamuel.scrimage.nio;

import at.dhyan.open_imaging.GifDecoder;
import com.sksamuel.scrimage.ImmutableImage;

import java.io.IOException;

public class OpenGifReader implements Reader {

    @Override
    public ImmutableImage fromBytes(byte[] bytes, int type) throws IOException {
        final GifDecoder.GifImage gif = GifDecoder.read(bytes);
        return ImmutableImage.wrapAwt(gif.getFrame(0));
    }
}
