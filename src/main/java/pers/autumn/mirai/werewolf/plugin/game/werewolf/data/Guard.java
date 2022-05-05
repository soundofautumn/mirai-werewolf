package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 10:28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Guard extends AbstractCharacter{
    Camp camp = Camp.God;
    String name = "守卫";

    public Guard(Member member, SharedGameData sharedGameData) {
        super(member, sharedGameData);
    }

    @Override
    public void skillDuringTheNight() {
        sendMessage("请问你今晚要守卫的人是");
        sharedGameData.guard(getResponseAsMember());
    }
}
