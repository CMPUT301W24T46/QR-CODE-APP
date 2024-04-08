package com.example.eventapp.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * ProfileImageGenerator is a utility class to generate profile images based on a given hash string.
 * It creates a symmetrical, avatar-like image using a simple algorithm that decides the presence and color of squares
 * based on the hash. The generated image is circular, with a pattern that mirrors across the vertical axis.
 */

public class ProfileImageGenerator {


    private static final int IMAGE_SIZE = 256; // Size of the final image
    private static final int COLUMNS = 5; // Number of columns for half of the avatar
    private static final int ROWS = 10; // Total number of rows

    /**
     * Generates a profile image from a given hash string.
     * The image is a circular, symmetrical pattern where the presence and color of each square
     * are determined by the hash string.
     *
     * @param hash The hash string used to generate the image.
     * @return A Bitmap object representing the generated profile image.
     */

    public static Bitmap generateImageFromHash(String hash) {
        Bitmap bitmap = Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        int startColor = getColorFromHashSegment(hash.substring(0, 6));
        int endColor = getColorFromHashSegment(hash.substring(hash.length() - 6));

        // Calculate the size of each pixel
        int squareSize = IMAGE_SIZE / ROWS;
        int radius = IMAGE_SIZE / 2; // Radius for the circular shape

        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                // Check if the center of the square falls within the circle
                if (isWithinCircle(x, y, squareSize, radius)) {
                    // Use part of the hash to decide if the square should be drawn
                    if (Character.digit(hash.charAt((x * ROWS + y) % hash.length()), 16) % 2 == 0) {
                        Paint paint = new Paint();

                        // Determine the color for the square based on its hash position
                        float ratio = (float) (x + y * COLUMNS) / (COLUMNS + ROWS * COLUMNS);
                        paint.setColor(interpolateColor(startColor, endColor, ratio));

                        // Draw the square on the left side
                        canvas.drawRect(x * squareSize, y * squareSize, (x + 1) * squareSize, (y + 1) * squareSize, paint);

                        // Draw the mirrored square on the right side
                        canvas.drawRect(IMAGE_SIZE - (x + 1) * squareSize, y * squareSize, IMAGE_SIZE - x * squareSize, (y + 1) * squareSize, paint);
                    }
                }
            }
        }

        return bitmap;
    }

    /**
     * Determines if a given square position should be included in the circular image.
     *
     * @param x The x-coordinate of the square (in square units, not pixels).
     * @param y The y-coordinate of the square (in square units, not pixels).
     * @param squareSize The size of each square in pixels.
     * @param radius The radius of the circle in pixels.
     * @return True if the square's center lies within the circle, false otherwise.
     */

    private static boolean isWithinCircle(int x, int y, int squareSize, int radius) {
        // Calculate the center of the square
        float centerX = x * squareSize + squareSize / 2f;
        float centerY = y * squareSize + squareSize / 2f;
        // Calculate the center of the image
        float imageCenterX = IMAGE_SIZE / 2f;
        float imageCenterY = IMAGE_SIZE / 2f;
        // Check if the distance from the center of the square to the center of the image is less than the radius
        return Math.sqrt(Math.pow(centerX - imageCenterX, 2) + Math.pow(centerY - imageCenterY, 2)) <= radius;
    }

    /**
     * Converts a segment of the hash string into a color.
     *
     * @param hashSegment A substring of the hash, used to generate the color.
     * @return An integer representing the color generated from the hash segment.
     */

    private static int getColorFromHashSegment(String hashSegment) {
        long value = Long.parseLong(hashSegment, 16);
        return 0xFF000000 | (int) (value & 0xFFFFFF); // Ensure full opacity
    }

    /**
     * Interpolates between two colors based on a given ratio.
     *
     * @param startColor The starting color.
     * @param endColor The ending color.
     * @param ratio A float between 0 and 1 indicating the interpolation ratio.
     * @return An integer representing the interpolated color.
     */

    private static int interpolateColor(int startColor, int endColor, float ratio) {
        int alpha = (int) ((Color.alpha(endColor) - Color.alpha(startColor)) * ratio + Color.alpha(startColor));
        int red = (int) ((Color.red(endColor) - Color.red(startColor)) * ratio + Color.red(startColor));
        int green = (int) ((Color.green(endColor) - Color.green(startColor)) * ratio + Color.green(startColor));
        int blue = (int) ((Color.blue(endColor) - Color.blue(startColor)) * ratio + Color.blue(startColor));
        return Color.argb(alpha, red, green, blue);
    }



}
