package pers.autumn.mirai.werewolf.plugin.utils;

import net.mamoe.mirai.contact.Group;

/**
 * @author SoundOfAutumn
 * @date 2022/4/26 10:11
 */
public class Util {
    public static boolean isEqualGroups(Group g1, Group g2) {
        if (g1 == null || g2 == null) {
            throw new NullPointerException();
        }
        return g1.getId() == g2.getId();
    }
}
