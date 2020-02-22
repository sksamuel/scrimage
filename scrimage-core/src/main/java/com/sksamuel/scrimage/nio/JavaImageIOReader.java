/*
   Copyright 2013 Stephen K Samuel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImmutableImage;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * An implementation of Reader that delegates to the AWT ImageIO system.
 */
public class JavaImageIOReader implements Reader {

    @Override
    public ImmutableImage fromBytes(byte[] bytes, int type) throws IOException {
        return ImmutableImage.wrapAwt(ImageIO.read(new ByteArrayInputStream(bytes)), type);
    }
}


