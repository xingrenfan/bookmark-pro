package org.bookmark.pro.windows.setting;

import org.bookmark.pro.service.base.settings.BackupSettings;
import org.bookmark.pro.utils.BookmarkEditorUtil;

import javax.swing.*;
import java.util.Objects;

public class BackupSetting {
    protected JPanel mainPane;
    protected JButton backupButton;
    protected JTextField backUpPath;
    protected JCheckBox autoBackup;
    protected JSpinner backUpTime;

    public BackupSetting(JPanel mainPane, JButton backupButton, JTextField backUpPath, JCheckBox autoBackup, JSpinner backUpTime) {
        this.mainPane = mainPane;
        this.backupButton = backupButton;
        this.backUpPath = backUpPath;
        this.autoBackup = autoBackup;
        this.backUpTime = backUpTime;
    }

    protected void initBackupSettings() {
        BackupSettings backupSettings = BackupSettings.getInstance();

        backUpPath.setText(backupSettings.getBackUp());
        // 备份间隔时间
        SpinnerModel backupTimeInterval = new SpinnerNumberModel(Integer.valueOf(backupSettings.getBackUpTime()).intValue(), 1, 720, 1);
        BookmarkEditorUtil.addNumberFormatter(backUpTime, backupTimeInterval);
        autoBackup.setSelected(backupSettings.getAutoBackup());
        backUpTime.setEnabled(autoBackup.isSelected());
        backUpTime.setValue(Integer.valueOf(backupSettings.getBackUpTime()));

        autoBackup.addChangeListener(e -> {
            backUpTime.setEnabled(autoBackup.isSelected());
        });
        backupButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(backUpPath.getText());
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int approve = fileChooser.showOpenDialog(mainPane);
            String dbFilePath;
            if (approve == JFileChooser.APPROVE_OPTION) {
                dbFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                backUpPath.setText(dbFilePath);
            }
        });
        // TODO 变更之后移动文件到新目录
        /*backUpPath.addActionListener(e -> {
        });*/
    }

    protected void saveBackupSetting() {
        BackupSettings backupSettings = BackupSettings.getInstance();
        // 备份路径
        backupSettings.setBackUP(backUpPath.getText());
        // 备份时间
        backupSettings.setBackUpTime(Objects.toString(backUpTime.getValue(), null));
        // 自动备份
        backupSettings.setAutoBackup(autoBackup.isSelected());
    }
}
