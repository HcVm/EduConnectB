package com.EduConnectB.app.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
public class BackupService {
    
    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);

    private static final String APPLICATION_NAME = "EduConnect BackupApp";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/etc/secrets/keysgdrive";

    private String railwayDbHost = "viaduct.proxy.rlwy.net";
    private String railwayDbPort = "25703";
    private String railwayDbName = "railway";
    private String railwayDbUsername = "root";
    private String railwayDbPassword = "JeNKbAmTufQVxgvcKBvqJVFGfxiGHRRN";

    @Scheduled(cron = "0 0 0 */15 * ?")
    public void performBackup() {
        logger.info("Iniciando proceso de backup de Railway");

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFileName = "backup_railway_" + timestamp + ".sql";

        try {
            Path tempDir = Files.createTempDirectory("railway_backups");
            String fullBackupPath = tempDir.resolve(backupFileName).toString();

            try (Connection connection = DriverManager.getConnection(
                    String.format("jdbc:mysql://%s:%s/%s?useSSL=true&requireSSL=true&verifyServerCertificate=false&connectTimeout=5000", 
                                  railwayDbHost, railwayDbPort, railwayDbName), 
                    railwayDbUsername, railwayDbPassword);

                 Statement stmt = connection.createStatement();
                 PrintWriter out = new PrintWriter(fullBackupPath)) {

                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet tables = metaData.getTables(null, null, "%", new String[] {"TABLE"});

                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    
                    if (tableName.equals("sys_config")) {
                        continue; 
                    }

                    logger.info("Respaldando tabla: {}", tableName);

                    ResultSet tableStructureRs = stmt.executeQuery("SHOW CREATE TABLE " + tableName);
                    tableStructureRs.next();
                    out.println(tableStructureRs.getString(2) + ";"); 

                    ResultSet tableDataRs = stmt.executeQuery("SELECT * FROM " + tableName);
                    while (tableDataRs.next()) {
                        String rowData = buildInsertStatement(tableName, tableDataRs);
                        out.println(rowData + ";");
                    }
                }
                
                logger.info("Backup de Railway creado exitosamente: {}", backupFileName);
                out.close();
                uploadToDrive(fullBackupPath);
            } catch (SQLException e) {
                logger.error("Error al realizar el backup de Railway", e);
            }

            Files.deleteIfExists(Paths.get(fullBackupPath));
        } catch (IOException e) {
            logger.error("Error al crear el directorio temporal o al eliminar el archivo de backup", e);
        }
    }
    
    private String buildInsertStatement(String tableName, ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(tableName).append(" VALUES (");

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            if (i > 1) sb.append(", ");
            sb.append(formatValue(rs.getObject(i), metaData.getColumnType(i)));
        }

        sb.append(")");
        return sb.toString();
    }
    
    private String formatValue(Object value, int sqlType) {
        if (value == null) {
            return "NULL";
        } else if (sqlType == Types.VARCHAR || sqlType == Types.CHAR || sqlType == Types.LONGVARCHAR) {
            return "'" + value.toString().replace("'", "''") + "'";
        } else {
            return value.toString();
        }
    }

    private void uploadToDrive(String filePath) {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                    .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Drive service = new Drive.Builder(httpTransport, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            File fileMetadata = new File();
            fileMetadata.setName(filePath.substring(filePath.lastIndexOf("/") + 1));

            java.io.File fileContent = new java.io.File(filePath);
            FileContent mediaContent = new FileContent("application/octet-stream", fileContent);

            File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            logger.info("File uploaded to Drive. File ID: {}", file.getId());
        } catch (IOException | java.security.GeneralSecurityException e) {
            logger.error("Error al subir el archivo a Google Drive", e);
        }
    }
}