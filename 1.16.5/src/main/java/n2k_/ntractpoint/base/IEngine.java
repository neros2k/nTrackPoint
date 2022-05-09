package n2k_.ntractpoint.base;
import n2k_.ntractpoint.base.model.PointModel;
import org.bukkit.entity.Player;
public interface IEngine extends IInitializable {
    void start();
    void stop();
    void tick();
    void timerExecute();
    void interact();
    void sendCompass(PointModel POINT);
    Player getPlayer();
    IInteractor getInteractor();
}
