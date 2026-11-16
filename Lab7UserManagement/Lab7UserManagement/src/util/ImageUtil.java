package util;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

/** Loads a photo from disk and scales it to fit a preview box without distortion. */
public final class ImageUtil {

    private ImageUtil() {
    }

    /** @return a scaled icon, or null when the path is empty / unreadable. */
    public static ImageIcon loadScaled(String path, int maxWidth, int maxHeight) {
        if (InputValidator.isBlank(path)) {
            return null;
        }
        File file = new File(path);
        if (!file.isFile()) {
            return null;
        }
        try {
            BufferedImage original = ImageIO.read(file);
            if (original == null) {
                return null;
            }
            double scale = Math.min(
                    (double) maxWidth / original.getWidth(),
                    (double) maxHeight / original.getHeight());
            if (scale > 1.0) {
                scale = 1.0;   // never blow a small image up
            }
            int w = Math.max(1, (int) Math.round(original.getWidth() * scale));
            int h = Math.max(1, (int) Math.round(original.getHeight() * scale));
            return new ImageIcon(original.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.err.println("Could not load image: " + e.getMessage());
            return null;
        }
    }
}
