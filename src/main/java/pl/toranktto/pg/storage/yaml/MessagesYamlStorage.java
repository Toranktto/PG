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
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.toranktto.pg.PGPlugin;
import pl.toranktto.pg.storage.MessagesStorage;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public class MessagesYamlStorage implements MessagesStorage {

    private final File yamlFile;
    private final Map<String, String> messages;

    public MessagesYamlStorage() {
        this.yamlFile = new File(PGPlugin.getInstance().getDataFolder(), "messages.yml");
        this.messages = new HashMap<>();
    }

    @Override
    public String getMessage(String id) {
        String message = this.messages.get(id);
        if (message == null) {
            return id;
        }

        return message;
    }

    @Override
    public synchronized void load() {
        this.messages.clear();

        if (!this.yamlFile.exists()) {
            try {
                this.yamlFile.getParentFile().mkdirs();
                PGPlugin.getInstance().saveResource("messages.yml", false);
            } catch (Exception ex) {
                PGPlugin.getInstance().getLogger().log(Level.WARNING, "Error in creating messages.yml: {0}", ex.toString());
            }
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(this.yamlFile);

        ConfigurationSection section = yaml.getRoot();

        for (String i : section.getKeys(false)) {
            this.messages.put(i, ChatColor.translateAlternateColorCodes('&', section.getString(i)));
        }

        PGPlugin.getInstance().getLogger().log(Level.INFO, "Loaded {0} messages.", String.valueOf(this.messages.size()));
    }
}
