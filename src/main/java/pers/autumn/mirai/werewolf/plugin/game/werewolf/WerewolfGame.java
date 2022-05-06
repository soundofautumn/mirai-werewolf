package pers.autumn.mirai.werewolf.plugin.game.werewolf;

import lombok.SneakyThrows;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pers.autumn.mirai.werewolf.plugin.game.Game;
import pers.autumn.mirai.werewolf.plugin.game.GameManager;
import pers.autumn.mirai.werewolf.plugin.game.werewolf.data.*;
import pers.autumn.mirai.werewolf.plugin.game.werewolf.data.Character;
import pers.autumn.mirai.werewolf.plugin.utils.Util;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author SoundOfAutumn
 * @date 2022/4/27 10:14
 */
public class WerewolfGame implements Game {

    private final MiraiLogger logger = GameManager.LOGGER;

    //参加游戏的玩家
    private final List<Member> playerList = new LinkedList<>();

    //总玩家数
    private int totalPlayer;

    //游戏是否在运行
    private boolean running;

    //游戏所运行的群组
    private final Group currentGroup;

    //机器人是否有管理员权限
    private final boolean isAdministrator;

    //最大玩家数（暂定）
    private final static int maxPlayers = 12;

    //最小玩家数（暂定）
    private final static int minPlayers = 1;

    //游戏准备阶段监听
    private final Listener<GroupMessageEvent> gamePrepareStageListener;

    //获得此群组的消息通道
    private final EventChannel<Event> thisGroupChannel;

    //存活名单
    private final Set<Member> aliveSet = new HashSet<>();

    //每天晚上的死亡名单
    private final Set<Member> dailyDeathSet = new HashSet<>();

    //投票时的计分板
    private final Map<Member, Float> voteScoreboard = new HashMap<>();

    //储存每天已投票的玩家
    private final Set<Member> dailyVotedSet = new HashSet<>();

    //共享数据
    private final SharedGameData sharedGameData;

    //已投票数
    private int voteCount;

    //玩家和其身份的映射
    private final Map<Member, Character> characterMap = new HashMap<>();

    public WerewolfGame(Group group) {
        logger.debug("狼人杀游戏初始化开始");
        currentGroup = group;
        isAdministrator = currentGroup.getBotPermission().getLevel() > 0;
        thisGroupChannel = GlobalEventChannel.INSTANCE
                .filter(event -> event instanceof GroupMessageEvent && Util.isEqualGroups(((GroupMessageEvent) event).getGroup(), currentGroup));
        sharedGameData = new SharedGameData(characterMap, dailyDeathSet, aliveSet, thisGroupChannel);
        //添加准备阶段的指令
        gamePrepareStageListener = thisGroupChannel.subscribeAlways(GroupMessageEvent.class, event -> {
            if (event.getMessage().contentToString().equals("加入游戏")) {
                this.joinGame(event.getSender());
            }
            if (event.getMessage().contentToString().equals("离开游戏")) {
                this.exitGame(event.getSender());
            }
        });
        sendMessage("游戏准备阶段，输入 加入游戏 离开游戏 来参与本轮游戏");
        logger.debug("狼人杀游戏初始化结束");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    @SneakyThrows
    public void run() {
        logger.debug("狼人杀游戏开始运行");
        if (totalPlayer < minPlayers) {
            sendMessage("人数未满，不能开始游戏");
            return;
        }
        running = true;
        gamePrepareStageListener.complete();
        sendMessage("游戏已开始，正在发放身份牌");
        prepareForGame();
        sendMessage("游戏将在10秒后开始");
        //等待10秒后开始游戏
        Thread.sleep(10 * 1000);
        //主逻辑
        while (running) {
            prepareForGameLoop();
            /* --------------------------------夜晚开始---------------------------------------------- */
            sendMessage("天黑请闭眼");
            if (isAdministrator) {
                currentGroup.getSettings().setMuteAll(true);
            }
            final ExecutorService service =
                    new ThreadPoolExecutor(1, maxPlayers, 10, TimeUnit.MINUTES, new SynchronousQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
            characterMap.forEach((member, character) -> service.execute(character::skillDuringTheNight));
            service.shutdown();
            while (!service.awaitTermination(30,TimeUnit.SECONDS));
            sharedGameData.calculate();
            /* --------------------------------白天开始---------------------------------------------- */
            sendMessage("天亮了");
            if (!dailyDeathSet.isEmpty()) {
                dailyDeathSet.forEach(this::out);
                //生成死亡信息
                final MessageChain deathMessage = new PlainText("").plus("昨天晚上，");
                dailyDeathSet.forEach(member -> deathMessage.plus(getAtMessage(member)).plus(" "));
                deathMessage.plus("死了");
                sendMessage(deathMessage);
                dailyDeathSet.forEach(member -> characterMap.get(member).skillAfterDeath());
            } else {
                sendMessage("昨天是平安夜");
            }
            sharedGameData.clear();
            if (isAdministrator) {
                currentGroup.getSettings().setMuteAll(false);
            }
            /* --------------------------------白天讨论时间开始------------------------------------------ */
            sendMessage("讨论开始，输入 投票@玩家 来指定你要投票的对象，全体投票完成后自动进入下一轮");

            while (true) {
                //添加投票监听
                final Listener<GroupMessageEvent> voteListener = thisGroupChannel.subscribeAlways(GroupMessageEvent.class, event -> {
                    final String content = event.getMessage().contentToString();
                    final Member sender = event.getSender();
                    //有效投票格式为 投票@玩家
                    if (content.startsWith("投票@") && event.getMessage().get(2) instanceof At) {
                        final At at = (At) event.getMessage().get(2);
                        final NormalMember targetMember = currentGroup.get(at.getTarget());
                        if (!aliveSet.contains(sender)) {
                            sendAtMessage("你已经死了，不能投票", sender);
                        } else if (dailyVotedSet.contains(sender)) {
                            sendAtMessage("你已经投过票了", sender);
                        } else if (!playerList.contains(targetMember)) {
                            sendAtMessage("投票失败，请确定你选择的是已参加游戏群成员", sender);
                        } else {
                            vote(targetMember, characterMap.get(sender).getVoteWeight());
                            dailyVotedSet.add(sender);
                            voteCount++;
                            sendAtMessage("投票成功", sender);
                        }

                    }
                });
                //等待投票完成
                while (voteCount < totalPlayer) {
                    Thread.sleep(500);
                    if (!running) {
                        break;
                    }
                }
                voteListener.complete();
                sendMessage("投票结束");
                //获取投票数最高的玩家
                final Optional<Map.Entry<Member, Float>> max = voteScoreboard.entrySet().stream().max(Map.Entry.comparingByValue());
                if (max.isPresent()) {
                    Member highestScoreMember = max.get().getKey();
                    Float highestScore = max.get().getValue();
                    sendMessage(highestScoreMember.getNick() + "被票出去了，总票数为" + highestScore);
                    characterMap.get(highestScoreMember).skillAfterDeath();
                    out(highestScoreMember);
                    break;
                } else {
                    sendMessage("出现平局，重新投票");
                    sendMessage("输入 投票@玩家 来指定你要投票的对象");
                }
            }
            if (checkGameOver()) {
                sendMessage("游戏结束");
                break;
            }
            sendMessage("投票环节结束，将在10秒后进入下一轮");
            //等待10秒后开始下一轮
            Thread.sleep(10 * 1000);
        }
        this.shutdown();
    }

    //分配玩家的身份
    private void prepareForGame() {
        logger.debug("狼人杀游戏开始分配阵营");
        //确定每种阵营的人数
        //尽量平均，若不是三的倍数，则先加平民，再加神职
        int commonPeopleNums = totalPlayer / 3;
        int godNums = commonPeopleNums;
        int werewolfNums = commonPeopleNums;
        if (totalPlayer % 3 == 1) {
            commonPeopleNums++;
        } else if (totalPlayer % 3 == 2) {
            godNums++;
            commonPeopleNums++;
        }
        //打乱玩家顺序，保证随机性
        Collections.shuffle(playerList);
        //开始分配阵营

        //TODO 初始版本，角色较少，等后续优化
        for (Member member : playerList) {
            //平民阵营
            if (commonPeopleNums > 0) {
                characterMap.put(member, new Villager(member, sharedGameData));
                commonPeopleNums--;
                continue;
            }
            //狼人阵营
            if (werewolfNums > 0) {
                characterMap.put(member, new Werewolf(member, sharedGameData));
                werewolfNums--;
                continue;
            }
            //神职阵营
            if (godNums > 0) {
                switch (godNums) {
                    case 1:
                        characterMap.put(member, new Prophet(member, sharedGameData));
                        godNums--;
                        break;
                    case 2:
                        characterMap.put(member, new Witch(member, sharedGameData));
                        godNums--;
                        break;
                    case 3:
                        characterMap.put(member, new Guard(member, sharedGameData));
                        godNums--;
                        break;
                        //TODO 猎人职业未完成
//                    case 4:
//                        characterMap.put(member, new Hunter(member, sharedGameData));
//                        godNums--;
//                        break;
                }
            }
        }
        sharedGameData.setWerewolfCount(werewolfNums);
        logger.debug("狼人杀游戏分配阵营结束");
        sendMessage("角色抽取完成，已私聊发送各位身份，请注意查收");
        //向所有玩家发送身份信息
        characterMap.forEach((member, character) -> member.sendMessage("你的身份是：" + character.getName()));
        aliveSet.addAll(playerList);
    }

    private void prepareForGameLoop() {
        dailyDeathSet.clear();
        voteScoreboard.clear();
        sharedGameData.clear();
        voteCount = 0;
    }

    private void vote(Member target, float weight) {
        logger.debug("投票给" + target + "，票数为" + weight);
        if (voteScoreboard.containsKey(target)) {
            voteScoreboard.merge(target, weight, Float::sum);
        } else {
            voteScoreboard.put(target, weight);
        }
    }

    private void out(Member member) {
        aliveSet.remove(member);
    }

    //检查是否已经满足胜利条件
    private boolean checkGameOver() {
        boolean commonPeopleAlive = false;
        boolean godAlive = false;
        boolean werewolfAlive = false;
        for (Member member : aliveSet) {
            switch (characterMap.get(member).getCamp()) {
                case CommonPeople:
                    commonPeopleAlive = true;
                    break;
                case God:
                    godAlive = true;
                    break;
                case Werewolf:
                    werewolfAlive = true;
                    break;
            }
        }
        if (commonPeopleAlive && !godAlive && !werewolfAlive) {
            sendMessage("平民获胜");
            return true;
        } else if (!commonPeopleAlive && godAlive && !werewolfAlive) {
            sendMessage("神职获胜");
            return true;
        } else if (!commonPeopleAlive && !godAlive && werewolfAlive) {
            sendMessage("狼人获胜");
            return true;
        } else if (!commonPeopleAlive && !godAlive && !werewolfAlive) {
            sendMessage("平局");
            return true;
        }
        return false;
    }

    private void sendMessage(MessageChain messageChain) {
        currentGroup.sendMessage(messageChain);
    }

    private void sendMessage(String message) {
        currentGroup.sendMessage(message);
    }

    @NotNull
    @Contract("_ -> new")
    private Message getAtMessage(@NotNull Member target) {
        return new At(target.getId());
    }

    private void sendAtMessage(String message, @NotNull Member target) {
        currentGroup.sendMessage(getAtMessage(target).plus(message));
    }


    @Override
    public void joinGame(Member member) {
        if (totalPlayer < maxPlayers) {
            if (!playerList.contains(member) && !running) {
                playerList.add(member);
                totalPlayer++;
                sendAtMessage("成功加入游戏", member);
            } else {
                sendAtMessage("你已在游戏中", member);
            }
        } else {
            sendMessage("人数已满");
        }


    }

    @Override
    public void exitGame(Member member) {
        if (playerList.contains(member) && !running) {
            playerList.remove(member);
            totalPlayer--;
            sendAtMessage("成功离开游戏", member);
        }
    }

    @Override
    public void shutdown() {
        running = false;
        totalPlayer = 0;
        dailyDeathSet.clear();
        dailyVotedSet.clear();
        aliveSet.clear();
        characterMap.clear();
        voteScoreboard.clear();
        voteCount = 0;
        playerList.clear();
        sharedGameData.clear();
        sendMessage("游戏已退出");
    }
}
