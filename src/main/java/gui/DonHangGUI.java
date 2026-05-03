package gui;

import bus.DonHangBUS;
import dto.DonHangDTO;
import util.AppColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class DonHangGUI extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private DonHangBUS donHangBUS;
    private JLabel lblTabs;
    
    // Giả lập mã khách hàng đang đăng nhập trong hệ thống (Bạn có thể đổi sau khi ráp code Login)
    private String currentMaKH = "KH000017"; 

    public DonHangGUI() {
        donHangBUS = new DonHangBUS();
        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(AppColor.BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ==========================================
        // 1. HEADER PANEL (Tiêu đề màu xanh)
        // ==========================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColor.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("ĐƠN HÀNG CỦA TÔI");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(AppColor.SURFACE); // Chữ màu trắng
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // ==========================================
        // 2. FILTER PANEL (Thanh lọc trạng thái giả lập)
        // ==========================================
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(AppColor.BACKGROUND);
        lblTabs = new JLabel("Đang tải dữ liệu...");
        lblTabs.setFont(new Font("Arial", Font.BOLD, 14));
        lblTabs.setForeground(AppColor.TEXT_PRIMARY);
        filterPanel.add(lblTabs);

        // Gom Header và Filter lại
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppColor.BACKGROUND);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // 3. TABLE PANEL (Bảng dữ liệu)
        // ==========================================
        String[] columnNames = {"Mã Đơn hàng", "Ngày Đặt", "Sản phẩm", "Trạng thái", "Tổng Tiền", "Hành động"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Chỉ cho phép thao tác click trên cột Hành động (cột số 5)
                return column == 5; 
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40); // Tăng chiều cao dòng cho giống thiết kế
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(AppColor.BORDER);
        table.setShowGrid(true);
        table.setGridColor(AppColor.BORDER);

        // Căn giữa các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        
        // Render màu sắc cho cột Trạng thái (Cột 3)
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        // Căn phải cho cột Tổng tiền (Cột 4)
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        // Gắn Button vào cột Hành động (Cột 5)
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(AppColor.SURFACE);
        add(scrollPane, BorderLayout.CENTER);

        // ==========================================
        // 4. PAGINATION PANEL (Phân trang giả lập)
        // ==========================================
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        paginationPanel.setBackground(AppColor.BACKGROUND);
        
        JButton btnPrev = new JButton("Trang trước");
        JButton btnPage1 = new JButton("1");
        btnPage1.setBackground(AppColor.PRIMARY);
        btnPage1.setForeground(AppColor.SURFACE); 
        btnPage1.setOpaque(true);
        btnPage1.setBorderPainted(false); 
        JButton btnPage2 = new JButton("2");
        JButton btnPage3 = new JButton("3");
        JButton btnNext = new JButton("Trang sau");

        paginationPanel.add(btnPrev);
        paginationPanel.add(btnPage1);
        paginationPanel.add(btnPage2);
        paginationPanel.add(btnPage3);
        paginationPanel.add(btnNext);

        add(paginationPanel, BorderLayout.SOUTH);
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ LOGIC
    // ==========================================

    private void loadDataToTable() {
        tableModel.setRowCount(0); // Xoá dữ liệu cũ
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        DecimalFormat df = new DecimalFormat("#,### VND");

        try {
            List<DonHangDTO> listDH = donHangBUS.getDanhSachDonHang(currentMaKH);
            
            // Khai báo các biến đếm
            int countTatCa = 0, countDangXuLy = 0, countDaGiao = 0, countDaHuy = 0;

            for (DonHangDTO dh : listDH) {
                // Đếm số lượng theo trạng thái
                countTatCa++;
                String status = dh.getTrangThaiDH();
                if (status.equalsIgnoreCase("Chờ xử lý") || status.equalsIgnoreCase("Đã đặt") || status.equalsIgnoreCase("Chờ giao hàng")) {
                    countDangXuLy++;
                } else if (status.equalsIgnoreCase("Hoàn thành") || status.equalsIgnoreCase("Đã giao")) {
                    countDaGiao++;
                } else if (status.equalsIgnoreCase("Đã huỷ")) {
                    countDaHuy++;
                }

                String ngayDat = dh.getTgDat() != null ? sdf.format(dh.getTgDat()) : "";
                String tongTien = df.format(dh.getTongTien());
                
                // SỬA HTML Ở ĐÂY: Ép text-align: left và thêm ký tự gạch đầu dòng (&#8226;)
                String rawSP = dh.getDanhSachSP() != null ? dh.getDanhSachSP() : "";
                String htmlDanhSachSP = "<html><div style='text-align: left; padding: 5px;'>&#8226; " 
                                      + rawSP.replace(", ", "<br>&#8226; ") 
                                      + "</div></html>";

                String actionLabel = "Huỷ đơn"; 

                tableModel.addRow(new Object[]{
                        dh.getMaDH(),
                        ngayDat,
                        htmlDanhSachSP,
                        dh.getTrangThaiDH(),
                        tongTien,
                        actionLabel
                });
            }
            lblTabs.setText(String.format("Tất cả (%d)  |  Đang xử lý (%d)  |  Hoàn thành (%d)  |  Đã hủy (%d)", 
                            countTatCa, countDangXuLy, countDaGiao, countDaHuy));
            
            updateRowHeights();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuyDon(int row) {
        String maDH = (String) tableModel.getValueAt(row, 0);
        String trangThai = (String) tableModel.getValueAt(row, 3);

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn hủy đơn hàng " + maDH + " không?", 
                "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Tạo một DTO tạm để truyền xuống BUS kiểm tra điều kiện
                DonHangDTO dto = new DonHangDTO();
                dto.setMaDH(maDH);
                dto.setTrangThaiDH(trangThai);

                // Gọi tầng BUS xử lý
                donHangBUS.huyDonHang(dto);
                
                JOptionPane.showMessageDialog(this, "Hủy đơn hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable(); // Tải lại bảng

            } catch (Exception ex) {
                // Nếu BUS ném lỗi (vì trạng thái không hợp lệ), hiển thị ra đây
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi thao tác", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    // Hàm tự động tính toán và kéo giãn chiều cao của từng dòng
    private void updateRowHeights() {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = 60; // Chiều cao tối thiểu ban đầu
            // Duyệt qua từng cột trong dòng để tìm xem ô nào có nội dung dài nhất
            for (int column = 0; column < table.getColumnCount(); column++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }
            // Gán chiều cao mới cho dòng (cộng thêm 10px khoảng trắng cho đẹp)
            table.setRowHeight(row, rowHeight + 10); 
        }
    }

    // ==========================================
    // CLASS HỖ TRỢ RENDER GIAO DIỆN (UI)
    // ==========================================

    // Class tô màu cột Trạng thái
    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(JLabel.CENTER);
            setFont(new Font("Arial", Font.BOLD, 13));
            
            String status = value != null ? value.toString() : "";
            
            if (status.equalsIgnoreCase("Hoàn thành") || status.equalsIgnoreCase("Đã giao")) {
                setForeground(AppColor.SUCCESS);
            } else if (status.equalsIgnoreCase("Đã đặt") || status.equalsIgnoreCase("Chờ xử lý") || status.equalsIgnoreCase("Chờ giao hàng")) {
                setForeground(AppColor.WARNING);
            } else {
                setForeground(AppColor.TEXT_SECONDARY); // Đã huỷ
            }
            return c;
        }
    }

    // Class vẽ nút "Huỷ đơn" trong bảng
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBorderPainted(false); 
            setFocusPainted(false);  
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Arial", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String status = (String) table.getValueAt(row, 3);
            
            if (status.equalsIgnoreCase("Đã đặt") || status.equalsIgnoreCase("Chờ xử lý")) {
                setText("Huỷ đơn");
                setBackground(AppColor.ERROR);    
                setForeground(Color.WHITE);       
                setEnabled(true);
            } else {
                setText("Không khả dụng");
                setBackground(AppColor.BORDER);             
                setForeground(AppColor.TEXT_SECONDARY);     
                setEnabled(false);
            }
            return this;
        }
    }

    // Class xử lý sự kiện khi click vào nút "Huỷ đơn"
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private int clickedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setBorderPainted(false); // Ép tắt viền mặc định của Windows
            button.setFocusPainted(false);  // Tắt khung đứt nét khi click
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setFont(new Font("Arial", Font.BOLD, 12));
            
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    xuLyHuyDon(clickedRow);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            clickedRow = row;
            String status = (String) table.getValueAt(row, 3);
            
            if (status.equalsIgnoreCase("Đã đặt") || status.equalsIgnoreCase("Chờ xử lý")) {
                button.setText("Huỷ đơn");
                button.setBackground(AppColor.ERROR);      // Nền đỏ
                button.setForeground(Color.WHITE);         // Chữ trắng
                button.setEnabled(true);
            } else {
                button.setText("Không khả dụng");
                button.setBackground(AppColor.BORDER);             // Nền xám nhạt
                button.setForeground(AppColor.TEXT_SECONDARY);     // Chữ xám đậm
                button.setEnabled(false);
            }
            return button;
        }
    }
    // ==========================================
    // HÀM MAIN ĐỂ TEST ĐỘC LẬP (SAU KHI TEST XONG CÓ THỂ XOÁ)
    // ==========================================
    public static void main(String[] args) {
        // Cài đặt giao diện nhìn cho giống Windows/Mac thật thay vì giao diện Java cổ điển
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tạo một cửa sổ ảo để chứa cái Panel DonHangGUI của bạn
        JFrame frame = new JFrame("Test Giao diện - Lịch sử đơn hàng");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600); // Kích thước cửa sổ
        frame.setLocationRelativeTo(null); // Hiện ra ở giữa màn hình
        
        // Gắn GUI của bạn vào cửa sổ và cho hiển thị lên
        frame.add(new DonHangGUI());
        frame.setVisible(true);
    }
}
