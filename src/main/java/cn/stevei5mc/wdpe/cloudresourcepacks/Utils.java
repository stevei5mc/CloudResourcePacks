package cn.stevei5mc.wdpe.cloudresourcepacks;

import java.util.UUID;

public class Utils {

    public static boolean checkUUID(String uuid) {
        if (uuid == null) {
            return false;
        }

        try {
            UUID.fromString(uuid);
            return true;
        }catch (IllegalArgumentException ignore) {
            CloudResourcePacksMain.getInstance().getLogger().warn(uuid + "不是合法的UUID ！");
        }
        return false;
    }
}
