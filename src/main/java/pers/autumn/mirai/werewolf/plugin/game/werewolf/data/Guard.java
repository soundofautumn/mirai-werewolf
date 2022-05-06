package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.EqualsAndHashCode;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 10:28
 */
@EqualsAndHashCode(callSuper = true)
public class Guard extends AbstractCharacter {

    public Guard(Member member, SharedGameData sharedGameData) {
        super(Camp.God, "守卫", member, sharedGameData);
    }

    @Override
    public void skillDuringTheNight() {
        sendMessage("请问你今晚要守卫的人是");
        sharedGameData.guard(getResponseAsMember());
    }
}
