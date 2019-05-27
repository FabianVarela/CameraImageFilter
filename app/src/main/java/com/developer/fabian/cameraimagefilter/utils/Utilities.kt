package com.developer.fabian.cameraimagefilter.utils

import android.graphics.Bitmap

class Utilities {

    fun matToBit(matrix: Array<Array<IntArray>>, width: Int, height: Int): Bitmap {
        val pix = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x

                pix[index] = -0x1000000 or (matrix[x][y][1] shl 16) or (matrix[x][y][2] shl 8) or matrix[x][y][3]
            }
        }

        val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
        bm.setPixels(pix, 0, width, 0, 0, width, height)

        return bm
    }

    fun grayScale(matrix: Array<Array<IntArray>>, width: Int, height: Int): Array<Array<IntArray>> {
        val resultMatrix = Array(width) { Array(height) { IntArray(4) } }

        for (y in 0 until height) {
            for (x in 0 until width) {
                val r = matrix[x][y][1]
                val g = matrix[x][y][2]
                val b = matrix[x][y][3]
                val result = (0.299 * r + 0.587 * g + 0.114 * b).toInt()

                resultMatrix[x][y][1] = result
                resultMatrix[x][y][2] = result
                resultMatrix[x][y][3] = result
            }
        }

        return resultMatrix
    }

    fun negativeScale(matrix: Array<Array<IntArray>>, width: Int, height: Int): Array<Array<IntArray>> {
        val resultMatrix = Array(width) { Array(height) { IntArray(4) } }

        for (y in 0 until height) {
            for (x in 0 until width) {

                val r = matrix[x][y][1]
                val g = matrix[x][y][2]
                val b = matrix[x][y][3]

                resultMatrix[x][y][1] = 255 - r
                resultMatrix[x][y][2] = 255 - g
                resultMatrix[x][y][3] = 255 - b
            }
        }

        return resultMatrix
    }

    companion object {

        fun filterTest(bmp: Bitmap): Array<Array<IntArray>> {
            val imageWidth = bmp.width
            val imageHeight = bmp.height
            val pixel = IntArray(imageWidth * imageHeight)

            bmp.getPixels(pixel, 0, imageWidth, 0, 0, imageWidth, imageHeight)
            val matrix = Array(imageWidth) { Array(imageHeight) { IntArray(4) } }

            for (y in 0 until imageHeight) {
                for (x in 0 until imageWidth) {
                    val index = y * imageWidth + x

                    matrix[x][y][0] = pixel[index] shr 24 and 0xff
                    matrix[x][y][1] = pixel[index] shr 16 and 0xff
                    matrix[x][y][2] = pixel[index] shr 8 and 0xff
                    matrix[x][y][3] = pixel[index] and 0xff
                }
            }

            return matrix
        }
    }
}
