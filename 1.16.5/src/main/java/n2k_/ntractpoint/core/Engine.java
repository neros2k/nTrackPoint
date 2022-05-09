package n2k_.ntractpoint.core;
import n2k_.ntractpoint.base.IEngine;
import n2k_.ntractpoint.base.IInteractor;
import n2k_.ntractpoint.base.model.PointModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
public class Engine implements IEngine {
    private final Player PLAYER;
    private final IInteractor INTERACTOR;
    private BukkitTask TICK_TASK;
    private BukkitTask TIMER_TASK;
    private Boolean TIMER_BLOCK;
    public Engine(Player PLAYER, IInteractor INTERACTOR) {
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
        AtomicReference<PointModel> LEAST_POINT = new AtomicReference<>();
        Arrays.stream(this.INTERACTOR.getModel().POINTS).forEach(POINT -> {
            Location POINT_LOCATION = new Location(
                    this.PLAYER.getWorld(), POINT.X, POINT.Y, POINT.Z
            );
            double DISTANCE = POINT_LOCATION.distance(this.PLAYER.getLocation());
            if(DISTANCE < POINT.RADIUS) {
                this.timerExecute();
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
        });
        this.sendCompass(LEAST_POINT.get());
    }
    @Override
    public void timerExecute() {
        if(!this.TIMER_BLOCK) {
            this.TIMER_BLOCK = true;
            this.TIMER_TASK = Bukkit.getScheduler().runTaskLater(
                    this.getInteractor().getPlugin(),
                    this::interact, this.INTERACTOR.getModel().PERIOD
            );
        }
    }
    @Override
    public void interact() {
        this.PLAYER.sendMessage("interact");
    }
    @Override
    public void sendCompass(@NotNull PointModel POINT) {
        Location POINT_LOCATION = new Location(
                this.PLAYER.getWorld(), POINT.X, POINT.Y, POINT.Z
        );
        Location LOCATION = this.PLAYER.getLocation();
        String STR = "[-]";
        if(POINT_LOCATION.distance(LOCATION) > POINT.RADIUS) {
            Vector SUBTRACT = POINT_LOCATION.toVector().subtract(LOCATION.toVector()).normalize();
            Vector DIRECTION = LOCATION.getDirection();
            double ANGLE = Math.toDegrees(Math.acos(SUBTRACT.dot(DIRECTION)));
            if(ANGLE < 180) {
                if(ANGLE < 15) {
                    STR = "ВПЕРЕД";
                } else if(ANGLE < 45) {
                    STR = "В СТОРОНУ";
                } else {
                    STR = "ПОВОРОТ";
                }
            } else {
                if(ANGLE > 345) {
                    STR = "ВПЕРЕД";
                } else if(ANGLE > 315) {
                    STR = "В СТОРОНУ";
                } else {
                    STR = "ПОВОРОТ";
                }
            }
        }
        this.PLAYER.sendMessage(STR);
    }
    @Override
    public Player getPlayer() {
        return this.PLAYER;
    }
    @Override
    public IInteractor getInteractor() {
        return this.INTERACTOR;
    }
}
