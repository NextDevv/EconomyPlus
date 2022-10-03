package sharppvp.plugins.economyplus.db

import java.sql.Date

data class PlayerData(val uuid: String, var name: String, var balance: Long, var lastLogin: java.util.Date, var lastLogout: java.util.Date)
