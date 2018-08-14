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
import org.bukkit.configuration.file.YamlConfiguration;
import pl.toranktto.pg.PGPlugin;
import pl.toranktto.pg.storage.ConfigStorage;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public class ConfigYamlStorage implements ConfigStorage {

    private static ConfigYamlStorage instance;
    
    private final File yamlFile = new File(PGPlugin.getInstance().getDataFolder(), "config.yml");

    private long levelDivider = 0;

    public ConfigYamlStorage() {
        instance = this;
    }
    
    public static ConfigYamlStorage getInstance() {
        return instance;
    }
    
    @Override
    public long getLevelDivider() {
        return this.levelDivider;
    }

    @Override
    public synchronized void load() {
        if (!this.yamlFile.exists()) {
            try {
                PGPlugin.getInstance().saveResource("config.yml", true);
            } catch (Exception ex) {
                PGPlugin.error("Error in creating config.yml: " + ex.getLocalizedMessage());
            }
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(this.yamlFile);

        this.levelDivider = yaml.getLong("levelDivider");
        if (this.levelDivider == 0) {
            PGPlugin.warning("Invalid level divider in config.yml!");
        }
    }
}
