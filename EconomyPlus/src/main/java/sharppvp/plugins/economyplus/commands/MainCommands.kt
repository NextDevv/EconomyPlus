package sharppvp.plugins.economyplus.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import sharppvp.plugins.economyplus.EconomyPlus
import sharppvp.plugins.economyplus.EconomyPlus.Companion.instance
import sharppvp.plugins.economyplus.db.DataBase
import sharppvp.plugins.economyplus.db.PlayerData
import sharppvp.plugins.economyplus.service.Balance
import sharppvp.plugins.economyplus.service.ConfigManager
import sharppvp.plugins.economyplus.translateAlternateColorCodes

object MainCommands : CommandExecutor {
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if(sender !is Player) {
            sender?.sendMessage(instance.getMessage("command-not-player"))
        }

        val player = sender as Player
        if(args?.isNotEmpty() == true) {
            val subcommand = args[0]
            if(subcommand == "pay") {
                if(args.size == 3) {
                    try {
                        val amount = args[2].toInt()
                        val target = args[1].toPlayer()

                        if(target == player) {
                            player.msg(instance.getMessage("pay-failure", player = player))
                            return true
                        }

                        if(target != null) {
                            val data = target.getPlayerData()
                            val senderData = player.getPlayerData()
                            if(data!= null && senderData != null) {
                                data.balance += amount
                                senderData.balance -= amount
                                target.setPlayerData(data)
                                player.setPlayerData(senderData)

                                player.msg(
                                    instance.getMessage(
                                        "pay-success",
                                        amount = amount.toLong(),
                                        target = target
                                    )
                                )

                                target.msg(
                                    instance.getMessage(
                                        "pay-receiver",
                                        player = player,
                                        target = target,
                                        amount = amount.toLong()
                                    )
                                )
                            }
                        }
                    }catch (e: NumberFormatException) {
                        player.msg(instance.getMessage("number-format-exception", player = player))
                    }

                }else {
                    player.msg(
                        instance.getMessage("pay-usage", player = player)
                    )
                }
            }else if (subcommand == "show") {
                if(args.size == 2) {
                    val target = args[1].toPlayer()
                    if(target == player) {
                        player.msg(instance.getMessage("show-self", player = player))
                        return true
                    }

                    if(target != null) {
                        player.msg(instance.getMessage("show-success", player = player, target = target))
                    }
                }else {
                    player.msg(instance.getMessage("show-self", player = player))
                }
            }else if(subcommand == "top") {
                var playerDataArray = ArrayList<PlayerData>()
                playerDataArray = DataBase.getAllPlayers()

                val players = hashMapOf<String, Long>()
                for(data in playerDataArray) {
                    players[data.name] = data.balance
                }

                // Now we sort players from the highest to lowest
                val sortedMap = players.toList()
                    .sortedBy { (key, value) -> value }
                    .reversed()
                    .toMap()
                    .toMutableMap()

                val positionOfPlayer = sortedMap.toList().indexOf(Pair(player.name, player.getBalance())) +1
                if(sortedMap.size > 10) {
                    while(sortedMap.size > 10) {
                        sortedMap.remove(sortedMap.keys.last())
                    }
                }

                player.msg(instance.getMessage("top.title", player = player))
                var count = 1
                for((k,v) in sortedMap) {
                    player.msg(instance.getMessage("top.format", playerName = k, i = count.toString(), amount = v))
                    count++
                }
                player.msg(instance.getMessage("top.footer", player = player))
                player.msg(instance.getMessage("top.player-position", player = player, i = positionOfPlayer.toString()))
            }else if (subcommand == "give") {
                if(args.size == 3) {
                    val target = args[1].toPlayer()
                    if(target == null) {
                        player.msg(instance.getMessage("no-target", targetName = args[1]))
                        return true
                    }

                    try {
                        val amount = args[2].toLong()
                        val targetData = target.getPlayerData()
                        if(targetData == null) {
                            player.msg(instance.getMessage("no-target-data", target = target))
                            return true
                        }

                        targetData.balance += amount
                        target.setPlayerData(targetData)

                        player.msg(instance.getMessage("give-success", target = target, amount = amount))
                        target.msg(instance.getMessage("give-target", player = player, amount = amount))

                    }catch (e: NumberFormatException) {
                        player.msg(instance.getMessage("number-format-exception", player = player))
                    }

                }else {
                    player.msg(instance.getMessage("give-usage", player = player))
                }
            }else if (subcommand == "withdraw") {
                if(args.size == 3) {
                    val target = args[1].toPlayer()
                    if(target == null) {
                        player.msg(instance.getMessage("no-target", targetName = args[1]))
                        return true
                    }

                    try {
                        val amount = args[2].toLong()
                        val targetData = target.getPlayerData()
                        if(targetData == null) {
                            player.msg(instance.getMessage("no-target", target = target))
                            return true
                        }

                        targetData.balance -= amount
                        target.setPlayerData(targetData)

                        player.msg(instance.getMessage("withdraw-success", target = target, amount = amount))
                        target.msg(instance.getMessage("withdraw-target", player = player, amount = amount))

                    }catch (e: NumberFormatException) {
                        player.msg(instance.getMessage("number-format-exception", player = player))
                    }

                }else {
                    player.msg(instance.getMessage("withdraw-usage", player = player))
                }
            }else if (subcommand == "set") {
                if(args.size == 3) {
                    val target = args[1].toPlayer()
                    if(target == null) {
                        player.msg(instance.getMessage("no-target", targetName = args[1]))
                        return true
                    }

                    try {
                        val amount = args[2].toLong()
                        val targetData = target.getPlayerData()
                        if(targetData == null) {
                            player.msg(instance.getMessage("no-target", target = target))
                            return true
                        }

                        targetData.balance = amount
                        target.setPlayerData(targetData)

                        player.msg(instance.getMessage("set-success", target = target, amount = amount))
                        target.msg(instance.getMessage("set-target", player = player, amount = amount))

                    }catch (e: NumberFormatException) {
                        player.msg(instance.getMessage("number-format-exception", player = player))
                    }

                }else {
                    player.msg(instance.getMessage("set-usage", player = player))
                }
            }else if (subcommand == "reload") {
                if(player.isOp) {
                    instance.reloadConfig()
                    ConfigManager.reloadMessages()
                    player.msg(instance.getMessage("reload", player = player))
                }
            }

        }else {
            for(i in 1 .. 10) {
                val message = instance.getMessage("balance.line-$i", player = player)
                if(message != "" && message != " " && message != null) {
                    player.msg(message)
                }
            }

            if(player.isOp) {
                for(i in 1 .. 10) {
                    val message = instance.getMessage("balance.line-$i-op", player = player)
                    if(message != "" && message != " " && message != null) {
                        player.msg(message)
                    }
                }
            }
        }

        return true
    }

}

private fun String.toPlayer(): Player? {
    return Bukkit.getPlayer(this)
}

fun Player.getBalance(): Long {
    return Balance().getBalance(this.uniqueId.toString())?.balance ?: 0
}

fun Player.getPlayerData(): PlayerData? {
    return Balance().getBalance(this.uniqueId.toString())
}

fun Player.setPlayerData(data: PlayerData): Unit {
    Balance().setBalance(data)
}

fun Player.msg(s:Any) {
    this.sendMessage(s.toString().translateAlternateColorCodes())
}