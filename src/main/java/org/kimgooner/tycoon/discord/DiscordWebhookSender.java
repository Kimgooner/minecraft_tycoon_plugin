package org.kimgooner.tycoon.discord;

import com.google.gson.JsonObject;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.util.EnvLoader;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

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

    public void sendMessage(String message) {
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

                JsonObject json = new JsonObject();
                json.addProperty("content", message);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                connection.getInputStream().close(); // 응답 스트림 닫기
                connection.disconnect();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "웹훅 오류!", e);
            }
        });
    }

    // 플레이어 채팅을 그대로 웹훅으로 전송하는 예시
    public void sendPlayerChat(Player player, String message) {
        sendMessage("**" + player.getName() + "**: " + message);
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        // Adventure Component → 문자열 변환 (플레인 텍스트)
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        // 디스코드 웹훅으로 메시지 전송
        sendPlayerChat(player, message);

        // (선택) 플레이어에게 피드백 메시지 보내기
        // player.sendMessage("§a당신이 보낸 채팅: " + message);
    }
}