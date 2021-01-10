package net.xzos.upgradeall.core.module.app

import net.xzos.upgradeall.core.downloader.DownloadInfoItem
import net.xzos.upgradeall.core.downloader.DownloadOb
import net.xzos.upgradeall.core.downloader.Downloader
import net.xzos.upgradeall.core.installer.ApkInstaller
import net.xzos.upgradeall.core.installer.isApkFile
import net.xzos.upgradeall.core.module.Hub
import net.xzos.upgradeall.core.module.network.GrpcApi
import net.xzos.upgradeall.core.module.network.toMap
import net.xzos.upgradeall.core.utils.Func
import net.xzos.upgradeall.core.utils.FuncR

class FileAsset(
        val name: String,
        internal val downloadUrl: String,
        internal val fileType: String,
        internal val assetIndex: Pair<Int, Int>,
        _app: App? = null,
        _hub: Hub? = null,
) {
    private val hub: Hub = _hub!!
    private val app: App = _app!!
    var downloader: Downloader? = null

    suspend fun download(
            taskStartedFun: FuncR<Int>,
            taskStartFailedFun: Func,
            downloadOb: DownloadOb,
    ) {
        val appId = app.appId
        val hubUuid = hub.uuid
        val downloadResponse = GrpcApi.getDownloadInfo(hubUuid, appId, mapOf(), assetIndex)
        var list = downloadResponse?.listList?.map { downloadPackage ->
            val fileName = if (downloadPackage.name.isNotBlank())
                downloadPackage.name
            else {
                name
            }
            DownloadInfoItem(
                    fileName, downloadPackage.url, downloadPackage.headersList?.toMap()
                    ?: mapOf(), downloadPackage.cookiesList?.toMap() ?: mapOf()
            )
        }
        if (list.isNullOrEmpty())
            list = listOf(DownloadInfoItem(name, downloadUrl, mapOf(), mapOf()))
        downloader = Downloader(this).apply {
            for (downloadInfo in list) {
                addTask(
                        downloadInfo.name,
                        downloadInfo.url,
                        downloadInfo.headers,
                        downloadInfo.cookies
                )
            }
        }.also {
            it.start(taskStartedFun, taskStartFailedFun, downloadOb)
        }
    }

    val installable: Boolean
        get() = downloader?.downloadDir?.isApkFile() ?: false

    suspend fun install(failedInstallObserverFun: FuncR<Throwable>, completeInstallFunc: Func) {
        if (installable) {
            downloader?.getFileList()?.run {
                when (this.size) {
                    0 -> return
                    1 -> {
                        ApkInstaller.install(this[0],
                                fun(e) { failedInstallObserverFun.call(e) },
                                fun(_) { completeInstallFunc.call() }
                        )
                    }
                    else -> {
                        ApkInstaller.multipleInstall(
                                downloader!!.downloadDir,
                                fun(e) { failedInstallObserverFun.call(e) },
                                fun(_) { completeInstallFunc.call() }
                        )
                    }
                }
            }
        }
    }
}