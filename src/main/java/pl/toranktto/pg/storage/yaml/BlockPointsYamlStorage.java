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
package pl.toranktto.pg.storage.yaml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.toranktto.pg.PGPlugin;
import pl.toranktto.pg.object.BlockData;
import pl.toranktto.pg.storage.BlockPointsStorage;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public class BlockPointsYamlStorage implements BlockPointsStorage {

    private final File yamlFile;
    private final Map<BlockData, Integer> blocks;

    public BlockPointsYamlStorage() {
        this.yamlFile = new File(PGPlugin.getInstance().getDataFolder(), "points.yml");
        this.blocks = new HashMap<>();
    }

    @Override
    public int getPoints(BlockData blockData) {
        Integer points = this.blocks.get(blockData);
        if (points == null) {
            return 0;
        }

        return points;
    }

    @Override
    public synchronized void setPoints(BlockData blockData, int points) {
        this.blocks.put(blockData, points);

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(this.yamlFile);
        yaml.set(blockData.toString() + ".points", points);
        this.save(yaml);
    }

    private void save(YamlConfiguration yaml) {
        try {
            yaml.save(this.yamlFile);
        } catch (Exception ex) {
            PGPlugin.getInstance().getLogger().log(Level.WARNING, "Error in saving points.yml: {0}", ex.toString());
        }
    }

    @Override
    public synchronized void load() {
        this.blocks.clear();

        if (!this.yamlFile.exists()) {
            try {
                this.yamlFile.getParentFile().mkdirs();
                PGPlugin.getInstance().saveResource("points.yml", false);
            } catch (Exception ex) {
                PGPlugin.getInstance().getLogger().log(Level.WARNING, "Error in creating points.yml: {0}", ex.toString());
            }
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(this.yamlFile);
        ConfigurationSection section = yaml.getRoot();

        int cleaned = 0;
        for (String i : section.getKeys(false)) {
            String[] key = i.split(":");

            if (key.length != 2) {
                PGPlugin.getInstance().getLogger().log(Level.WARNING, "Invalid block points definition '{0}'", i);
                continue;
            }

            int id;
            byte metadata;

            try {
                id = Integer.parseInt(key[0]);
                metadata = Byte.parseByte(key[1]);
            } catch (NumberFormatException ex) {
                PGPlugin.getInstance().getLogger().log(Level.WARNING, "Invalid block points definition '{0}'", i);
                continue;
            }

            int points = section.getInt(i + ".points");

            if (points == 0) {
                section.set(i, null);
                ++cleaned;
            } else {
                this.blocks.put(new BlockData(id, metadata), points);
            }
        }

        if (cleaned > 0) {
            this.save(yaml);
            PGPlugin.getInstance().getLogger().log(Level.INFO, "Cleaned {0} block points definitions.", String.valueOf(cleaned));
        }

        PGPlugin.getInstance().getLogger().log(Level.INFO, "Loaded {0} block points definitions.", String.valueOf(this.blocks.size()));
    }
}
