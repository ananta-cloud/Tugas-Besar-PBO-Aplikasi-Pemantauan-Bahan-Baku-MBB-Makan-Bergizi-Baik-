package com.codeway.daoTemplate.utils;

import java.io.BufferedReader;
import java.io.FileInputStream; // Tambahan import
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class TemplateConfiguration {

    private static Map<String, String> map;

    public static String getString(String key) throws Exception {
        if (map == null) loadProperties();
        return map.get(key);
    }

    public static boolean getBoolean(String key) {
        try {
            String val = getString(key);
            return val != null && val.equalsIgnoreCase("true");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void loadProperties() throws Exception {
        map = new HashMap<>();
        InputStream is = null;

        // 1. Coba load dari Classpath (Standar Production/JAR)
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream("template-dao.properties");

        // 2. Coba load dari Classpath root (Fallback)
        if (is == null) {
            is = TemplateConfiguration.class.getResourceAsStream("/template-dao.properties");
        }

        // 3. Coba load langsung dari File System (Standar Development/IDE)
        // Ini akan mencari file di folder proyek Anda secara langsung
        if (is == null) {
            try {
                is = new FileInputStream("src/main/resources/template-dao.properties");
                System.out.println("[INFO] TemplateConfiguration: Loaded from src/main/resources (File System)");
            } catch (Exception e) {
                // Ignore, lanjut ke pengecekan null di bawah
            }
        }

        if (is == null) {
            map = null; // Reset map agar error tetap muncul jika benar-benar tidak ketemu
            throw new Exception("CRITICAL ERROR: template-dao.properties tidak ditemukan di Classpath maupun src/main/resources.");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty() || !line.contains("=")) continue;

            String[] parts = line.split("=", 2);
            String key = parts[0].trim();
            String value = (parts.length > 1) ? parts[1].trim() : "";

            map.put(key, value);
        }

        // Debugging: Print loaded keys (Opsional)
        // System.out.println("Loaded config keys: " + map.keySet());
    }
}