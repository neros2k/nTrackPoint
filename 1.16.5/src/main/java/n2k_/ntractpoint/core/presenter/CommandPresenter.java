package n2k_.ntractpoint.core.presenter;
import n2k_.ntractpoint.base.APresenter;
import n2k_.ntractpoint.base.IInteractor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandPresenter extends APresenter implements CommandExecutor {
    public CommandPresenter(IInteractor INTERACTOR) {
        super(INTERACTOR);
    }
    @Override
    public void init() {

    }
    @Override
    public boolean onCommand(@NotNull CommandSender SENDER,
                             @NotNull Command COMMAND,
                             @NotNull String STR, String[] ARGS) {
        return false;
    }
}
