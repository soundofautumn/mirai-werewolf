package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 10:28
 */
@Data
public class Guard implements Character{
    Camp camp = Camp.God;
    String name = "守卫";
    Member member;

    public Guard(Member member) {
        this.member = member;
    }
}
