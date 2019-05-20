package com.toolkit.assetscan.global.common;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.*;
import com.toolkit.assetscan.bean.dto.ExcelDataDto;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URLEncoder;
import java.util.List;

public class HtmlUtil {

    /**
     * 使用浏览器选择路径下载
     * @param response
     * @param fileName
     * @param data
     * @throws Exception
     */
    public static void exportHtml(HttpServletResponse response, String fileName, ExcelDataDto data) throws Exception {

        // 告诉浏览器用什么软件可以打开此文件
        response.setContentType("application/html;charset=UTF-8");
        // 下载文件的默认名称
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".html", "utf-8"));

        exportHtml(data, response.getOutputStream(), fileName);
    }

    private static void exportHtml(ExcelDataDto data, ServletOutputStream out, String fileName) throws Exception {

        PrintStream fileCon = new PrintStream(out);
        fileCon.println("<!doctype html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>" + fileName + "</title></head><body>");

        List<String> titles = data.getTitles();
        List<List<Object>> rowsDatas = data.getRows();
        int rowNum = rowsDatas.size();
        int colNum = titles.size();

        fileCon.println("<h1 align=\"center\">" + fileName + "</h1>");
        fileCon.println("<table border='1' cellpadding=\"0\" cellspacing=\"0\" align=\"center\">");
        fileCon.println("<tr>");

        for (String field : titles) {
            // 设置单元格内容
            fileCon.println("<td>" + field + "</td>");
        }
        fileCon.println("</tr>");

        // 数据
        for (int i=0; i < rowNum; i++) {
            List<Object> rowData = rowsDatas.get(i);
            fileCon.println("<tr>");
            for (Object cellData : rowData) {
                if (cellData != null) {
                    fileCon.println("<td>" + cellData.toString() + "</td>");
                } else {
                    fileCon.println("<td></td>");

                }
            }
            fileCon.println("</tr>");
        }
        fileCon.println("</body></html>");


    }

}
