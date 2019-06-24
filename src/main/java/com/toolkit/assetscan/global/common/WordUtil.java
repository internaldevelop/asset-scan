package com.toolkit.assetscan.global.common;

import com.toolkit.assetscan.bean.dto.ExcelDataDto;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.STTblWidthImpl;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.List;

public class WordUtil {

    /**
     * 使用浏览器选择路径下载
     * @param response
     * @param fileName
     * @param data
     * @throws Exception
     */
    public static void exportWord(HttpServletResponse response, String fileName, ExcelDataDto data) throws Exception {
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的默认名称
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".docx", "utf-8"));
        exportWord(data, response.getOutputStream(), fileName);
    }

    private static void exportWord(ExcelDataDto data, ServletOutputStream out, String fileName) throws Exception {

        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph titleName = doc.createParagraph(); // 创建段落
        titleName.setAlignment(ParagraphAlignment.CENTER);

        CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
        CTPageSz pgSz = sectPr.addNewPgSz();
        //设置横板
        pgSz.setW(BigInteger.valueOf(15840));
        pgSz.setH(BigInteger.valueOf(11907));
        pgSz.setOrient(STPageOrientation.LANDSCAPE);

        XWPFRun r1 = titleName.createRun(); // 创建段落文本
        r1.setFontSize(16);
        r1.setText(fileName); // 设置文本

        try {
            List<String> titles = data.getTitles();
            List<List<Object>> rowsDatas = data.getRows();
            int rowNum = rowsDatas.size();
            int colNum = titles.size();
            XWPFTable table = doc.createTable(rowNum + 1, colNum);
//            XWPFTable table = doc.createTable();
//            table.setWidthType(TableWidthType.AUTO);

            CTTblPr tblPr = table.getCTTbl().getTblPr() == null ? table.getCTTbl().addNewTblPr() : table.getCTTbl().getTblPr();
            CTTblLayoutType t = tblPr.isSetTblLayout()?tblPr.getTblLayout():tblPr.addNewTblLayout();
            t.setType(STTblLayoutType.FIXED);//使布局固定，不随内容改变宽度


            // 表头
            XWPFTableRow row0 = table.getRow(0);
            int colIndex = 0;
            for (String field : titles) {
                // 设置单元格内容
                row0.getCell(colIndex).setText(field);
                if (colIndex < 7) {
                    row0.getCell(colIndex).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1200));
                } else {
                    row0.getCell(colIndex).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(4000));
                }
                colIndex++;
            }

            // 数据
            for (int i=0; i < rowNum; i++) {
                List<Object> rowData = rowsDatas.get(i);
                // 获取到刚刚插入的行
                XWPFTableRow rows = table.getRow(i+1);

                colIndex = 0;
                for (Object cellData : rowData) {
                    if (cellData != null) {
                        rows.getCell(colIndex).setText(cellData.toString());
                    } else {
                        rows.getCell(colIndex).setText("");
                    }

                    colIndex++;
                }
            }

            doc.setTable(0, table);

            doc.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
