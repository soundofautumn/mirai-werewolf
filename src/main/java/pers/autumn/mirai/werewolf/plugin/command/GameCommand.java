package pers.autumn.mirai.werewolf.plugin.command;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.MemberCommandSender;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import org.jetbrains.annotations.NotNull;
import pers.autumn.mirai.werewolf.plugin.JavaPluginMain;
import pers.autumn.mirai.werewolf.plugin.game.GameManager;

/**
 * @author SoundOfAutumn
 * @date 2022/4/26 9:44
 */
public class GameCommand {

    public static final class StartGameCommand extends JSimpleCommand {
        public static final StartGameCommand INSTANCE = new StartGameCommand();

        public StartGameCommand() {
            super(JavaPluginMain.INSTANCE, "开始游戏","startGame");
            setPermission(JavaPluginMain.INSTANCE.getParentPermission());
        }

        @Handler
        public void handle(@NotNull MemberCommandSender sender) {
            sender.sendMessage("开始游戏中...");
            GameManager.getDefaultManager().startGame(sender.getGroup());
        }
    }

    public static final class CreateGameCommand extends JSimpleCommand {
        public static final CreateGameCommand INSTANCE = new CreateGameCommand();

        public CreateGameCommand() {
            super(JavaPluginMain.INSTANCE, "创建游戏","createGame");
            setPermission(JavaPluginMain.INSTANCE.getParentPermission());
        }

        @Handler
        public void handle(@NotNull MemberCommandSender sender) {
            sender.sendMessage("创建游戏中...");
            GameManager.getDefaultManager().createGame(sender.getGroup());
        }
    }
}
