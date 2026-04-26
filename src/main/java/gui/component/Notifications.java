package gui.component; 

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.UIScale;         

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class Notifications {

    private static Notifications instance;
    private JComponent targetComponent; // Chuyển từ JFrame sang JComponent
    private final Map<Location, List<NotificationAnimation>> lists = new HashMap<>();
    private final NotificationHolder notificationHolder = new NotificationHolder();

    private ComponentListener windowEvent;

    // Truyền JComponent thay vì JFrame
    private void installEvent(JComponent component) {
        if (windowEvent == null && component != null) {
            windowEvent = new ComponentAdapter() {
                @Override
                public void componentMoved(ComponentEvent e) {
                    if (targetComponent != null && targetComponent.isShowing()) {
                        move(new Rectangle(targetComponent.getLocationOnScreen(), targetComponent.getSize()));
                    }
                }

                @Override
                public void componentResized(ComponentEvent e) {
                    if (targetComponent != null && targetComponent.isShowing()) {
                        move(new Rectangle(targetComponent.getLocationOnScreen(), targetComponent.getSize()));
                    }
                }
            };
        }
        if (this.targetComponent != null) {
            this.targetComponent.removeComponentListener(windowEvent);
        }
        if (component != null) {
            component.addComponentListener(windowEvent);
        }
        this.targetComponent = component;
    }

    public static Notifications getInstance() {
        if (instance == null) {
            instance = new Notifications();
        }
        return instance;
    }
    private int getCurrentShowCount(Location location) {
        List<NotificationAnimation> list = lists.get(location); 
        return list == null ? 0 : list.size();
    }

    private synchronized void move(Rectangle rectangle) {
        for (Map.Entry<Location, List<NotificationAnimation>> set : lists.entrySet()) {
            for (int i = 0; i < set.getValue().size(); i++) {
                NotificationAnimation an = set.getValue().get(i);
                if (an != null) {
                    an.move(rectangle);
                }
            }
        }
    }

    public void setTargetComponent(JComponent component) {
        installEvent(component);
    }

    public void show(Type type, String message) {
        show(type, Location.TOP_CENTER, message);
    }

    public void show(Type type, long duration, String message) {
        show(type, Location.TOP_CENTER, duration, message);
    }

    public void show(Type type, Location location, String message) {
        long duration = FlatUIUtils.getUIInt("Toast.duration", 2500);
        show(type, location, duration, message);
    }

    public void show(Type type, Location location, long duration, String message) {
        initStart(new NotificationAnimation(type, location, duration, message), duration);
    }

    public void show(JComponent component) {
        show(Location.TOP_CENTER, component);
    }

    public void show(Location location, JComponent component) {
        long duration = FlatUIUtils.getUIInt("Toast.duration", 2500);
        show(location, duration, component);
    }

    public void show(Location location, long duration, JComponent component) {
        initStart(new NotificationAnimation(location, duration, component), duration);
    }

    private synchronized boolean initStart(NotificationAnimation notificationAnimation, long duration) {
        int limit = FlatUIUtils.getUIInt("Toast.limit", -1);
        if (limit == -1 || getCurrentShowCount(notificationAnimation.getLocation()) < limit) {
            notificationAnimation.start();
            return true;
        } else {
            notificationHolder.hold(notificationAnimation);
            return false;
        }
    }

    private synchronized void notificationClose(NotificationAnimation notificationAnimation) {
        NotificationAnimation hold = notificationHolder.getHold(notificationAnimation.getLocation());
        if (hold != null) {
            if (initStart(hold, hold.getDuration())) {
                notificationHolder.removeHold(hold);
            }
        }
    }

    public void clearAll() {
        notificationHolder.clearHold();
        for (Map.Entry<Location, List<NotificationAnimation>> set : lists.entrySet()) {
            for (int i = 0; i < set.getValue().size(); i++) {
                NotificationAnimation an = set.getValue().get(i);
                if (an != null) {
                    an.close();
                }
            }
        }
    }

    public void clear(Location location) {
        notificationHolder.clearHold(location);
        List<NotificationAnimation> list = lists.get(location);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                NotificationAnimation an = list.get(i);
                if (an != null) {
                    an.close();
                }
            }
        }
    }

    public void clearHold() {
        notificationHolder.clearHold();
    }

    public void clearHold(Location location) {
        notificationHolder.clearHold(location);
    }

    protected ToastNotificationPanel createNotification(Type type, String message) {
        ToastNotificationPanel toastNotificationPanel = new ToastNotificationPanel();
        toastNotificationPanel.set(type, message);
        return toastNotificationPanel;
    }

    private synchronized void updateList(Location key, NotificationAnimation values, boolean add) {
        if (add) {
            if (lists.containsKey(key)) {
                lists.get(key).add(values);
            } else {
                List<NotificationAnimation> list = new ArrayList<>();
                list.add(values);
                lists.put(key, list);
            }
        } else {
            if (lists.containsKey(key)) {
                lists.get(key).remove(values);
                if (lists.get(key).isEmpty()) {
                    lists.remove(key);
                }
            }
        }
    }

    public enum Type {
        SUCCESS, INFO, WARNING, ERROR
    }

    public enum Location {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }

    public class NotificationAnimation {

        private JWindow window;
        private Animator animator;
        private boolean show = true;
        private float animate;
        private int x;
        private int y;
        private Location location;
        private long duration;
        private Insets frameInsets;
        private int horizontalSpace;
        private int animationMove;
        private boolean top;
        private boolean close = false;

        public NotificationAnimation(Type type, Location location, long duration, String message) {
            installDefault();
            this.location = location;
            this.duration = duration;
            // Đã sửa: Tìm Window chứa Component thay vì truy cập thẳng JFrame
            Window parentWindow = targetComponent != null ? SwingUtilities.getWindowAncestor(targetComponent) : null;
            window = new JWindow(parentWindow);
            ToastNotificationPanel toastNotificationPanel = createNotification(type, message);
            toastNotificationPanel.putClientProperty("Toast.closeCallback", (Consumer<Object>) o -> close());
            window.setContentPane(toastNotificationPanel);
            window.setFocusableWindowState(false);
            window.pack();
            toastNotificationPanel.setDialog(window);
        }

        public NotificationAnimation(Location location, long duration, JComponent component) {
            installDefault();
            this.location = location;
            this.duration = duration;
            Window parentWindow = targetComponent != null ? SwingUtilities.getWindowAncestor(targetComponent) : null;
            window = new JWindow(parentWindow);
            window.setBackground(new Color(0, 0, 0, 0));
            window.setContentPane(component);
            window.setFocusableWindowState(false);
            window.setSize(component.getPreferredSize());
        }

        private void installDefault() {
            frameInsets = UIUtils.getInsets("Toast.frameInsets", new Insets(10, 10, 10, 10));
            horizontalSpace = FlatUIUtils.getUIInt("Toast.horizontalGap", 10);
            animationMove = FlatUIUtils.getUIInt("Toast.animationMove", 10);
        }

        public void start() {
            int animation = FlatUIUtils.getUIInt("Toast.animation", 200);
            int resolution = FlatUIUtils.getUIInt("Toast.animationResolution", 5);
            animator = new Animator(animation, new Animator.TimingTarget() {
                @Override
                public void begin() {
                    if (show) {
                        updateList(location, NotificationAnimation.this, true);
                        installLocation();
                    }
                }

                @Override
                public void timingEvent(float f) {
                    animate = show ? f : 1f - f;
                    updateLocation(true);
                }

                @Override
                public void end() {
                    if (show && close == false) {
                        SwingUtilities.invokeLater(() -> {
                            new Thread(() -> {
                                sleep(duration);
                                if (close == false) {
                                    show = false;
                                    animator.start();
                                }
                            }).start();
                        });
                    } else {
                        updateList(location, NotificationAnimation.this, false);
                        window.dispose();
                        notificationClose(NotificationAnimation.this);
                    }
                }
            });
            animator.setResolution(resolution);
            animator.start();
        }

        private void installLocation() {
            Insets insets;
            Rectangle rec;
            // Đã sửa: Tính toán tọa độ dựa trên Target Component
            if (targetComponent == null || !targetComponent.isShowing()) {
                insets = UIScale.scale(frameInsets);
                rec = new Rectangle(new Point(0, 0), Toolkit.getDefaultToolkit().getScreenSize());
            } else {
                insets = UIScale.scale(FlatUIUtils.addInsets(frameInsets, targetComponent.getInsets()));
                rec = new Rectangle(targetComponent.getLocationOnScreen(), targetComponent.getSize());
            }
            setupLocation(rec, insets);
            window.setOpacity(0f);
            window.setVisible(true);
        }

        private void move(Rectangle rec) {
            Insets insets;
            if (targetComponent != null) {
                insets = UIScale.scale(FlatUIUtils.addInsets(frameInsets, targetComponent.getInsets()));
            } else {
                insets = UIScale.scale(frameInsets);
            }
            setupLocation(rec, insets);
        }

        private void setupLocation(Rectangle rec, Insets insets) {
            if (location == Location.TOP_LEFT) {
                x = rec.x + insets.left;
                y = rec.y + insets.top;
                top = true;
            } else if (location == Location.TOP_CENTER) {
                x = rec.x + (rec.width - window.getWidth()) / 2;
                y = rec.y + insets.top;
                top = true;
            } else if (location == Location.TOP_RIGHT) {
                x = rec.x + rec.width - (window.getWidth() + insets.right);
                y = rec.y + insets.top;
                top = true;
            } else if (location == Location.BOTTOM_LEFT) {
                x = rec.x + insets.left;
                y = rec.y + rec.height - (window.getHeight() + insets.bottom);
                top = false;
            } else if (location == Location.BOTTOM_CENTER) {
                x = rec.x + (rec.width - window.getWidth()) / 2;
                y = rec.y + rec.height - (window.getHeight() + insets.bottom);
                top = false;
            } else if (location == Location.BOTTOM_RIGHT) {
                x = rec.x + rec.width - (window.getWidth() + insets.right);
                y = rec.y + rec.height - (window.getHeight() + insets.bottom);
                top = false;
            }
            int am = UIScale.scale(top ? animationMove : -animationMove);
            int ly = (int) (getLocation(NotificationAnimation.this) + y + animate * am);
            window.setLocation(x, ly);
        }

        private void updateLocation(boolean loop) {
            int am = UIScale.scale(top ? animationMove : -animationMove);
            int ly = (int) (getLocation(NotificationAnimation.this) + y + animate * am);
            window.setLocation(x, ly);
            window.setOpacity(animate);
            if (loop) {
                update(this);
            }
        }

        private int getLocation(NotificationAnimation notification) {
            int height = 0;
            List<NotificationAnimation> list = lists.get(location);
            for (int i = 0; i < list.size(); i++) {
                NotificationAnimation n = list.get(i);
                if (notification == n) {
                    return height;
                }
                double v = n.animate * (list.get(i).window.getHeight() + UIScale.scale(horizontalSpace));
                height += top ? v : -v;
            }
            return height;
        }

        private void update(NotificationAnimation except) {
            List<NotificationAnimation> list = lists.get(location);
            for (int i = 0; i < list.size(); i++) {
                NotificationAnimation n = list.get(i);
                if (n != except) {
                    n.updateLocation(false);
                }
            }
        }

        public void close() {
            if (show) {
                if (animator.isRunning()) {
                    animator.stop();
                }
                close = true;
                show = false;
                animator.start();
            }
        }

        private void sleep(long l) {
            try {
                Thread.sleep(l);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }

        public Location getLocation() {
            return location;
        }

        public long getDuration() {
            return duration;
        }
    }
}