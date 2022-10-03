package sharppvp.plugins.economyplus.events

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable
import sharppvp.plugins.economyplus.EconomyPlus
import sharppvp.plugins.economyplus.EconomyPlus.Companion.instance
import sharppvp.plugins.economyplus.commands.getPlayerData
import sharppvp.plugins.economyplus.commands.msg
import sharppvp.plugins.economyplus.commands.setPlayerData
import sharppvp.plugins.economyplus.db.DataBase
import sharppvp.plugins.economyplus.db.PlayerData
import sharppvp.plugins.economyplus.getCurrentDate
import java.util.*

object PlayerJoinListener:Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val data = player.getPlayerData()
        if(data == null) {
            DataBase().createPlayerDatabase(
                PlayerData(
                    player.uniqueId.toString(),
                    player.displayName,
                    EconomyPlus.instance.config.getLong("initial-balance"),
                    player.getCurrentDate(),
                    player.getCurrentDate()
                )
            )
        }else {
            data.lastLogin = Date()
            player.setPlayerData(data)
        }

        if(instance.config.getBoolean("pay-check")) {
            payCheck(player)
        }
    }

    private fun payCheck(player: Player) {
        object : BukkitRunnable() {
            override fun run() {
                if(!player.isOnline){
                    this.cancel()
                    return
                }

                val data = player.getPlayerData()
                if(data!= null) {
                    data.balance += instance.config.getLong("pay-check-amount")
                    data.lastLogin = Date()
                    player.setPlayerData(data)
                    player.msg(instance.getMessage("paycheck", amount = instance.config.getLong("pay-check-amount")))
                }

            }

        }.runTaskTimer(instance, instance.delay, instance.delay)
    }
}