package pers.autumn.mirai.werewolf.plugin.game

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

/**
 * @author SoundOfAutumn
 * @date 2022/4/25 18:59
 */
object GameConfig : AutoSavePluginConfig("config") {
    //最大运行游戏数
    val maxRunningGameCount: Int by value(10)
}