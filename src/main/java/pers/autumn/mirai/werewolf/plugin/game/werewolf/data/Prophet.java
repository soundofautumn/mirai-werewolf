package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.EqualsAndHashCode;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 8:21
 */
@EqualsAndHashCode(callSuper = true)
public class Prophet extends AbstractCharacter {

    public Prophet(Member member, SharedGameData sharedGameData) {
        super(Camp.God, "预言家", member, sharedGameData);
    }

    @Override
    public void skillDuringTheNight() {
        sendMessage("请问你今晚要查验的对象是");
        switch (sharedGameData.getCharacterMap().get(getResponseAsMember()).getCamp()) {
            case God:
            case CommonPeople:
                sendMessage("他的身份是：好人");
                break;
            case Werewolf:
                sendMessage("他的身份是：坏人");
                break;
        }
    }
}
