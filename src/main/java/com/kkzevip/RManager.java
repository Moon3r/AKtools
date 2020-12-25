package com.kkzevip;

import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse;
import com.aliyuncs.exceptions.ClientException;
import com.kkzevip.utils.AliOperator;
import com.kkzevip.utils.TableData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RManager {
    private JLabel accessKeyIDLabel;
    private JLabel accessKeySecretLabel;
    private JPanel rootPanel;
    private JButton queryButton;
    private JTextField accessKeyIdText;
    private JTextField accessKeySecretText;
    private JScrollPane tablePane;
    private JTable table1;
    private JLabel notice;


    public void initTable() {
        TableData tableData = new TableData();
        Object[] cData = tableData.getInitColumns();
        Object[][] cRow = tableData.getInitData();
        TableModel tableModel = new DefaultTableModel(cRow, cData);
        table1.setModel(tableModel);
        JScrollPane jScrollPane = new JScrollPane(table1);
        tablePane.add(jScrollPane);
        tablePane.setViewportView(table1);
    }

    public RManager() {

        initTable();

        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String accessKeyId = accessKeyIdText.getText();
                String accessKeySecret = accessKeySecretText.getText();
                if (accessKeyId.equals("") || accessKeySecret.equals("")) {
                    notice.setText("请检查AccessKeyID和AccessKeySecret!");
                    return;
                }
                queryButton.setText("正在查询");
                queryButton.setEnabled(false);
                AliOperator aliOperator = new AliOperator(accessKeyId, accessKeySecret);
                try {
//                    aliOperator.Connect();
                    DescribeInstancesResponse response = aliOperator.testConnect();
                    TableData tableData = new TableData();
                    tableData.updateTable(response, table1);
                    queryButton.setText("查询");
                    queryButton.setEnabled(true);
                } catch (ClientException clientException) {
                    queryButton.setText("查询");
                    queryButton.setEnabled(true);
                    clientException.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("RManager");
        frame.setContentPane(new RManager().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize(960, 720);
        frame.setVisible(true);
    }


}
