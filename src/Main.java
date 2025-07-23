import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Workbook workbook = new XSSFWorkbook();  // create workbook
        Sheet sheet = workbook.createSheet("Sheet1");  // create sheet

        Row row = sheet.createRow(0);  // create row
        Cell cell = row.createCell(0);  // create cell
        cell.setCellValue("Hello Dazzle!");  // set value

        try (FileOutputStream outputStream = new FileOutputStream("hello_dazzle.xlsx")) {
            workbook.write(outputStream);  // write file
            workbook.close();  // close workbook
            System.out.println("Excel file created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
