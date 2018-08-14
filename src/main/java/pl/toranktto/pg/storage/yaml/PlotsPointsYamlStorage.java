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

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.toranktto.pg.PGPlugin;
import pl.toranktto.pg.object.PlotData;
import pl.toranktto.pg.storage.PlotsPointsStorage;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public class PlotsPointsYamlStorage implements PlotsPointsStorage {

    private final File yamlDir;
    private final Map<PlotData, Long> plots;

    public PlotsPointsYamlStorage() {
        this.yamlDir = new File(PGPlugin.getInstance().getDataFolder(), "plots/");
        this.plots = new HashMap<>();
    }

    @Override
    public long getPoints(Plot plot) {
        PlotData plotData = new PlotData(plot);
        Long points = this.plots.get(plotData);

        if (points == null) {
            return 0;
        }

        return points;
    }

    @Override
    public synchronized void setPoints(Plot plot, long points) {
        PlotData plotData = new PlotData(plot);

        if (points == 0) {
            this.plots.remove(plotData);
            this.delete(plotData);
        } else {
            this.plots.put(plotData, points);
            this.save(plotData);
        }
    }

    private void save(PlotData plotData) {
        if (!this.plots.containsKey(plotData)) {
            return;
        }

        long points = this.plots.get(plotData);

        File file = new File(this.yamlDir, plotData.toString() + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception ex) {
                PGPlugin.getInstance().getLogger().log(Level.WARNING, "Error in creating YAML storage plot file {0}: {1}", new Object[]{file.getName(), ex.toString()});
                return;
            }
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        yaml.set("points", points);

        try {
            yaml.save(file);
        } catch (Exception ex) {
            PGPlugin.getInstance().getLogger().log(Level.WARNING, "Error in saving YAML storage plot file {0}: {1}", new Object[]{file.getName(), ex.toString()});
        }
    }

    private void delete(PlotData plotData) {
        File file = new File(this.yamlDir, plotData.toString() + ".yml");

        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public synchronized void load() {
        PlotAPI plotApi = new PlotAPI();

        this.plots.clear();

        if (!this.yamlDir.exists()) {
            try {
                this.yamlDir.mkdirs();
            } catch (Exception ex) {
                PGPlugin.getInstance().getLogger().log(Level.WARNING, "Error in creating YAML storage directory: {0}", ex.toString());
            }
        }

        int cleaned = 0;
        for (File i : this.yamlDir.listFiles()) {
            String fileName = i.getName();

            if (!fileName.endsWith(".yml")) {
                PGPlugin.getInstance().getLogger().log(Level.WARNING, "Invalid name of YAML storage plot file {0}", fileName);
                continue;
            }

            String[] plotId = fileName.substring(0, fileName.lastIndexOf(".yml")).split(";");

            if (plotId.length != 3) {
                PGPlugin.getInstance().getLogger().log(Level.WARNING, "Invalid name of YAML storage plot file {0}", fileName);
                continue;
            }

            PlotData plotData;

            try {
                plotData = new PlotData(plotId[0], Integer.parseInt(plotId[1]), Integer.parseInt(plotId[2]));
            } catch (NumberFormatException ex) {
                PGPlugin.getInstance().getLogger().log(Level.WARNING, "Invalid plot coordinates in name of YAML storage plot file {0}", fileName);
                continue;
            }

            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(i);

            long points = yaml.getLong("points");

            if (points == 0) {
                this.delete(plotData);
                ++cleaned;
            } else {
                this.plots.put(plotData, points);
            }
        }

        if (cleaned > 0) {
            PGPlugin.getInstance().getLogger().log(Level.INFO, "Cleaned {0} plot files in YAML storage.", String.valueOf(cleaned));
        }

        PGPlugin.getInstance().getLogger().log(Level.INFO, "Loaded {0} plots with YAML storage.", String.valueOf(this.plots.size()));
    }

}
