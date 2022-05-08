package n2k_.ntractpoint;
import n2k_.ntractpoint.base.IInteractor;
import n2k_.ntractpoint.core.Interactor;
import org.bukkit.plugin.java.JavaPlugin;
public final class nTrackPoint extends JavaPlugin {
    private final IInteractor INTERACTOR;
    public nTrackPoint() {
        this.INTERACTOR = new Interactor(this);
    }
    @Override
    public void onEnable() {
        this.INTERACTOR.init();
    }
}