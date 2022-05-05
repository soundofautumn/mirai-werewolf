package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 8:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Prophet extends AbstractCharacter {
    Camp camp = Camp.God;
    String name = "预言家";

    public Prophet(Member member, SharedGameData sharedGameData) {
        super(member, sharedGameData);
    }

    @Override
    public void skillDuringTheNight() {
        sendMessage("请问你今晚要查验的对象是");
        final String name = sharedGameData.getCharacterMap().get(getResponseAsMember()).getName();
        sendMessage("他的身份是：" + name);
    }
}
