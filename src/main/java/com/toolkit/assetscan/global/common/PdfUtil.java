package com.toolkit.assetscan.global.common;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.*;
import com.toolkit.assetscan.bean.dto.ExcelDataDto;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

public class PdfUtil {

    /**
     * 使用浏览器选择路径下载
     * @param response
     * @param fileName
     * @param data
     * @throws Exception
     */
    public static void exportPdf(HttpServletResponse response, String fileName, ExcelDataDto data) throws Exception {

        // 告诉浏览器用什么软件可以打开此文件
        response.setContentType("application/pdf;charset=UTF-8");
        // 下载文件的默认名称
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".pdf", "utf-8"));

        exportPdf(data, response.getOutputStream(), fileName);
    }

    private static void exportPdf(ExcelDataDto data, ServletOutputStream out, String fileName) throws Exception {

        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font font = new Font(baseFont, 14, Font.NORMAL);

        //创建文件
        Document doc = new Document();
        //建立一个书写器
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        //打开文件
        doc.open();

        Font font18 = new Font(baseFont, 18, Font.NORMAL);
        Paragraph titleName = new Paragraph(fileName, font18);
        titleName.setAlignment(Element.ALIGN_CENTER);
        //添加内容
        doc.add(titleName);


        List<String> titles = data.getTitles();
        List<List<Object>> rowsDatas = data.getRows();
        int rowNum = rowsDatas.size();
        int colNum = titles.size();

        // 3列的表.
        PdfPTable table = new PdfPTable(colNum);
        table.setWidthPercentage(100); // 宽度100%填充
        table.setSpacingBefore(10f); // 前间距
        table.setSpacingAfter(10f); // 后间距

        List<PdfPRow> listRow = table.getRows();
//        //设置列宽
//        float[] columnWidths = { 1f, 2f, 3f };
//        table.setWidths(columnWidths);


        // 表头
        PdfPCell cells1[]= new PdfPCell[colNum];
        PdfPRow row1 = new PdfPRow(cells1);
        int colIndex = 0;
        for (String field : titles) {
            // 设置单元格内容
            cells1[colIndex] = new PdfPCell(new Paragraph(field, font));
            colIndex++;
        }
        listRow.add(row1);


        // 数据
        for (int i=0; i < rowNum; i++) {
            List<Object> rowData = rowsDatas.get(i);

            //行
            PdfPCell cells[]= new PdfPCell[colNum];
            PdfPRow row = new PdfPRow(cells);

            colIndex = 0;
            for (Object cellData : rowData) {
                if (cellData != null) {
                    cells[colIndex] = new PdfPCell(new Paragraph(cellData.toString(), font));
                } else {
                    cells[colIndex] = new PdfPCell(new Paragraph(""));
                }
                colIndex++;
            }
            //把行添加到集合
            listRow.add(row);

        }

        //把表格添加到文件中
        doc.add(table);

        //关闭文档
        doc.close();
        //关闭书写器
        writer.close();
        out.close();

    }

}
