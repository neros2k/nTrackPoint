package n2k_.ntractpoint.core;
import n2k_.ntractpoint.base.APresenter;
import n2k_.ntractpoint.base.IEngine;
import n2k_.ntractpoint.base.IInteractor;
import n2k_.ntractpoint.base.model.ConfigModel;
import n2k_.ntractpoint.core.presenter.CommandPresenter;
import n2k_.ntractpoint.core.presenter.EventPresenter;
import n2k_.ntractpoint.nTrackPoint;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public final class Interactor implements IInteractor {
    private final List<APresenter> PRESENTER_LIST;
    private final Map<String, IEngine> ENGINE_MAP;
    private final JavaPlugin PLUGIN;
    public Interactor(JavaPlugin PLUGIN) {
        this.PRESENTER_LIST = new ArrayList<>();
        this.ENGINE_MAP = new HashMap<>();
        this.PLUGIN = PLUGIN;
        this.PRESENTER_LIST.add(new EventPresenter(this));
        this.PRESENTER_LIST.add(new CommandPresenter(this));
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
            if(!this.getModel().ONLY_COMMANDS) ENGINE.start();
            this.ENGINE_MAP.put(PLAYER.getName(), ENGINE);
        }
    }
    @Override
    public void unloadEngine(@NotNull Player PLAYER) {
        if(this.ENGINE_MAP.containsKey(PLAYER.getName())) {
            IEngine ENGINE = this.ENGINE_MAP.get(PLAYER.getName());
            if(!ENGINE.isStarted()) ENGINE.stop();
            this.ENGINE_MAP.remove(PLAYER.getName());
        }
    }
    @Override
    public IEngine getEngine(@NotNull Player PLAYER) {
        if(this.ENGINE_MAP.containsKey(PLAYER.getName())) {
            return this.ENGINE_MAP.get(PLAYER.getName());
        }
        return null;
    }
    @Override
    public JavaPlugin getPlugin() {
        return this.PLUGIN;
    }
    @Override
    public ConfigModel getModel() {
        return ((nTrackPoint) this.getPlugin()).getJsonConfig().getJson();
    }
}
