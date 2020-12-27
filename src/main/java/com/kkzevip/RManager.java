package com.kkzevip;

import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse;
import com.aliyuncs.ecs.model.v20140526.InvokeCommandResponse;
import com.aliyuncs.exceptions.ClientException;
import com.kkzevip.utils.AliOperator;
import com.kkzevip.utils.TableData;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.AsyncBoxView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

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
    private JButton execButton;
    private JTextField instanceText;
    private JComboBox systemCom;
    private JTextField execText;
    private JTextArea describeTextArea;
    private AliOperator aliOperator;

    public void initTable() {
        TableData tableData = new TableData(null);
        Object[] cData = tableData.getInitColumns();
        Object[][] cRow = tableData.getInitData();
        TableModel tableModel = new DefaultTableModel(cRow, cData);
        table1.setModel(tableModel);
        JScrollPane jScrollPane = new JScrollPane(table1);
        tablePane.add(jScrollPane);
        tablePane.setViewportView(table1);
        instanceText.setText("请输入实例ID，选中实例后自动填充");
        StringBuilder dtext = new StringBuilder();
        for (Object x: cData) {
            dtext.append(x.toString()).append(":\t\n");
        }
        describeTextArea.setText(dtext.toString());
    }

    public RManager() {

        initTable();

        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notice.setText("正在查询...");
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        String accessKeyId = accessKeyIdText.getText();
                        String accessKeySecret = accessKeySecretText.getText();
                        if (accessKeyId.equals("") || accessKeySecret.equals("")) {
                            notice.setText("请检查AccessKeyID和AccessKeySecret!");
                            return;
                        }
                        aliOperator = new AliOperator(accessKeyId, accessKeySecret);

                        if (aliOperator.equals(null)) {
                            notice.setText("请检查AccessKeyID和AccessKeySecret!");
                        } else {
                            try {
//                                List<DescribeInstancesResponse.Instance> list = aliOperator.DescribeIns();
                                List<DescribeInstancesResponse.Instance> list = aliOperator.testConnect();
                                TableData tableData = new TableData(aliOperator.getMap());
                                tableData.updateTable(list, table1);
                            } catch (ClientException clientException) {
                                clientException.printStackTrace();
                            } finally {
                                notice.setText("查询完成");
                            }
                        }
                    }
                });
            }
        });
        table1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int n = table1.getSelectedRow();
                String insID = table1.getValueAt(n, 1).toString();
                instanceText.setText(insID);
                StringBuilder text = new StringBuilder();
                TableData tableData = new TableData(null);
                Object[] cname = tableData.getInitColumns();

                for (int i=0; i<13; i++) {
                    text.append(cname[i].toString()).append(":\t").append(table1.getValueAt(n, i)).append("\n");
                }
                describeTextArea.setText(text.toString().toString());
            }
        });
        execButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notice.setText("命令执行中...");
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        String comtype = "";
                        int x = systemCom.getSelectedIndex();
                        int n = table1.getSelectedRow();
                        String regionID = table1.getValueAt(n, 3).toString();
                        String command = execText.getText();
                        String insID = instanceText.getText();
                        switch (x) {
                            case 2:
                                comtype = "RunBatScript";
                            case 3:
                                comtype = "RunPowerShellScript";
                            default:
                                comtype = "RunShellScript";
                        }
                        String commandID = aliOperator.createCommand(regionID, comtype, command);
                        if (commandID != null) {
                            InvokeCommandResponse response = aliOperator.invokeCommand(regionID, insID, commandID);
                            if (response != null) {
                                notice.setText("命令执行成功，RequestID: " + response.getRequestId());
                            } else {
                                notice.setText("命令执行失败！");
                            }
                        }
                    }
                });
            }
        });
        instanceText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (instanceText.getText().equals("请输入实例ID，选中实例后自动填充")) {
                    instanceText.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (instanceText.getText().equals("")) {
                    instanceText.setText("请输入实例ID，选中实例后自动填充");
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
