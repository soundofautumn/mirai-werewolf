package pers.autumn.mirai.werewolf.plugin.game.werewolf.exception;

import pers.autumn.mirai.werewolf.plugin.game.GameException;

/**
 * @author SoundOfAutumn
 * @date 2022/5/3 18:09
 */
public class WerewolfException extends GameException {
    public WerewolfException() {
        super();
    }

    public WerewolfException(String message) {
        super(message);
    }
}
