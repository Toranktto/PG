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

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import java.util.Collections;
import java.util.Set;
import org.bukkit.ChunkSnapshot;
import pl.toranktto.pg.util.ChunkUtils;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public class PointsArea {

    private final Set<ChunkSnapshot> chunks;
    private final CuboidSelection selection;

    public PointsArea(CuboidSelection selection) {
        this.selection = selection;
        this.chunks = Collections.unmodifiableSet(ChunkUtils.getChunks(this.selection));
    }
    
    public CuboidSelection getSelection() {
        return this.selection;
    }

    public Set<ChunkSnapshot> getChunks() {
        return this.chunks;
    }
}
