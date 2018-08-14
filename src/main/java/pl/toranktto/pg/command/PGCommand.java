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
package pl.toranktto.pg.command;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.toranktto.pg.PGPlugin;
import pl.toranktto.pg.PointsProcessor;
import pl.toranktto.pg.TopPlotsManager;
import pl.toranktto.pg.object.PointsArea;
import pl.toranktto.pg.storage.BlockPointsStorage;
import pl.toranktto.pg.storage.ConfigStorage;
import pl.toranktto.pg.storage.MessagesStorage;
import pl.toranktto.pg.storage.PlotsPointsStorage;
import pl.toranktto.pg.util.PlayerUtils;
import pl.toranktto.pg.util.PlotUtils;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public class PGCommand implements CommandExecutor, Listener {

    private final PGPlugin plugin;

    private final MessagesStorage messagesStorage;
    private final TopPlotsManager topPlotsManager;
    private final PlotsPointsStorage plotsPointsStorage;
    private final BlockPointsStorage blockPointsStorage;
    private final ConfigStorage configStorage;
    private final PointsProcessor pointsProcessor;

    private final PlotAPI plotApi;

    private static final int[] TOP_GUI_SLOTS = new int[]{4, 9, 10, 11, 12, 13, 14, 15, 16, 17};
    private static final ItemStack TOP_EMPTY_ITEM = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 11);

    public PGCommand(PGPlugin plugin) {
        super();
        this.plugin = plugin;
        this.messagesStorage = this.plugin.getMessagesStorage();
        this.topPlotsManager = this.plugin.getTopPlotsManager();
        this.plotsPointsStorage = this.plugin.getPlotsPointsStorage();
        this.blockPointsStorage = this.plugin.getBlockPointsStorage();
        this.pointsProcessor = this.plugin.getPointsProcessor();
        this.configStorage = this.plugin.getConfigStorage();
        this.plotApi = new PlotAPI();
        
        Bukkit.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    private void usage(CommandSender sender) {
        if (sender.hasPermission("pg.admin")) {
            sender.sendMessage(this.messagesStorage.getMessage("PG_USAGE_ADMIN"));
        } else {
            sender.sendMessage(this.messagesStorage.getMessage("PG_USAGE"));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0: {
                if (!(sender instanceof Player)) {
                    this.usage(sender);
                    return false;
                }

                if (!sender.hasPermission("pg.calc")) {
                    sender.sendMessage(this.messagesStorage.getMessage("PG_NOPERM"));
                    return false;
                }

                Player player = (Player) sender;
                Plot plot = this.plotApi.getPlot(player.getLocation());
                if (plot == null) {
                    player.sendMessage(this.messagesStorage.getMessage("PG_NOPLOT"));
                    break;
                }

                String plotOwner = PlotUtils.getOwner(plot);
                if (plotOwner == null) {
                    player.sendMessage(this.messagesStorage.getMessage("PG_FREEPLOT"));
                    break;
                }

                if (!plot.isAdded(player.getUniqueId())) {
                    if (player.hasPermission("pg.admin")) {
                        player.sendMessage(this.messagesStorage.getMessage("PG_OTHER")
                                .replace("%owner%", plotOwner));
                    } else {
                        player.sendMessage(this.messagesStorage.getMessage("PG_NOTADDED"));
                        break;
                    }
                }

                player.sendMessage(this.messagesStorage.getMessage("PG_CALCULATING"));

                PointsArea area = new PointsArea(PlotUtils.getSelection(plot));

                Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    long points = this.plotsPointsStorage.getPoints(plot);

                    long newPoints = this.pointsProcessor.process(area);
                    long newLevel = this.pointsProcessor.getLevel(newPoints);

                    if (newPoints != points || newPoints == 0) {
                        this.plotsPointsStorage.setPoints(plot, newPoints);
                        this.topPlotsManager.refresh();
                    }

                    player.sendMessage(this.messagesStorage.getMessage("PG_RESULT")
                            .replace("%points%", String.valueOf(newPoints))
                            .replace("%level%", String.valueOf(newLevel)));
                });

                break;
            }

            case 1: {
                switch (args[0].toLowerCase()) {
                    case "ver":
                    case "author":
                    case "version": {
                        sender.sendMessage(ChatColor.AQUA + this.plugin.getName() + " v" + this.plugin.getDescription().getVersion());
                        sender.sendMessage(ChatColor.AQUA + "Copyright (c) 2017-2018 ~Toranktto");
                        
                        break;
                    }

                    case "top": {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(this.messagesStorage.getMessage("PG_CONSOLE"));
                            return false;
                        }

                        if (!sender.hasPermission("pg.top")) {
                            sender.sendMessage(this.messagesStorage.getMessage("PG_NOPERM"));
                            return false;
                        }
                        
                        if (this.plotApi.getAllPlots().size() < 10) {
                            sender.sendMessage(this.messagesStorage.getMessage("PG_TOP_NOT_ENOUGH"));
                            return false;
                        }

                        Player player = (Player) sender;
                        player.openInventory(this.buildTopGui());
                        
                        break;
                    }

                    case "reload": {
                        if (!sender.hasPermission("pg.admin")) {
                            sender.sendMessage(this.messagesStorage.getMessage("PG_NOPERM"));
                            break;
                        }

                        Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            this.configStorage.load();
                            this.messagesStorage.load();
                            this.blockPointsStorage.load();
                            this.pointsProcessor.setLevelDivider(this.configStorage.getLevelDivider());
                            sender.sendMessage(this.messagesStorage.getMessage("PG_RELOADED"));
                        });

                        break;
                    }

                    default: {
                        this.usage(sender);
                        break;
                    }
                }

                break;
            }

            case 2: {
                switch (args[0].toLowerCase()) {
                    case "reload": {
                        if (!sender.hasPermission("pg.admin")) {
                            sender.sendMessage(this.messagesStorage.getMessage("PG_NOPERM"));
                            break;
                        }

                        switch (args[1].toLowerCase()) {
                            case "all": {
                                Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.configStorage.load();
                                    this.messagesStorage.load();
                                    this.blockPointsStorage.load();
                                    this.pointsProcessor.setLevelDivider(this.configStorage.getLevelDivider());
                                    sender.sendMessage(this.messagesStorage.getMessage("PG_RELOADED"));
                                });

                                break;
                            }

                            case "messages": {
                                Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.messagesStorage.load();
                                    sender.sendMessage(this.messagesStorage.getMessage("PG_RELOADED"));
                                });

                                break;
                            }

                            case "points": {
                                Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.blockPointsStorage.load();
                                    sender.sendMessage(this.messagesStorage.getMessage("PG_RELOADED"));
                                });

                                break;
                            }

                            case "plots": {
                                Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.plotsPointsStorage.load();
                                    this.topPlotsManager.refresh();
                                    sender.sendMessage(this.messagesStorage.getMessage("PG_RELOADED"));
                                });

                                break;
                            }

                            case "config": {
                                Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.configStorage.load();
                                    this.pointsProcessor.setLevelDivider(this.configStorage.getLevelDivider());
                                    sender.sendMessage(this.messagesStorage.getMessage("PG_RELOADED"));
                                });

                                break;
                            }

                            default: {
                                this.usage(sender);
                                break;
                            }
                        }

                        break;
                    }

                    default: {
                        this.usage(sender);
                        break;
                    }
                }

                break;
            }

            default: {
                this.usage(sender);
                break;
            }
        }

        return false;

    }

    private Inventory buildTopGui() {
        List<Plot> top = this.topPlotsManager.getPlaces(10);

        Inventory inventory = Bukkit.createInventory(
                null,
                9 * 3,
                this.plugin.getMessagesStorage().getMessage("PG_TOP_GUI_NAME")
        );

        for (int i = 0; i < top.size(); ++i) {
            Plot plot = top.get(i);

            String owner = PlotUtils.getOwner(plot);

            long points = this.plotsPointsStorage.getPoints(plot);
            long level = this.pointsProcessor.getLevel(points);

            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

            skullMeta.setOwner(owner);
            skullMeta.setDisplayName(this.messagesStorage.getMessage("PG_TOP_GUI_HEAD")
                    .replace("%place%", String.valueOf(i + 1))
                    .replace("%owner%", owner)
                    .replace("%points%", String.valueOf(points))
                    .replace("%level%", String.valueOf(level)));

            List<String> lore = new ArrayList<>();

            plot.getTrusted().forEach((j) -> {
                String trusted = PlayerUtils.getNameFromUUID(j);
                if (PlayerUtils.isOnline(j)) {
                    lore.add(this.messagesStorage.getMessage("PG_TOP_GUI_TRUSTED_ONLINE")
                            .replace("%trusted%", trusted));
                } else {
                    lore.add(this.messagesStorage.getMessage("PG_TOP_GUI_TRUSTED_OFFLINE")
                            .replace("%trusted%", trusted));
                }
            });

            plot.getMembers().forEach((j) -> {
                String member = PlayerUtils.getNameFromUUID(j);
                if (PlayerUtils.isOnline(j)) {
                    lore.add(this.messagesStorage.getMessage("PG_TOP_GUI_MEMBER_ONLINE")
                            .replace("%member%", member));
                } else {
                    lore.add(this.messagesStorage.getMessage("PG_TOP_GUI_MEMBER_OFFLINE")
                            .replace("%member%", member));
                }
            });

            skullMeta.setLore(lore);
            skull.setItemMeta(skullMeta);
            inventory.setItem(TOP_GUI_SLOTS[i], skull);
        }

        for (int i = 0; i < inventory.getSize(); ++i) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, TOP_EMPTY_ITEM);
            }
        }

        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getName().equals(this.messagesStorage.getMessage("PG_TOP_GUI_NAME"))) {
            event.setCancelled(true);
        }
    }
}
