package codes.shiftmc.minecraft.mappings;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Motd {
    private Version version;
    private Players players;
    private Description description;
    private String favicon;
    @SerializedName("enforcesSecureChat")
    private boolean enforcesSecureChat;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Version {
        private String name;
        private int protocol;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Players {
        private int max;
        private int online;
        private List<Player> sample;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Player {
        private String name;
        private String id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Description {
        private String text;
    }

    // JSON utility methods
    public static Motd fromJson(String json) {
        return new Gson().fromJson(json, Motd.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}