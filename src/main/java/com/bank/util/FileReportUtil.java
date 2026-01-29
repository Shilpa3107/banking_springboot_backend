package com.bank.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileReportUtil {
    private static final String REPORT_FOLDER = "records";
    private static final String REPORT_FILE = REPORT_FOLDER + "/transaction_report.txt";

    static {
        File folder = new File(REPORT_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static void writeLine(String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(REPORT_FILE, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("File writer error: " + e.getMessage());
        }
    }
}
