package net.xzos.upgradeAll.data.database.manager

import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.xzos.upgradeAll.R
import net.xzos.upgradeAll.application.MyApplication.Companion.context
import net.xzos.upgradeAll.data.json.gson.AppConfig
import net.xzos.upgradeAll.data.json.gson.CloudConfig
import net.xzos.upgradeAll.data.json.gson.HubConfig
import net.xzos.upgradeAll.server.ServerContainer
import net.xzos.upgradeAll.utils.FileUtil
import net.xzos.upgradeAll.utils.GitUrlTranslation
import net.xzos.upgradeAll.utils.network.OkHttpApi

object CloudConfigGetter {
    private const val TAG = "CloudConfigGetter"
    private val LogObjectTag = Pair("Core", TAG)
    private val Log = ServerContainer.Log

    private val okHttpApi = OkHttpApi(LogObjectTag)

    private val cloudHubGitUrlTranslation: GitUrlTranslation
        get() {
            val prefKey = "cloud_rules_hub_url"
            val defaultCloudRulesHubUrl = context.resources.getString(R.string.default_cloud_rules_hub_url)
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            val cloudRulesHubUrl = pref.getString(prefKey, defaultCloudRulesHubUrl)
                    ?: defaultCloudRulesHubUrl
            var cloudHubGitUrlTranslation = GitUrlTranslation(cloudRulesHubUrl)
            if (!cloudHubGitUrlTranslation.testUrl()) {
                cloudHubGitUrlTranslation = GitUrlTranslation(
                        defaultCloudRulesHubUrl.also {
                            pref.edit().putString(prefKey, defaultCloudRulesHubUrl).apply()
                            GlobalScope.launch(Dispatchers.Main) {
                                Toast.makeText(context, context.resources.getString(R.string.auto_fixed_wrong_configuration), Toast.LENGTH_LONG).show()
                            }
                        }
                )
            }
            return cloudHubGitUrlTranslation
        }

    private val hubListRawUrl: String
        get() {
            return cloudHubGitUrlTranslation.getGitRawUrl("rules/hub/")!!
        }

    private val rulesListJsonFileRawUrl: String
        get() {
            return cloudHubGitUrlTranslation.getGitRawUrl("rules/rules_list.json")!!
        }

    private val cloudConfig: CloudConfig?
        get() = renewCloudConfig()

    val appList: List<CloudConfig.AppListBean>?
        get() = cloudConfig?.appList

    val hubList: List<CloudConfig.HubListBean>?
        get() = cloudConfig?.hubList

    private fun renewCloudConfig(): CloudConfig? {
        val jsonText = okHttpApi.getHttpResponse(rulesListJsonFileRawUrl)
        return if (jsonText != null && jsonText.isNotEmpty()) {
            try {
                Gson().fromJson(jsonText, CloudConfig::class.java)
            } catch (e: JsonSyntaxException) {
                Log.e(LogObjectTag, TAG, "refreshData: ERROR_MESSAGE: $e")
                null
            }
        } else null
    }

    fun getAppCloudConfig(appUuid: String?): AppConfig? {
        getAppCloudConfigUrl(appUuid)?.let { appCloudConfigUrl ->
            val appConfigString = okHttpApi.getHttpResponse(
                    appCloudConfigUrl
            )
            return try {
                Gson().fromJson(appConfigString, AppConfig::class.java)
            } catch (e: JsonSyntaxException) {
                null
            }
        } ?: return null
    }

    fun getHubCloudConfig(hubUuid: String?): HubConfig? {
        getHubCloudConfigUrl(hubUuid)?.let { hubCloudConfigUrl ->
            val hubConfigString = okHttpApi.getHttpResponse(
                    hubCloudConfigUrl
            )
            return try {
                Gson().fromJson(hubConfigString, HubConfig::class.java)
            } catch (e: JsonSyntaxException) {
                null
            }
        } ?: return null
    }

    private fun getAppCloudConfigUrl(appUuid: String?): String? {
        appList?.let {
            for (appItem in it) {
                if (appItem.appConfigUuid == appUuid)
                    return cloudHubGitUrlTranslation.getGitRawUrl("rules/apps/${appItem.appConfigFileName}.json")
            }
        }
        return null
    }

    private fun getHubCloudConfigUrl(hubUuid: String?): String? {
        hubList?.let {
            for (hubItem in it) {
                if (hubItem.hubConfigUuid == hubUuid)
                    return cloudHubGitUrlTranslation.getGitRawUrl("rules/hub/${hubItem.hubConfigFileName}.json")
            }
        }
        return null
    }

    private fun getCloudHubConfigJS(filePath: String): String? {
        val hubConfigJSRawUrl = FileUtil.pathTransformRelativeToAbsolute(hubListRawUrl, filePath)
        return okHttpApi.getHttpResponse(hubConfigJSRawUrl)
    }

    /**
     * @return 1 获取 HubConfig 成功, 2 获取 JS 成功, 3 添加数据库成功, -1 获取 HubConfig 失败, -2 解析 JS 失败, -3 添加数据库失败
     */
    fun downloadCloudHubConfig(hubUuid: String?): Int {
        val context = context
        GlobalScope.launch(Dispatchers.Main) { Toast.makeText(context, "开始下载", Toast.LENGTH_LONG).show() }  // 下载
        val cloudHubConfigGson = getHubCloudConfig(hubUuid)
        // TODO: 配置文件地址与仓库地址分离
        return (if (cloudHubConfigGson != null) {
            val cloudHubConfigJS = getCloudHubConfigJS(cloudHubConfigGson.webCrawler?.filePath
                    ?: "")
            if (cloudHubConfigJS != null) {
                // 添加数据库
                if (HubDatabaseManager.addDatabase(cloudHubConfigGson, cloudHubConfigJS)) {
                    3
                } else -3
            } else -2
        } else -1).apply {
            GlobalScope.launch(Dispatchers.Main) {
                when (this@apply) {
                    3 -> Toast.makeText(context, "数据添加成功", Toast.LENGTH_LONG).show()
                    -1 -> Toast.makeText(context, "获取基础配置文件失败", Toast.LENGTH_LONG).show()
                    -2 -> Toast.makeText(context, "获取 JS 代码失败", Toast.LENGTH_LONG).show()
                    -3 -> Toast.makeText(context, "什么？数据库添加失败！", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * @return 1 获取 AppConfig 成功, 2 添加数据库成功, -1 获取 AppConfig 失败, -2 添加数据库失败
     */
    fun downloadCloudAppConfig(appUuid: String?): Int {
        GlobalScope.launch(Dispatchers.Main) { Toast.makeText(context, "开始下载", Toast.LENGTH_LONG).show() }  // 下载
        val cloudHubConfigGson = getAppCloudConfig(appUuid)
        return (if (cloudHubConfigGson != null) {
            // 添加数据库
            if (AppDatabaseManager.setDatabase(0, cloudHubConfigGson)) {
                2
            } else -2
        } else -1).apply {
            GlobalScope.launch(Dispatchers.Main) {
                when (this@apply) {
                    2 -> Toast.makeText(context, "数据添加成功", Toast.LENGTH_LONG).show()
                    -1 -> Toast.makeText(context, "获取基础配置文件失败", Toast.LENGTH_LONG).show()
                    -2 -> Toast.makeText(context, "什么？数据库添加失败！", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}