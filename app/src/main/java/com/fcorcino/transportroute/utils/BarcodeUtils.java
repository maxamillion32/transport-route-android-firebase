package com.fcorcino.transportroute.utils;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

public class BarcodeUtils {

    /**
     * This method encodes an string into a barcode.
     *
     * @param contents    to be encoded.
     * @param format      to used in the process.
     * @param imageWidth  the image width to be used.
     * @param imageHeight the image height to be used.
     * @return a bitmap representation of the barcode.
     * @throws WriterException if can not write.
     */
    private static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int imageWidth, int imageHeight) throws WriterException {
        String contentsToEncode = contents;

        if (contentsToEncode == null) {
            return null;
        }

        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);

        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;

        try {
            result = writer.encode(contentsToEncode, format, imageWidth, imageHeight, hints);
        } catch (IllegalArgumentException iae) {
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            int offset = y * width;

            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * This method guesses the appropriate encoding.
     *
     * @param contents to be evaluated.
     * @return the appropriate encoding.
     */
    private static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {

            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }

        return null;
    }

    /**
     * This method generate a barcode based on the string passed.
     *
     * @param barcodeData the string to be converted to barcode.
     * @return a barcode representation of the string passed.
     */
    public static Bitmap generateBarCode(String barcodeData) {
        try {
            return encodeAsBitmap(barcodeData, BarcodeFormat.CODE_128, 600, 300);
        } catch (WriterException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
