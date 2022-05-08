package n2k_.ntractpoint.core;
import n2k_.ntractpoint.base.IEngine;
import n2k_.ntractpoint.base.IInteractor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
public class Engine implements IEngine {
    private final Player PLAYER;
    private final IInteractor INTERACTOR;
    private BukkitTask TICK_TASK;
    private Boolean TIMER_BLOCK;
    private Boolean ENTERED;
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
        AtomicReference<Location> LEAST_POINT = new AtomicReference<>();
        Arrays.stream(this.INTERACTOR.getModel().POINTS).forEach(POINT -> {
            Location POINT_LOCATION = new Location(
                    this.PLAYER.getWorld(), POINT.X, POINT.Y, POINT.Z
            );
            double DISTANCE = POINT_LOCATION.distance(this.PLAYER.getLocation());
            if(DISTANCE < 10) {
                this.ENTERED = true;
                this.timerExecute();
            } else {
                this.ENTERED = false;
                this.TIMER_BLOCK = false;
            }
            if(LAST_LEAST_DISTANCE.get() == null) {
                LAST_LEAST_DISTANCE.set(DISTANCE);
            }
            if(LAST_LEAST_DISTANCE.get() > DISTANCE) {
                LAST_LEAST_DISTANCE.set(DISTANCE);
                LEAST_POINT.set(POINT_LOCATION);
            }
        });
        this.sendCompass(LEAST_POINT.get());
    }
    @Override
    public void timerExecute() {
        if(!this.TIMER_BLOCK) {
            this.TIMER_BLOCK = true;
            Bukkit.getScheduler().runTaskLater(this.getInteractor().getPlugin(), () -> {
                if(this.ENTERED) this.interact();
            }, 100L);
        }
    }
    @Override
    public void interact() {
        this.PLAYER.sendMessage("interact");
    }
    @Override
    public void sendCompass(Location LOCATION) {

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
