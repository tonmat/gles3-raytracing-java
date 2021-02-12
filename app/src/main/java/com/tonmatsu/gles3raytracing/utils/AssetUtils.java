package com.tonmatsu.gles3raytracing.utils;

import android.content.*;
import android.content.res.*;
import android.util.*;

import java.io.*;

public final class AssetUtils {
    private static AssetManager assetManager;

    private AssetUtils() {
    }

    public static void initialize(Context context) {
        assetManager = context.getAssets();
    }

    public static InputStream getStream(String name) {
        try {
            return assetManager.open(name);
        } catch (Exception e) {
            Log.e("AssetUtils", "could not get asset as stream: " + name, e);
            throw new RuntimeException(e);
        }
    }

    public static BufferedReader getReader(String name) {
        return new BufferedReader(new InputStreamReader(getStream(name)));
    }

    public static String getString(String name) {
        try (final BufferedReader reader = getReader(name)) {
            final StringBuilder sb = new StringBuilder();
            while (true) {
                final String line = reader.readLine();
                if (line == null)
                    break;
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            Log.e("AssetUtils", "could not get asset as string: " + name, e);
            throw new RuntimeException(e);
        }
    }
}
