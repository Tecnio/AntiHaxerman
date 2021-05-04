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

package me.tecnio.antihaxerman.check.impl.movement.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", type = "D", description = "Checks for invalid vertical acceleration.")
public final class MotionD extends Check {
    public MotionD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final double modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1;
            final double modifierVelocity = isExempt(ExemptType.VELOCITY) ? data.getVelocityProcessor().getVelocityY() + 0.5 : 0.0;

            final double maximum = 0.6 + modifierJump + modifierVelocity;

            final boolean exempt = isExempt(ExemptType.PISTON, ExemptType.LIQUID,
                    ExemptType.FLYING, ExemptType.WEB, ExemptType.TELEPORT, ExemptType.SLIME, ExemptType.CHUNK, ExemptType.BOAT,

                    ExemptType.VELOCITY_ON_TICK, ExemptType.VELOCITY_RECENTLY
            );
            final boolean invalid = deltaY > maximum;

            if (invalid && !exempt) fail();
        }
    }
}
