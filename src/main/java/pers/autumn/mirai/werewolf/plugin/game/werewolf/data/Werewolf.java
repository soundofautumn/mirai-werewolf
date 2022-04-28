package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 8:27
 */
@Data
public class Werewolf implements Character{
    Camp camp = Camp.Werewolf;
    String name = "狼人";
    Member member;

    public Werewolf(Member member) {
        this.member = member;
    }
}
