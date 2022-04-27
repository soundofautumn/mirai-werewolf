package pers.autumn.mirai.werewolf.plugin.game;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.utils.MiraiLogger;
import pers.autumn.mirai.werewolf.plugin.JavaPluginMain;

/**
 * @author SoundOfAutumn
 * @date 2022/4/25 14:26
 */
public abstract class Game {

    private Group gameGroup;

    public Game(Group group) {
        gameGroup = group;
    }

    public abstract void run();

    public abstract void prepare(Member member);

    public abstract void exit();
}
