package app.screen;

import app.bean.ScreenPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

@Slf4j
@Service
public class ScreenQueue {
    private int fullCheckCounterToRemove = 0;
    private int max = 20;
    private final Queue<ScreenPacket> pipe = new LinkedList<>();

    public void add(ScreenPacket screenPacket) {
        pipe.add(screenPacket);
    }

    public ScreenPacket peek() {
        return pipe.peek();
    }

    public ScreenPacket poll() {
        return pipe.poll();
    }

    public int size() {
        return pipe.size();
    }

    public boolean isFull() {
        try {
            if (fullCheckCounterToRemove > max && !pipe.isEmpty()) {
                pipe.remove();
            } else {
                fullCheckCounterToRemove++;
            }
        } catch (NoSuchElementException e) {
            log.error("ScreenQueue Exception " + e.getMessage(), e);
        }
        return pipe.size() == max;
    }

    public void remove() {
        try {
            pipe.remove();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
    }
}
