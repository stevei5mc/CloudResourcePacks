package cn.stevei5mc.wdpe.cloudresourcepacks.listener;

import cn.stevei5mc.wdpe.cloudresourcepacks.CloudResourcePacksMain;
import dev.waterdog.waterdogpe.event.defaults.PlayerResourcePackInfoSendEvent;
import dev.waterdog.waterdogpe.event.defaults.ResourcePacksRebuildEvent;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePacksInfoPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResourcePacksListener {

    private static final CloudResourcePacksMain main = CloudResourcePacksMain.getInstance();
    private static final ArrayList<UUID> loadPackId = new ArrayList<>();
    private static final ResourcePacksInfoPacket defaultResourcePacksInfo = new ResourcePacksInfoPacket();
    private static final HashMap<String, ResourcePacksInfoPacket> permissionResourcePacksInfoMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void onResourcePacksRebuildEvent(ResourcePacksRebuildEvent event) {
        HashMap<String, ArrayList<String>> permissionPacksMaps = new HashMap<>((Map<? extends String, ? extends ArrayList<String>>) main.getConfig().get("need_permission_packs"));
        HashMap<UUID, ResourcePacksInfoPacket.Entry> loadPacksMap = new HashMap<>();

        ArrayList<String> defaultPacks = new ArrayList<>(main.getConfig().getStringList("default_packs"));
        if (main.getProxy().getPackManager().getPacksInfoPacket().getResourcePackInfos().isEmpty()) {
            return;
        }

        main.getProxy().getPackManager().getPacksInfoPacket().getResourcePackInfos().forEach(pack -> {
            loadPacksMap.put(pack.getPackId(), pack);
        });

        defaultPacks.forEach(defaultPackId -> {
            if (loadPacksMap.containsKey(UUID.fromString(defaultPackId))) {
                if (loadPackId.contains(UUID.fromString(defaultPackId))) {
                    main.getLogger().info("§c找到目标资源包，但在配置中出现了重复，目标资源包ID§f=§e" + defaultPackId);
                }else {
                    main.getLogger().info("§a寻找到目标资源包，目标资源包ID§f=§e" + defaultPackId);
                    defaultResourcePacksInfo.getResourcePackInfos().add(loadPacksMap.get(UUID.fromString(defaultPackId)));
                    loadPackId.add(UUID.fromString(defaultPackId));
                }
            } else {
                main.getLogger().info("§c目标资源包无法找到，目标资源包ID§f=§e" + defaultPackId);
            }
        });
        
        permissionPacksMaps.forEach((permission, packsId) -> {
            ResourcePacksInfoPacket permissionPacksInfo = new ResourcePacksInfoPacket();
            packsId.forEach(permissionPackId -> {
                if (loadPacksMap.containsKey(UUID.fromString(permissionPackId))) {
                    if (loadPackId.contains(UUID.fromString(permissionPackId))) {
                        main.getLogger().info("§c找到目标资源包，但在配置中出现了重复，目标资源包ID§f=§e" + permissionPackId);
                    }else {
                        main.getLogger().info("§a寻找到目标资源包，目标资源包ID§f=§e" + permissionPackId);
                        permissionPacksInfo.getResourcePackInfos().add(loadPacksMap.get(UUID.fromString(permissionPackId)));
                        loadPackId.add(UUID.fromString(permissionPackId));
                    }
                } else {
                    main.getLogger().info("§c目标资源包无法找到，目标资源包ID§f=§e" + permissionPackId);
                }
            });
            permissionResourcePacksInfoMap.put(permission, permissionPacksInfo);
        });
    }


    public static void onPlayerResourcePackInfoSendEvent(PlayerResourcePackInfoSendEvent event) {
        ResourcePacksInfoPacket packs = new ResourcePacksInfoPacket();
        packs.setWorldTemplateId(event.getPacket().getWorldTemplateId());
        packs.setWorldTemplateVersion(event.getPacket().getWorldTemplateVersion());

        packs.getResourcePackInfos().addAll(defaultResourcePacksInfo.getResourcePackInfos());

        permissionResourcePacksInfoMap.forEach((permission, packsInfo) -> {
            if (event.getPlayer().hasPermission(permission)) {
                packs.getResourcePackInfos().addAll(packsInfo.getResourcePackInfos());
            }
        });

        event.setPacket(packs);
    }
}