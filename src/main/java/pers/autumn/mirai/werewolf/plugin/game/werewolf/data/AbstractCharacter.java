package pers.autumn.mirai.werewolf.plugin.game.werewolf.data;

import lombok.Data;
import lombok.SneakyThrows;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.UserMessageEvent;
import pers.autumn.mirai.werewolf.plugin.utils.Util;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author SoundOfAutumn
 * @date 2022/4/29 19:54
 */
@Data
public abstract class AbstractCharacter implements Character {
    private final Camp camp;
    private final String name;
    private final Member member;
    protected final SharedGameData sharedGameData;
    final EventChannel<Event> userListener;

    public AbstractCharacter(Camp camp, String name, Member member, SharedGameData sharedGameData) {
        this.camp = camp;
        this.name = name;
        this.member = member;
        this.sharedGameData = sharedGameData;
        userListener = GlobalEventChannel.INSTANCE
                .filter(event -> event instanceof UserMessageEvent && Util.isEqualUsers(((UserMessageEvent) event).getSubject(), member));
    }

    @SneakyThrows
    boolean getResponseTrueOrFalse() {
        AtomicReference<Boolean> result = new AtomicReference<>();

        final Listener<UserMessageEvent> listener = userListener.subscribeAlways(UserMessageEvent.class, event -> {
            final String s = event.getMessage().contentToString().toLowerCase(Locale.ROOT);
            if (s.contains("是") && s.contains("y")) {
                result.set(true);
            } else if (s.contains("否") && s.contains("n")) {
                result.set(false);
            } else {
                sendMessage("无法识别的回答");
            }

        });
        while (result.get() == null) Thread.sleep(500);
        listener.complete();
        return result.get();
    }

    Member getResponseAsMember() {
        AtomicReference<Member> subject = new AtomicReference<>();
        final Listener<UserMessageEvent> listener = userListener.subscribeAlways(UserMessageEvent.class, event -> {
            subject.set(Util.getMemberByName(member.getGroup(), event.getMessage().contentToString()));
            if (subject.get() == null) {
                sendMessage("无法识别的名字");
            }
        });
        while (subject.get() == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
        listener.complete();
        return subject.get();
    }

    void sendMessage(String message) {
        member.sendMessage(message);
    }

    @Override
    public Camp getCamp() {
        return camp;
    }

    @Override
    public String getName() {
        return name;
    }
}
