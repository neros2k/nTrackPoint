package n2k_.ntractpoint.utils;
import com.google.gson.Gson;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
public final class JsonConfig<T> {
    private final static Gson GSON = new Gson();
    private final Class<T> CLASS;
    private final Plugin PLUGIN;
    private final Path PATH;
    private T JSON;
    public JsonConfig(@NotNull Plugin PLUGIN, Class<T> CLASS, String NAME) {
        this.CLASS = CLASS;
        this.PLUGIN = PLUGIN;
        this.PATH = PLUGIN.getDataFolder().toPath().resolve(NAME);
    }
    public void createConfig() {
        this.PLUGIN.saveResource(this.PATH.getFileName().toString(), false);
    }
    public void reload() {
        if(!new File(this.PLUGIN.getDataFolder(), this.PATH.getFileName().toString()).exists()) {
            this.createConfig();
        }
        try {
            this.JSON = GSON.fromJson(Files.newBufferedReader(this.PATH), this.CLASS);
        } catch(IOException EXCEPTION) {
            this.PLUGIN.getLogger().warning(EXCEPTION.toString());
        }
    }
    public T getJson() {
        assert this.JSON != null;
        return this.JSON;
    }
}