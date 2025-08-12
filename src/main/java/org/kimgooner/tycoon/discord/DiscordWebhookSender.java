package org.kimgooner.tycoon.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.util.EnvLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DiscordWebhookSender implements Listener {
    // 발급받은 디스코드 웹훅 URL
    private final JavaPlugin plugin;
    private final EnvLoader env;
    private final String WEBHOOK_URL;

    public DiscordWebhookSender(JavaPlugin plugin) {
        this.plugin = plugin;
        this.env = new EnvLoader(plugin);
        this.WEBHOOK_URL = env.getWebhookUrl();
    }

    public void sendEmbedMessage(String description, int color) {
        if (WEBHOOK_URL == null || WEBHOOK_URL.isEmpty()) {
            plugin.getLogger().severe("웹훅 URL이 설정되어 있지 않습니다! .env 파일을 확인하세요.");
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URI uri = URI.create(WEBHOOK_URL);
                URL url = uri.toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                JsonObject embed = new JsonObject();
                embed.addProperty("description", description);
                embed.addProperty("color", color);  // 10진수 RGB 색상 코드

                JsonArray embedsArray = new JsonArray();
                embedsArray.add(embed);

                JsonObject json = new JsonObject();
                json.add("embeds", embedsArray);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                connection.getInputStream().close();
                connection.disconnect();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "웹훅 오류!", e);
            }
        });
    }
    public void sendStyledEmbedMessage(String title, String description, String footerText, String authorName, int color) {
        if (WEBHOOK_URL == null || WEBHOOK_URL.isEmpty()) {
            plugin.getLogger().severe("웹훅 URL이 설정되어 있지 않습니다! .env 파일을 확인하세요.");
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URI uri = URI.create(WEBHOOK_URL);
                URL url = uri.toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                JsonObject embed = new JsonObject();

                embed.addProperty("title", title);
                embed.addProperty("description", description);
                embed.addProperty("color", color);

                // 타임스탬프: ISO 8601 포맷 (현재 시간)
                embed.addProperty("timestamp", java.time.OffsetDateTime.now().toString());

                // 작성자(Author)
                JsonObject author = new JsonObject();
                author.addProperty("name", authorName);
                embed.add("author", author);

                // 푸터 (Footer)
                JsonObject footer = new JsonObject();
                footer.addProperty("text", footerText);
                embed.add("footer", footer);

                // 여러 필드 (Fields)
                JsonArray fields = new JsonArray();

                JsonObject field1 = new JsonObject();
                field1.addProperty("name", "필드 1");
                field1.addProperty("value", "값 1");
                field1.addProperty("inline", true);
                fields.add(field1);

                JsonObject field2 = new JsonObject();
                field2.addProperty("name", "필드 2");
                field2.addProperty("value", "값 2");
                field2.addProperty("inline", true);
                fields.add(field2);

                embed.add("fields", fields);

                JsonArray embedsArray = new JsonArray();
                embedsArray.add(embed);

                JsonObject json = new JsonObject();
                json.add("embeds", embedsArray);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                connection.getInputStream().close();
                connection.disconnect();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "웹훅 꾸며진 임베드 메시지 전송 오류!", e);
            }
        });
    }

    public void sendPlayerEmbed(Player player, String title, String description, String thumbnailUrl, Integer color) {
        if (WEBHOOK_URL == null || WEBHOOK_URL.isEmpty()) {
            plugin.getLogger().severe("웹훅 URL이 설정되어 있지 않습니다! .env 파일을 확인하세요.");
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String uuid = player.getUniqueId().toString().replace("-", "");
                String iconUrl = "https://minotar.net/avatar/" + uuid;

                URI uri = URI.create(WEBHOOK_URL);
                URL url = uri.toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Java-DiscordWebhook");

                JsonObject author = new JsonObject();
                author.addProperty("name", player.getName());
                author.addProperty("icon_url", iconUrl + "?v=" + System.currentTimeMillis());

                JsonObject embed = new JsonObject();
                embed.add("author", author);
                embed.addProperty("title", title);
                embed.addProperty("description", description);
                embed.addProperty("color", color);  // 파란색 예시
                embed.addProperty("timestamp", java.time.OffsetDateTime.now().toString());

                // 썸네일 URL이 있으면 thumbnail 객체 추가
                if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                    JsonObject thumbnail = new JsonObject();
                    thumbnail.addProperty("url", thumbnailUrl);
                    embed.add("thumbnail", thumbnail);
                }

                JsonArray embedsArray = new JsonArray();
                embedsArray.add(embed);

                JsonObject json = new JsonObject();
                json.add("embeds", embedsArray);

                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                connection.setRequestProperty("Content-Length", String.valueOf(input.length));

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode != 204) {  // 204는 성공 코드
                    try (InputStream errorStream = connection.getErrorStream()) {
                        if (errorStream != null) {
                            String errorMsg = new BufferedReader(new InputStreamReader(errorStream))
                                    .lines().collect(Collectors.joining("\n"));
                            plugin.getLogger().warning("Discord Webhook error response: " + errorMsg);
                        }
                    }
                }

                connection.getInputStream().close();
                connection.disconnect();

            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "웹훅 임베드 메시지 전송 오류!", e);
            }
        });
    }



//    // 플레이어 채팅을 그대로 웹훅으로 전송하는 예시
//    public void sendPlayerChat(Player player, String message) {
//        sendMessage("**" + player.getName() + "**: " + message);
//    }
//
//    @EventHandler
//    public void onPlayerChat(AsyncChatEvent event) {
//        Player player = event.getPlayer();
//        // Adventure Component → 문자열 변환 (플레인 텍스트)
//        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
//
//        // 디스코드 웹훅으로 메시지 전송
//        sendPlayerChat(player, message);
//
//        // (선택) 플레이어에게 피드백 메시지 보내기
//        // player.sendMessage("§a당신이 보낸 채팅: " + message);
//    }
}