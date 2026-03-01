package cn.stevei5mc.wdpe.cloudresourcepacks;

import cn.stevei5mc.wdpe.cloudresourcepacks.listener.ResourcePacksListener;
import dev.waterdog.waterdogpe.event.defaults.PlayerResourcePackInfoSendEvent;
import dev.waterdog.waterdogpe.event.defaults.ResourcePacksRebuildEvent;
import dev.waterdog.waterdogpe.plugin.Plugin;
import dev.waterdog.waterdogpe.utils.config.Configuration;
import dev.waterdog.waterdogpe.utils.config.YamlConfig;
import lombok.Getter;

public class CloudResourcePacksMain extends Plugin {

    @Getter
    public static CloudResourcePacksMain instance;
    @Getter
    private Configuration config;

    @Override
    public void onEnable() {
        instance = this;
        this.saveResource("config.yml");
        this.config = new YamlConfig(this.getDataFolder() + "/config.yml");
        this.getLogger().warn("§c警告! §c本插件为免费且开源的，如果您付费获取获取的，则有可能被误导");
        this.getLogger().info("§aGITHUB:§b https://github.com/stevei5mc/AutoRestart");
        this.getProxy().getEventManager().subscribe(ResourcePacksRebuildEvent.class, ResourcePacksListener::onResourcePacksRebuildEvent);
        this.getProxy().getEventManager().subscribe(PlayerResourcePackInfoSendEvent.class, ResourcePacksListener::onPlayerResourcePackInfoSendEvent);
    }

    @Override
    public void onDisable() {
        this.getLogger().info("已停止运行，感谢你的使用");
    }
}