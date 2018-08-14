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
package pl.toranktto.pg.listener;

import com.intellectualcrafters.plot.object.Plot;
import com.plotsquared.bukkit.events.PlayerClaimPlotEvent;
import com.plotsquared.bukkit.events.PlotClearEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.toranktto.pg.PGPlugin;
import pl.toranktto.pg.PointsProcessor;
import pl.toranktto.pg.TopPlotsManager;
import pl.toranktto.pg.object.PointsArea;
import pl.toranktto.pg.storage.PlotsPointsStorage;
import pl.toranktto.pg.util.PlotUtils;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public class PlotListener implements Listener {

    private final PGPlugin plugin;

    private final TopPlotsManager topPlotsManager;
    private final PlotsPointsStorage plotsPointsStorage;
    private final PointsProcessor pointsProcessor;

    public PlotListener(PGPlugin plugin) {
        this.plugin = plugin;
        this.topPlotsManager = this.plugin.getTopPlotsManager();
        this.plotsPointsStorage = this.plugin.getPlotsPointsStorage();
        this.pointsProcessor = this.plugin.getPointsProcessor();
    }

    @EventHandler
    public void onPlotClear(PlotClearEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.plotsPointsStorage.setPoints(event.getPlot(), 0);
            this.topPlotsManager.refresh();
        });
    }

    @EventHandler
    public void onPlotClaim(PlayerClaimPlotEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            Plot plot = event.getPlot();
            PointsArea area = new PointsArea(PlotUtils.getSelection(plot));
            
            long points = this.plotsPointsStorage.getPoints(plot);

            long newPoints = this.pointsProcessor.process(area);
            long newLevel = this.pointsProcessor.getLevel(newPoints);

            if (newPoints != points || newPoints == 0) {
                this.plotsPointsStorage.setPoints(plot, newPoints);
                this.topPlotsManager.refresh();
            }
        });
    }
}
