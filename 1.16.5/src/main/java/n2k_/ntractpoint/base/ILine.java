package n2k_.ntractpoint.base;
import org.bukkit.Location;
import org.bukkit.entity.Player;
public interface ILine {
    void update(Location POINT_LOCATION, Location LOCATION, String DISTANCE);
    void sendActionBar(Player PLAYER);
    void sendMessage(Player PLAYER);
    String get();
}
