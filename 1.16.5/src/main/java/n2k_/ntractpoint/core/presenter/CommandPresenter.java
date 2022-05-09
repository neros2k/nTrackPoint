package n2k_.ntractpoint.core.presenter;
import n2k_.ntractpoint.base.APresenter;
import n2k_.ntractpoint.base.IEngine;
import n2k_.ntractpoint.base.IInteractor;
import n2k_.ntractpoint.base.model.ConfigModel;
import n2k_.ntractpoint.nTrackPoint;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.Collections;
public class CommandPresenter extends APresenter implements CommandExecutor {
    public CommandPresenter(IInteractor INTERACTOR) {
        super(INTERACTOR);
    }
    @Override
    public void init() {
        PluginCommand COMMAND = super.getInteractor().getPlugin().getCommand("ntp");
        assert COMMAND != null;
        COMMAND.setExecutor(this);
    }
    @Override
    public boolean onCommand(@NotNull CommandSender SENDER, @NotNull Command COMMAND, @NotNull String STR, String @NotNull [] ARGS) {
        ConfigModel MODEL = this.getInteractor().getModel();
        if(ARGS.length == 0 || ARGS[0].equals("help")) {
            Arrays.stream(MODEL.MESSAGES.HELP_COMMAND).forEach(SENDER::sendMessage);
            return true;
        }
        if(ARGS[0].equals("reload")) {
            if(!SENDER.hasPermission("ntrackpoint.reload")) {
                SENDER.sendMessage(MODEL.MESSAGES.PERM_ERROR);
                return true;
            }
            ((nTrackPoint) this.getInteractor().getPlugin()).getJsonConfig().reload();
            SENDER.sendMessage(MODEL.MESSAGES.RELOAD_COMMAND);
            return true;
        }
        if(ARGS[0].equals("start")) {
            if(!SENDER.hasPermission("ntrackpoint.use")) {
                SENDER.sendMessage(MODEL.MESSAGES.PERM_ERROR);
                return true;
            }
            if(SENDER instanceof Player) {
                Player PLAYER = (Player) SENDER;
                IEngine ENGINE = this.getInteractor().getEngine(PLAYER);
                if(!ENGINE.isStarted()) {
                    ENGINE.start();
                    SENDER.sendMessage(MODEL.MESSAGES.START_COMMAND);
                } else {
                    SENDER.sendMessage(MODEL.MESSAGES.CONSOLE_SENDER_MESSAGE);
                }
            }
            return true;
        }
        if(ARGS[0].equals("stop")) {
            if(!SENDER.hasPermission("ntrackpoint.stop")) {
                SENDER.sendMessage(MODEL.MESSAGES.PERM_ERROR);
                return true;
            }
            if(SENDER instanceof Player) {
                Player PLAYER = (Player) SENDER;
                IEngine ENGINE = this.getInteractor().getEngine(PLAYER);
                if(ENGINE.isStarted()) {
                    ENGINE.stop();
                    SENDER.sendMessage(MODEL.MESSAGES.STOP_COMMAND);
                } else {
                    SENDER.sendMessage(MODEL.MESSAGES.CONSOLE_SENDER_MESSAGE);
                }
            }
            return true;
        }
        SENDER.sendMessage(MODEL.MESSAGES.UNKNOWN_COMMAND);
        return false;
    }
}
