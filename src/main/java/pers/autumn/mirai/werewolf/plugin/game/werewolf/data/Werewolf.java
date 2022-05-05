package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 8:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Werewolf extends AbstractCharacter {
    Camp camp = Camp.Werewolf;
    String name = "狼人";

    public Werewolf(Member member, SharedGameData sharedGameData) {
        super(member, sharedGameData);
        sharedGameData.getWerewolvesSet().add(this);
    }

    @Override
    public void skillDuringTheNight() {
        sendMessage("请选择你们今晚要猎杀的对象");
        while (true){
            sharedGameData.werewolfVotesTarget(this, getResponseAsMember());
            sharedGameData.broadcastWerewolfInformation();
            if (!sharedGameData.isWerewolfConsensus()) {
                sendMessage("请统一意见");
            }else {
                break;
            }
        }

    }
}
