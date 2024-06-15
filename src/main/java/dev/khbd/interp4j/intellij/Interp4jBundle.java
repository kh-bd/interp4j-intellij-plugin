package dev.khbd.interp4j.intellij;

import com.intellij.AbstractBundle;

import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * Message bundle for interp4j plugin.
 */
public class Interp4jBundle {

    private static final String PATH_TO_BUNDLE = "dev.khbd.interp4j.intellij.messages.Interp4jBundle";

    private static SoftReference<ResourceBundle> BUNDLE;

    private Interp4jBundle() {
    }

    /**
     * Get message.
     *
     * @param key    message key
     * @param params message parameters
     * @return composed message
     */
    public static String getMessage(String key, Object... params) {
        return AbstractBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle() {
        if (BUNDLE == null || BUNDLE.get() == null) {
            ResourceBundle bundle = ResourceBundle.getBundle(PATH_TO_BUNDLE);
            BUNDLE = new SoftReference<>(bundle);
            return bundle;
        }
        return BUNDLE.get();
    }
}
