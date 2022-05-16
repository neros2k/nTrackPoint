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
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
public final class Engine implements IEngine {
    private final List<Location> PASSING_LIST;
    private final Player PLAYER;
    private final IInteractor INTERACTOR;
    private BossBar BOSSBAR;
    private PointModel ENTERED_POINT;
    private BukkitTask TICK_TASK;
    private BukkitTask TIMER_TASK;
    private Boolean TIMER_BLOCK;
    private Boolean SEND_BOSSBAR;
    private Boolean STARTED;
    public Engine(Player PLAYER, IInteractor INTERACTOR) {
        this.PASSING_LIST = new ArrayList<>();
        this.PLAYER = PLAYER;
        this.INTERACTOR = INTERACTOR;
        this.SEND_BOSSBAR = false;
        this.STARTED = false;
    }
    @Override
    public void init() {
        ConfigModel MODEL = this.INTERACTOR.getModel();
        this.BOSSBAR = PLAYER.getServer().createBossBar("...",
                BarColor.valueOf(MODEL.BOSS_BAR_COLOR_DEFAULT),
                BarStyle.valueOf(MODEL.BOSS_BAR_STYLE));
    }
    @Override
    public void start() {
        if(!this.STARTED) {
            this.TICK_TASK = Bukkit.getScheduler()
                    .runTaskTimerAsynchronously(this.INTERACTOR.getPlugin(), this::tick, 0L,
                                                this.INTERACTOR.getModel().TICK);
            this.STARTED = true;
        }
    }
    @Override
    public void stop() {
        if(this.STARTED) {
            this.TICK_TASK.cancel();
            this.SEND_BOSSBAR = false;
            this.BOSSBAR.removeAll();
            this.STARTED = false;
        }
    }
    @Override
    public void tick() {
        AtomicReference<Double> LAST_LEAST_DISTANCE = new AtomicReference<>(null);
        AtomicReference<PointModel> LEAST_POINT = new AtomicReference<>(null);
        ConfigModel MODEL = this.INTERACTOR.getModel();
        if(!Arrays.asList(MODEL.ENABLED_WORLDS).contains(this.PLAYER.getWorld().getName())) {
            return;
        }
        Arrays.stream(MODEL.POINTS).forEach(POINT -> {
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
            }
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
                String CONTENT = ACTION.CONTENT.replace("{player}", this.PLAYER.getName());
                switch(ACTION.TYPE) {
                    case "COMMAND": {
                        Server SERVER = this.INTERACTOR.getPlugin().getServer();
                        SERVER.dispatchCommand(SERVER.getConsoleSender(), CONTENT);
                        break;
                    }
                    case "MESSAGE": {
                        this.PLAYER.sendMessage(CONTENT);
                        break;
                    }
                }
            });
            this.PASSING_LIST.add(POINT_LOCATION);
            this.TIMER_BLOCK = false;
            boolean CLEAR;
            if(MODEL.CLEAR_PASSING_VALUE.equals("ALL")) {
                CLEAR = this.PASSING_LIST.size() >= MODEL.POINTS.length;
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
            LINE = new Line(MODEL, FORMAT_DISTANCE);
            if(DISTANCE > POINT.RADIUS) {
                LINE.update(POINT_LOCATION, LOCATION, FORMAT_DISTANCE);
                this.BOSSBAR.setColor(BarColor.valueOf(MODEL.BOSS_BAR_COLOR_DEFAULT));
            } else {
                this.BOSSBAR.setColor(BarColor.valueOf(MODEL.BOSS_BAR_COLOR_ENTERED));
            }
            double PROGRESS = 1.0 - (DISTANCE - POINT.RADIUS)/MODEL.BOSS_BAR_PROGRESS_DIVISOR;
            this.BOSSBAR.setProgress(Math.min(Math.max(PROGRESS, 0), 1.0));
        } else {
            LINE = new Line(this.INTERACTOR.getModel(), "0");
        }
        Arrays.stream(MODEL.COMPASS_MESSAGE_TYPES).forEach(TYPE -> {
            switch(TYPE) {
                case "ACTION_BAR": {
                    LINE.sendActionBar(PLAYER);
                    break;
                }
                case "BOSS_BAR": {
                    this.BOSSBAR.setTitle(LINE.get());
                    if(!this.SEND_BOSSBAR) {
                        this.BOSSBAR.addPlayer(PLAYER);
                        this.SEND_BOSSBAR = true;
                    }
                    break;
                }
                case "MESSAGE": {
                    LINE.sendMessage(PLAYER);
                    break;
                }
            }
        });
    }
    @Override
    public Boolean isStarted() {
        return this.STARTED;
    }
}
