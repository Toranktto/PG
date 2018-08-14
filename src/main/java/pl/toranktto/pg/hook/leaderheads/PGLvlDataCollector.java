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
package pl.toranktto.pg.hook.leaderheads;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import pl.toranktto.pg.PGPlugin;
import pl.toranktto.pg.PointsProcessor;
import pl.toranktto.pg.TopPlotsManager;
import pl.toranktto.pg.util.PlotUtils;
import pl.toranktto.pg.storage.PlotsPointsStorage;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public class PGLvlDataCollector extends DataCollector {

    private final PGPlugin plugin;

    private final TopPlotsManager topPlotsManager;
    private final PlotsPointsStorage plotsPointsStorage;
    private final PointsProcessor pointsProcessor;

    public PGLvlDataCollector(PGPlugin plugin) {
        super("pg-lvl", plugin.getName(), BoardType.DEFAULT, plugin.getMessagesStorage().getMessage("PG_TOP_GUI_NAME"), "pg-lvl-top", Arrays.asList(null, null, "&e{amount} LvL", null), false, String.class);
        this.plugin = plugin;
        this.topPlotsManager = this.plugin.getTopPlotsManager();
        this.plotsPointsStorage = this.plugin.getPlotsPointsStorage();
        this.pointsProcessor = this.plugin.getPointsProcessor();
    }

    @Override
    public List<Map.Entry<?, Double>> requestAll() {
        List<Map.Entry<?, Double>> list = new ArrayList<>();

        this.topPlotsManager.getAllPlaces().stream().forEach((plot) -> {
            list.add(new AbstractMap.SimpleEntry<>(
                    PlotUtils.getOwner(plot),
                    (double) this.pointsProcessor.getLevel(this.plotsPointsStorage.getPoints(plot))));
        });

        return list;
    }
}
