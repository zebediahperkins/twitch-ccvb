package main.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import main.util.BrowserUtils;
import main.util.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Controller {
    @FXML
    public Label chromeDriverPathLabel;
    @FXML
    public Button selectDriverButton;
    @FXML
    public Label accountsPathLabel;
    @FXML
    public Button selectAccountsButton;
    @FXML
    public Label proxiesPathLabel;
    @FXML
    public Button selectProxiesButton;
    @FXML
    public TextField botsPerIPField;
    @FXML
    public TextField botsTotalField;
    @FXML
    public RadioButton cvRButton;
    @FXML
    public TextField streamerNameField;
    @FXML
    public RadioButton accCreateRButton;
    @FXML
    public Button startButton;

    ToggleGroup functionalGroup = new ToggleGroup();


    @FXML
    public void selectDriverButtonClicked() {
        FileChooser driverChooser = new FileChooser();
        driverChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("executables", "*.exe"));
        File chromeDriverFile = driverChooser.showOpenDialog(selectDriverButton.getScene().getWindow());
        chromeDriverPathLabel.setText(chromeDriverFile.toString());
    }

    @FXML
    public void selectAccountsButtonClicked() {
        FileChooser accountsChooser = new FileChooser();
        accountsChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json", "*.json"));
        File accountsJSONFile = accountsChooser.showOpenDialog(selectDriverButton.getScene().getWindow());
        accountsPathLabel.setText(accountsJSONFile.toString());
    }

    @FXML
    public void selectProxiesButtonClicked() {
        FileChooser proxiesChooser = new FileChooser();
        proxiesChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("text", "*.txt"));
        File proxiesTXTFile = proxiesChooser.showOpenDialog(selectDriverButton.getScene().getWindow());
        proxiesPathLabel.setText(proxiesTXTFile.toString());
    }

    @FXML
    public void cvRButtonClicked() {
        streamerNameField.setDisable(false);
    }

    @FXML
    public void accCreateRButtonClicked() {
        streamerNameField.setDisable(true);
    }

    @FXML
    public void startButtonClicked() {
        if (chromeDriverPathLabel.getText().length() > 0
                && accountsPathLabel.getText().length() > 0
                && botsPerIPField.getText().length() > 0
                && botsTotalField.getText().length() > 0
                && (!functionalGroup.getSelectedToggle().equals(cvRButton) || streamerNameField.getText().length() > 0)
        ) {
            try {
                FileWriter profileWriter = new FileWriter("twitch-ccvb-profile.txt");
                profileWriter.write(chromeDriverPathLabel.getText());
                profileWriter.write("\n" + accountsPathLabel.getText());
                profileWriter.write("\n" + proxiesPathLabel.getText());
                profileWriter.write("\n" + botsPerIPField.getText());
                profileWriter.write("\n" + botsTotalField.getText());
                profileWriter.write("\n" + streamerNameField.getText());
                profileWriter.write("\nEOF");
                profileWriter.close();
                System.setProperty("webdriver.chrome.driver", chromeDriverPathLabel.getText());
                if (functionalGroup.getSelectedToggle().equals(cvRButton)) BrowserUtils.launchCVB(new File(accountsPathLabel.getText()), new File(proxiesPathLabel.getText()), Integer.parseInt(botsPerIPField.getText()), Integer.parseInt(botsTotalField.getText()), streamerNameField.getText());
                else BrowserUtils.launchAccCreator(new File(accountsPathLabel.getText()), new File(proxiesPathLabel.getText()), Integer.parseInt(botsPerIPField.getText()), Integer.parseInt(botsTotalField.getText()));
            } catch (IOException e) {e.printStackTrace();}
        }
        else JOptionPane.showMessageDialog(null, "Error: Please fill out all required fields", "Error", JOptionPane.INFORMATION_MESSAGE);
    }

    public void initialize() {
        cvRButton.setToggleGroup(functionalGroup);
        accCreateRButton.setToggleGroup(functionalGroup);
        functionalGroup.selectToggle(cvRButton);
        File profile = new File("twitch-ccvb-profile.txt");
        int fieldsCount = 6;
        try {
            if (!profile.createNewFile()) {
                Scanner profileReader = new Scanner(profile);
                if (FileUtils.getLineCount(profile) > fieldsCount) {
                    chromeDriverPathLabel.setText(profileReader.nextLine());
                    accountsPathLabel.setText(profileReader.nextLine());
                    proxiesPathLabel.setText(profileReader.nextLine());
                    botsPerIPField.setText(profileReader.nextLine());
                    botsTotalField.setText(profileReader.nextLine());
                    streamerNameField.setText(profileReader.nextLine());
                }
            }
        } catch (IOException e) {e.printStackTrace();}
    }
}
