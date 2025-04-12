package io.dogsbean.fblauncher;

import io.dogsbean.fblauncher.config.Config;
import io.dogsbean.fblauncher.listener.FireballListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        Config config = new Config();
        config.load(getConfig());

        getLogger().info("   _____         __        ____  __                      __          \n" +
                "  / __(_)______ / /  ___ _/ / / / /  ___ ___ _____  ____/ /  ___ ____\n" +
                " / _// / __/ -_) _ \\/ _ `/ / / / /__/ _ `/ // / _ \\/ __/ _ \\/ -_) __/\n" +
                "/_/ /_/_/  \\__/_.__/\\_,_/_/_/ /____/\\_,_/\\_,_/_//_/\\__/_//_/\\__/_/   \n" +
                "                                                                     \n" +
                "v1.0\n" +
                "Plugin by Dogsbean\n");

        getServer().getPluginManager().registerEvents(new FireballListener(config), this);
    }
}
