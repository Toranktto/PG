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
package pl.toranktto.pg.hook.placeholderapi;

import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.entity.Player;
import pl.toranktto.pg.PGPlugin;
import pl.toranktto.pg.PointsProcessor;
import pl.toranktto.pg.storage.PlotsPointsStorage;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public class PGPlaceholder implements PlaceholderReplacer {

    private final PGPlugin plugin;

    private final PlotsPointsStorage plotsPointsStorage;
    private final PointsProcessor pointsProcessor;
    private final PlotAPI plotApi;

    public PGPlaceholder(PGPlugin plugin) {
        super();
        this.plugin = plugin;
        this.plotsPointsStorage = this.plugin.getPlotsPointsStorage();
        this.pointsProcessor = this.plugin.getPointsProcessor();
        this.plotApi = new PlotAPI();
    }

    @Override
    public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return "";
        }

        Plot plot = this.plotApi.getPlot(player);
        if (plot == null) {
            return "";
        }
        
        return String.valueOf(this.pointsProcessor.getLevel(this.plotsPointsStorage.getPoints(plot)));
    }
}
