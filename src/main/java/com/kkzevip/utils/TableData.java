package com.kkzevip.utils;

import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;
import java.util.Vector;

public class TableData {

    private Object[] initColumns = {"序号", "实例ID", "服务器地区", "主机名称", "系统版本", "运行状态", "私有IP", "公网IP", "安全组", "云助手", "主机配置", "创建时间", "过期时间"};
    private Object[][] initData = {
            {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"}
    };

    public void updateTable(DescribeInstancesResponse response, JTable jTable) {

//        String[][] cRows = {};
        Vector<Vector<String>> cRows = new Vector<Vector<String>>();
        int n = 1;
         for (DescribeInstancesResponse.Instance ins: response.getInstances()) {
             String privateIP = "";
             String publicIP = "";
             String secs = "";
             String configs = ins.getCpu() + "核处理器\n" + ins.getMemory() + "MB内存";
             for (String ip: ins.getVpcAttributes().getPrivateIpAddress()) {
                 privateIP = privateIP + ip + ",";
             }
             for (String ip: ins.getPublicIpAddress()) {
                 publicIP = publicIP + ip + ",";
             }
             for (String sec: ins.getSecurityGroupIds()) {
                 secs = secs + sec + ",";
             }
             Vector<String> cRow = new Vector<String>();
             cRow.add(String.valueOf(n));
             cRow.add(ins.getInstanceId());
             cRow.add(ins.getRegionId());
             cRow.add(ins.getInstanceName());
             cRow.add(ins.getOSName());
             cRow.add(ins.getStatus());
             cRow.add(privateIP);
             cRow.add(publicIP);
             cRow.add(secs);
             cRow.add("未知");
             cRow.add(configs);
             cRow.add(ins.getCreationTime());
             cRow.add(ins.getExpiredTime());

             cRows.add(cRow);
             n++;
//             System.out.println(ins.getInstanceId());
//             System.out.println(ins.getRegionId());
//             System.out.println(ins.getInstanceName());
//             System.out.println(ins.getOSName());
//             System.out.println(ins.getStatus());
//             System.out.println(ins.getVpcAttributes().getPrivateIpAddress());
//             System.out.println(ins.getVpcAttributes().getNatIpAddress());
//             System.out.println(ins.getSecurityGroupIds());
//             System.out.println(ins.getCpu());
//             System.out.println(ins.getMemory());
//             System.out.println(ins.getCreationTime());
//             System.out.println(ins.getExpiredTime());
         }
         Vector<String> title = new Vector<String>();
         for (int i=0; i<initColumns.length; i++) {
             title.add(initColumns[i].toString());
         }
         TableModel tableModel = new DefaultTableModel(cRows, title);
         jTable.setModel(tableModel);
    }

    public Object[] getInitColumns() {
        return initColumns;
    }

    public Object[][] getInitData() {
        return initData;
    }
}
