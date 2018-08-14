/*
 * Copyright (c) 2017, Toranktto
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package pl.toranktto.pg;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.toranktto.pg.command.PGCommand;
import pl.toranktto.pg.storage.yaml.BlockPointsYamlStorage;
import pl.toranktto.pg.storage.yaml.MessagesYamlStorage;
import pl.toranktto.pg.storage.yaml.PlotsPointsYamlStorage;
import java.util.logging.Level;
import pl.toranktto.pg.hook.Hooker;
import pl.toranktto.pg.listener.PlotListener;
import pl.toranktto.pg.storage.BlockPointsStorage;
import pl.toranktto.pg.storage.ConfigStorage;
import pl.toranktto.pg.storage.MessagesStorage;
import pl.toranktto.pg.storage.PlotsPointsStorage;
import pl.toranktto.pg.storage.yaml.ConfigYamlStorage;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public final class PGPlugin extends JavaPlugin {

    private static PGPlugin instance;

    private BlockPointsStorage blockPointsStorage;
    private MessagesStorage messagesStorage;
    private ConfigStorage configStorage;
    private PlotsPointsStorage plotsPointsStorage;
    private TopPlotsManager topPlotsManager;
    private PointsProcessor pointsProcessor;

    public static PGPlugin getInstance() {
        return instance;
    }

    public static void warning(String message) {
        Bukkit.getLogger().log(Level.WARNING, "[PG] {0}", message);
    }

    public static void info(String message) {
        Bukkit.getLogger().log(Level.INFO, "[PG] {0}", message);
    }

    public static void error(String message) {
        Bukkit.getLogger().log(Level.SEVERE, "[PG] {0}", message);
    }

    public ConfigStorage getConfigStorage() {
        return this.configStorage;
    }

    public BlockPointsStorage getBlockPointsStorage() {
        return this.blockPointsStorage;
    }

    public MessagesStorage getMessagesStorage() {
        return this.messagesStorage;
    }

    public PlotsPointsStorage getPlotsPointsStorage() {
        return this.plotsPointsStorage;
    }

    public TopPlotsManager getTopPlotsManager() {
        return this.topPlotsManager;
    }

    public PointsProcessor getPointsProcessor() {
        return this.pointsProcessor;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        instance = this;
    }

    private boolean checkDependency(String plugin) {
        return this.getServer().getPluginManager().getPlugin(plugin) != null;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (!this.checkDependency("WorldEdit")) {
            PGPlugin.error("Not found WorldEdit!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!this.checkDependency("PlotSquared")) {
            PGPlugin.error("Not found PlotSquared!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.configStorage = new ConfigYamlStorage();
        this.blockPointsStorage = new BlockPointsYamlStorage();
        this.messagesStorage = new MessagesYamlStorage();
        this.plotsPointsStorage = new PlotsPointsYamlStorage();

        this.blockPointsStorage.load();
        this.messagesStorage.load();
        this.plotsPointsStorage.load();
        this.configStorage.load();

        this.topPlotsManager = new TopPlotsManager(this.plotsPointsStorage);
        this.pointsProcessor = new PointsProcessor(this.blockPointsStorage);

        this.pointsProcessor.setLevelDivider(this.configStorage.getLevelDivider());
        this.topPlotsManager.refresh();

        this.getCommand("pg").setExecutor(new PGCommand(this));

        Bukkit.getServer().getPluginManager().registerEvents(new PlotListener(this), this);

        Hooker.hook();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        PGPlugin.info("Cancelling all tasks...");
        Bukkit.getScheduler().cancelTasks(this);

        this.messagesStorage = null;
        this.plotsPointsStorage = null;
        this.blockPointsStorage = null;
        this.topPlotsManager = null;
        this.configStorage = null;
        this.pointsProcessor = null;

        instance = null;
    }
}
