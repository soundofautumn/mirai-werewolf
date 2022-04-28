package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 8:26
 */
@Data
public class Witch implements Character{
    Camp camp = Camp.God;
    String name = "女巫";
    Member member;

    public Witch(Member member) {
        this.member = member;
    }
}
