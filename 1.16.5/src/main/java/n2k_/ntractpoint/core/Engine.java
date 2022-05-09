package n2k_.ntractpoint.core;
import n2k_.ntractpoint.base.IEngine;
import n2k_.ntractpoint.base.IInteractor;
import n2k_.ntractpoint.base.ILine;
import n2k_.ntractpoint.base.model.ConfigModel;
import n2k_.ntractpoint.base.model.PointModel;
import n2k_.ntractpoint.utils.Line;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
public class Engine implements IEngine {
    private final List<Location> PASSING_LIST;
    private final Player PLAYER;
    private final IInteractor INTERACTOR;
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
        boolean CLEAR;
        if(this.INTERACTOR.getModel().CLEAR_PASSING_VALUE.equals("ALL")) {
            CLEAR = this.PASSING_LIST.size() >= MODEL.DEFAULT_POINTS.length;
        } else if(MODEL.CLEAR_PASSING_VALUE.equals("NONE")) {
            CLEAR = false;
        } else {
            CLEAR = this.PASSING_LIST.size() >= Integer.parseInt(MODEL.CLEAR_PASSING_VALUE);
        }
        if(CLEAR) this.PASSING_LIST.clear();
        Arrays.stream(MODEL.DEFAULT_POINTS).forEach(POINT -> {
            Location POINT_LOCATION = new Location(
                    this.PLAYER.getWorld(), POINT.X, POINT.Y, POINT.Z
            );
            if(!this.PASSING_LIST.contains(POINT_LOCATION)) {
                double DISTANCE = POINT_LOCATION.distance(this.PLAYER.getLocation());
                if(DISTANCE < POINT.RADIUS) {
                    this.timerExecute(POINT_LOCATION);
                } else {
                    this.TIMER_BLOCK = false;
                    if(this.TIMER_TASK != null) {
                        this.TIMER_TASK.cancel();
                    }
                }
                if(LAST_LEAST_DISTANCE.get() == null || LAST_LEAST_DISTANCE.get() > DISTANCE) {
                    LAST_LEAST_DISTANCE.set(DISTANCE);
                    LEAST_POINT.set(POINT);
                }
            }
        });
        if(LEAST_POINT.get() != null) {
            this.sendCompass(LEAST_POINT.get());
        } else {
            new Line(this.INTERACTOR.getModel(), "0");
        }
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
    public void interact(Location POINT_LOCATION) {
        if(this.INTERACTOR.getModel().ENABLE_PASSING) {
            this.PASSING_LIST.add(POINT_LOCATION);
        }
    }
    @Override
    public void sendCompass(@NotNull PointModel POINT) {
        Location POINT_LOCATION = new Location(
                this.PLAYER.getWorld(), POINT.X, POINT.Y, POINT.Z
        );
        Location LOCATION = this.PLAYER.getLocation();
        double DISTANCE = POINT_LOCATION.distance(LOCATION);
        String FORMAT_DISTANCE = String.format(this.INTERACTOR.getModel().DISTANCE_FORMAT, DISTANCE);
        ILine LINE = new Line(this.INTERACTOR.getModel(), FORMAT_DISTANCE);
        if(DISTANCE > POINT.RADIUS) {
            LINE.update(POINT_LOCATION, LOCATION, FORMAT_DISTANCE);
        }
        LINE.sendActionBar(this.PLAYER);
        LINE.sendBossBar(this.PLAYER);
        LINE.sendMessage(this.PLAYER);
    }
}
