package com.example.sicapweb.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcellExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<HashMap<String,Object>> list;

    private String nomeArquivo;

    public ExcellExporter(List<HashMap<String,Object>> list,String nome) {
        this.list = list;
        workbook = new XSSFWorkbook();
        this.nomeArquivo=nome;
    }

    public void export(HttpServletResponse response) throws IOException {
        populaPlanilha();
        response.setContentType("application/vnd.ms-excel");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename="+ nomeArquivo + ".xlsx";
        response.setHeader(headerKey, headerValue);
        response.setHeader("filename", this.nomeArquivo);



        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }


    private void populaPlanilha(){
        XSSFSheet spreadsheet = workbook.createSheet(Objects.requireNonNullElse(nomeArquivo,"planilha"));
        XSSFRow linhaNova =  spreadsheet.createRow(0);
        if (list.size()>0){

            CellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setFontHeight(16);
            style.setFont(font);
            int indexDados= 0;
            for (Map.Entry entry : list.get(0).entrySet()){
                Cell cell = linhaNova.createCell(indexDados);
                cell.setCellValue((String)entry.getKey());
                cell.setCellStyle(style);
                indexDados++;
            }

            for ( int linhaAtual=0;linhaAtual < list.size() ;linhaAtual++ ){
                indexDados= 0;
                linhaNova =  spreadsheet.createRow(linhaAtual+1);
                for (Map.Entry entry : list.get(linhaAtual).entrySet()){
                    Cell cell = linhaNova.createCell(indexDados);
                    cell.setCellValue(Objects.requireNonNullElse(entry.getValue(),"").toString());
                    indexDados++;
                }
            }
        }
    }
}
