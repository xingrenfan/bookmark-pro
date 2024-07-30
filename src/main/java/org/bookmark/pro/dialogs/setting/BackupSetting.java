package org.bookmark.pro.dialogs.setting;

import org.bookmark.pro.context.BookmarkRunService;
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
        backUpPath.setText(BookmarkRunService.getBookmarkSettings().getBackUp());
        // TODO 变更之后移动文件到新目录
        /*backUpPath.addActionListener(e -> {
        });*/
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
        // 备份间隔时间
        SpinnerModel lineEndModel = new SpinnerNumberModel(BookmarkRunService.getBookmarkSettings().getShowNum().intValue(), 1, 720, 1);
        BookmarkEditorUtil.addNumberFormatter(backUpTime, lineEndModel);
        autoBackup.setSelected(BookmarkRunService.getBookmarkSettings().getAutoBackup());
        backUpTime.setEnabled(autoBackup.isSelected());
        backUpTime.setValue(Integer.valueOf(BookmarkRunService.getBookmarkSettings().getBackUpTime()));
        autoBackup.addChangeListener(e -> {
            backUpTime.setEnabled(autoBackup.isSelected());
        });
    }

    protected void saveBackupSetting() {
        // 备份路径
        BookmarkRunService.getBookmarkSettings().setBackUP(backUpPath.getText());
        // 备份时间
        BookmarkRunService.getBookmarkSettings().setBackUpTime(Objects.toString(backUpTime.getValue(), null));
        // 自动备份
        BookmarkRunService.getBookmarkSettings().setAutoBackup(autoBackup.isSelected());
    }
}
