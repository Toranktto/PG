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
package pl.toranktto.pg.object;

import com.intellectualcrafters.plot.object.Plot;
import java.util.Objects;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public class PlotData {

    private final String worldName;
    private final int x;
    private final int y;

    public PlotData(String worldName, int x, int y) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
    }

    public PlotData(Plot plot) {
        this.worldName = plot.getWorldName();
        this.x = plot.getId().x;
        this.y = plot.getId().y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public String getWorld() {
        return this.worldName;
    }

    @Override
    public String toString() {
        return String.format("%s;%d;%d", this.worldName, this.x, this.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.worldName, this.x, this.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof PlotData)) {
            return false;
        }

        PlotData plotData = (PlotData) obj;

        return this.getWorld().equals(plotData.getWorld())
                && this.x == plotData.getX()
                && this.y == plotData.getY();
    }
}
