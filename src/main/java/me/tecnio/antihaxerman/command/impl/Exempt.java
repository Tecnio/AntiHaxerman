/*
 *  Copyright (C) 2020 - 2021 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package me.tecnio.antihaxerman.command.impl;

import me.tecnio.antihaxerman.command.AntiHaxermanCommand;
import me.tecnio.antihaxerman.command.CommandInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "exempt", syntax = "<player>", purpose = "Exempts a player from anti-cheat detections.")
public final class Exempt extends AntiHaxermanCommand {
    @Override
    protected boolean handle(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            final Player player = Bukkit.getPlayer(args[1]);

             if (player != null) {
                 final PlayerData playerData = PlayerDataManager.getInstance().getPlayerData(player);

                 if (playerData != null) {
                     if (playerData.isExempt()) {
                         sendMessage(sender, "&8Un-exempted '&c" + player.getName() + "&8' from AntiHaxerman.");
                         playerData.setExempt(false);
                     } else {
                         sendMessage(sender, "&8Exempted '&c" + player.getName() + "&8' from AntiHaxerman.");
                         playerData.setExempt(true);
                     }
                     return true;
                 }
             }
        }
        return false;
    }
}
