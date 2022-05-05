package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 8:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Witch extends AbstractCharacter {
    Camp camp = Camp.God;
    String name = "女巫";
    boolean isPoisonUsed;
    boolean isAntidoteUsed;

    public Witch(Member member, SharedGameData sharedGameData) {
        super(member, sharedGameData);
    }

    @Override
    @SneakyThrows
    public void skillDuringTheNight() {
        sendMessage("请先等待狼人统一意见");
        while (!sharedGameData.isWerewolfConsensus()){
            Thread.sleep(500);
        }
        if (!isAntidoteUsed) {
            sendMessage("你还有一瓶解药没有使用");
            sendMessage("今天死的是" + sharedGameData.getFinalWerewolfTarget().getNick() + "，请问你要救吗");
            if (getResponseTrueOrFalse()) {
                sharedGameData.witchAntidote(sharedGameData.getFinalWerewolfTarget());
            }
        }
        if (!isPoisonUsed) {
            sendMessage("你还有一瓶毒药没有使用，请问你要使用吗");
            if (getResponseTrueOrFalse()) {
                sendMessage("请选择你要使用的对象");
                sharedGameData.witchPoison(getResponseAsMember());
            }
        }

    }
}
