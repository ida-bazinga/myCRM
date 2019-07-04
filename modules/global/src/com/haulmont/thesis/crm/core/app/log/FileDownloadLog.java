/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.log;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Scanner;

//import java.nio.channels.Channels;
//import java.nio.channels.ReadableByteChannel;


@ManagedBean(IFileDowonloadLogMBean.NAME)
public class FileDownloadLog implements IFileDowonloadLogMBean {

    protected static Log log = LogFactory.getLog(FileDownloadLog.class);

    private String getPath() {
        String workDir = System.getProperty("catalina.home"); //..\deploy\tomcat
        String folderLog = AppBeans.get(Configuration.class).getConfig(CrmConfig.class).getFolderLog(); //по умолчанию парка "/logs/"
        String fullFilename = String.format("%s%s", workDir, folderLog);
        String pathToFile = fullFilename.replace("/", File.separator); //по умолчанию разделитель "/"; File.separator: Unix/Linux/MacOS: "/", Windows: "\"
        return pathToFile;
    }

    public String dowonloadLog(String fileName, String message) {
        String pathToFile = getPath();
        String filePath = String.format("%s%s.txt", pathToFile, fileName);
        // обновляем файл с помощью BufferedWriter
        appendUsingBufferedWriter(filePath, message);
        return "Данные записаны в файл";
    }

    // обновляем файл с помощью BufferedWriter
    private static void appendUsingBufferedWriter(String filePath, String text) {
        File file = new File(filePath);
        FileWriter fr = null;
        BufferedWriter br = null;
        //проверка на существование файла
        boolean isExistFile = file.exists();
        try {
            //для обновления файла нужно инициализировать FileWriter с помощью этого конструктора
            fr = new FileWriter(file,isExistFile);
            br = new BufferedWriter(fr);
            br.newLine();
            br.write(text);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally{
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    public String readFileScanner(String fileName) {
        try {
            String pathToFile = getPath();
            String filePath = String.format("%s%s.txt", pathToFile, fileName);
            Scanner scanner = new Scanner(Paths.get(filePath), StandardCharsets.UTF_8.name());
            //здесь мы можем использовать разделитель, например: "\\A", "\\Z" или "\\z"
            String data = scanner.useDelimiter("\\A").next();
            scanner.close();
            return data;
        } catch (FileNotFoundException e) {
            return e.getMessage();
        } catch (IOException e) {
            return e.getMessage();
        }
    }


    public String logsFiles() {
        try {
            String pathToFile = getPath();
            final String ext = ".txt";
            String stringListFile = "";
            File file = new File(pathToFile);
            if(!file.exists()) return String.format("%S папка не существует", pathToFile);
            File[] listFiles = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(ext);
                }
            });
            if(listFiles.length == 0){
                return String.format("%S не содержит файлов с расширением %s", pathToFile, ext);
            }else{
                for(File f : listFiles)
                    stringListFile = stringListFile + System.lineSeparator() + f.getName();
            }
            return String.format("Файлы в папке \"logs\": %s", stringListFile);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // качаем файл с помощью NIO
    /*
    public String downloadClientFile(String fileName) {
        try {
            String pathToFile = getPath();
            String filePathInputStream = String.format("%s%s.txt", pathToFile, fileName);
            String filePathOutputStream = String.format("C:/Downloads/%s.txt", fileName).replace("/", File.separator);
            ReadableByteChannel rbc = Channels.newChannel(new FileInputStream(filePathInputStream));
            FileOutputStream fos = new FileOutputStream(new File(filePathOutputStream));
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            return String.format("Файл загружен. Путь к файлу: %s", filePathOutputStream);
        } catch (IOException e) {
            return e.getMessage();
        }
    }    */

}
