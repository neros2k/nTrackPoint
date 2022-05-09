package n2k_.ntractpoint.utils;
import n2k_.ntractpoint.base.ILine;
import n2k_.ntractpoint.base.model.ConfigModel;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
public class Line implements ILine {
    private final ConfigModel MODEL;
    private String LINE;
    public Line(@NotNull ConfigModel MODEL) {
        this.MODEL = MODEL;
        this.LINE = MODEL.COMPASS_DEFAULT_FORMAT;
    }
    @Override
    public void update(@NotNull Location POINT_LOCATION, @NotNull Location LOCATION) {
        Vector SUBTRACT = POINT_LOCATION.toVector().subtract(LOCATION.toVector()).normalize();
        Vector DIRECTION = LOCATION.getDirection();
        double ANGLE = Math.toDegrees(Math.acos(SUBTRACT.dot(DIRECTION)));
        Arrays.stream(MODEL.COMPASS_INTERVAL_FORMAT).forEach(INTERVAL_MODEL -> {
            if(ANGLE >= INTERVAL_MODEL.INTERVAL[1] && ANGLE <= INTERVAL_MODEL.INTERVAL[2]) {
                this.LINE = INTERVAL_MODEL.FORMAT;
            }
        });
    }
    @Override
    public void sendActionBar(Player PLAYER) {

    }
    @Override
    public void sendBossBar(Player PLAYER) {

    }
    @Override
    public void sendMessage(@NotNull Player PLAYER) {
        PLAYER.sendMessage(LINE);
    }
}
