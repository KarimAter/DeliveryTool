package sample;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Utils {

    private static File defaultPath = new File(System.getProperty("user.home"));


    private static FileChooser getFileChooser(String extensions) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("." + extensions + " files", extensions);
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser;
    }

    private static void configureFileChooser(String title, final FileChooser fileChooser) {
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(defaultPath);

    }


    public static String loadExcelFile(Stage stage) {

        FileChooser fileChooser = getFileChooser("*.xls*");
        configureFileChooser("Load Excel File..", fileChooser);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            defaultPath = new File(file.getParentFile().getPath());
            return file.getPath();
        }
        return null;
    }

}
