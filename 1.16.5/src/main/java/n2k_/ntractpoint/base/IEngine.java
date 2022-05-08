package n2k_.ntractpoint.base;
import org.bukkit.entity.Player;
public interface IEngine extends IInitiliazible {
    void start();
    void stop();
    void tick();
    void timerExecute();
    void interact();
    void sendCompass();
    Player getPlayer();
    IInteractor getInteractor();
}
