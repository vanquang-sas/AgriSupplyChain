package gui.panel;

import bus.ThamSoBUS;
import dto.ThamSoDTO;
import gui.dialog.ThamSoForm;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ThamSoPanel extends JPanel {

    private final ThamSoBUS bus = new ThamSoBUS();
    private JPanel cardsContainer;

    public ThamSoPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppColor.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColor.BACKGROUND);

        JLabel title = new JLabel("Cấu hình Tham số hệ thống");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(AppColor.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Chỉnh sửa các tham số điều hành hoạt động của hệ thống");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(AppColor.TEXT_SECONDARY);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBlock.setBackground(AppColor.BACKGROUND);
        titleBlock.add(title);
        titleBlock.add(subtitle);
        header.add(titleBlock, BorderLayout.WEST);

        // Nút Làm mới
        JButton btnRefresh = new JButton("⟳  Làm mới");
        btnRefresh.setBackground(AppColor.SURFACE);
        btnRefresh.setForeground(AppColor.TEXT_SECONDARY);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadData());
        header.add(btnRefresh, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Container chứa các card — dùng GridLayout 2 cột
        cardsContainer = new JPanel(new GridLayout(0, 2, 16, 16));
        cardsContainer.setBackground(AppColor.BACKGROUND);

        // Bọc trong ScrollPane để cuộn khi cần
        JScrollPane scroll = new JScrollPane(cardsContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppColor.BACKGROUND);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    // ===================== LOAD DỮ LIỆU =====================
    public void loadData() {
        cardsContainer.removeAll();
        List<ThamSoDTO> list = bus.getAll();
        for (ThamSoDTO ts : list) {
            cardsContainer.add(buildCard(ts));
        }
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    // ===================== BUILD CARD =====================
    private JPanel buildCard(ThamSoDTO ts) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(AppColor.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColor.BORDER, 1, true),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        // Biểu tượng bên trái
        JLabel icon = new JLabel(getIcon(ts.getTenTS()));
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        icon.setPreferredSize(new Dimension(44, 44));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setVerticalAlignment(SwingConstants.CENTER);
        icon.setOpaque(true);
        icon.setBackground(getIconBg(ts.getTenTS()));
        icon.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Khối text giữa
        JPanel textBlock = new JPanel(new GridLayout(3, 1, 0, 2));
        textBlock.setBackground(AppColor.SURFACE);

        JLabel lblTen = new JLabel(getDisplayName(ts.getTenTS()));
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTen.setForeground(AppColor.TEXT_PRIMARY);

        JLabel lblGiaTri = new JLabel(formatValue(ts));
        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGiaTri.setForeground(getValueColor(ts.getTenTS()));

        JLabel lblMoTa = new JLabel(ts.getMoTa() != null ? ts.getMoTa() : "");
        lblMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMoTa.setForeground(AppColor.TEXT_SECONDARY);

        textBlock.add(lblTen);
        textBlock.add(lblGiaTri);
        textBlock.add(lblMoTa);

        // Nút Sửa bên phải
        JButton btnEdit = new JButton("Sửa");
        btnEdit.setBackground(new Color(236, 253, 245));
        btnEdit.setForeground(AppColor.PRIMARY);
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEdit.setFocusPainted(false);
        btnEdit.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.setAlignmentY(Component.CENTER_ALIGNMENT);
        btnEdit.addActionListener(e -> showEditDialog(ts.getMaTS()));

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(AppColor.SURFACE);
        rightPanel.add(btnEdit);

        card.add(icon, BorderLayout.WEST);
        card.add(textBlock, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    // ===================== DIALOG CHỈNH SỬA =====================
    private void showEditDialog(String maTS) {
        ThamSoDTO ts = bus.getById(maTS);
        if (ts == null) return;

        ThamSoForm form = new ThamSoForm(maTS);
        String title = "Chỉnh sửa: " + getDisplayName(ts.getTenTS());

        SimpleModalBorder modal = new SimpleModalBorder(
                form, title, SimpleModalBorder.YES_NO_OPTION,
                (ctrl, action) -> {
                    if (action == SimpleModalBorder.YES_OPTION) {
                        if (form.saveData()) { ctrl.close(); loadData(); }
                    } else ctrl.close();
                });
        ModalDialog.showModal(this, modal);
    }

    // ===================== HELPER - TÊN HIỂN THỊ =====================
    private String getDisplayName(String tenTS) {
        switch (tenTS) {
            case "MIN_TONKHO":          return "Tồn kho tối thiểu";
            case "CANHBAO_HETHAN":      return "Cảnh báo hạn sử dụng";
            case "MAX_TG_THANHTOAN":    return "Thời gian chờ thanh toán";
            case "DON_GIA_VANCHUYEN":   return "Đơn giá vận chuyển";
            case "GG_THUONG":           return "Giảm giá Khách Thường";
            case "GG_THANTHIET":        return "Giảm giá Khách Thân thiết";
            case "GG_VIP":              return "Giảm giá Khách VIP";
            default:                    return tenTS;
        }
    }

    private String formatValue(ThamSoDTO ts) {
        switch (ts.getTenTS()) {
            case "DON_GIA_VANCHUYEN":
                return NumberFormat.getNumberInstance(new Locale("vi", "VN"))
                        .format((long) ts.getGiaTri()) + " VNĐ/km";
            case "MIN_TONKHO":
                return (long) ts.getGiaTri() + " đơn vị";
            case "CANHBAO_HETHAN":
                return (long) ts.getGiaTri() + " ngày";
            case "MAX_TG_THANHTOAN":
                return (long) ts.getGiaTri() + " giờ";
            case "GG_THUONG": case "GG_THANTHIET": case "GG_VIP":
                return String.format("%.0f%%", ts.getGiaTri() * 100);
            default:
                return String.valueOf(ts.getGiaTri());
        }
    }

    private String getIcon(String tenTS) {
        switch (tenTS) {
            case "MIN_TONKHO":          return "📦";
            case "CANHBAO_HETHAN":      return "⏰";
            case "MAX_TG_THANHTOAN":    return "💳";
            case "DON_GIA_VANCHUYEN":   return "🚚";
            case "GG_THUONG":           return "🏷";
            case "GG_THANTHIET":        return "⭐";
            case "GG_VIP":              return "👑";
            default:                    return "⚙";
        }
    }

    private Color getIconBg(String tenTS) {
        switch (tenTS) {
            case "DON_GIA_VANCHUYEN":   return new Color(254, 243, 199);
            case "MIN_TONKHO":          return new Color(220, 252, 231);
            case "CANHBAO_HETHAN":      return new Color(254, 226, 226);
            case "MAX_TG_THANHTOAN":    return new Color(219, 234, 254);
            case "GG_THUONG":           return new Color(243, 244, 246);
            case "GG_THANTHIET":        return new Color(255, 251, 235);
            case "GG_VIP":              return new Color(253, 230, 138);
            default:                    return new Color(243, 244, 246);
        }
    }

    private Color getValueColor(String tenTS) {
        switch (tenTS) {
            case "CANHBAO_HETHAN":  return AppColor.ERROR;
            case "DON_GIA_VANCHUYEN": return AppColor.WARNING;
            case "GG_VIP":          return new Color(109, 40, 217);
            default:                return AppColor.PRIMARY;
        }
    }
}