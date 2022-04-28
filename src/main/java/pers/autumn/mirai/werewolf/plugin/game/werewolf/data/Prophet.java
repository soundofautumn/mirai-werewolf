package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 8:21
 */
@Data
public class Prophet implements Character {
    Camp camp = Camp.God;
    String name = "预言家";
    Member member;

    public Prophet(Member member) {
        this.member = member;
    }

}
