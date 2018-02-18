/*
Copyright 2006 Jerry Huxtable

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

package thirdparty.jhlabs.image;

import thirdparty.jhlabs.math.*;

import java.util.Random;

/**
 * A filter which distorts an image as if it were underwater.
 */
public class SwimFilter extends TransformFilter {

    private final NoiseInstance noise;
    private float scale = 32;
    private float stretch = 1.0f;
    private float angle = 0.0f;
    private float amount = 1.0f;
    private float turbulence = 1.0f;
    private float time = 0.0f;
    private float m00 = 1.0f;
    private float m01 = 0.0f;
    private float m10 = 0.0f;
    private float m11 = 1.0f;

    public SwimFilter(Random random) {
        this.noise = new NoiseInstance(random);
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * Specifies the scale of the distortion.
     *
     * @param scale the scale of the distortion.
     * @min-value 1
     * @max-value 300+
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Specifies the stretch factor of the distortion.
     *
     * @param stretch the stretch factor of the distortion.
     * @min-value 1
     * @max-value 50+
     */
    public void setStretch(float stretch) {
        this.stretch = stretch;
    }

    /**
     * Specifies the angle of the effect.
     *
     * @param angle the angle of the effect.
     * @angle
     */
    public void setAngle(float angle) {
        this.angle = angle;
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        m00 = cos;
        m01 = sin;
        m10 = -sin;
        m11 = cos;
    }

    /**
     * Specifies the turbulence of the texture.
     *
     * @param turbulence the turbulence of the texture.
     * @min-value 0
     * @max-value 1
     */
    public void setTurbulence(float turbulence) {
        this.turbulence = turbulence;
    }

    /**
     * Specifies the time. Use this to animate the effect.
     *
     * @param time the time.
     * @angle
     */
    public void setTime(float time) {
        this.time = time;
    }

    protected void transformInverse(int x, int y, float[] out) {
        float nx = m00 * x + m01 * y;
        float ny = m10 * x + m11 * y;
        nx /= scale;
        ny /= scale * stretch;

        if (turbulence == 1.0f) {
            out[0] = x + amount * noise.noise3(nx + 0.5f, ny, time);
            out[1] = y + amount * noise.noise3(nx, ny + 0.5f, time);
        } else {
            out[0] = x + amount * noise.turbulence3(nx + 0.5f, ny, turbulence, time);
            out[1] = y + amount * noise.turbulence3(nx, ny + 0.5f, turbulence, time);
        }
    }

    public String toString() {
        return "Distort/Swim...";
    }
}
