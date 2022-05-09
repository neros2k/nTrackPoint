package n2k_.ntractpoint.base;
import n2k_.ntractpoint.base.model.ConfigModel;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
public interface IInteractor extends IInitializable {
    void loadEngine(Player PLAYER);
    void unloadEngine(Player PLAYER);
    IEngine getEngine(Player PLAYER);
    JavaPlugin getPlugin();
    ConfigModel getModel();
}
