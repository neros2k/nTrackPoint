package n2k_.ntractpoint.base;
import n2k_.ntractpoint.base.model.PointModel;
import org.bukkit.Location;
public interface IEngine extends IInitializable {
    void start();
    void stop();
    void tick();
    void timerExecute(Location POINT_LOCATION);
    void interact(Location POINT_LOCATION);
    void sendCompass(PointModel POINT);
}
