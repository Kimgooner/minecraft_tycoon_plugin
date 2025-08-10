package org.kimgooner.tycoon.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

public class EnvLoader {
    private static final String ENV_FILE_NAME = ".env";
    private Properties props = new Properties();

    public EnvLoader(JavaPlugin plugin) {
        File envFile = new File(System.getProperty("user.dir"), ENV_FILE_NAME);
        if (!envFile.exists()) {
            plugin.getLogger().warning(".env 파일을 찾을 수 없습니다: " + envFile.getAbsolutePath());
            return;
        }

        try (FileInputStream fis = new FileInputStream(envFile)) {
            props.load(fis);
            plugin.getLogger().info("WEBHOOK_URL: " + getWebhookUrl());
        } catch (IOException e) {
            plugin.getLogger().severe("EnvLoader 오류!");
            plugin.getLogger().log(Level.SEVERE, "로더 오류!", e);
        }
    }

    public String getWebhookUrl() {
        return props.getProperty("WEBHOOK_URL");
    }
    public String getDBUrl() {
        return props.getProperty("DB_URL");
    }
    public String getUSERNAME() {
        return props.getProperty("USER_NAME");
    }
    public String getUSERPASSWORD() {
        return props.getProperty("USER_PASSWORD");
    }
}