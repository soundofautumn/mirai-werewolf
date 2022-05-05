package pers.autumn.mirai.werewolf.plugin.game;

/**
 * @author SoundOfAutumn
 * @date 2022/5/3 18:08
 */
public class GameException extends RuntimeException{
    public GameException() {
        super();
    }

    public GameException(String message) {
        super(message);
    }
}
