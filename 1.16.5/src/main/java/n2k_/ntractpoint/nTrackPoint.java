package n2k_.ntractpoint;
import n2k_.ntractpoint.base.IInteractor;
import n2k_.ntractpoint.base.model.ConfigModel;
import n2k_.ntractpoint.core.Interactor;
import n2k_.ntractpoint.utils.JsonConfig;
import org.bukkit.plugin.java.JavaPlugin;
public final class nTrackPoint extends JavaPlugin {
    private final IInteractor INTERACTOR;
    private JsonConfig<ConfigModel> JSON_CONFIG;
    public nTrackPoint() {
        this.INTERACTOR = new Interactor(this);
    }
    @Override
    public void onEnable() {
        this.JSON_CONFIG = new JsonConfig<>(this, ConfigModel.class, "config.json");
        this.JSON_CONFIG.reload();
        this.INTERACTOR.init();
    }
    public JsonConfig<ConfigModel> getJsonConfig() {
        assert this.JSON_CONFIG != null;
        return this.JSON_CONFIG;
    }
}