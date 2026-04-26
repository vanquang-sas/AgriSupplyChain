
package gui.khohang;

import javax.swing.table.DefaultTableModel;


public class NhapKho extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(NhapKho.class.getName());

    /**
     * Creates new form NhapKho
     */
     //thietlapbang
    
     private void setupTable(){
            DefaultTableModel model = (DefaultTableModel) tbNhap.getModel();
            model.setRowCount(0);
            model.addRow(new Object[]{"SP001", "Cá Tra Fillet", "500", "Chọn kho...", "Chọn vị trí...", "2024-12-31", "Chờ xác nhận"});
            model.addRow(new Object[]{"SP002", "Xoài Cát", "200", "Chọn kho...", "Chọn vị trí...", "2024-05-15", "Chờ xác nhận"});
            javax.swing.JComboBox<String> cbKho = new javax.swing.JComboBox<>(new String[]{"Mát", "Lạnh", "Đông"});
            tbNhap.getColumnModel().getColumn(3).setCellEditor(new javax.swing.DefaultCellEditor(cbKho));
            javax.swing.JComboBox<String> cbViTri = new javax.swing.JComboBox<>(new String[]{"A","B","C"});
            tbNhap.getColumnModel().getColumn(4).setCellEditor(new javax.swing.DefaultCellEditor(cbViTri));
            tbNhap.setRowHeight(30);
        }
     
    public NhapKho() {
        initComponents();
        setTitle("Nhập Kho");
        setupTable();
    }

   
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbNhap = new javax.swing.JTable();
        btnXacNhanNhap = new javax.swing.JButton();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        tbNhap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã lô hàng", "Tên sản phẩm", "Số lượng", "Chọn kho", "Vị trí", "Ngày hết hạn", "Trạng thái"
            }
        ));
        jScrollPane1.setViewportView(tbNhap);

        btnXacNhanNhap.setBackground(new java.awt.Color(51, 255, 51));
        btnXacNhanNhap.setForeground(new java.awt.Color(255, 255, 255));
        btnXacNhanNhap.setText("Xác nhận nhập kho");
        btnXacNhanNhap.addActionListener(this::btnXacNhanNhapActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnXacNhanNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnXacNhanNhap, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnXacNhanNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXacNhanNhapActionPerformed
        try {
        int selectedRow = tbNhap.getSelectedRow();
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm từ danh sách!");
            return;
        }

        String maCTLH = tbNhap.getValueAt(selectedRow, 0).toString();
        String tenSP = tbNhap.getValueAt(selectedRow, 1).toString();
        String kho = tbNhap.getValueAt(selectedRow, 3).toString();
        String viTri = tbNhap.getValueAt(selectedRow, 4).toString();
        String hanDung = tbNhap.getValueAt(selectedRow, 5).toString();

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd"); // Sửa lại format nếu cần (VD: "dd/MM/yyyy")
        java.util.Date ngayHH = sdf.parse(hanDung);

        dto.TonKhoDTO dtoObj = new dto.TonKhoDTO();
        dtoObj.setMaCTLH(maCTLH);
        dtoObj.setMaKho(kho);
        dtoObj.setViTri(viTri);
        dtoObj.setTgHetHan(ngayHH);

        bus.nhapkhoBUS bus = new bus.nhapkhoBUS();
        String result = bus.xacNhanNhapKho(dtoObj, tenSP);

        if ("SUCCESS".equals(result)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Nhập kho thành công cho sản phẩm " + tenSP + "!");
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, result, "Cảnh báo", javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    } catch (java.text.ParseException e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Lỗi định dạng ngày tháng. Vui lòng kiểm tra lại định dạng!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage(), "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnXacNhanNhapActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new NhapKho().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnXacNhanNhap;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbNhap;
    // End of variables declaration//GEN-END:variables
}
