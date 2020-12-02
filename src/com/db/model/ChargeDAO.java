package com.db.model;

import java.sql.*;
import java.util.*;

public class ChargeDAO extends DAO {
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public ChargeDAO() {
        super();
        conn = getConn();
        ps = getPs();
        rs = getRs();
    }

    // 가격 정보 수정
    public void changeCharge(ChargeDTO charge) throws DAOException, SQLException {
        String insert_sql = "update charges set price = ? where type = ?";

        ps = conn.prepareStatement(insert_sql);

        ps.setInt(1, charge.getPrice());
        ps.setString(2, charge.getType());

        int r = ps.executeUpdate();

        ps.close();
    }

    // 타입별 가격 리스트반환
    public ArrayList<ChargeDTO> getChargeList() throws DAOException, SQLException {
        ArrayList<ChargeDTO> temp_list = new ArrayList<ChargeDTO>();
        String insert_sql = "select * from charges";
        ps = conn.prepareStatement(insert_sql);

        rs = ps.executeQuery();
        while (rs.next()) {
            String type = rs.getString("type");
            int price = rs.getInt("price");
            temp_list.add(new ChargeDTO(type, price));
        }

        rs.close();
        ps.close();

        return temp_list;
    }
}
