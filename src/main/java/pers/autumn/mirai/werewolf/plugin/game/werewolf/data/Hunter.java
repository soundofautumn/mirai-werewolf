package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 10:26
 */
@Data
public class Hunter implements Character{
    Camp camp = Camp.God;
    String name = "猎人";
    Member member;

    public Hunter(Member member) {
        this.member = member;
    }


}
