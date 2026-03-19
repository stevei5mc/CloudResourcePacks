package cn.stevei5mc.wdpe.cloudresourcepacks;

import dev.waterdog.waterdogpe.event.defaults.PlayerResourcePackApplyEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerResourcePackInfoSendEvent;
import dev.waterdog.waterdogpe.event.defaults.ResourcePacksRebuildEvent;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePacksInfoPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ResourcePacksListener {

    private static final CloudResourcePacksMain main = CloudResourcePacksMain.getInstance();
    private static final ResourcePacksInfoPacket defaultResourcePacksInfo = new ResourcePacksInfoPacket();
    private static final ResourcePackStackPacket  defaultResourcePacksStack = new ResourcePackStackPacket();
    private static final HashMap<String, ResourcePacksInfoPacket> permissionResourcePacksInfoMap = new HashMap<>();
    private static final HashMap<String, ResourcePackStackPacket> permissionResourcePacksStackMap = new HashMap<>();

    public static void onResourcePacksRebuildEvent(ResourcePacksRebuildEvent event) {
        ArrayList<String> permissionsList = new ArrayList<>(main.getNeedPermissionPacksConfig().getAll().keySet());
        HashMap<UUID, ResourcePacksInfoPacket.Entry> loadPacksInfoMap = new HashMap<>();
        HashMap<String, ResourcePackStackPacket.Entry> loadPacksStackMap = new HashMap<>();
        ArrayList<UUID> loadPackId = new ArrayList<>();


        if (main.getProxy().getPackManager().getPacksInfoPacket().getResourcePackInfos().isEmpty() && main.getProxy().getPackManager().getStackPacket().getResourcePacks().isEmpty()) {
            return;
        }

        main.getProxy().getPackManager().getPacksInfoPacket().getResourcePackInfos().forEach(pack -> loadPacksInfoMap.put(pack.getPackId(), pack));
        main.getProxy().getPackManager().getStackPacket().getResourcePacks().forEach(pack -> loadPacksStackMap.put(pack.getPackId(), pack));

        ArrayList<String> defaultPacks = new ArrayList<>(main.getDefaultPacksConfig().getStringList("list", new ArrayList<>()));

        // 加载默认的资源包
        if (!defaultPacks.isEmpty()) {
            defaultPacks.forEach(defaultPackId -> {
                if (Utils.checkUUID(defaultPackId) && loadPacksInfoMap.containsKey(UUID.fromString(defaultPackId))) {
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

        // 加载需要权限的资源包
        for (String permission: permissionsList) {
            if (!main.getNeedPermissionPacksConfig().exists(permission)) {
                continue;
            }
            ArrayList<String> packsIdList = new ArrayList<>(main.getNeedPermissionPacksConfig().getStringList(permission, new ArrayList<>()));
            ResourcePacksInfoPacket permissionPacksInfo = new ResourcePacksInfoPacket();
            ResourcePackStackPacket permissionPacksStack = new ResourcePackStackPacket();
            if (!packsIdList.isEmpty()) {
                packsIdList.forEach(permissionPackId -> {
                    if (Utils.checkUUID(permissionPackId) && loadPacksInfoMap.containsKey(UUID.fromString(permissionPackId))) {
                        if (loadPackId.contains(UUID.fromString(permissionPackId))) {
                            main.getLogger().info("§c找到目标资源包，但在配置中出现了重复，目标资源包ID§f=§e" + permissionPackId + "§c，目标资源包所需权限§f=§e" + permission);
                        }else {
                            main.getLogger().info("§a寻找到目标资源包，目标资源包ID§f=§e" + permissionPackId + "§a，目标资源包所需权限§f=§e" + permission);
                            permissionPacksInfo.getResourcePackInfos().add(loadPacksInfoMap.get(UUID.fromString(permissionPackId)));
                            permissionPacksStack.getResourcePacks().add(loadPacksStackMap.get(permissionPackId));
                            loadPackId.add(UUID.fromString(permissionPackId));
                        }
                    } else {
                        main.getLogger().info("§c目标资源包无法找到，目标资源包ID§f=§e" + permissionPackId + "§c，目标资源包所需权限§f=§e" + permission);
                    }
                });
            }
            permissionResourcePacksInfoMap.put(permission, permissionPacksInfo);
            permissionResourcePacksStackMap.put(permission, permissionPacksStack);
        }
    }

    public static void onPlayerResourcePackInfoSendEvent(PlayerResourcePackInfoSendEvent event) {
        if (main.getProxy().getPackManager().getPacks().isEmpty()) {
            return;
        }
        ResourcePacksInfoPacket infoPacket = new ResourcePacksInfoPacket();
        infoPacket.setWorldTemplateId(event.getPacket().getWorldTemplateId());
        infoPacket.setWorldTemplateVersion(event.getPacket().getWorldTemplateVersion());
        infoPacket.setForcedToAccept(event.getPacket().isForcedToAccept());
        infoPacket.setHasAddonPacks(event.getPacket().isHasAddonPacks());
        infoPacket.setScriptingEnabled(event.getPacket().isScriptingEnabled());
        infoPacket.setVibrantVisualsForceDisabled(event.getPacket().isVibrantVisualsForceDisabled());
        infoPacket.setForcingServerPacksEnabled(event.getPacket().isForcingServerPacksEnabled());

        if (!defaultResourcePacksInfo.getResourcePackInfos().isEmpty()) {
            infoPacket.getResourcePackInfos().addAll(defaultResourcePacksInfo.getResourcePackInfos());
        }

        if (!permissionResourcePacksInfoMap.isEmpty()) {
            permissionResourcePacksInfoMap.forEach((permission, packsInfo) -> {
                if (event.getPlayer().hasPermission(permission) && !packsInfo.getResourcePackInfos().isEmpty()) {
                    infoPacket.getResourcePackInfos().addAll(packsInfo.getResourcePackInfos());
                }
            });
        }

        event.setPacket(infoPacket);
    }

    public static void onPlayerResourcePackApplyEvent(PlayerResourcePackApplyEvent event) {
        if (main.getProxy().getPackManager().getPacks().isEmpty()) {
            return;
        }

        ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
        stackPacket.setForcedToAccept(event.getStackPacket().isForcedToAccept());
        stackPacket.setGameVersion(event.getStackPacket().getGameVersion());
        stackPacket.getExperiments().addAll(event.getStackPacket().getExperiments());
        stackPacket.setExperimentsPreviouslyToggled(event.getStackPacket().isExperimentsPreviouslyToggled());
        stackPacket.setHasEditorPacks(event.getStackPacket().isHasEditorPacks());

        if (!defaultResourcePacksStack.getResourcePacks().isEmpty()) {
            stackPacket.getResourcePacks().addAll(defaultResourcePacksStack.getResourcePacks());
        }

        if (!permissionResourcePacksStackMap.isEmpty()) {
            permissionResourcePacksStackMap.forEach((permission, packsStack) -> {
                if (event.getPlayer().hasPermission(permission) && !packsStack.getResourcePacks().isEmpty()) {
                    stackPacket.getResourcePacks().addAll(packsStack.getResourcePacks());
                }
            });
        }

        event.setStackPacket(stackPacket);
    }
}