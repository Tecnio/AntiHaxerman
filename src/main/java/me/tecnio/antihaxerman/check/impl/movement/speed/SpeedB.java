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

package me.tecnio.antihaxerman.check.impl.movement.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Speed", type = "B", description = "Checks for invalid friction on air.")
public final class SpeedB extends Check {
    public SpeedB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final int airTicks = data.getPositionProcessor().getClientAirTicks();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double predicted = (lastDeltaXZ * 0.91F) + 0.026F;
            final double difference = deltaXZ - predicted;

            final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.VEHICLE, ExemptType.BOAT,
                    ExemptType.UNDERBLOCK, ExemptType.TELEPORT, ExemptType.LIQUID, ExemptType.PISTON,
                    ExemptType.CLIMBABLE, ExemptType.VELOCITY_ON_TICK);
            final boolean invalid = difference > 1E-5 && predicted > 0.075 && airTicks > 2;

            if (invalid && !exempt) {
                if (increaseBuffer() > 5) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.5);
            }
        }
    }
}
