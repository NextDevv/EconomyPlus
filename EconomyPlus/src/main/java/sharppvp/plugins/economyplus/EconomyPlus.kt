package sharppvp.plugins.economyplus

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import sharppvp.plugins.economyplus.commands.MainCommands
import sharppvp.plugins.economyplus.commands.getBalance
import sharppvp.plugins.economyplus.db.DataBase
import sharppvp.plugins.economyplus.events.PlayerJoinListener
import sharppvp.plugins.economyplus.service.ConfigManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.properties.Delegates

class EconomyPlus : JavaPlugin() {
    companion object {
        lateinit var instance:EconomyPlus
    }

    init {
        instance = this
    }

    var prefix:String by Delegates.notNull<String>()
    var color:String by Delegates.notNull<String>()

    var delay:Long by Delegates.notNull<Long>()

    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        ConfigManager.setup()
        ConfigManager.saveDefaultConfig()

        DataBase.InitilizeDatabase()

        prefix = config.getString("prefix").translateAlternateColorCodes()
        color = config.getString("color-prefix").translateAlternateColorCodes()

        delay = config.getLong("pay-check-interval") * 60
        delay *= 20

        Bukkit.getPluginManager().registerEvents(PlayerJoinListener, this)

        getCommand("balance").executor = MainCommands

        val console = Bukkit.getServer().consoleSender
        console.msg("${prefix}Is enabled!".translateAlternateColorCodes())
        console.msg("${prefix}Version: ${this.description.version}")
        console.msg("${prefix}Config loaded: ${if(config == null) "false" else "true"}")
        console.msg("${prefix}Messages loaded: ${if(ConfigManager.get() == null) "false" else "true"}")
    }

    fun getMessage(path: String,
                   player: Player?=null,
                   playerName: String="",
                   target: Player?=null,
                   targetName: String="",
                   msg: String="",
                   command: String="",
                   amount: Long = 0,
                   i: String = ""
    ): String {
        val config = ConfigManager.get()
        var message = config?.getString(path) ?: return ""

        // Getting the current date
        val time = SimpleDateFormat("HH:mm:ss")
        val date = Date()
        val currentTime:String = time.format(date).toString()

        val keyWords:HashMap<String, Any> = hashMapOf(
            "%prefix%" to prefix, "%player%" to (player?.name ?: playerName),
            "%command%" to command, "%color-prefix%" to color, "%target%" to (target?.name ?: targetName),
            "%message%" to msg, "%time%" to currentTime, "%balance%" to (player?.getBalance() ?: "0"),
            "%balance-target%" to (target?.getBalance() ?: "0"), "%amount%" to (amount), "%i%" to i
        )

        keyWords.map {(k,v) ->
            message = message.replace(k, v.toString())
        }

        return message.translateAlternateColorCodes()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}

private fun ConsoleCommandSender.msg(s: String) {
    sendMessage(s)
}

fun String.translateAlternateColorCodes(code: Char = '&'):String {
    return ChatColor.translateAlternateColorCodes(code, this)
}

fun Player.getCurrentDate(): Date {
    return Date()
}
