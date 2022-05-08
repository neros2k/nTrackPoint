package n2k_.ntractpoint.core.presenter;
import n2k_.ntractpoint.base.APresenter;
import n2k_.ntractpoint.base.IInteractor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
public class EventPresenter extends APresenter implements Listener {
    public EventPresenter(IInteractor INTERACTOR) {
        super(INTERACTOR);
    }
    @Override
    public void init() {
        JavaPlugin PLUGIN = this.getInteractor().getPlugin();
        PLUGIN.getServer().getPluginManager().registerEvents(this, PLUGIN);
    }
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent EVENT) {
        this.getInteractor().loadEngine(EVENT.getPlayer());
    }
    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent EVENT) {
        this.getInteractor().unloadEngine(EVENT.getPlayer());
    }
}
