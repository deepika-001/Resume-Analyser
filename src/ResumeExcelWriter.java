import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ResumeExcelWriter {
    public static void main(String[] args) {
        // Dummy resume data
        String name = "Deepika Vetrivel";
        String email = "deepika@example.com";
        String skills = "Python, Java, Machine Learning";
        int score = 92;

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Resumes");

        // Create header row
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Name");
        header.createCell(1).setCellValue("Email");
        header.createCell(2).setCellValue("Skills");
        header.createCell(3).setCellValue("Score");

        // Create data row
        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(name);
        row.createCell(1).setCellValue(email);
        row.createCell(2).setCellValue(skills);
        row.createCell(3).setCellValue(score);

        try (FileOutputStream out = new FileOutputStream("resume_data.xlsx")) {
            workbook.write(out);
            workbook.close();
            System.out.println("✅ Resume data exported to Excel!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
