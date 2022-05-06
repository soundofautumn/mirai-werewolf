package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.EqualsAndHashCode;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 10:26
 */
@EqualsAndHashCode(callSuper = true)
public class Hunter extends AbstractCharacter {

    public Hunter(Member member, SharedGameData sharedGameData) {
        super(Camp.God, "猎人", member, sharedGameData);
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
