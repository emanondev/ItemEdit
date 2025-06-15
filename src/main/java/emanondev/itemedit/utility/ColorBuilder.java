package emanondev.itemedit.utility;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;

/**
 * A utility class for building and manipulating RGBA colors.
 * Supports conversions to/from AWT, Bukkit, and Bungee color types.
 */
public class ColorBuilder implements Cloneable {
    private int red;
    private int green;
    private int blue;
    private int alpha;

    // Private constructor to enforce factory usage
    private ColorBuilder(int red, int green, int blue, int alpha) {
        this.red = clamp(red);
        this.green = clamp(green);
        this.blue = clamp(blue);
        this.alpha = clamp(alpha);
    }

    // ======= Factory Methods =======

    /**
     * Create a color from RGBA components (0–255).
     */
    public static ColorBuilder fromRGBA(int red, int green, int blue, int alpha) {
        return new ColorBuilder(red, green, blue, alpha);
    }

    /**
     * Create a color from RGB components (0–255).
     */
    public static ColorBuilder fromRGB(int red, int green, int blue) {
        return new ColorBuilder(red, green, blue, 255);
    }

    /**
     * Create a black color (0, 0, 0, 255).
     */
    public static ColorBuilder fromRGB() {
        return new ColorBuilder(0, 0, 0, 255);
    }

    /**
     * Create from a java.awt.Color.
     */
    public static ColorBuilder from(Color color) {
        return new ColorBuilder(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    /**
     * Create from a Bukkit Color.
     */
    public static ColorBuilder from(org.bukkit.Color color) {
        return new ColorBuilder(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    /**
     * Create from a Bungee ChatColor (must be RGB-based).
     */
    public static ColorBuilder from(ChatColor color) {
        int rgb = color.getColor().getRGB();
        return fromRGB(rgb).setAlpha(color.getColor().getAlpha());
    }

    /**
     * Create from a Bukkit ChatColor (must be RGB-based).
     */
    public static ColorBuilder from(org.bukkit.ChatColor color) {
        int rgb = color.asBungee().getColor().getRGB();
        return fromRGB(rgb);
    }

    /**
     * Create from a hex string (e.g. "#FFAABB" or "FFAABB").
     */
    public static ColorBuilder fromRGB(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() != 6) {
            throw new IllegalArgumentException("Hex color must be 6 characters: " + hex);
        }
        int rgb = Integer.parseInt(hex, 16);
        return fromRGB(rgb);
    }

    /**
     * Create from an integer RGB value (0xRRGGBB).
     */
    public static ColorBuilder fromRGB(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return new ColorBuilder(red, green, blue, 255);
    }

    // ======= Getters =======

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getAlpha() {
        return alpha;
    }

    // ======= Setters with chaining =======

    public ColorBuilder setRed(int red) {
        this.red = clamp(red);
        return this;
    }

    public ColorBuilder setGreen(int green) {
        this.green = clamp(green);
        return this;
    }

    public ColorBuilder setBlue(int blue) {
        this.blue = clamp(blue);
        return this;
    }

    public ColorBuilder setAlpha(int alpha) {
        this.alpha = clamp(alpha);
        return this;
    }

    // ======= Increment Methods (negative for decrement) =======

    public ColorBuilder incRed(int amount) {
        this.red = clamp(this.red + amount);
        return this;
    }

    public ColorBuilder incGreen(int amount) {
        this.green = clamp(this.green + amount);
        return this;
    }

    public ColorBuilder incBlue(int amount) {
        this.blue = clamp(this.blue + amount);
        return this;
    }

    public ColorBuilder inc(int red, int green, int blue) {
        this.red = clamp(this.red + red);
        this.green = clamp(this.green + green);
        this.blue = clamp(this.blue + blue);
        return this;
    }

    public ColorBuilder inc(ColorBuilder color) {
        this.red = clamp(this.red + color.getRed());
        this.green = clamp(this.green + color.getGreen());
        this.blue = clamp(this.blue + color.getBlue());
        return this;
    }

    public ColorBuilder dec(ColorBuilder color) {
        this.red = clamp(this.red - color.getRed());
        this.green = clamp(this.green - color.getGreen());
        this.blue = clamp(this.blue - color.getBlue());
        return this;
    }

    // ======= Output Conversions =======

    /**
     * Returns the hex representation of this color (e.g. "#FFAABB").
     */
    public String toHex() {
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    /**
     * Returns the 24-bit integer representation (e.g. 0xFFAABB).
     */
    public int toInt() {
        return (red << 16) | (green << 8) | blue;
    }

    /**
     * Converts to java.awt.Color.
     */
    public Color toAwt() {
        return new Color(red, green, blue);
    }

    /**
     * Converts to org.bukkit.Color.
     */
    public org.bukkit.Color toBukkit() {
        return org.bukkit.Color.fromRGB(red, green, blue);
    }

    /**
     * Converts to net.md_5.bungee.api.ChatColor (RGB).
     */
    public ChatColor toBungee() {
        return ChatColor.of(new Color(red, green, blue));
    }

    // ======= Utility Methods =======

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ColorBuilder other = (ColorBuilder) obj;
        return red == other.red
                && green == other.green
                && blue == other.blue
                && alpha == other.alpha;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(red);
        result = 31 * result + Integer.hashCode(green);
        result = 31 * result + Integer.hashCode(blue);
        result = 31 * result + Integer.hashCode(alpha);
        return result;
    }

    /**
     * Clones the color builder.
     */
    @Override
    public ColorBuilder clone() {
        return new ColorBuilder(this.red, this.green, this.blue, this.alpha);
    }

    // Clamp helper to restrict values to 0–255
    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
