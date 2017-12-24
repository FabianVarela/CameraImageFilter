package com.developer.fabian.cameraimagefilter.utils;

import android.graphics.Bitmap;

public class Utilities {

    public Bitmap matToBit(int matrix[][][], int width, int height) {
        int[] pix = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;

                pix[index] = 0xff000000 | (matrix[x][y][1] << 16) | (matrix[x][y][2] << 8) | matrix[x][y][3];
            }
        }

        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        bm.setPixels(pix, 0, width, 0, 0, width, height);

        return bm;
    }

    public static int[][][] filterTest(Bitmap bmp) {
        int imageWidth = bmp.getWidth();
        int imageHeight = bmp.getHeight();
        int[] pixel = new int[imageWidth * imageHeight];

        bmp.getPixels(pixel, 0, imageWidth, 0, 0, imageWidth, imageHeight);
        int matrix[][][] = new int[imageWidth][imageHeight][4];

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                int index = y * imageWidth + x;

                matrix[x][y][0] = (pixel[index] >> 24) & 0xff;
                matrix[x][y][1] = (pixel[index] >> 16) & 0xff;
                matrix[x][y][2] = (pixel[index] >> 8) & 0xff;
                matrix[x][y][3] = pixel[index] & 0xff;
            }
        }

        return matrix;
    }

    public int[][][] grayScale(int matrix[][][], int width, int height) {
        int resultMatrix[][][] = new int[width][height][4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = matrix[x][y][1];
                int g = matrix[x][y][2];
                int b = matrix[x][y][3];
                int R = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                resultMatrix[x][y][1] = R;
                resultMatrix[x][y][2] = R;
                resultMatrix[x][y][3] = R;
            }
        }

        return resultMatrix;
    }

    public int[][][] negativeScale(int matrix[][][], int width, int height) {
        int resultMatrix[][][] = new int[width][height][4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int r = matrix[x][y][1];
                int g = matrix[x][y][2];
                int b = matrix[x][y][3];

                resultMatrix[x][y][1] = 255 - r;
                resultMatrix[x][y][2] = 255 - g;
                resultMatrix[x][y][3] = 255 - b;
            }
        }

        return resultMatrix;
    }
}
