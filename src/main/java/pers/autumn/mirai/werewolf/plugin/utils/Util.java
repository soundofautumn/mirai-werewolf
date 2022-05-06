package pers.autumn.mirai.werewolf.plugin.utils;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.autumn.mirai.werewolf.plugin.JavaPluginMain;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author SoundOfAutumn
 * @date 2022/4/26 10:11
 */
public class Util {
    private final static MiraiLogger LOGGER = JavaPluginMain.INSTANCE.getLogger();

    private Util() {

    }

    public static boolean isEqualGroups(Group g1, Group g2) {
        if (g1 == null || g2 == null) {
            throw new NullPointerException();
        }
        return g1.getId() == g2.getId();
    }

    public static boolean isEqualUsers(User u1, User u2) {
        if (u1 == null || u2 == null) {
            throw new NullPointerException();
        }
        return u1.getId() == u2.getId();
    }

    @Nullable
    public static Member getMemberByName(@NotNull Group group, String name) {
        for (NormalMember member : group.getMembers()) {
            if (member.getNick().equals(name) || member.getNick().equalsIgnoreCase(name)) {
                return member;
            }
        }
        return fuzzyGetMemberByName(group, name);
    }

    @Nullable
    private static Member fuzzyGetMemberByName(@NotNull Group group, String name) {
        LOGGER.debug("模糊查找开始" + name);
        Set<Member> candidates = new HashSet<>();
        int highPossibleCount = 0;
        Member highPossibleMember = null;
        for (NormalMember member : group.getMembers()) {
            final float matchRate = getMatchRate(name, member.getNick());
            LOGGER.debug(member.getNick() + "的匹配率为" + matchRate);
            if (matchRate > 0.2) {
                candidates.add(member);
                if (matchRate > 0.8) {
                    highPossibleMember = member;
                    highPossibleCount++;
                }
            }
        }
        LOGGER.debug("候选列表" + candidates.toString());
        if (candidates.size() == 1) {
            return (Member) candidates.toArray()[0];
        } else if (highPossibleCount == 1) {
            return highPossibleMember;
        } else {
            return null;
        }
    }

    private static float getMatchRate(String origin, String target) {
        if (origin == null || target == null) {
            return 0.0F;
        }
        origin = origin.toLowerCase();
        target = target.toLowerCase();
        LOGGER.debug("获取" + origin + "和" + target + "的匹配率");
        if (origin.equals(target)) {
            return 1.0F;
        }
        final byte[] originBytes = origin.getBytes(StandardCharsets.UTF_8);
        final byte[] targetBytes = target.getBytes(StandardCharsets.UTF_8);
        int originIndex = 0;
        int targetIndex = 0;
        //当target的下标未移动到尽头时
        while (targetIndex != targetBytes.length - 1) {
            //以origin当前的字符遍历target
            for (int i = originIndex; i < originBytes.length; i++) {
                //如果有匹配
                if (originBytes[originIndex] == targetBytes[targetIndex]) {
                    //origin步进一格
                    originIndex++;
                    break;
                }
            }
            //target步进一格
            targetIndex++;
        }
        //返回origin匹配上的百分比
        return (float) (originIndex + 1) / originBytes.length;
    }
}
