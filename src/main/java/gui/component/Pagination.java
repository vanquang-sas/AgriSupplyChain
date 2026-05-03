package gui.component;

import util.AppColor; 
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Pagination extends JPanel {

    public interface PageChangeListener {
        void onPageChanged(int newPage);
    }

    // ── State ──────────────────────────────────
    private int totalItems;
    private int pageSize;
    private int currentPage = 1;
    private int totalPages;
    private int maxVisible  = 5;

    private final List<PageChangeListener> listeners = new ArrayList<>();

    // ── UI ─────────────────────────────────────
    private JButton btnFirst, btnPrev, btnNext, btnLast;
    private JPanel  pageButtonPanel;
    private JComboBox<Integer> cbJumpTo;
    private JLabel  lblTotal;

    // ── Màu ────────────────────────────────────
    private static final Color C_PRIMARY   = AppColor.PRIMARY;
    private static final Color C_HOVER_BG  = Color.decode("#F0FDF4"); 
    private static final Color C_BORDER    = AppColor.BORDER;
    private static final Color C_TEXT      = AppColor.TEXT_PRIMARY;
    private static final Color C_MUTED     = AppColor.TEXT_SECONDARY;
    private static final Color C_WHITE     = AppColor.SURFACE;

    // ── Constructor ────────────────────────────
    public Pagination(int totalItems, int pageSize) {
        this.totalItems = totalItems;
        this.pageSize   = pageSize;
        buildUI();
        refresh();
    }

    public Pagination() { this(0, 10); }

    // ── Build UI ───────────────────────────────
    private void buildUI() {
        setLayout(new MigLayout("insets 6 0 6 0, gap 4", "[][][][]push[]", "[]"));
        setOpaque(false);

        btnFirst = navBtn("\u00ab");   // ««
        btnPrev  = navBtn("\u2039");   // ‹
        btnNext  = navBtn("\u203a");   // ›
        btnLast  = navBtn("\u00bb");   // »»

        btnFirst.addActionListener(e -> goToPage(1));
        btnPrev .addActionListener(e -> goToPage(currentPage - 1));
        btnNext .addActionListener(e -> goToPage(currentPage + 1));
        btnLast .addActionListener(e -> goToPage(totalPages));

        pageButtonPanel = new JPanel(new MigLayout("insets 0, gap 4", "", ""));
        pageButtonPanel.setOpaque(false);

        // ── Phần bên phải: "Page [dropdown] of N" ──
        JPanel rightPanel = new JPanel(new MigLayout("insets 0, gap 6", "[][][]", "[]"));
        rightPanel.setOpaque(false);

        JLabel lblPage = new JLabel("Page");
        lblPage.setFont(lblPage.getFont().deriveFont(13f));
        lblPage.setForeground(C_MUTED);

        cbJumpTo = new JComboBox<>();
        cbJumpTo.setPreferredSize(new Dimension(64, 32));
        cbJumpTo.setFont(cbJumpTo.getFont().deriveFont(Font.BOLD, 13f));
        cbJumpTo.addActionListener(e -> {
            if (cbJumpTo.getSelectedItem() != null && cbJumpTo.isFocusOwner()) {
                goToPage((Integer) cbJumpTo.getSelectedItem());
            }
        });

        lblTotal = new JLabel();
        lblTotal.setFont(lblTotal.getFont().deriveFont(13f));
        lblTotal.setForeground(C_MUTED);

        rightPanel.add(lblPage);
        rightPanel.add(cbJumpTo);
        rightPanel.add(lblTotal);

        add(btnFirst);
        add(btnPrev);
        add(pageButtonPanel);
        add(btnNext);
        add(btnLast);
        add(rightPanel); 
    }

    // ── Refresh ────────────────────────────────
    private void refresh() {
        totalPages  = calcTotalPages();
        currentPage = Math.max(1, Math.min(currentPage, totalPages));

        btnFirst.setEnabled(currentPage > 1);
        btnPrev .setEnabled(currentPage > 1);
        btnNext .setEnabled(currentPage < totalPages);
        btnLast .setEnabled(currentPage < totalPages);

        // Cập nhật dropdown
        cbJumpTo.removeAllItems();
        for (int i = 1; i <= totalPages; i++) cbJumpTo.addItem(i);
        cbJumpTo.setSelectedItem(currentPage);

        lblTotal.setText("of " + totalPages);

        buildPageButtons();
        revalidate();
        repaint();
    }

    private void buildPageButtons() {
        pageButtonPanel.removeAll();
        if (totalPages <= 1) return;

        int half  = maxVisible / 2;
        int start = Math.max(1, currentPage - half);
        int end   = Math.min(totalPages, start + maxVisible - 1);
        if (end - start + 1 < maxVisible)
            start = Math.max(1, end - maxVisible + 1);

        if (start > 1) {
            pageButtonPanel.add(pageBtn(1));
            if (start > 2) pageButtonPanel.add(dotsLabel());
        }

        for (int i = start; i <= end; i++)
            pageButtonPanel.add(pageBtn(i));

        if (end < totalPages) {
            if (end < totalPages - 1) pageButtonPanel.add(dotsLabel());
            pageButtonPanel.add(pageBtn(totalPages));
        }

        pageButtonPanel.revalidate();
        pageButtonPanel.repaint();
    }

    // ── Factories ──────────────────────────────

    private JButton navBtn(String text) {
        JButton btn = roundBtn(text, false);
        btn.setForeground(C_MUTED);
        btn.setFont(btn.getFont().deriveFont(14f));
        btn.addMouseListener(hoverListener(btn, false));
        return btn;
    }

    private JButton pageBtn(int page) {
        boolean active = (page == currentPage);
        JButton btn = roundBtn(String.valueOf(page), active);
        btn.setForeground(active ? C_WHITE : C_TEXT);
        btn.setFont(btn.getFont().deriveFont(active ? Font.BOLD : Font.PLAIN, 13f));
        if (!active) {
            btn.addActionListener(e -> goToPage(page));
            btn.addMouseListener(hoverListener(btn, false));
        }
        return btn;
    }

    /** Nút bo góc vẽ tay — không phụ thuộc FlatLaf */
    private JButton roundBtn(String text, boolean active) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (active) {
                    g2.setColor(C_PRIMARY);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else if (getModel().isRollover() && isEnabled()) {
                    g2.setColor(C_HOVER_BG);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(C_PRIMARY);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                } else {
                    g2.setColor(C_WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(C_BORDER);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setCursor(active ? Cursor.getDefaultCursor()
                             : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel dotsLabel() {
        JLabel l = new JLabel("\u00b7\u00b7\u00b7");
        l.setPreferredSize(new Dimension(24, 32));
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setForeground(C_MUTED);
        l.setFont(l.getFont().deriveFont(13f));
        return l;
    }

    /** Hover: đổi màu chữ khi di chuột vào/ra */
    private MouseAdapter hoverListener(JButton btn, boolean ignored) {
        return new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setForeground(C_PRIMARY);
                btn.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setForeground(C_MUTED);
                btn.repaint();
            }
        };
    }

    // ── Navigation ─────────────────────────────
    private void goToPage(int page) {
        page = Math.max(1, Math.min(page, totalPages));
        if (page == currentPage) return;
        currentPage = page;
        refresh();
        firePageChanged();
    }

    private void firePageChanged() {
        listeners.forEach(l -> l.onPageChanged(currentPage));
    }

    // ── Public API ─────────────────────────────
    public void addPageChangeListener(PageChangeListener l)    { listeners.add(l); }
    public void removePageChangeListener(PageChangeListener l) { listeners.remove(l); }

    public void setTotalItems(int totalItems) {
        this.totalItems  = totalItems;
        this.currentPage = 1;
        refresh();
    }

    public void setPageSize(int pageSize) {
        this.pageSize    = pageSize;
        this.currentPage = 1;
        refresh();
        firePageChanged();
    }

    public void setCurrentPage(int page) {
        this.currentPage = Math.max(1, Math.min(page, totalPages));
        refresh();
    }

    public void setData(int totalItems, int pageSize) {
        this.totalItems  = totalItems;
        this.pageSize    = pageSize;
        this.currentPage = 1;
        refresh();
    }

    public void setMaxVisibleButtons(int max) {
        this.maxVisible = Math.max(3, max);
        refresh();
    }

    public int getCurrentPage() { return currentPage; }
    public int getTotalPages()  { return totalPages; }
    public int getTotalItems()  { return totalItems; }
    public int getPageSize()    { return pageSize; }
    public int getOffset()      { return (currentPage - 1) * pageSize; }

    private int calcTotalPages() {
        if (totalItems <= 0 || pageSize <= 0) return 1;
        return (int) Math.ceil((double) totalItems / pageSize);
    }
}