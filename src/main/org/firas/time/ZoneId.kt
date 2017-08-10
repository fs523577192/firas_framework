package org.firas.time

/**
 *
 */
abstract class ZoneId {

    companion object {
        val SHORT_IDS: Map<String, String>

        var default: ZoneId = ZoneOffset.of("+8")

        init {
            val map = HashMap<String, String>(64)
            map.put("ACT", "Australia/Darwin")
            map.put("AET", "Australia/Sydney")
            map.put("AGT", "America/Argentina/Buenos_Aires")
            map.put("ART", "Africa/Cairo")
            map.put("AST", "America/Anchorage")
            map.put("BET", "America/Sao_Paulo")
            map.put("BST", "Asia/Dhaka")
            map.put("CAT", "Africa/Harare")
            map.put("CNT", "America/St_Johns")
            map.put("CST", "America/Chicago")
            map.put("CTT", "Asia/Shanghai")
            map.put("EAT", "Africa/Addis_Ababa")
            map.put("ECT", "Europe/Paris")
            map.put("IET", "America/Indiana/Indianapolis")
            map.put("IST", "Asia/Kolkata")
            map.put("JST", "Asia/Tokyo")
            map.put("MIT", "Pacific/Apia")
            map.put("NET", "Asia/Yerevan")
            map.put("NST", "Pacific/Auckland")
            map.put("PLT", "Asia/Karachi")
            map.put("PNT", "America/Phoenix")
            map.put("PRT", "America/Puerto_Rico")
            map.put("PST", "America/Los_Angeles")
            map.put("SST", "Pacific/Guadalcanal")
            map.put("VST", "Asia/Ho_Chi_Minh")
            map.put("EST", "-05:00")
            map.put("MST", "-07:00")
            map.put("HST", "-10:00")
            SHORT_IDS = map
        }
    }

    abstract fun getId(): String
    override fun toString(): String {
        return getId()
    }
    override fun equals(obj: Any?): Boolean {
        if (this == obj) return true
        if (obj is ZoneId) {
            return getId().equals(obj.getId())
        }
        return false
    }
    override fun hashCode(): Int {
        return getId().hashCode()
    }
}