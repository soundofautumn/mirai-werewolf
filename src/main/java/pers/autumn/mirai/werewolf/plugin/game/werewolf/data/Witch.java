package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import net.mamoe.mirai.contact.Member;

/**
 * @author SoundOfAutumn
 * @date 2022/4/28 8:26
 */
@EqualsAndHashCode(callSuper = true)
public class Witch extends AbstractCharacter {
    boolean isPoisonUsed;
    boolean isAntidoteUsed;

    public Witch(Member member, SharedGameData sharedGameData) {
        super(Camp.God, "女巫", member, sharedGameData);
    }

    @Override
    @SneakyThrows
    public void skillDuringTheNight() {
        sendMessage("请先等待狼人统一意见");
        while (!sharedGameData.isWerewolfConsensus()) {
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
