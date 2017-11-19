package me.crypnotic.combattimer.api;

import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.crypnotic.combattimer.CombatTimer;

@AllArgsConstructor
public class CombatPlayer {

    @Getter
    private UUID uuid;
    @Getter
    @Setter
    private Long combatTime;
    @Getter
    @Setter
    private boolean allowedFlight;

    public Player getHandle() {
        return CombatTimer.getInstance().getServer().getPlayer(uuid);
    }
}
