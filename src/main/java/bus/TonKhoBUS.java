/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bus;

import dao.TonKhoDAO;
import dto.TonKhoDTO;
import java.util.ArrayList;

public class TonKhoBUS {
    private TonKhoDAO tonKhoDAO = new TonKhoDAO();

    public ArrayList<TonKhoDTO> getAllTonKho() {
        return tonKhoDAO.selectAll();
    }
}
