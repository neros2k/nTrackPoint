package n2k_.ntractpoint.base;
import org.bukkit.Location;
import org.bukkit.entity.Player;
public interface IEngine extends IInitializable {
    void start();
    void stop();
    void tick();
    void timerExecute();
    void interact();
    void sendCompass(Location LOCATION);
    Player getPlayer();
    IInteractor getInteractor();
}
