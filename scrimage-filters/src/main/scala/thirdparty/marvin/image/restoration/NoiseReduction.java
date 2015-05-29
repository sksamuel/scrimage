/**
 Marvin Project <2007-2009>

 Initial version by:

 Danilo Rosetto Munoz
 Fabio Andrijauskas
 Gabriel Ambrosio Archanjo

 site: http://marvinproject.sourceforge.net

 GPL
 Copyright (C) <2007>

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

/**
 * Restores noise from the degraded noisy image. 
 * Ref paper - Rudin Osher Fatemi:'Nonlinear TotalVariation based noise removal techniques'.
 *
 *
 * @version 1.0 02/28/2008
 * @author Smita Nair
 */

package thirdparty.marvin.image.restoration;

import thirdparty.marvin.image.MarvinAbstractImagePlugin;
import thirdparty.marvin.image.MarvinAttributes;
import thirdparty.marvin.image.MarvinImage;
import thirdparty.marvin.image.MarvinImageMask;

public class NoiseReduction extends MarvinAbstractImagePlugin {

    double mat1[][], mat2[][], mat3[][], mat4[][], mata[][];
    double img_x[][], img_y[][], img_xx[][], img_yy[][], img_xy[][];
    double matr[][], matg[][], matb[][];
    double img_org[][];
    int width;
    int height;

    public void process(MarvinImage a_imageIn,
                        MarvinImage a_imageOut,
                        MarvinAttributes a_attributesOut,
                        MarvinImageMask a_mask,
                        boolean a_previewMode) {
        width = a_imageIn.getWidth();
        height = a_imageIn.getHeight();

        mat1 = new double[width][height];
        mat2 = new double[width][height];
        mat4 = new double[width][height];
        mata = new double[width][height];

        img_x = new double[width][height];
        img_y = new double[width][height];
        img_xx = new double[width][height];
        img_yy = new double[width][height];
        img_xy = new double[width][height];

        matr = new double[width][height];
        matg = new double[width][height];
        matb = new double[width][height];


        int iter = 20; //no of iterations

        // Put the color values in double array
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                matr[x][y] = a_imageIn.getIntComponent0(x, y);
                matg[x][y] = a_imageIn.getIntComponent1(x, y);
                matb[x][y] = a_imageIn.getIntComponent2(x, y);
            }
        }

        // Call denoise function
        matr = denoise(matr, iter);
        matg = denoise(matg, iter);
        matb = denoise(matb, iter);


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                a_imageOut.setIntColor(x, y, (int) truncate(matr[x][y]), (int) truncate(matg[x][y]), (int) truncate(matb[x][y]));
            }
        }
    }


    //Function : denoise - Restores noisy images, input- double array containing color data  
    public double[][] denoise(double mat[][], int iter) {
        img_org = new double[width][height];
        double img_res[][] = new double[width][height];
        double l_currentNum;
        double l_currentDen;
        int val = 1;
        double lam = 0;
        double dt = 0.4;


        img_org = mat;

        //Perform iterations

        for (int it = 0; it < iter; it++) {
            //compute derivatives
            img_x = diff_x(mat);
            img_y = diff_y(mat);
            img_xx = diff_xx(mat);
            img_yy = diff_yy(mat);
            img_xy = diff_xy(mat);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    double a = img_xx[i][j] * (val + Math.pow(img_y[i][j], 2));
                    double b = (2 * img_x[i][j] * img_y[i][j] * img_xy[i][j]);
                    double c = (img_yy[i][j] * (val + Math.pow(img_x[i][j], 2)));
                    l_currentNum = a - b + c;
                    l_currentDen = Math.pow((val + Math.pow(img_x[i][j], 2) + Math.pow(img_y[i][j], 2)), (1.5));
                    img_res[i][j] = (l_currentNum / l_currentDen) + lam * (img_org[i][j] - mat[i][j]);
                    mat[i][j] = mat[i][j] + dt * img_res[i][j];//evolve image by dt.
                }
            }

        }// end of iterations.
        return mat;
    }


    // Function : diff_x - To compute differntiation along x axis.
    public double[][] diff_x(double matx[][]) {
        mat3 = new double[width][height];
        double mat1, mat2;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (j == 0) {
                    mat1 = matx[i][j];
                    mat2 = matx[i][j + 1];
                } else if (j == height - 1) {
                    mat1 = matx[i][j - 1];
                    mat2 = matx[i][j];
                } else {
                    mat1 = matx[i][j - 1];
                    mat2 = matx[i][j + 1];
                }
                mat3[i][j] = (mat2 - mat1) / 2;
            }
        }
        return mat3;
    }


    // Function : diff_y -To compute differntiation along y axis.
    public double[][] diff_y(double maty[][]) {
        mat3 = new double[width][height];
        double mat1, mat2;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i == 0) {
                    mat1 = maty[i][j];
                    mat2 = maty[i + 1][j];
                } else if (i == width - 1) {
                    mat1 = maty[i - 1][j];
                    mat2 = maty[i][j];
                } else {
                    mat1 = maty[i - 1][j];
                    mat2 = maty[i + 1][j];
                }
                mat3[i][j] = (mat2 - mat1) / 2;
            }
        }
        // maty= subMatrix(mat2,mat1,width,height);

        return mat3;
    }


    //Function : diff_xx -To compute second order differentiation along x axis.
    public double[][] diff_xx(double matxx[][]) {
        mat3 = new double[width][height];
        double mat1, mat2;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (j == 0) {
                    mat1 = matxx[i][j];
                    mat2 = matxx[i][j + 1];

                } else if (j == height - 1) {
                    mat1 = matxx[i][j - 1];
                    mat2 = matxx[i][j];
                } else {
                    mat1 = matxx[i][j - 1];
                    mat2 = matxx[i][j + 1];
                }

                mat3[i][j] = (mat1 + mat2 - 2 * matxx[i][j]);
            }

        }
        return mat3;
    }


    //Function : diff_yy - To compute second order differentiation along y axis.
    public double[][] diff_yy(double matyy[][]) {
        mat3 = new double[width][height];
        double mat1, mat2;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i == 0) {
                    mat1 = matyy[i][j];
                    mat2 = matyy[i + 1][j];
                } else if (i == width - 1) {
                    mat1 = matyy[i - 1][j];
                    mat2 = matyy[i][j];
                } else {
                    mat1 = matyy[i - 1][j];
                    mat2 = matyy[i + 1][j];
                }
                mat3[i][j] = (mat1 + mat2 - 2 * matyy[i][j]);
            }
        }
        return mat3;

    }


    //Function: diff_xy  -To compute differentiation along xy direction
    public double[][] diff_xy(double matxy[][]) {

        mat3 = new double[width][height];
        double Dp;
        double Dm;

        for (int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height - 1; j++) {
                mat1[i][j] = matxy[i + 1][j + 1];
                mat2[i + 1][j + 1] = matxy[i][j];
                mat3[i + 1][j] = matxy[i][j + 1];
                mat4[i][j + 1] = matxy[i + 1][j];
            }

        }


        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (j == height - 1 && i < width - 1)
                    mat1[i][j] = mat1[i][j - 1];

                else if (i == width - 1)
                    mat1[i][j] = mat1[i - 1][j];


                if (i == 0 && j > 0)
                    mat2[i][j] = mat2[1][j];
                else if (j == 0)
                    mat2[i][0] = mat2[i][1];

                if (i == 0 && j < height - 1)
                    mat3[i][j] = mat3[1][j];
                else if (j == height - 1)
                    mat3[i][j] = mat3[i][j - 1];

                if (j == 0 && i < width - 1)
                    mat4[i][j] = mat4[i][1];
                else if (i == width - 1)
                    mat4[i][j] = mat4[i - 1][j];
            }
            mat2[0][0] = mat2[0][1];
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Dp = mat1[i][j] + mat2[i][j];
                Dm = mat3[i][j] + mat4[i][j];
                mata[i][j] = ((Dp - Dm) / 4);
            }
        }

        return mata;

    }


    public double truncate(double a) {
        if (a < 0) return 0;
        else if (a > 255) return 255;
        else return a;
    }


}
