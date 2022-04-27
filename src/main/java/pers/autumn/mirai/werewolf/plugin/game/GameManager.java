package pers.autumn.mirai.werewolf.plugin.game;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.autumn.mirai.werewolf.plugin.JavaPluginMain;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author SoundOfAutumn
 * @date 2022/4/25 19:34
 */
public class GameManager {
    private final static MiraiLogger LOGGER = JavaPluginMain.INSTANCE.getLogger();

    //储存所有game manager的实例
    private final static HashMap<Class<? extends Game>, GameManager> gameManagers = new HashMap<>();

    //game manager所储存的类型
    private final Class<? extends Game> gameType;

    //默认的游戏类型
    private static Class<? extends Game> defaultGameType;

    //最大游戏运行总数
    public static final int maxGameCount = GameConfig.INSTANCE.getMaxRunningGameCount();

    //执行游戏的线程池
    private final ExecutorService GAME_RUNNING_SERVICE =
            new ThreadPoolExecutor(1, maxGameCount, 10, TimeUnit.MINUTES, new SynchronousQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    //每个game manager所储存的所有游戏
    private final Map<Group, Game> RUNNING_GAMES = new HashMap<>();

    private GameManager(Class<? extends Game> gameType) {
        this.gameType = gameType;
    }

    public static GameManager getDefaultManager() {
        if (defaultGameType == null) {
            try {
                defaultGameType = (Class<? extends Game>) Class.forName("pers.autumn.mirai.werewolf.plugin.game.WerewolfGame");
            } catch (ClassNotFoundException | ClassCastException e) {
                throw new RuntimeException("cannot find default game");
            }
        }
        return getManager(defaultGameType);

    }

    public static GameManager getManager(Class<? extends Game> gameType) {
        if (gameManagers.containsKey(gameType)) {
            return gameManagers.get(gameType);
        } else {
            final GameManager newManager = new GameManager(gameType);
            if (defaultGameType == null) {
                defaultGameType = gameType;
            }
            gameManagers.put(gameType, newManager);
            return newManager;
        }

    }

    public void createGame(Group group) {
        Game game;
        try {
            game = gameType.getConstructor(Group.class).newInstance(group);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error(e);
            return;
        }
        registerGame(group, game);
        LOGGER.debug("游戏已创建");
    }

    public Game getGameByGroup(Group group) {
        return RUNNING_GAMES.get(group);
    }

    //开始一个游戏
    public void startGame(Group group) {
        if (!isRegistered(group)) {
            group.sendMessage("还未创建游戏，请先创建一个游戏");
            return;
        }
        startGame(group, getGameByGroup(group));
    }

    private void startGame(Group group, @NotNull Game game) {
        try {
            GAME_RUNNING_SERVICE.execute(game::run);
        } catch (RejectedExecutionException e) {
            group.sendMessage("游戏队列已满，请稍后再试吧~");
        }
    }

    public boolean isRegistered(Group group) {
        return RUNNING_GAMES.containsKey(group);
    }

    public void registerGame(Group group, Game game) {
        if (!isRegistered(group)) {
            RUNNING_GAMES.put(group, game);
        } else {
            group.sendMessage("有一个游戏已经开始了");
        }
    }

    public void unregisterGame(Group group) {
        if (isRegistered(group)) {
            RUNNING_GAMES.remove(group);
        }
    }

    public static void shutdownAllGames() {
        gameManagers.forEach((gameType, gameManager) -> gameManager.shutdown());
    }

    public void shutdown() {
        RUNNING_GAMES.forEach((group, game) -> game.exit());
        GAME_RUNNING_SERVICE.shutdown();
    }
}