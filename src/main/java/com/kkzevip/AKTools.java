package com.kkzevip;

import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse;
import com.kkzevip.utils.AliOperator;
import com.kkzevip.utils.TableData;
import com.kkzevip.utils.TenOperator;
import com.tencentcloudapi.cvm.v20170312.models.Instance;
import com.tencentcloudapi.tat.v20201028.models.InvocationTask;
import org.apache.commons.codec.binary.Base64;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import java.util.List;

public class AKTools {

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
    private JComboBox cloudName;
    private JScrollPane scrollPane;
    private JLabel resultDisplay;
    //    private JLabel resultDisplay;
    private AliOperator aliOperator;
    private TenOperator tenOperator;
    public String currentCloud = "aliyun";

    public AKTools() {

        cloudName.addActionListener(e -> {
            int selid = cloudName.getSelectedIndex();
            String aid = "";
            String ast = "";
            switch (selid) {
                case 1:
                    aid = String.format("%-24s", "SecretID:");
                    ast = String.format("%-24s", "SecretKey:");
                    accessKeyIDLabel.setText(aid);
                    accessKeySecretLabel.setText(ast);
                    systemCom.removeAllItems();
                    systemCom.addItem("默认");
                    systemCom.addItem("SHELL");
                    systemCom.addItem("POWERSHELL");
                    currentCloud = "tencent";
                    break;
                default:
                    aid = String.format("%s", "AccessKeyID:");
                    ast = String.format("%s", "AccessKeySecret:");
                    accessKeyIDLabel.setText(aid);
                    accessKeySecretLabel.setText(ast);
                    systemCom.addItem("默认");
                    systemCom.addItem("Linux Shell Script");
                    systemCom.addItem("Windows Bat Script");
                    systemCom.addItem("Windows Powershell Script");
                    currentCloud = "aliyun";
            }
        });

        queryButton.addActionListener(e -> {
            notice.setText("正在查询...");
            describeTextArea.setAutoscrolls(true);
            SwingUtilities.invokeLater(() -> {
                String accessKeyId = accessKeyIdText.getText();
                String accessKeySecret = accessKeySecretText.getText();
                if (accessKeyId.equals("") || accessKeySecret.equals("")) {
                    String aid = "";
                    String ast = "";
                    switch (currentCloud) {
                        case "tencent":
                            aid = "SecretID";
                            ast = "SecretKey";
                            break;
                        case "aliyun":
                        default:
                            aid = "AccessKeyID";
                            ast = "AccessKeySecret";
                    }
                    String tips = String.format("请检查%s和%s!", aid, ast);
                    notice.setText(tips);
                    return;
                }
                switch (currentCloud) {
                    case "tencent":
                        tenOperator = new TenOperator(accessKeyId, accessKeySecret);
                        List<Instance> list = tenOperator.DescribeIns();
                        if (list == null) {
                            initTable();
                            notice.setText("SecretID或SecretKey有误！");
                        } else {
                            TableData tableData = new TableData(tenOperator.getMap());
                            tableData.updateTableTencet(list, this.table1);
                            notice.setText("查询完成");
                        }
                        break;
                    case "aliyun":
                    default:
                        aliOperator = new AliOperator(accessKeyId, accessKeySecret);
                        List<DescribeInstancesResponse.Instance> listaliyun = aliOperator.DescribeIns();
                        if (listaliyun == null) {
                            initTable();
                            notice.setText("AccessKeyID或AccessKeySecret有误！");
                        } else {
                            TableData tableData = new TableData(aliOperator.getMap());
                            tableData.updateTableA(listaliyun, this.table1);
                            notice.setText("查询完成");
                        }
                }
            });
        });
        table1.getSelectionModel().addListSelectionListener(e -> {
            int n = table1.getSelectedRow();
            try {
                String insID = table1.getValueAt(n, 1).toString();
                if (insID.equals("") || insID == null) {
                    return;
                }
                instanceText.setText(insID);
                StringBuilder text = new StringBuilder();
                TableData tableData = new TableData(null);
                Object[] cname = tableData.getInitColumns();

                for (int i = 0; i < 13; i++) {
                    text.append(cname[i].toString()).append(":\t").append(table1.getValueAt(n, i)).append("\n");
                }
                describeTextArea.setText(text.toString());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        execButton.addActionListener(e -> {
            notice.setText("命令执行中...");
            SwingUtilities.invokeLater(() -> {
                String comtype;
                int x = systemCom.getSelectedIndex();
                int n = table1.getSelectedRow();
                if (n == -1) {
                    notice.setText("请输入命令");
                    return;
                }
                String regionID = table1.getValueAt(n, 3).toString();
                String command = execText.getText();
                String insID = instanceText.getText();
                if (insID.equals("请输入实例ID，选中实例后自动填充")) {
                    notice.setText("请选择实例或手动输入实例ID");
                    return;
                }
                switch (currentCloud) {
                    case "tencent":
                        if (x == 2) {
                            comtype = "POWERSHELL";
                        } else {
                            comtype = "SHELL";
                        }
                        com.tencentcloudapi.tat.v20201028.models.DescribeInvocationTasksResponse responset = tenOperator.runCommand(regionID, insID, comtype, command);
                        if (responset != null) {
                            notice.setText("命令执行成功，RequestID: " + responset.getRequestId());
                            InvocationTask[] tasks = responset.getInvocationTaskSet();
                            StringBuilder stringBuilder = new StringBuilder();
                            for (InvocationTask task: tasks) {
                                String r = task.getTaskResult().getOutput();
                                stringBuilder.append(new String(Base64.decodeBase64(r)));
                                stringBuilder.append("\n");
                            }
                            describeTextArea.setText(stringBuilder.toString());
                        } else {
                            notice.setText("命令执行失败！");
                        }
                        break;
                    case "aliyun":
                    default:
                        switch (x) {
                            case 2:
                                comtype = "RunBatScript";
                                break;
                            case 3:
                                comtype = "RunPowerShellScript";
                                break;
                            default:
                                comtype = "RunShellScript";
                        }
                        HashMap<String, String> response = aliOperator.runCommand(regionID, insID, comtype, command);
                        if (response == null) {
                            notice.setText("命令执行失败！");
                        } else {
                            notice.setText("命令执行成功！" + response.get("status"));
                            describeTextArea.setText(response.get("result"));
                        }
                }

            });
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
        initTable();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("AKTools");
        AKTools akTools = new AKTools();
        akTools.rootPanel.setName("AKTools");
        frame.setContentPane(akTools.rootPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize(960, 640);
        frame.setVisible(true);
    }

    public void initTable() {

        TableData tableData = new TableData(null);
        final Object[] cData = tableData.getInitColumns();
        final Object[][] cRow = tableData.getInitData();
        final TableModel tableModel = new DefaultTableModel(cRow, cData);
        this.table1.setModel(tableModel);
        JScrollPane jScrollPane = new JScrollPane(this.table1);
        tablePane.add(jScrollPane);
        tablePane.setViewportView(table1);
        instanceText.setText("请输入实例ID，选中实例后自动填充");
        StringBuilder dtext = new StringBuilder();
        for (Object x : cData) {
            dtext.append(x.toString()).append(":\t\n");
        }
        describeTextArea.setText(dtext.toString());
    }

}
