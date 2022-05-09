package n2k_.ntractpoint.utils;
import n2k_.ntractpoint.base.ILine;
import n2k_.ntractpoint.base.model.ConfigModel;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
public class Line implements ILine {
    private final ConfigModel MODEL;
    private String LINE;
    public Line(@NotNull ConfigModel MODEL, String DISTANCE) {
        this.MODEL = MODEL;
        this.LINE = this.replace(MODEL.COMPASS_DEFAULT_FORMAT, DISTANCE);
    }
    @Override
    public void update(@NotNull Location POINT_LOCATION, @NotNull Location LOCATION, String DISTANCE) {
        Vector SUBTRACT = POINT_LOCATION.toVector().subtract(LOCATION.toVector()).normalize();
        Vector DIRECTION = LOCATION.getDirection();
        double DOT = (SUBTRACT.getX() * DIRECTION.getX()) + (SUBTRACT.getZ() * DIRECTION.getZ());
        double DET = (SUBTRACT.getX() * DIRECTION.getZ()) - (SUBTRACT.getZ() * DIRECTION.getX());
        double ANGLE = Math.toDegrees(Math.atan2(DET, DOT));
        Arrays.stream(MODEL.COMPASS_INTERVAL_FORMAT).forEach(INTERVAL_MODEL -> {
            if(ANGLE >= INTERVAL_MODEL.INTERVAL[0] && ANGLE <= INTERVAL_MODEL.INTERVAL[1]) {
                this.LINE = this.replace(INTERVAL_MODEL.FORMAT, DISTANCE);
            }
        });
    }
    @Override
    public void sendActionBar(@NotNull Player PLAYER) {
        PLAYER.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(LINE));
    }
    @Override
    public void sendBossBar(Player PLAYER) {

    }
    @Override
    public void sendMessage(@NotNull Player PLAYER) {
        PLAYER.sendMessage(LINE);
    }
    @NotNull
    private String replace(@NotNull String STR, String DISTANCE) {
        return STR.replace("{DISTANCE}", DISTANCE);
    }
}
