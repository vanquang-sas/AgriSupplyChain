package bus;

import dao.ThongBaoDAO;
import dto.ThongBaoDTO;

import java.util.List;

public class ThongBaoBUS {
    private final ThongBaoDAO dao = new ThongBaoDAO();

    public List<ThongBaoDTO> getNotificationsForUser(String role, String userId) {
        if (role == null) role = "Khách hàng";
        if (userId == null) userId = "";
        return dao.getNotificationsForUser(role, userId);
    }

    public int getUnreadCount(String role, String userId) {
        if (role == null) return 0;
        if (userId == null) userId = "";
        return dao.getUnreadCount(role, userId);
    }

    public void markAllAsRead(String role, String userId) {
        if (role == null) return;
        if (userId == null) userId = "";
        dao.markAllAsRead(role, userId);
    }
}
