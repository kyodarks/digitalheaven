package com.example.controller.submenu;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.controller.HistoryItem;
import com.example.utils.DataBase;
import com.example.utils.Grid;
import com.example.utils.Grid.GridItem;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class History extends SubMenuController{
    @FXML AnchorPane container;

    private Grid grid;
    private String user;

    private String getHistorySQL;

    public History(){
        initUI("History");
        initComponents();
        initQuerys();
    }

    public void setupUser(String userid){
        this.user = userid;
        update();
    }

    public void update(){
        grid.clear();

        try{
            PreparedStatement stm = DataBase.getConnection()
                .prepareStatement(getHistorySQL);
            stm.setString(1, user);

            ResultSet result = stm.executeQuery();
            boolean c = result.next();
            while (c) {
                HistoryItem current = new HistoryItem(result);
                grid.add(new GridItem(current));
                result.next();
            }
        }catch(SQLException e){}
    }

    private void initQuerys(){
        getHistorySQL = "SELECT i.url AS icon, p.name, s.amount, s.date, s.total FROM sell s JOIN product p ON s.product = p.productid \r\n" + //
                        "JOIN users u ON s.userid = u.userid\r\n" + //
                        "JOIN producticon i ON i.productid = p.productid\r\n" + //
                        "WHERE u.userid = ? AND i.icon_index = 0 AND i.resolution = '150' ORDER BY s.date DESC";
    }

    private void initComponents(){
        grid = new Grid(container);
        grid.setColumns(1);
        grid.setItemSize(741, 140);
    }
}
