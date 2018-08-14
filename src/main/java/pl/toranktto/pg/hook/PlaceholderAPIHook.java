/*
 * Copyright (c) 2018, Toranktto
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
package pl.toranktto.pg.hook;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import pl.toranktto.pg.PGPlugin;
import pl.toranktto.pg.hook.placeholderapi.PGPlaceholder;
import pl.toranktto.pg.hook.placeholderapi.PGTopPlaceholder;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public final class PlaceholderAPIHook extends PluginHook {

    public static void init() {
        PGPlugin.info("Hooking into MVdWPlaceholderAPI...");
        PGPlugin plugin = PGPlugin.getInstance();

        PlaceholderAPI.registerPlaceholder(plugin, "pg", new PGPlaceholder(plugin));

        for (int i = 1; i <= 10; ++i) {
            PlaceholderAPI.registerPlaceholder(plugin, String.format("pgtop10_%d", i), new PGTopPlaceholder(plugin, i));
        }
    }
}
