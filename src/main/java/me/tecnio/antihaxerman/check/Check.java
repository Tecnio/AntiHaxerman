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

package me.tecnio.antihaxerman.check;

import lombok.Getter;
import lombok.Setter;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.api.APIManager;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.manager.AlertManager;
import me.tecnio.antihaxerman.manager.PunishmentManager;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Objects;

@Getter
public abstract class Check {

    protected final PlayerData data;

    private int vl;
    private CheckType checkType;
    @Setter
    private int maxVl;
    private double buffer;
    @Setter
    private String punishCommand;

    @Setter
    private boolean debug;

    public Check(final PlayerData data) {
        this.data = data;

        final String packageName = this.getClass().getPackage().getName();

        if (packageName.contains("combat")) {
            checkType = CheckType.COMBAT;
        } else if (packageName.contains("movement")) {
            checkType = CheckType.MOVEMENT;
        } else if (packageName.contains("player")) {
            checkType = CheckType.PLAYER;
        }

        if (!this.getClass().getSimpleName().equalsIgnoreCase("check")) {
            this.maxVl = Config.MAX_VIOLATIONS.get(this.getClass().getSimpleName());
            this.punishCommand = Config.PUNISH_COMMANDS.get(this.getClass().getSimpleName());
        }
    }

    public abstract void handle(final Packet packet);

    public final void fail(final Object info) {
        if (!data.getPlayer().hasPermission("antihaxerman.bypass") || Config.TESTMODE || !Config.BYPASS_OP) {
            if (!data.isExempt()) {
                APIManager.callFlagEvent(this);

                ++vl;
                data.setTotalViolations(data.getTotalViolations() + 1);

                switch (checkType) {
                    case COMBAT:
                        data.setCombatViolations(data.getCombatViolations() + 1);
                        break;
                    case MOVEMENT:
                        data.setMovementViolations(data.getMovementViolations() + 1);
                        break;
                    case PLAYER:
                        data.setPlayerViolations(data.getPlayerViolations() + 1);
                        break;
                }

                AlertManager.handleAlert(this, data, Objects.toString(info));

                if (vl >= maxVl) {
                    PunishmentManager.punish(this, data);
                }
            }
        }
    }

    public final void fail() {
        fail("No information.");
    }

    public final void ban() {
        if (!data.getPlayer().hasPermission("antihaxerman.bypass") || Config.TESTMODE || !Config.BYPASS_OP) {
            if (!data.isExempt()) {
                fail();
                PunishmentManager.punish(this, data);
            }
        }
    }

    public final void kick(final String reason) {
        if (!data.getPlayer().hasPermission("antihaxerman.bypass") || Config.TESTMODE || !Config.BYPASS_OP) {
            if (!data.isExempt()) {
                fail();
                Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> data.getPlayer().kickPlayer(reason));
            }
        }
    }

    protected final boolean isExempt(final ExemptType exemptType) {
        return data.getExemptProcessor().isExempt(exemptType);
    }

    protected final boolean isExempt(final ExemptType... exemptTypes) {
        return data.getExemptProcessor().isExempt(exemptTypes);
    }

    public final long now() {
        return System.currentTimeMillis();
    }

    public final int ticks() { return AntiHaxerman.INSTANCE.getTickManager().getTicks(); }

    public final double increaseBuffer() {
        return buffer = Math.min(10000, buffer + 1);
    }

    public final double increaseBufferBy(final double amount) {
        return buffer = Math.min(10000, buffer + amount);
    }

    public final double decreaseBuffer() {
        return buffer = Math.max(0, buffer - 1);
    }

    public final double decreaseBufferBy(final double amount) {
        return buffer = Math.max(0, buffer - amount);
    }

    public final void resetBuffer() {
        buffer = 0;
    }

    public final void setBuffer(final double amount) {
        buffer = amount;
    }

    public final void multiplyBuffer(final double multiplier) {
        buffer *= multiplier;
    }

    public final int hitTicks() {
        return data.getCombatProcessor().getHitTicks();
    }

    public final boolean digging() {
        return data.getActionProcessor().isDigging();
    }

    public final CheckInfo getCheckInfo() {
        if (this.getClass().isAnnotationPresent(CheckInfo.class)) {
            return this.getClass().getAnnotation(CheckInfo.class);
        } else {
            System.err.println("CheckInfo annotation hasn't been added to the class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }

    public final void debug(final Object object) {
        if (debug) {
            data.getPlayer().sendMessage(ChatColor.RED + "[AHM-Debug] " + ChatColor.GRAY + object);
        }
    }

    public final void broadcast(final Object object) {
        Bukkit.broadcastMessage(ChatColor.RED + "[AHM-Debug] " + ChatColor.GRAY + object);
    }

    enum CheckType {
        COMBAT, MOVEMENT, PLAYER;
    }
}
