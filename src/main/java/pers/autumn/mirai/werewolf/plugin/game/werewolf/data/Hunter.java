package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 10:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Hunter extends AbstractCharacter {
    Camp camp = Camp.God;
    String name = "猎人";

    public Hunter(Member member, SharedGameData sharedGameData) {
        super(member, sharedGameData);
    }


    @Override
    public void skillAfterDeath() {
        sendMessage("请问你是否要发动技能");
        if (getResponseTrueOrFalse()) {
            sendMessage("请选择你要带走的对象");
            sharedGameData.hunt(getResponseAsMember());
        }


    }
}
