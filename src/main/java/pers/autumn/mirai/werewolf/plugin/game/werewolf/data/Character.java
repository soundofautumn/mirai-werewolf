package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 8:14
 */
public interface Character extends WerewolfGameData {
    Camp getCamp();

    String getName();

    Member getMember();

    default float getVoteWeight() {
        return 1f;
    }

    default void skillDuringTheNight() {
    }

    default void skillDuringTheDay() {
    }

    default void skillAfterDeath() {
    }
}
