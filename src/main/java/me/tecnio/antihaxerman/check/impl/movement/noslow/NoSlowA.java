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

package me.tecnio.antihaxerman.check.impl.movement.noslow;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "NoSlow", type = "A", description = "Checks if the player is not slowing down while blocking.")
public final class NoSlowA extends Check {

    public NoSlowA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean sprinting = data.getActionProcessor().isSprinting();
            final boolean blocking = data.getActionProcessor().isBlocking() && data.getPlayer().isBlocking();

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.BOAT, ExemptType.VEHICLE, ExemptType.CHUNK) || data.getPositionProcessor().isInAir();
            final boolean invalid = blocking && sprinting;

            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    data.getPlayer().setItemInHand(data.getPlayer().getItemInHand());
                }
            } else {
                decreaseBufferBy(2.5);
            }
        }
    }
}
