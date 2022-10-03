package sharppvp.plugins.economyplus.service

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import sharppvp.plugins.economyplus.EconomyPlus
import java.io.*
import java.nio.charset.StandardCharsets

class ConfigManager {
    @Throws(UnsupportedEncodingException::class)
    fun reloadCustomConfig() {
        if (file == null) {
            file = File(plugin.dataFolder, "messages.yml")
        }
        customFile = YamlConfiguration.loadConfiguration(file)

        // Look for defaults in the jar
        val defConfigStream: Reader = InputStreamReader(plugin.getResource("messages.yml"), "UTF8")
        if (defConfigStream != null) {
            val defConfig = YamlConfiguration.loadConfiguration(defConfigStream)
            (customFile as YamlConfiguration?)?.defaults = defConfig
        }
    }

    companion object {
        private var file: File? = null
        private var customFile: FileConfiguration? = null

        private var file2: File? = null
        var locationData: FileConfiguration? = null
            private set

        private var subdir: File? = null
        private val plugin: EconomyPlus = EconomyPlus.instance

        //Finds or generates the custom config file
        fun setup() {
            file = File(plugin.dataFolder, "messages.yml")
            if (!file!!.exists()) {
                try {
                    file!!.createNewFile()
                    plugin.saveResource("messages.yml", true)
                } catch (ignored: IOException) {
                }
            }
            customFile = YamlConfiguration.loadConfiguration(plugin.getResource("messages.yml"))
            subdir = File(plugin.dataFolder.path + System.getProperty("file.separator") + "Data")
            subdir!!.mkdir()

            /*file2 = new File(subdir.getPath() + System.getProperty("file.separator"), "location.yml");

        if(!file2.exists()) {
            try{
                file2.createNewFile();
                plugin.saveResource(file2.getPath(), true);
            }catch (IOException ignored){}
        }

        customFile2 = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("location.yml")));*/
        }

        fun get(): FileConfiguration? {
            return customFile
        }

        fun save() {
            try {
                customFile!!.save(file)
            } catch (e: IOException) {
                println("Couldn't save file")
            }
        }

        fun saveLocationData() {
            try {
                locationData!!.save(file2)
            } catch (e: IOException) {
                println("Couldn't save file")
            }
        }

        fun reloadLocationData() {
            if (file2 == null) {
                file2 = File(subdir!!.path + System.getProperty("file.separator"), "location.yml")
            }
            locationData = YamlConfiguration.loadConfiguration(file)

            // Look for defaults in the jar
            var defConfigStream: Reader? = null
            defConfigStream = try {
                InputStreamReader(plugin.getResource("location.yml"), "UTF8")
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException(e)
            }
            if (defConfigStream != null) {
                val defConfig = YamlConfiguration.loadConfiguration(defConfigStream)
                (locationData as YamlConfiguration?)?.defaults = defConfig
            }
        }

        fun saveDefaultLocation() {
            val stream: Reader
            try {
                stream = InputStreamReader(plugin.getResource("location.yml"), StandardCharsets.UTF_8)
                if (stream != null) {
                    val defConfig = YamlConfiguration.loadConfiguration(stream)
                    locationData = YamlConfiguration.loadConfiguration(file2)
                    (locationData as YamlConfiguration?)?.defaults = defConfig
                    stream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun saveDefaultConfig() {
            val stream: Reader
            try {
                stream = InputStreamReader(plugin.getResource("messages.yml"), StandardCharsets.UTF_8)
                if (stream != null) {
                    val defConfig = YamlConfiguration.loadConfiguration(stream)
                    customFile = YamlConfiguration.loadConfiguration(file)
                    (customFile as YamlConfiguration?)?.defaults = defConfig
                    stream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun reloadMessages() {
            customFile = YamlConfiguration.loadConfiguration(file)
        }
    }
}