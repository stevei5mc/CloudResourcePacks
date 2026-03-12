package cn.stevei5mc.wdpe.cloudresourcepacks;

import cn.stevei5mc.wdpe.cloudresourcepacks.listener.ResourcePacksListener;
import dev.waterdog.waterdogpe.event.defaults.PlayerResourcePackApplyEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerResourcePackInfoSendEvent;
import dev.waterdog.waterdogpe.event.defaults.ResourcePacksRebuildEvent;
import dev.waterdog.waterdogpe.plugin.Plugin;
import dev.waterdog.waterdogpe.utils.config.YamlConfig;
import lombok.Getter;

public class CloudResourcePacksMain extends Plugin {

    @Getter
    public static CloudResourcePacksMain instance;
    @Getter
    private YamlConfig defaultPacksConfig;
    @Getter
    private YamlConfig needPermissionPacksConfig;

    @Override
    public void onEnable() {
        instance = this;
        this.saveResource("default_pack.yml");
        this.saveResource("need_permission_packs.yml");

        this.defaultPacksConfig = new YamlConfig(this.getDataFolder() + "/default_pack.yml");
        this.needPermissionPacksConfig = new YamlConfig(this.getDataFolder() + "/need_permission_packs.yml");
        this.getLogger().warn("§c警告! §c本插件为免费且开源的，如果您付费获取获取的，则有可能被误导");
        this.getLogger().info("§aGITHUB:§b https://github.com/stevei5mc/CloudResourcePacks");
        this.getProxy().getEventManager().subscribe(ResourcePacksRebuildEvent.class, ResourcePacksListener::onResourcePacksRebuildEvent);
        this.getProxy().getEventManager().subscribe(PlayerResourcePackInfoSendEvent.class, ResourcePacksListener::onPlayerResourcePackInfoSendEvent);
        this.getProxy().getEventManager().subscribe(PlayerResourcePackApplyEvent.class, ResourcePacksListener::onPlayerResourcePackApplyEvent);
    }

    @Override
    public void onDisable() {
        this.getLogger().info("已停止运行，感谢你的使用");
    }
}