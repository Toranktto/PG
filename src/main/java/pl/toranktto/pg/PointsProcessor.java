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

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import pl.toranktto.pg.object.PointsArea;
import pl.toranktto.pg.object.BlockData;
import pl.toranktto.pg.storage.BlockPointsStorage;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public class PointsProcessor {

    private final BlockPointsStorage blockPointsStorage;
    private long levelDivider;

    public PointsProcessor(BlockPointsStorage blockPointsStorage) {
        this.blockPointsStorage = blockPointsStorage;
        this.levelDivider = 0;
    }

    public synchronized void setLevelDivider(long levelDivider) {
        this.levelDivider = levelDivider;
    }

    public long process(PointsArea area) {
        long points = 0;
        CuboidSelection selection = area.getSelection();
        int height = selection.getHeight();

        for (ChunkSnapshot i : area.getChunks()) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    if (!selection.contains(new Location(
                            selection.getWorld(),
                            i.getX() * 16 + x,
                            1,
                            i.getZ() * 16 + z))) {
                        continue;
                    }

                    for (int y = 0; y < height; ++y) {
                        points += this.blockPointsStorage.getPoints(new BlockData(
                                i.getBlockTypeId(x, y, z),
                                i.getBlockData(x, y, z)));
                    }
                }
            }
        }

        return points;
    }

    public long getLevel(long points) {
        try {
            return points / this.levelDivider;
        } catch (ArithmeticException ex) {
            return 0;
        }
    }
}
