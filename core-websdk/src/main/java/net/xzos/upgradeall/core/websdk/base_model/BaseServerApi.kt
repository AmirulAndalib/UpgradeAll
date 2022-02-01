package net.xzos.upgradeall.core.websdk.base_model

import net.xzos.upgradeall.core.websdk.json.CloudConfigList
import net.xzos.upgradeall.core.websdk.json.DownloadItem
import net.xzos.upgradeall.core.websdk.json.ReleaseGson

internal interface BaseServerApi<E> {
    suspend fun getCloudConfig(host: String): CloudConfigList?

    fun getAppRelease(data: E, callback: (List<ReleaseGson>?) -> Unit)

    fun getAppReleaseList(data: E, callback: (List<ReleaseGson>?) -> Unit)


    suspend fun getDownloadInfo(data: E, assetIndex: Pair<Int, Int>): List<DownloadItem>
}