package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 8:03
 */
@Data
public class Villager implements Character {
    Camp camp = Camp.CommonPeople;
    String name = "村民";
    Member member;

    public Villager(Member member) {
        this.member = member;
    }

}
