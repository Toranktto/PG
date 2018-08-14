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

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import java.util.ArrayList;
import java.util.List;
import pl.toranktto.pg.util.PlotUtils;
import pl.toranktto.pg.storage.PlotsPointsStorage;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public class TopPlotsManager {

    private final List<Plot> places;
    private final PlotsPointsStorage plotsPointStorage;
    private final PlotAPI plotApi;

    public TopPlotsManager(PlotsPointsStorage plotsPointStorage) {
        this.places = new ArrayList<>();
        this.plotsPointStorage = plotsPointStorage;
        this.plotApi = new PlotAPI();
    }

    public synchronized void refresh() {
        this.places.clear();
        this.places.addAll(this.plotApi.getAllPlots());
        
        this.places.sort((Plot o1, Plot o2) -> Long.compare(
                this.plotsPointStorage.getPoints(o2),
                this.plotsPointStorage.getPoints(o1)
        ));
    }

    public synchronized List<Plot> getPlaces(int count) {
        List<Plot> list = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            Plot plot = this.getPlace(i + 1);
            if (plot == null) {
                break;
            }

            list.add(plot);
        }

        return list;
    }

    public synchronized List<Plot> getAllPlaces() {
        return this.getPlaces(this.places.size());
    }

    public synchronized Plot getPlace(int place) {
        if (place < 1) {
            return null;
        }

        if ((place - 1) >= this.places.size()) {
            return null;
        }

        Plot plot = this.places.get(place - 1);

        if (PlotUtils.isFree(plot)) {
            this.places.remove(place - 1);
            return this.getPlace(place);
        }

        return this.places.get(place - 1);
    }
}
