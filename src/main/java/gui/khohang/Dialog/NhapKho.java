
package gui.khohang.Dialog;

import javax.swing.table.DefaultTableModel;


public class NhapKho extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(NhapKho.class.getName());

    /**
     * Creates new form NhapKho
     */
     //thietlapbang
     private void setupTable(){
            String[] columnNames = {"Mã lô hàng", "Tên sản phẩm", "Số lượng", "Mã Kho", "Loại kho", "Vị trí", "Ngày hết hạn", "Trạng thái"};
    
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 4 || column == 5 || column == 6;
                }
            };
            tbNhap.setModel(model);
            javax.swing.JComboBox<String> cbLoaiKho = new javax.swing.JComboBox<>(new String[]{"Mát", "Lạnh", "Đông"});
            tbNhap.getColumnModel().getColumn(4).setCellEditor(new javax.swing.DefaultCellEditor(cbLoaiKho));
            javax.swing.JComboBox<String> cbViTri = new javax.swing.JComboBox<>(new String[]{"A","B","C"});
            tbNhap.getColumnModel().getColumn(5).setCellEditor(new javax.swing.DefaultCellEditor(cbViTri));
            
            // Thiết lập JDateChooser cho cột Ngày hết hạn 
            com.toedter.calendar.JDateChooser dateChooser = new com.toedter.calendar.JDateChooser();
            dateChooser.setDateFormatString("yyyy-MM-dd"); // Định dạng hiển thị

            // Gắn vào cột số 6
            tbNhap.getColumnModel().getColumn(6).setCellEditor(new com.toedter.calendar.JDateChooserCellEditor());
            tbNhap.setRowHeight(30);
        }
     //lay du lieu tu db len bang
     private void loadDataToTable() {
    DefaultTableModel model = (DefaultTableModel) tbNhap.getModel();
    model.setRowCount(0);

    bus.NhapKhoBUS bus = new bus.NhapKhoBUS();

    for (Object[] row : bus.getDanhSachNhapKho()) {
        model.addRow(row);
    }
}
     
    public NhapKho() {
        initComponents();
        setTitle("Nhập Kho");
        setupTable();
        loadDataToTable();
    }

   
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbNhap = new javax.swing.JTable();
        btnXacNhanNhap = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        tbNhap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã CT lô", "Tên sản phẩm", "Số lượng", "Mã Kho", "Loại kho", "Vị trí", "Ngày hết hạn", "Trạng thái"
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnXacNhanNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 643, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 639, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
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
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm!");
            return;
        }

       
        String maCTLH = tbNhap.getValueAt(selectedRow, 0).toString(); // Cột 0
        String tenSP   = tbNhap.getValueAt(selectedRow, 1).toString(); // Cột 1
        
       
        Object objMaKho = tbNhap.getValueAt(selectedRow, 3); 
        if (objMaKho == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Chưa chọn Mã Kho!");
            return;
        }
        String maKho = objMaKho.toString();
         Object objViTri = tbNhap.getValueAt(selectedRow, 5);
         //CHẶN NÈ: Kiểm tra nếu null hoặc chỉ có khoảng trắng
         if (objViTri == null || objViTri.toString().trim().isEmpty()) {
             javax.swing.JOptionPane.showMessageDialog(this, 
                 "Lỗi: Bạn chưa chọn Vị trí cho sản phẩm này!", 
                 "Thiếu thông tin", 
                 javax.swing.JOptionPane.WARNING_MESSAGE);
             return; // Dừng lại luôn, không cho gọi Procedure nữa
         }

         //Nếu đã chọn thì mới lấy giá trị ra
         String viTri = objViTri.toString().trim();
        
       
        //XỬ LÝ NGÀY HẾT HẠN 
        Object objHanDung = tbNhap.getValueAt(selectedRow, 6);
        
        //Chặn lỗi chuỗi trống hoặc null trước khi parse
        if (objHanDung == null || objHanDung.toString().trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn Ngày hết hạn!");
            return;
        }

        java.util.Date ngayHH = null;
        if (objHanDung instanceof java.util.Date) {
            //Nếu dùng JDateChooserEditor, nó sẽ trả về kiểu Date luôn
            ngayHH = (java.util.Date) objHanDung;
        } else {
            //Nếu là kiểu String (do SQL trả về hoặc gõ tay), thì mới parse
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false); // Chặn ngày ảo kiểu 31/02
            ngayHH = sdf.parse(objHanDung.toString().trim());
        }

        dto.TonKhoDTO dtoObj = new dto.TonKhoDTO();
        dtoObj.setMaCTLH(maCTLH);
        dtoObj.setMaKho(maKho); 
        dtoObj.setViTri(viTri);
        dtoObj.setTgHetHan(ngayHH);

        bus.NhapKhoBUS bus = new bus.NhapKhoBUS();
        String result = bus.xacNhanNhapKho(dtoObj, tenSP);

        if ("SUCCESS".equals(result)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Thành công!");
            loadDataToTable();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, result, "Lỗi SQL", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
    }
    }//GEN-LAST:event_btnXacNhanNhapActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
//            logger.log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(() -> new NhapKho().setVisible(true));
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnXacNhanNhap;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbNhap;
    // End of variables declaration//GEN-END:variables
}
