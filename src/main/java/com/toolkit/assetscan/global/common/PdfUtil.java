package com.toolkit.assetscan.global.common;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.toolkit.assetscan.bean.dto.ExcelDataDto;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Date;
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

        BaseFont baseFont = BaseFont.createFont("simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);

        Font font = new Font(baseFont, 14, Font.NORMAL);

        //创建文件
        Document doc = new Document(new RectangleReadOnly(842F,595F));  // 横版
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
        //设置列宽
        if (8 == colNum) {
            float[] columnWidths = { 1f, 1f, 1f, 2f, 1f, 1f, 2f, 4f };
            table.setWidths(columnWidths);
        }

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

    public static void saveReportPDF(HttpServletResponse response, String fileName, ExcelDataDto data) throws Exception{
        Date now = new Date();
        // 告诉浏览器用什么软件可以打开此文件
        response.setContentType("application/pdf;charset=UTF-8");
        // 下载文件的默认名称
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".pdf", "utf-8"));

        ServletOutputStream out = response.getOutputStream();
        BaseFont baseFont = BaseFont.createFont("simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        //创建文件
        Document doc = new Document(PageSize.A4);
        //建立一个书写器
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        //打开文件
        doc.open();

        Font font10 = new Font(baseFont, 10, Font.NORMAL);   // 10号字体
        Font font14 = new Font(baseFont, 14, Font.NORMAL);   // 14号字体
        Font font18 = new Font(baseFont, 18, Font.NORMAL);   // 18号字体
        Font font28 = new Font(baseFont, 18, Font.BOLD);     // 28号加粗字体

        Paragraph titleName = new Paragraph(fileName, font28);
        titleName.setAlignment(Element.ALIGN_CENTER);  // 居中
        doc.add(titleName);

        Paragraph reportTime = new Paragraph("报告生成时间：" + DateFormat.dateToString(now, DateFormat.UTIL_FORMAT), font14);  //报告时间
        reportTime.setAlignment(Element.ALIGN_RIGHT);  // 居右
        doc.add(reportTime);
        doc.add(new Chunk(new LineSeparator(2.0F, 100.0F, null, 1, 0.0F)));  // 单实线


//        任务名称、检测目标、目标IP、问题类型、危害等级、问题描述、检测时间、建议方案
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100); // 宽度100%填充

        List<PdfPRow> listRow = table.getRows();

        PdfPCell cel1 = new PdfPCell(new Paragraph("系统信息:Linux", font14));
        cel1.disableBorderSide(15);
        table.addCell(cel1);
        PdfPCell cel2 = new PdfPCell(new Paragraph("系统版本:18.0.4", font14));
        cel2.disableBorderSide(15);
        table.addCell(cel2);
        PdfPCell cel3 = new PdfPCell(new Paragraph("IP:127.0.0.1", font14));
        cel3.disableBorderSide(15);
        table.addCell(cel3);
        PdfPCell cel4 = new PdfPCell(new Paragraph("问题数:20", font14));
        cel4.disableBorderSide(15);
        table.addCell(cel4);
        PdfPCell cel5 = new PdfPCell(new Paragraph("检测时间:2019-07-08 15:00", font14));
        cel5.disableBorderSide(15);
        table.addCell(cel5);

        PdfPCell cel6 = new PdfPCell(new Paragraph("检测时间:", font14));
        cel6.disableBorderSide(15);
        table.addCell(cel6);
//        把表格添加到文件中
        doc.add(table);

        doc.add(new Chunk(new DottedLineSeparator()));

        PdfPTable table2 = new PdfPTable(1);
        table2.setWidthPercentage(100); // 宽度100%填充

        List<PdfPRow> listRow2 = table2.getRows();

        PdfPCell cel21 = new PdfPCell(new Paragraph("问题描述：", font18));
        cel21.disableBorderSide(15);
        table2.addCell(cel21);

        String ss = "问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述";
        PdfPCell cel22 = new PdfPCell(new Paragraph(ss, font14));
        cel22.disableBorderSide(15);
        table2.addCell(cel22);
//        把表格添加到文件中
        doc.add(table2);


        doc.add(new Chunk(new LineSeparator(2.0F, 100.0F, null, 1, 0.0F)));  // 单实线
        Paragraph jieshu = new Paragraph("报告结束", font10);
        jieshu.setAlignment(Element.ALIGN_CENTER);  // 居中
        doc.add(jieshu);

        //关闭文档
        doc.close();
        //关闭书写器
        writer.close();
        out.close();

    }
}
