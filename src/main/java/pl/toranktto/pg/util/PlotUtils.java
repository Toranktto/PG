/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.toranktto.pg.util;

import com.intellectualcrafters.plot.object.Plot;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 *
 * @author Toranktto (Łukasz Derlatka) <toranktto@gmail.com>
 */
public class PlotUtils {

    public static String getOwner(Plot plot) {
        Set<UUID> owners = plot.getOwners();

        if (owners == null) {
            return null;
        }

        if (owners.isEmpty()) {
            return null;
        }

        return PlayerUtils.getNameFromUUID(new ArrayList<>(owners).get(0));
    }

    public static boolean isFree(Plot plot) {
        Set<UUID> owners = plot.getOwners();

        if (owners == null) {
            return true;
        }

        if (owners.isEmpty()) {
            return true;
        }

        return false;
    }

    public static CuboidSelection getSelection(Plot plot) {
        Location[] corners = LocationUtils.convertLocationArray(plot.getCorners());
        return new CuboidSelection(Bukkit.getWorld(plot.getWorldName()), corners[0], corners[1]);
    }

}
