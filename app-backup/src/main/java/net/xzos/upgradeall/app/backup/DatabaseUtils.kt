package net.xzos.upgradeall.app.backup

import com.google.gson.Gson
import net.xzos.upgradeall.core.database.Converters
import net.xzos.upgradeall.core.database.table.AppEntity
import net.xzos.upgradeall.core.database.table.HubEntity
import net.xzos.upgradeall.core.utils.getOrNull
import org.json.JSONObject
import java.security.MessageDigest

private val converters = Converters()
fun AppEntity.toJson(): JSONObject {
    val converters = Converters()
    return JSONObject(mapOf(
            "name" to name,
            "app_id" to converters.fromMapToString(appId),
            "ignore_version_number" to ignoreVersionNumber,
            "cloud_config" to Gson().toJson(cloudConfig)
    ))
}

fun AppEntity.md5(): String {
    val key = name + appId
    val md = MessageDigest.getInstance("MD5")
    md.update(key.toByteArray())
    return md.digest().toString(Charsets.UTF_8)
}

fun HubEntity.toJson(): JSONObject {
    return JSONObject(mapOf(
            "uuid" to uuid,
            "hub_config" to Gson().toJson(hubConfig),
            "auth" to converters.fromMapToString(auth),
            "ignore_app_id_list" to converters.fromCoroutinesMutableListMapToString(ignoreAppIdList),
    ))
}

fun HubEntity.md5(): String {
    val key = uuid
    val md = MessageDigest.getInstance("MD5")
    md.update(key.toByteArray())
    return md.digest().toString(Charsets.UTF_8)
}

fun parseAppEntityConfig(json: JSONObject): AppEntity {
    return AppEntity(
            0,
            json.getString("name"),
            converters.stringToMap(json.getString("app_id")) as Map<String, String>,
            json.getOrNull("ignore_version_number"),
            cloudConfig = converters.stringToAppConfigGson(json.getString("cloud_config")),
    )
}

fun parseHubEntityConfig(json: JSONObject): HubEntity {
    return HubEntity(
            json.getString("uuid"),
            converters.stringToHubConfigGson(json.getString("hub_config")),
            converters.stringToMap(json.getOrNull("auth")) as MutableMap<String, String>,
            converters.stringToCoroutinesMutableListMap(json.getOrNull("ignore_app_id_list")),
    )
}
