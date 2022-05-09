package n2k_.ntractpoint.core;
import n2k_.ntractpoint.base.APresenter;
import n2k_.ntractpoint.base.IEngine;
import n2k_.ntractpoint.base.IInteractor;
import n2k_.ntractpoint.base.model.ConfigModel;
import n2k_.ntractpoint.base.model.PointModel;
import n2k_.ntractpoint.core.presenter.EventPresenter;
import n2k_.ntractpoint.nTrackPoint;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Interactor implements IInteractor {
    private final List<APresenter> PRESENTER_LIST;
    private final Map<String, IEngine> ENGINE_MAP;
    private final JavaPlugin PLUGIN;
    public Interactor(JavaPlugin PLUGIN) {
        this.PRESENTER_LIST = new ArrayList<>();
        this.ENGINE_MAP = new HashMap<>();
        this.PLUGIN = PLUGIN;
        this.PRESENTER_LIST.addAll(List.of(
                new EventPresenter(this)
        ));
    }
    @Override
    public void init() {
        this.PRESENTER_LIST.forEach(APresenter::init);
    }
    @Override
    public void loadEngine(@NotNull Player PLAYER) {
        if(!this.ENGINE_MAP.containsKey(PLAYER.getName())) {
            IEngine ENGINE = new Engine(PLAYER, this);
            ENGINE.init();
            ENGINE.start();
            this.ENGINE_MAP.put(PLAYER.getName(), ENGINE);
        }
    }
    @Override
    public void unloadEngine(@NotNull Player PLAYER) {
        if(this.ENGINE_MAP.containsKey(PLAYER.getName())) {
            IEngine ENGINE = this.ENGINE_MAP.get(PLAYER.getName());
            ENGINE.stop();
            this.ENGINE_MAP.remove(PLAYER.getName());
        }
    }
    @Override
    public JavaPlugin getPlugin() {
        return this.PLUGIN;
    }
    @Override
    public Map<String, IEngine> getEngineMap() {
        return this.ENGINE_MAP;
    }
    @Override
    public ConfigModel getModel() {
        return ((nTrackPoint) this.getPlugin()).getJsonConfig().getJson();
    }
}
