package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 8:03
 */
@EqualsAndHashCode(callSuper = true)
public class Villager extends AbstractCharacter {

    public Villager(Member member, SharedGameData sharedGameData) {
        super(Camp.CommonPeople, "村民", member, sharedGameData);
    }
}
