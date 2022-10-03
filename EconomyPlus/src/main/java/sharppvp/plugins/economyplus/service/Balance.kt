package sharppvp.plugins.economyplus.service

import sharppvp.plugins.economyplus.db.DataBase
import sharppvp.plugins.economyplus.db.PlayerData

class Balance {
    fun getBalance(uuid: String): PlayerData? {
        return DataBase.getPlayerFromUUID(uuid)
    }

    fun setBalance(balance: PlayerData) {
        DataBase().updatePlayerDatabase(balance)
    }

    fun addCash(uuid: String, amount: Long) {
        val balance = getBalance(uuid)
        balance!!.balance += amount
        if (balance != null) {
            setBalance(balance)
        }
    }

    fun subtractCash(uuid: String, amount: Long) {
        val balance = getBalance(uuid)
        balance!!.balance -= amount
        if (balance != null) {
            setBalance(balance)
        }
    }

    fun setCash(uuid: String, amount: Long) {
        val balance = getBalance(uuid)
        balance!!.balance = amount
        setBalance(balance)
    }
}