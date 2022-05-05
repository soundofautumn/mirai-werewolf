package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import pers.autumn.mirai.werewolf.plugin.game.werewolf.exception.IllegalCallException;

import java.util.*;

/**
 * @author SoundOfAutumn
 * @date 2022/4/29 8:13
 */
@Data
public class SharedGameData implements WerewolfGameData {
    private final Map<Member, Character> characterMap;
    private final Set<Member> dailyDeathSet;
    private final Set<Member> aliveSet;
    private Member guarded;
    private Member witchPoison;
    private Member witchAntidote;
    private Member hunted;
    private final EventChannel<Event> thisGroupChannel;
    private int werewolfCount;
    private int werewolfVotedCount;
    private final Map<Werewolf, Member> werewolfVotingMap = new HashMap<>();
    private final Set<Werewolf> werewolvesSet = new HashSet<>();

    public SharedGameData(Map<Member, Character> characterMap, Set<Member> dailyDeathSet, Set<Member> aliveSet, EventChannel<Event> thisGroupChannel) {
        this.characterMap = characterMap;
        this.dailyDeathSet = dailyDeathSet;
        this.aliveSet = aliveSet;
        this.thisGroupChannel = thisGroupChannel;
    }

    public void calculate() {
        dailyDeathSet.add(this.getFinalWerewolfTarget());
    }

    public void broadcastWerewolfInformation() {
        final Set<Member> set = new HashSet<>(werewolfVotingMap.values());
        StringJoiner msg = new StringJoiner("、","现在的投票列表：","");
        set.forEach(member -> msg.add(member.getNick()));
        werewolvesSet.forEach(werewolf -> werewolf.sendMessage(msg.toString()));
    }

    public void werewolfVotesTarget(Werewolf werewolf, Member member) {
        if (werewolfVotingMap.containsKey(werewolf)) {
            werewolfVotedCount++;
        }
        werewolfVotingMap.put(werewolf, member);
    }

    public boolean isWerewolfConsensus() {
        return werewolfCount == werewolfVotedCount && werewolfVotingMap.values().stream().distinct().count() == 1;
    }

    public Member getFinalWerewolfTarget() {
        if (isWerewolfConsensus()) {
            return (Member) werewolfVotingMap.values().toArray()[0];
        }else {
            throw new IllegalCallException("this should be called after werewolves are consensus");
        }
    }

    public void hunt(Member target) {
        hunted = target;
    }

    public void witchPoison(Member target) {
        witchPoison = target;
    }

    public void witchAntidote(Member target) {
        witchAntidote = target;
    }

    public void guard(Member target) {
        guarded = target;
    }

    public void clear() {
        werewolfCount = 0;
        werewolfVotedCount = 0;
        werewolfVotingMap.clear();
        werewolvesSet.clear();
        guarded = null;
        witchAntidote = null;
        witchPoison = null;
        hunted = null;
    }


}
