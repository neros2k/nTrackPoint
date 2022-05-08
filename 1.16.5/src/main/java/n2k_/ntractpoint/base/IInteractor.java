package n2k_.ntractpoint.base;
import n2k_.ntractpoint.base.model.ConfigModel;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Map;
public interface IInteractor extends IInitiliazible {
    void loadEngine(Player PLAYER);
    void unloadEngine(Player PLAYER);
    JavaPlugin getPlugin();
    Map<String, IEngine> getEngineMap();
    ConfigModel getModel();
}
