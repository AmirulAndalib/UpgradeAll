package net.xzos.upgradeall.core.data

class CoreConfig(
    // 缓存过期时间
    internal val data_expiration_time: Int,
    // 更新服务器地址
    internal val update_server_url: String,
    // 特别的云配置地址
    internal val cloud_rules_hub_url: String?,
    // 应用市场模式下是否忽略系统应用
    internal val applications_ignore_system_app: Boolean,
)