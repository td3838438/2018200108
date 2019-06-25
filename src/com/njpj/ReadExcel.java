package com.njpj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * @Author: liuyanxu
 * @Description:
 * @Date: Create in 2018/12/30 7:16
 */
public class ReadExcel { ;

  public static List<String> readExcel(String filePath) throws IOException, BiffException{
    List list = new ArrayList();
    //创建输入流

    InputStream stream = new FileInputStream(filePath);
    //获取Excel文件对象

    Workbook  rwb = Workbook.getWorkbook(stream);
    //获取文件的指定工作表 默认的第一个
    Sheet sheet = rwb.getSheet(0);
    //行数(表头的目录不需要，从1开始)
    for(int i=0; i<sheet.getRows(); i++){
      //创建一个数组 用来存储每一列的值
      //String[] str = new String[sheet.getColumns()];
      String str="";
      Cell cell = null;
      //列数
      cell = sheet.getCell(0,i);
      str =cell.getContents();
      /*for(int j=0; j<sheet.getColumns(); j++){
        //获取第i行，第j列的值

        str[j] = cell.getContents();
      }*/
      //把刚获取的列存入list
      //System.out.println(str);
      list.add(str);
    }
    return list;
  }
}
