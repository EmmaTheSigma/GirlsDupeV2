package me.GirlsDupeV2.girlsDupeV2.hooks;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import me.GirlsDupeV2.girlsDupeV2.GirlsDupeV2;

public class DiscordWebHook {

    private final GirlsDupeV2 plugin;
    private final String webHookUrl;
    private final Gson gson = new Gson();

    public DiscordWebHook(GirlsDupeV2 plugin, String webHookUrl) {
        this.plugin = plugin;
        this.webHookUrl = webHookUrl;
    }

    /**
     * Sends a simple text message to the Discord WebHook.
     *
     * @param content The message content to send.
     */
    public void sendMessage(String content) {
        send(new Payload(content));
    }

    /**
     * Sends an embed message to the Discord WebHook.
     *
     * @param embed The embed payload to send.
     */
    public void sendEmbed(Embed embed) {
        send(new Payload(embed));
    }

    /**
     * Sends a payload to the Discord WebHook.
     *
     * @param payload The payload to send.
     */
    private void send(Payload payload) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webHookUrl))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(gson.toJson(payload)))
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode != 200 && statusCode != 204) {
                plugin.getLogger().warning("Failed to send message to Discord WebHook. Response Code: " + statusCode);
            }

        } catch (IOException | InterruptedException e) {
            plugin.getLogger().severe("Error while sending message to Discord WebHook: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Represents the payload structure for a WebHook message.
     */
    public static class Payload {
        @SerializedName("content")
        private final String content;

        @SerializedName("embeds")
        private final Embed[] embeds;

        public Payload(String content) {
            this.content = content;
            this.embeds = null;
        }

        public Payload(Embed embed) {
            this.content = null;
            this.embeds = new Embed[]{embed};
        }
    }

    /**
     * Represents an embed structure for Discord messages.
     */
    public static class Embed {
        @SerializedName("title")
        private final String title;

        @SerializedName("description")
        private final String description;

        @SerializedName("color")
        private final int color;

        public Embed(String title, String description, int color) {
            this.title = title;
            this.description = description;
            this.color = color;
        }
    }
}