package pers.autumn.mirai.werewolf.plugin.game;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.utils.MiraiLogger;
import pers.autumn.mirai.werewolf.plugin.JavaPluginMain;

/**
 * @author SoundOfAutumn
 * @date 2022/4/25 14:26
 */
public interface Game {

    public boolean isRunning();

    public void run();

    public void joinGame(Member member);

    public void exitGame(Member member);

    public void shutdown();
}
