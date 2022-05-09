package n2k_.ntractpoint.core;
import n2k_.ntractpoint.base.IEngine;
import n2k_.ntractpoint.base.IInteractor;
import n2k_.ntractpoint.base.ILine;
import n2k_.ntractpoint.base.model.ConfigModel;
import n2k_.ntractpoint.base.model.PointModel;
import n2k_.ntractpoint.utils.Line;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
public class Engine implements IEngine {
    private final List<Location> PASSING_LIST;
    private final Player PLAYER;
    private final IInteractor INTERACTOR;
    private PointModel ENTERED_POINT;
    private BukkitTask TICK_TASK;
    private BukkitTask TIMER_TASK;
    private Boolean TIMER_BLOCK;
    public Engine(Player PLAYER, IInteractor INTERACTOR) {
        this.PASSING_LIST = new ArrayList<>();
        this.PLAYER = PLAYER;
        this.INTERACTOR = INTERACTOR;
    }
    @Override
    public void init() {

    }
    @Override
    public void start() {
        this.TICK_TASK = Bukkit.getScheduler()
            .runTaskTimerAsynchronously(this.INTERACTOR.getPlugin(), this::tick, 0L, 1L);
    }
    @Override
    public void stop() {
        this.TICK_TASK.cancel();
    }
    @Override
    public void tick() {
        AtomicReference<Double> LAST_LEAST_DISTANCE = new AtomicReference<>(null);
        AtomicReference<PointModel> LEAST_POINT = new AtomicReference<>(null);
        ConfigModel MODEL = this.INTERACTOR.getModel();
        Arrays.stream(MODEL.DEFAULT_POINTS).forEach(POINT -> {
            Location POINT_LOCATION = new Location(
                    this.PLAYER.getWorld(), POINT.X, POINT.Y, POINT.Z
            );
            double DISTANCE = POINT_LOCATION.distance(this.PLAYER.getLocation());
            if(!this.PASSING_LIST.contains(POINT_LOCATION)) {
                if(DISTANCE < POINT.RADIUS) {
                    this.ENTERED_POINT = POINT;
                    this.timerExecute(POINT_LOCATION);
                } else this.cancelTimer(POINT);
                if(LAST_LEAST_DISTANCE.get() == null || LAST_LEAST_DISTANCE.get() > DISTANCE) {
                    LAST_LEAST_DISTANCE.set(DISTANCE);
                    LEAST_POINT.set(POINT);
                }
            } else this.cancelTimer(POINT);
        });
        this.sendCompass(LEAST_POINT.get());
    }
    @Override
    public void timerExecute(Location POINT_LOCATION) {
        if(this.TIMER_BLOCK != null && !this.TIMER_BLOCK) {
            this.TIMER_BLOCK = true;
            this.TIMER_TASK = Bukkit.getScheduler().runTaskLater(
                    this.INTERACTOR.getPlugin(),
                    () -> this.interact(POINT_LOCATION),
                    this.INTERACTOR.getModel().PERIOD
            );
        }
    }
    @Override
    public void cancelTimer(PointModel POINT_MODEL) {
        if(this.ENTERED_POINT == POINT_MODEL) {
            this.TIMER_BLOCK = false;
            this.ENTERED_POINT = null;
            if(this.TIMER_TASK != null) {
                this.TIMER_TASK.cancel();
            }
        }
    }
    @Override
    public void interact(Location POINT_LOCATION) {
        ConfigModel MODEL = this.INTERACTOR.getModel();
        if(MODEL.ENABLE_PASSING) {
            Arrays.stream(MODEL.INTERACT_ACTIONS).forEach(ACTION -> {
                switch(ACTION.TYPE) {
                    case "COMMAND" -> {
                        Server SERVER = this.INTERACTOR.getPlugin().getServer();
                        SERVER.dispatchCommand(SERVER.getConsoleSender(), ACTION.CONTENT);
                    }
                    case "MESSAGE" -> this.PLAYER.sendMessage(ACTION.CONTENT);
                }
            });
            this.PASSING_LIST.add(POINT_LOCATION);
            boolean CLEAR;
            if(MODEL.CLEAR_PASSING_VALUE.equals("ALL")) {
                CLEAR = this.PASSING_LIST.size() >= MODEL.DEFAULT_POINTS.length;
            } else if(MODEL.CLEAR_PASSING_VALUE.equals("NONE")) {
                CLEAR = false;
            } else {
                CLEAR = this.PASSING_LIST.size() >= Integer.parseInt(MODEL.CLEAR_PASSING_VALUE);
            }
            if(CLEAR) {
                this.PASSING_LIST.clear();
                this.PASSING_LIST.add(POINT_LOCATION);
            }
        }
    }
    @Override
    public void sendCompass(@Nullable PointModel POINT) {
        ConfigModel MODEL = this.INTERACTOR.getModel();
        ILine LINE;
        if(POINT != null) {
            Location POINT_LOCATION = new Location(
                    this.PLAYER.getWorld(), POINT.X, POINT.Y, POINT.Z
            );
            Location LOCATION = this.PLAYER.getLocation();
            double DISTANCE = POINT_LOCATION.distance(LOCATION);
            String FORMAT_DISTANCE = String.format(MODEL.DISTANCE_FORMAT, DISTANCE);
            LINE = new Line(MODEL, this.INTERACTOR.getPlugin(), FORMAT_DISTANCE);
            if(DISTANCE > POINT.RADIUS) {
                LINE.update(POINT_LOCATION, LOCATION, FORMAT_DISTANCE);
            }
        } else {
            LINE = new Line(this.INTERACTOR.getModel(), this.INTERACTOR.getPlugin(), "0");
        }
        Arrays.stream(MODEL.COMPASS_MESSAGE_TYPES).forEach(TYPE -> {
            switch(TYPE) {
                case "ACTION_BAR" -> LINE.sendActionBar(PLAYER);
                case "BOSS_BAR" -> LINE.sendBossBar(PLAYER);
                case "MESSAGE" -> LINE.sendMessage(PLAYER);
            }
        });
    }
}
