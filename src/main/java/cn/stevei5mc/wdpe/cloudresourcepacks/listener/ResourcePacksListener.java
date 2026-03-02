package cn.stevei5mc.wdpe.cloudresourcepacks.listener;

import cn.stevei5mc.wdpe.cloudresourcepacks.CloudResourcePacksMain;
import dev.waterdog.waterdogpe.event.defaults.PlayerResourcePackApplyEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerResourcePackInfoSendEvent;
import dev.waterdog.waterdogpe.event.defaults.ResourcePacksRebuildEvent;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePacksInfoPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResourcePacksListener {

    private static final CloudResourcePacksMain main = CloudResourcePacksMain.getInstance();
    private static final ArrayList<UUID> loadPackId = new ArrayList<>();
    private static final ResourcePacksInfoPacket defaultResourcePacksInfo = new ResourcePacksInfoPacket();
    private static final ResourcePackStackPacket  defaultResourcePacksStack = new ResourcePackStackPacket();
    private static final ResourcePacksInfoPacket testResourcePacksInfo = new ResourcePacksInfoPacket();
    private static final HashMap<String, ResourcePacksInfoPacket> permissionResourcePacksInfoMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void onResourcePacksRebuildEvent(ResourcePacksRebuildEvent event) {
        HashMap<String, ArrayList<String>> permissionPacksMaps = new HashMap<>((Map<? extends String, ? extends ArrayList<String>>) main.getConfig().get("need_permission_packs"));
        HashMap<UUID, ResourcePacksInfoPacket.Entry> loadPacksInfoMap = new HashMap<>();
        HashMap<String, ResourcePackStackPacket.Entry> loadPacksStackMap = new HashMap<>();

        if (main.getProxy().getPackManager().getPacksInfoPacket().getResourcePackInfos().isEmpty() && !main.getProxy().getPackManager().getStackPacket().getResourcePacks().isEmpty()) {
            return;
        }

        main.getProxy().getPackManager().getPacksInfoPacket().getResourcePackInfos().forEach(pack -> {
            loadPacksInfoMap.put(pack.getPackId(), pack);
        });
        main.getProxy().getPackManager().getStackPacket().getResourcePacks().forEach(pack -> {
            loadPacksStackMap.put(pack.getPackId(), pack);
        });


        ArrayList<String> defaultPacks = new ArrayList<>(main.getConfig().getStringList("default_packs"));

        if (!defaultPacks.isEmpty()) {
            defaultPacks.forEach(defaultPackId -> {
                if (loadPacksInfoMap.containsKey(UUID.fromString(defaultPackId))) {
                    if (loadPackId.contains(UUID.fromString(defaultPackId))) {
                        main.getLogger().info("§c找到目标资源包，但在配置中出现了重复，目标资源包ID§f=§e" + defaultPackId);
                    }else {
                        main.getLogger().info("§a寻找到目标资源包，目标资源包ID§f=§e" + defaultPackId);
                        defaultResourcePacksInfo.getResourcePackInfos().add(loadPacksInfoMap.get(UUID.fromString(defaultPackId)));
                        defaultResourcePacksStack.getResourcePacks().add(loadPacksStackMap.get(defaultPackId));
                        loadPackId.add(UUID.fromString(defaultPackId));
                    }
                } else {
                    main.getLogger().info("§c目标资源包无法找到，目标资源包ID§f=§e" + defaultPackId);
                }
            });
        }

        /*permissionPacksMaps.forEach((permission, packsId) -> {
            ResourcePacksInfoPacket permissionPacksInfo = new ResourcePacksInfoPacket();
            packsId.forEach(permissionPackId -> {
                if (loadPacksInfoMap.containsKey(UUID.fromString(permissionPackId))) {
                    if (loadPackId.contains(UUID.fromString(permissionPackId))) {
                        main.getLogger().info("§c找到目标资源包，但在配置中出现了重复，目标资源包ID§f=§e" + permissionPackId);
                    }else {
                        main.getLogger().info("§a寻找到目标资源包，目标资源包ID§f=§e" + permissionPackId);
                        permissionPacksInfo.getResourcePackInfos().add(loadPacksInfoMap.get(UUID.fromString(permissionPackId)));
                        loadPackId.add(UUID.fromString(permissionPackId));
                    }
                } else {
                    main.getLogger().info("§c目标资源包无法找到，目标资源包ID§f=§e" + permissionPackId);
                }
            });
            permissionResourcePacksInfoMap.put(permission, permissionPacksInfo);
        });*/
    }


    public static void onPlayerResourcePackInfoSendEvent(PlayerResourcePackInfoSendEvent event) {
        ResourcePacksInfoPacket sendPacks = new ResourcePacksInfoPacket();
        sendPacks.setWorldTemplateId(event.getPacket().getWorldTemplateId());
        sendPacks.setWorldTemplateVersion(event.getPacket().getWorldTemplateVersion());
        sendPacks.setForcedToAccept(main.getProxy().getConfiguration().isForceServerPacks());

        sendPacks.getResourcePackInfos().addAll(defaultResourcePacksInfo.getResourcePackInfos());

        /*if (!permissionResourcePacksInfoMap.isEmpty()) {
            permissionResourcePacksInfoMap.forEach((permission, packsInfo) -> {
                if (event.getPlayer().hasPermission(permission) && !packsInfo.getResourcePackInfos().isEmpty()) {
                    sendPacks.getResourcePackInfos().addAll(packsInfo.getResourcePackInfos());
                }
            });
        }*/

        event.setPacket(sendPacks);
    }

    public static void onPlayerResourcePackApplyEvent(PlayerResourcePackApplyEvent event) {
        event.getStackPacket().getResourcePacks().clear();
        event.getStackPacket().getResourcePacks().addAll(defaultResourcePacksStack.getResourcePacks());
    }
}