package com.toolkit.assetscan.global.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.toolkit.assetscan.bean.dto.AssetScanRecordDto;
import com.toolkit.assetscan.bean.dto.ExcelDataDto;
import com.toolkit.assetscan.bean.po.AssetNetWorkPo;
import com.toolkit.assetscan.bean.po.AssetPerfDataPo;
import com.toolkit.assetscan.bean.po.AssetPo;
import com.toolkit.assetscan.bean.po.ConfigCheckResultPo;
import com.toolkit.assetscan.global.utils.StringUtils;
import sun.plugin2.os.windows.Windows;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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

    public static void savePerfReportPDF(HttpServletResponse response, String fileName, AssetPo assetPo, AssetNetWorkPo anwPo, AssetPerfDataPo apInfo, JSONObject assetMesg) throws Exception{
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
        Font font12 = new Font(baseFont, 12, Font.NORMAL);   // 10号字体
        Font font14 = new Font(baseFont, 14, Font.NORMAL);   // 14号字体
        Font font16 = new Font(baseFont, 16, Font.NORMAL);   // 18号字体
        Font font18 = new Font(baseFont, 18, Font.NORMAL);   // 18号字体
        Font font28 = new Font(baseFont, 18, Font.BOLD);     // 28号加粗字体

        Paragraph titleName = new Paragraph(fileName, font28);
        titleName.setAlignment(Element.ALIGN_CENTER);  // 居中
        doc.add(titleName);

        Paragraph reportTime = new Paragraph("报告生成时间：" + DateFormat.dateToString(now, DateFormat.UTIL_FORMAT), font14);  //报告时间
        reportTime.setAlignment(Element.ALIGN_RIGHT);  // 居右
        doc.add(reportTime);
        doc.add(new Chunk(new LineSeparator(2.0F, 100.0F, null, 1, 0.0F)));  // 单实线

        String assetIP = "";
        if (null != assetPo) {
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100); // 宽度100%填充

            PdfPCell cel1 = new PdfPCell(new Paragraph("资产名称:" + assetPo.getName(), font14));
            cel1.disableBorderSide(15);
            table.addCell(cel1);
            PdfPCell cel2 = new PdfPCell(new Paragraph("系统信息:" + ("1".equals(assetPo.getOs_type()) ? "Windows": "Linux"), font14));
            cel2.disableBorderSide(15);
            table.addCell(cel2);
            PdfPCell cel3 = new PdfPCell(new Paragraph("系统版本:" + assetPo.getOs_ver(), font14));
            cel3.disableBorderSide(15);
            table.addCell(cel3);
            PdfPCell cel4 = new PdfPCell(new Paragraph("IP:" + (assetIP = assetPo.getIp()), font14));
            cel4.disableBorderSide(15);
            table.addCell(cel4);

//            把表格添加到文件中
            doc.add(table);
        }

        if ( null != anwPo) {
            doc.add(new Chunk(new DottedLineSeparator()));  // 虚线

            PdfPTable table3 = new PdfPTable(1);
            table3.setWidthPercentage(100); // 宽度100%填充

            String connectFlag = "";
            String connectIp = "";
            if ( StringUtils.isValid(connectIp = anwPo.getConnect_ip()) && StringUtils.isValid(connectFlag = anwPo.getConnect_flag()) ) {
                PdfPCell cel1 = new PdfPCell(new Paragraph("与IP(" + connectIp + ")" + ("1".equals(connectFlag) ? "是" : "未") + "连通", font14));
                cel1.disableBorderSide(15);
                table3.addCell(cel1);
            }

            String urlDuration = "";
            if (StringUtils.isValid( urlDuration = anwPo.getUrl_duration())) {
                PdfPCell cel2 = new PdfPCell(new Paragraph("该URL(" + anwPo.getUrl() + ")访问时长是" + urlDuration, font14));
                cel2.disableBorderSide(15);
                table3.addCell(cel2);
            }

            String delay = "";
            if (StringUtils.isValid( delay = anwPo.getDelay())) {
                PdfPCell cel3 = new PdfPCell(new Paragraph("网络延时时长:" + delay, font14));
                cel3.disableBorderSide(15);
                table3.addCell(cel3);
            }

            String throughput = "";
            if ( StringUtils.isValid( throughput = anwPo.getThroughput())) {
                PdfPCell cel4 = new PdfPCell(new Paragraph("吞吐量为:" + throughput, font14));
                cel4.disableBorderSide(15);
                table3.addCell(cel4);
            }

            String bandWidth = "";
            if ( StringUtils.isValid(bandWidth = anwPo.getBandwidth())) {
                PdfPCell cel5 = new PdfPCell(new Paragraph("带宽容量:" + bandWidth, font14));
                cel5.disableBorderSide(15);
                table3.addCell(cel5);
            }

//            把表格添加到文件中
            doc.add(table3);
        }

        if (null != apInfo) {
            doc.add(new Chunk(new DottedLineSeparator()));  // 虚线

            PdfPTable table2 = new PdfPTable(2);
            table2.setWidthPercentage(100); // 宽度100%填充

            Double cupUsed = Double.parseDouble(apInfo.getCpu_used_percent());
            String format1 = String.format("%.3f", cupUsed);
            PdfPCell cel8 = new PdfPCell(new Paragraph("CPU 使用率:" + String.format("%.3f", cupUsed) + "%", font14));
            cel8.disableBorderSide(15);
            table2.addCell(cel8);
            PdfPCell cel9 = new PdfPCell(new Paragraph("CPU 空闲率:" + String.format("%.3f", 100 - cupUsed) + "%", font14));
            cel9.disableBorderSide(15);
            table2.addCell(cel9);

            Double memoryUsed = Double.parseDouble(apInfo.getMemory_used_percent());
            PdfPCell cel10 = new PdfPCell(new Paragraph("内存使用率:" + String.format("%.3f", memoryUsed) + "%", font14));
            cel10.disableBorderSide(15);
            table2.addCell(cel10);
            PdfPCell cel11 = new PdfPCell(new Paragraph("内存空闲率:" + String.format("%.3f", 100 - memoryUsed) + "%", font14));
            cel11.disableBorderSide(15);
            table2.addCell(cel11);

            Double diskUsed = Double.parseDouble(apInfo.getDisk_used_percent());
            PdfPCell cel12 = new PdfPCell(new Paragraph("磁盘使用率:" + String.format("%.3f", diskUsed) + "%", font14));
            cel12.disableBorderSide(15);
            table2.addCell(cel12);
            PdfPCell cel13 = new PdfPCell(new Paragraph("磁盘空闲率:" + String.format("%.3f", 100 - diskUsed) + "%", font14));
            cel13.disableBorderSide(15);
            table2.addCell(cel13);

            doc.add(table2);

        }

        if (StringUtils.isValid(assetIP) && null != assetMesg) {
            doc.add(new Chunk(new DottedLineSeparator()));  // 虚线

            Paragraph letterTitle = new Paragraph("磁盘详情", font16);
            letterTitle.setAlignment(Element.ALIGN_LEFT);  // 居中
            doc.add(letterTitle);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100); // 宽度100%填充
            float[] columnWidths = { 3f, 1f, 1f, 1f, 1f, 3f };
            table.setWidths(columnWidths);

            JSONArray infos = (JSONArray) assetMesg.get("FS");   // 磁盘

            String[] tits = {"文件系统", "容量", "已用", "可用", "已用%", "挂载点"};

            for (String tit : tits) {
                PdfPCell cell = new PdfPCell(new Paragraph(tit, font14));
                cell.disableBorderSide(15);
                table.addCell(cell);
            }

            for (Iterator it = infos.iterator(); it.hasNext(); ) {
                JSONObject fs = (JSONObject) it.next();

                if (fs.getIntValue("type") == 2) {

                    PdfPCell cel1 = new PdfPCell(new Paragraph(fs.getString("devName"), font12));
                    cel1.disableBorderSide(15);
                    table.addCell(cel1);

                    double total = Double.parseDouble(fs.getString("total")) / (1024 * 1024);
                    double used = Double.parseDouble(fs.getString("used")) / (1024 * 1024);

                    PdfPCell cel2 = new PdfPCell(new Paragraph(String.format("%.2f", total) + "G", font12));
                    cel2.disableBorderSide(15);
                    table.addCell(cel2);
                    PdfPCell cel3 = new PdfPCell(new Paragraph(String.format("%.2f", used) + "G", font12));
                    cel3.disableBorderSide(15);
                    table.addCell(cel3);
                    PdfPCell cel4 = new PdfPCell(new Paragraph(String.format("%.2f", (total - used)) + "G", font12));
                    cel4.disableBorderSide(15);
                    table.addCell(cel4);
                    PdfPCell cel5 = new PdfPCell(new Paragraph(String.format("%.2f", (used * 100 / total)) + "%", font12));
                    cel5.disableBorderSide(15);
                    table.addCell(cel5);
                    PdfPCell cel6 = new PdfPCell(new Paragraph(fs.getString("dirName"), font12));
                    cel6.disableBorderSide(15);
                    table.addCell(cel6);

                }

            }
            doc.add(table);

            doc.add(new Chunk(new DottedLineSeparator()));  // 虚线

            PdfPTable table1 = new PdfPTable(5);
            table1.setWidthPercentage(100); // 宽度100%填充
            float[] t1ColumnWidths = { 0.3f, 2f, 2f, 2f, 1f };
            table1.setWidths(t1ColumnWidths);

            String[] titsCpuMem = {"", "CPU占用率排行", "", "内存占用率排行", ""};

            for (String tit : titsCpuMem) {
                PdfPCell cell = new PdfPCell(new Paragraph(tit, font16));
                cell.disableBorderSide(15);
                table1.addCell(cell);
            }

            JSONArray infoCpus = (JSONArray) assetMesg.get("Proc CPU Ranking");   // 资源占有率
            JSONArray infoMeM = (JSONArray) assetMesg.get("Proc Memory Ranking");   // 资源占有率

            int sizeCpu = infoCpus.size();
            int sizeMem = infoMeM.size();
            for (int i=0; i<10; i++) {

                PdfPCell cel = new PdfPCell(new Paragraph((i+1) +"", font12));
                cel.disableBorderSide(15);

                boolean sFlag = false;
                PdfPCell cel1 = new PdfPCell(new Paragraph("", font12));
                if (sizeCpu > i){
                    JSONObject fsCpu = (JSONObject)infoCpus.get(i);
                    cel1 = new PdfPCell(new Paragraph(fsCpu.getString("name"), font12));
                    sFlag = true;
                }
                cel1.disableBorderSide(15);

                PdfPCell cel2 = new PdfPCell(new Paragraph("", font12));
                if (sizeCpu > i){
                    JSONObject fsCpu = (JSONObject)infoCpus.get(i);
                    cel2 = new PdfPCell(new Paragraph(String.format("%.2f", Double.parseDouble(fsCpu.getString("percent")) * 100) + "%", font12));
                }
                cel2.disableBorderSide(15);

                PdfPCell cel3 = new PdfPCell(new Paragraph("", font12));
                if (sizeMem > i){
                    JSONObject fsMem = (JSONObject)infoMeM.get(i);
                    cel3 = new PdfPCell(new Paragraph(fsMem.getString("name"), font12));
                    sFlag = true;
                }
                cel3.disableBorderSide(15);

                PdfPCell cel4 = new PdfPCell(new Paragraph("", font12));
                if (sizeMem > i){
                    JSONObject fsMem = (JSONObject)infoMeM.get(i);
                    cel4 = new PdfPCell(new Paragraph(String.format("%.2f", Double.parseDouble(fsMem.getString("percent")) * 100) + "%", font12));
                }
                cel4.disableBorderSide(15);
                if (sFlag) {
                    table1.addCell(cel);
                    table1.addCell(cel1);
                    table1.addCell(cel2);
                    table1.addCell(cel3);
                    table1.addCell(cel4);
                }

            }
            doc.add(table1);

        }

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

    public static String saveReportPDF(String fileName, String userAccount, List<ConfigCheckResultPo> resultPos,
                                       int riskCount, AssetPo assetPo, JSONObject jsonMsg) throws Exception{
        if(assetPo == null) {
            return null;
        }
        BaseFont baseFont = BaseFont.createFont("simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        //创建文件
        Document doc = new Document(PageSize.A4);
        // 文件名
        SimpleDateFormat sdfName = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        String dateName = sdfName.format(new Date());
        String date = sdfDate.format(new Date());
        File currentPath = new File("");
        File currentDir = new File(currentPath.getAbsolutePath(), "reports");
        if (!currentDir.exists()) {
            currentDir.mkdir();
        }
        File file = new File(currentDir, fileName + dateName + ".pdf");
        String pathName = file.getAbsolutePath();
        //建立一个书写器
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        //打开文件
        doc.open();

        Font font10 = new Font(baseFont, 10, Font.NORMAL);   // 10号字体
        Font font12 = new Font(baseFont, 12, Font.NORMAL);   // 12号字体
        Font font14 = new Font(baseFont, 14, Font.NORMAL);   // 14号字体
        Font font18 = new Font(baseFont, 18, Font.NORMAL);   // 18号字体
        Font font20Bold  = new Font(baseFont, 20, Font.BOLD);    // 20号加粗

        Paragraph coverTitle = new Paragraph("\n\n\n" + "资产扫描核查报告", font20Bold);
        coverTitle.setAlignment(Element.ALIGN_CENTER);
        doc.add(coverTitle);
        Paragraph coverdate = new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n\n" + "核查日期：" + date, font14);
        Paragraph coverchecker = new Paragraph("核查人员：" + userAccount, font14);
        coverdate.setIndentationLeft(180f);
        coverchecker.setIndentationLeft(180f);
        doc.add(coverdate);
        doc.add(coverchecker);
        Paragraph coverPage = new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + "中国电力科学研究院", font10);
        coverPage.setAlignment(Element.ALIGN_CENTER);
        doc.add(coverPage);

        doc.newPage();
        Paragraph paragraph = new Paragraph("1.基本信息", font18);
        paragraph.setSpacingAfter(10);
        doc.add(paragraph);

        Paragraph paragraph1 = new Paragraph("本次一共核查" + resultPos.size() + "项安全配置，其中存在风险的一共" +
                riskCount + "项，安全配置得分为" + (int)((((float)(resultPos.size() - riskCount)) / resultPos.size()) * 100) + "分。下面是资产基本信息：", font14);
        paragraph1.setFirstLineIndent(24);
        paragraph1.setSpacingAfter(20);
        doc.add(paragraph1);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100); // 宽度100%填充

        PdfPCell cel0 = new PdfPCell(new Paragraph("资产名称: " + assetPo.getName(), font12));
        cel0.disableBorderSide(15);
        table.addCell(cel0);

        String osType = assetPo.getOs_type().equals("1") ? "Windows":"Linux";
        PdfPCell cel1 = new PdfPCell(new Paragraph("系统类型: " + osType, font12));
        cel1.disableBorderSide(15);
        table.addCell(cel1);

        PdfPCell cel2 = new PdfPCell(new Paragraph("系统版本: " + assetPo.getOs_ver(), font12));
        cel2.disableBorderSide(15);
        table.addCell(cel2);

        PdfPCell cel3 = new PdfPCell(new Paragraph("资产IP: " + assetPo.getIp(), font12));
        cel3.disableBorderSide(15);
        table.addCell(cel3);

        if (jsonMsg != null) {
            JSONArray CPUConfigs = jsonMsg.getJSONArray("CPU");
            JSONObject MemConfigs = (JSONObject)(jsonMsg.get("Memory"));
            JSONArray NetConfigs = jsonMsg.getJSONArray("Net Config");
            if (CPUConfigs != null && CPUConfigs.size() > 0) {
                String vendor = ((JSONObject) CPUConfigs.get(0)).get("vendor").toString();
                String model = ((JSONObject) CPUConfigs.get(0)).get("model").toString();
                String mhz = ((JSONObject) CPUConfigs.get(0)).get("mhz").toString();
                String cacheSize = ((JSONObject) CPUConfigs.get(0)).get("cacheSize").toString();
                String totalCores = ((JSONObject) CPUConfigs.get(0)).get("totalCores").toString();
                String totalSockets = ((JSONObject) CPUConfigs.get(0)).get("totalSockets").toString();
                String coresPerSocket = ((JSONObject) CPUConfigs.get(0)).get("coresPerSocket").toString();
                PdfPCell cel4 = new PdfPCell(new Paragraph("CPU核: " + totalCores + "核", font12));
                cel4.disableBorderSide(15);
                table.addCell(cel4);
                PdfPCell cel5 = new PdfPCell(new Paragraph("制造商: " + vendor, font12));
                cel5.disableBorderSide(15);
                table.addCell(cel5);
                PdfPCell cel6 = new PdfPCell(new Paragraph("型号: " + model, font12));
                cel6.disableBorderSide(15);
                table.addCell(cel6);
                PdfPCell cel7 = new PdfPCell(new Paragraph("主频: " + mhz, font12));
                cel7.disableBorderSide(15);
                table.addCell(cel7);
            }
            if (MemConfigs != null) {
                String total = MemConfigs.get("total").toString();
                String used = MemConfigs.get("used").toString();
                String free = MemConfigs.get("free").toString();
                String actualUsed = MemConfigs.get("actualUsed").toString();
                String actualFree = MemConfigs.get("actualFree").toString();
                String usedPercent = MemConfigs.get("usedPercent").toString();
                String freePercent = MemConfigs.get("freePercent").toString();
                PdfPCell cel8 = new PdfPCell(new Paragraph("内存总量: " + getFormatCapacity(total), font12));
                cel8.disableBorderSide(15);
                table.addCell(cel8);
                PdfPCell cel9 = new PdfPCell(new Paragraph("已用占比: " + getFormatPercent(usedPercent), font12));
                cel9.disableBorderSide(15);
                table.addCell(cel9);
            }
            if (NetConfigs != null && NetConfigs.size() > 0) {
                String name = ((JSONObject) NetConfigs.get(0)).get("name").toString();
                String hwaddr = ((JSONObject) NetConfigs.get(0)).get("hwaddr").toString();
                String type = ((JSONObject) NetConfigs.get(0)).get("type").toString();
                String description = ((JSONObject) NetConfigs.get(0)).get("description").toString();
                String address = ((JSONObject) NetConfigs.get(0)).get("address").toString();

                PdfPCell cel10 = new PdfPCell(new Paragraph("MAC地址: " + hwaddr, font12));
                cel10.disableBorderSide(15);
                table.addCell(cel10);
                PdfPCell cel11 = new PdfPCell(new Paragraph("网络类型: " + type + "\n\n\n", font12));
                cel11.disableBorderSide(15);
                table.addCell(cel11);
            }
        }
        doc.add(table);

        List<ConfigCheckResultPo> iptablesResultPos = new ArrayList();
        List<ConfigCheckResultPo> syslogResultPos = new ArrayList();
        List<ConfigCheckResultPo> loginResultPos = new ArrayList();
        List<ConfigCheckResultPo> servicesResultPos = new ArrayList();
        List<ConfigCheckResultPo> passowrdResultPos = new ArrayList();
        List<ConfigCheckResultPo> accountsResultPos = new ArrayList();
        List<ConfigCheckResultPo> startupResultPos = new ArrayList();
        for(ConfigCheckResultPo configCheckResultPo: resultPos) {
            if (configCheckResultPo.getConfig_type().equals("startup")) {
                startupResultPos.add(configCheckResultPo);
            } else if (configCheckResultPo.getConfig_type().equals("accounts")) {
                accountsResultPos.add(configCheckResultPo);
            } else if (configCheckResultPo.getConfig_type().equals("services")) {
                servicesResultPos.add(configCheckResultPo);
            } else if (configCheckResultPo.getConfig_type().equals("passowrd")) {
                passowrdResultPos.add(configCheckResultPo);
            } else if (configCheckResultPo.getConfig_type().equals("login")) {
                loginResultPos.add(configCheckResultPo);
            } else if (configCheckResultPo.getConfig_type().equals("syslog")) {
                syslogResultPos.add(configCheckResultPo);
            } else if (configCheckResultPo.getConfig_type().equals("iptables")) {
                iptablesResultPos.add(configCheckResultPo);
            }
        }

        Rectangle rt=doc.getPageSize();  //横向显示
        doc.setPageSize(rt);
        Paragraph paragraph2 = new Paragraph("2.开机安全配置核查详情", font18);
        paragraph2.setSpacingAfter(10);
        doc.add(paragraph2);
        doc.add(getCheckTable(startupResultPos, font14, font10));

        Paragraph paragraph3 = new Paragraph("\n" + "3.账户安全配置核查详情", font18);
        paragraph3.setSpacingAfter(10);
        doc.add(paragraph3);
        doc.add(getCheckTable(accountsResultPos, font14, font10));

        Paragraph paragraph4 = new Paragraph("\n" + "4.密码配置核查详情", font18);
        paragraph4.setSpacingAfter(10);
        doc.add(paragraph4);
        doc.add(getCheckTable(passowrdResultPos, font14, font10));

        Paragraph paragraph5 = new Paragraph("\n" + "5.服务安全配置核查详情", font18);
        paragraph5.setSpacingAfter(10);
        doc.add(paragraph5);
        doc.add(getCheckTable(servicesResultPos, font14, font10));

        Paragraph paragraph6 = new Paragraph("\n" + "6.登录安全配置核查详情", font18);
        paragraph6.setSpacingAfter(10);
        doc.add(paragraph6);
        doc.add(getCheckTable(loginResultPos, font14, font10));

        Paragraph paragraph7 = new Paragraph("\n" + "7.日志安全配置核查详情", font18);
        paragraph7.setSpacingAfter(10);
        doc.add(paragraph7);
        doc.add(getCheckTable(syslogResultPos, font14, font10));

        Paragraph paragraph8 = new Paragraph("\n" + "8.iptables配置核查详情", font18);
        paragraph8.setSpacingAfter(10);
        doc.add(paragraph8);
        doc.add(getCheckTable(iptablesResultPos, font14, font10));

        //关闭文档
        doc.close();
        //关闭书写器
        writer.close();
        return pathName;
    }

    private static String getFormatCapacity(String capacityStr) {
        double capacity = Double.parseDouble(capacityStr);
        String result = "";
        if (capacity > 1073741824) {
            // GB
            result = String.valueOf((float)capacity / 1073741824);
            int trimIndex = result.length();
            if (result.indexOf(".") >= 0) {
                if (result.indexOf(".") + 2 > result.length()) {
                    trimIndex = result.indexOf(".") + 2;
                } else {
                    trimIndex = result.indexOf(".") + 3;
                }
            }
            result = result.substring(0, trimIndex) + " G";
        } else if (capacity > 1048576) {
            // MB
            result = String.valueOf((float)capacity / 1048576);
            int trimIndex = result.length();
            if (result.indexOf(".") >= 0) {
                if (result.indexOf(".") + 2 > result.length()) {
                    trimIndex = result.indexOf(".") + 2;
                } else {
                    trimIndex = result.indexOf(".") + 3;
                }
            }
            result = result.substring(0, trimIndex) + " M";
        } else if (capacity > 1024) {
            // KB
            result = String.valueOf((float)capacity / 1024);
            int trimIndex = result.length();
            if (result.indexOf(".") >= 0) {
                if (result.indexOf(".") + 2 > result.length()) {
                    trimIndex = result.indexOf(".") + 2;
                } else {
                    trimIndex = result.indexOf(".") + 3;
                }
            }
            result = result.substring(0, trimIndex) + " K";
        } else {
            result = capacityStr + " ";
        }

        return result + "B";
    }

    private static String getFormatPercent(String result) {
        if (result != null) {
            int trimIndex = result.length();
            if (result.indexOf(".") >= 0) {
                if (result.indexOf(".") + 2 > result.length()) {
                    trimIndex = result.indexOf(".") + 2;
                } else {
                    trimIndex = result.indexOf(".") + 3;
                }
            }
            result = result.substring(0, trimIndex) + " %";
        }
        return result;
    }

    private static PdfPTable getCheckTable(List<ConfigCheckResultPo> resultPos, Font headFont, Font cellFont) throws Exception{
        PdfPTable table = new PdfPTable(26);
        table.setSpacingBefore(10);
        table.addCell(createHCell("编号", 2, headFont));
        table.addCell(createHCell("检查项", 4, headFont));
        table.addCell(createHCell("检查结果", 4, headFont));
        table.addCell(createHCell("漏洞等级", 4, headFont));
        table.addCell(createHCell("是否通过", 4, headFont));
        table.addCell(createHCell("风险描述", 4, headFont));
        table.addCell(createHCell("解决方案", 4, headFont));

        String pass = "通过";
        for (int i = 0; i < resultPos.size(); i++) {
            if (resultPos.get(i).getRisk_level() > 0) {
                pass = "不通过";
            } else {
                pass = "通过";
            }
            table.addCell(createCell((i + 1) + "", 1, 2, cellFont));
            table.addCell(createCell(resultPos.get(i).getCheck_item(), 1, 4, cellFont));
            table.addCell(createCell(resultPos.get(i).getConfig_info(), 1, 4, cellFont));
            table.addCell(createCell(String.valueOf(resultPos.get(i).getRisk_level()), 1, 4, cellFont));
            table.addCell(createCell(pass, 1, 4, cellFont));
            table.addCell(createCell(resultPos.get(i).getRisk_desc(), 1, 4, cellFont));
            table.addCell(createCell(resultPos.get(i).getSolution(), 1, 4, cellFont));
        }
        return table;
    }

    private static PdfPCell createCell(String content, int rowspan, int colspan, Font f) throws Exception{
        PdfPCell cell = new PdfPCell(new Paragraph(content, f));
        cell.setRowspan(rowspan);
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private static PdfPCell createHCell(String content, int colspan, Font f) throws Exception{
        PdfPCell cell = new PdfPCell(new Paragraph(content, f));
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        return cell;
    }
}
