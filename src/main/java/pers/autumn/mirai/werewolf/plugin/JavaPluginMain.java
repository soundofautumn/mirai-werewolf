package pers.autumn.mirai.werewolf.plugin;

import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.utils.MiraiLogger;
import pers.autumn.mirai.werewolf.plugin.command.GameCommand;
import pers.autumn.mirai.werewolf.plugin.game.GameConfig;
import pers.autumn.mirai.werewolf.plugin.game.GameManager;


public final class JavaPluginMain extends JavaPlugin {
    public static final JavaPluginMain INSTANCE = new JavaPluginMain();
    private final static MiraiLogger LOGGER = JavaPluginMain.INSTANCE.getLogger();

    private JavaPluginMain() {
        super(new JvmPluginDescriptionBuilder("pers.autumn.mirai.werewolf.plugin", "0.1.0")
                .info("一个狼人杀游戏插件")
                .build());
    }

    @Override
    public void onEnable() {
        LOGGER.debug("插件初始化开始");
        this.reloadPluginConfig(GameConfig.INSTANCE);

        CommandManager.INSTANCE.registerCommand(GameCommand.CreateGameCommand.INSTANCE, true);
        CommandManager.INSTANCE.registerCommand(GameCommand.StartGameCommand.INSTANCE, true);
        CommandManager.INSTANCE.registerCommand(GameCommand.StopGameCommand.INSTANCE, true);

        LOGGER.debug("插件初始化完成");
        LOGGER.info("狼人杀插件加载成功");
    }

    @Override
    public void onDisable() {
        LOGGER.info("狼人杀插件关闭中...");
        GameManager.shutdownAllGames();
        LOGGER.info("狼人杀插件关闭完成");
    }
}
