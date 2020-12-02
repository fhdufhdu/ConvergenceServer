package com.protocol;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.TimerTask;

import com.db.model.DAO;
import com.db.model.ReservationDAO;

//가예매의 자리 잠금을 해제하기 위한 타이머 스레드
public class ClearTimer extends TimerTask
{
    private String mem_id;
    private String tt_id;
    private ArrayList<Integer> row_list;
    private ArrayList<Integer> col_list;
    
    public ClearTimer(String mem_id, String tt_id, ArrayList<Integer> row_list, ArrayList<Integer> col_list)
    {
        this.mem_id = mem_id;
        this.tt_id = tt_id;
        this.row_list = row_list;
        this.col_list = col_list;
    }
    
    @Override
    public void run()
    {
        try
        {
            System.out.println("스레드 시작");
            ReservationDAO rDao = new ReservationDAO();
            Connection conn = DAO.getConn();
            rDao.clearRsv(mem_id, tt_id, row_list, col_list);
            conn.commit();
            return;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
