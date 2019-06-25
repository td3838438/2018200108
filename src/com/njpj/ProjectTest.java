//E:\shiyan
//E:\刷\11.xls
package com.njpj;

import jxl.read.biff.BiffException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ProjectTest
{
    private JFrame frame = new JFrame("图片&Excel数据对比器"); //建立一个窗口对象frame，窗口标题为登录
    //定义一个页面标签的容器c，getContentPane()获得JFrame的内容版，窗口能显示的组件都显示在getContentPane()中
    private Container c = frame.getContentPane();
    //定义JTextField文本框型picAdress实现JTextField()对象

    private JTextField picAdress = new JTextField();

    //定义JTextField类型excAdress实现JTextField()对象
    private JTextField excAdress = new JTextField();

    //结果信息
    private JTextArea resultAddress = new JTextArea();

    //设置两个按钮，参数名为OK和cancel
    private JButton ok = new JButton("确定");
    private JButton cancel = new JButton("取消");
    private String picText = picAdress.getText();    //接收图片地址信息
    private String excText = excAdress.getText();    //接收Excel地址信息


    Vector<String> picList = new Vector<String>();
    Vector<String> excList = new Vector<String>();

    List<String> picArray = new ArrayList<>();
    List<String> execlArray = new ArrayList<>();

    public ProjectTest()  //构造器
    {
        //获取屏幕大小
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        frame.setSize(screenWidth / 2, screenHeight-200);
        c.setLayout(new BorderLayout());
        initFrame();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initFrame()
    {
        //顶部
        JPanel titlePanel = new JPanel(); //建立一个JPanel面板组件titlePanel
        titlePanel.setLayout(new FlowLayout()); //setLayout设置界面布局，此处用FlowLayout()布局
        titlePanel.add(new JLabel("图片&Excel对比器")); //JLabel()表示一个标签，本身用于显示信息
        c.add(titlePanel,"North"); //先已创建的Container容积c中添加JPanel组件，位置为北方

        //中部表单
        JPanel fieldPanel = new JPanel(); //建立一个JPanel面板组件fieldPanel
        fieldPanel.setLayout(null); //设置布局，设置为null
        JLabel l1 = new JLabel("图片文件夹地址："); //设置一个JLable标签l1，显示文字 图片地址
        l1.setBounds(50,20,150,20); //移动组件并调整大小，x，y为窗口的位置，之后两位为长宽
        JLabel l2 = new JLabel("Excel地址："); //设置一个JLable标签，显示文字 Excel地址
        l2.setBounds(50,100,150,20); //移动组件并调整大小
        JLabel l3 = new JLabel("结果："); //设置一个JLable标签，显结果信息
        l3.setBounds(50,180,150,20); //移动组件并调整大小
        fieldPanel.add(l3);
        //将标签l1，l2添加进容器fieldPanel
        fieldPanel.add(l1);
        fieldPanel.add(l2);
        //设置
        picAdress.setBounds(170,20,220,20); //设置文本框picAdress的位置和长宽
        excAdress.setBounds(170,100,220,20); //excel地址位置的长宽
        //画一个文本框显示内容
        resultAddress.setRows(3);
        resultAddress.setColumns(600);
        resultAddress.setBounds(170,180,700,800);
        resultAddress.setLineWrap(true);
        //resultAddress.setWrapStyleWord(true);
        //向容器fieldPanel中添加picAdress，excAdress标签
        fieldPanel.add(picAdress);
        fieldPanel.add(excAdress);
        fieldPanel.add(resultAddress);


        //将fieldPanel添加进container型容器c
        c.add(fieldPanel,"Center");

        //底部按钮
        JPanel buttonPanel = new JPanel(); //建立JPanel面板组件buttonPanel
        buttonPanel.setLayout(new FlowLayout()); //设置面板组件buttonPanel的界面布局，布局方式为FlowLayout()

        ok.setBorderPainted(false); //消除按钮边框
        cancel.setBorderPainted(false); //消除按钮边框

        cancel.addActionListener(event -> System.exit(0)); //使用取消按钮退出 （lambda用法）
        ok.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                picText = picAdress.getText();
                excText = excAdress.getText();
                picData(picText,picArray);
                try
                {
                    execlArray = ReadExcel.readExcel(excText);
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                catch (BiffException e1)
                {
                    e1.printStackTrace();
                }
                Map<Integer,List<String>> result= findListDiff1(picArray,execlArray);
                StringBuilder resultStr = new StringBuilder();
                if(result == null){
                    resultStr.append("查找不到任何信息");
                }else {
                    //取交集信息
                    List<String> union = result.get(0);
                    String sunion = String.join(",", union);
                    resultStr.append("交集信息:");
                    resultStr.append(sunion);
                    resultStr.append("\n");
                    //取图片独有的
                    String picDif = String.join(",", result.get(1));
                    resultStr.append("图片中独有信息:");
                    resultStr.append(picDif);
                    resultStr.append("\n");
                    //取excel独有的
                    String excelDif = String.join(",", result.get(2));
                    resultStr.append("excel中独有信息:");
                    resultStr.append(excelDif);
                    resultStr.append("\n");
                }
                resultAddress.setText(resultStr.toString());


            }
        });

        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        c.add(buttonPanel,"South");
    }

    public static void picData(String root, List<String> picArray) {
        File file = new File(root);
        File[] subFile = file.listFiles();
        for (int i = 0; i < subFile.length; i++) {
            if (subFile[i].isDirectory()) {
                picData(subFile[i].getAbsolutePath(), picArray);
            } else {
                String filename = subFile[i].getName();
                filename = filename.substring(0,filename.lastIndexOf("."));
                //System.out.println(filename);
                picArray.add(filename);
            }
        }
    }

    public Map<Integer,List<String>> findListDiff1(List<String> picArray,List<String> execlArray){
        Map<Integer,List<String>> resultMap = new HashMap<>();
        if(picArray.size() ==0 || execlArray.size()==0){
            return null;
        }else{
            List<String> Union = new ArrayList<>();
            Union.addAll(picArray);
            List<String> picDifference = new ArrayList<>();
            picDifference.addAll(picArray);
            List<String> execlDifference = new ArrayList<>();
            execlDifference.addAll(execlArray);
            Union.retainAll(execlArray);
            //并集信息输出
            System.out.println(Union);
            resultMap.put(0,Union);
            //照片与excel差集输出
            picDifference.removeAll(execlArray);
            System.out.println(picDifference);
            resultMap.put(1,picDifference);
            //excel与照片差集输出
            execlDifference.removeAll(picArray);
            System.out.println(execlDifference);
            resultMap.put(2,execlDifference);

            return resultMap;
        }

    }
    /**
     * 获取两个集合不同
     * @param rps1  rps1数据
     * @param rps2  rps2数据
     * @return  0:rps1中独有的数据;1:交集的数据;2:rps2中的独有数据
     */
}
