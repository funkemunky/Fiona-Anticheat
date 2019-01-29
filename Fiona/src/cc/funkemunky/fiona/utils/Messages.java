package cc.funkemunky.fiona.utils;

import cc.funkemunky.fiona.Fiona;

public class Messages {

    /**
     * Global
     **/
    public String prefix;

    /**
     * Alerts
     **/
    public String alertMessage, alertHoverMessage, checkVioReset;

    /**
     * Commands
     **/
    public String invalidCheck, checkOptions, noPermission, invalidArguments;


    public Messages() {
        prefix = Color.translate(getString("prefix"));
        alertMessage = translateWithPrefix(getString("alerts.alertMessage"));
        alertHoverMessage = Color.translate(getString("alerts.alertHoverMessage"));
        checkVioReset = translateWithPrefix(getString("alerts.checkVioReset"));
        invalidArguments = translateWithPrefix(getString("commands.invalidArguments"));
        noPermission = translateWithPrefix(getString("commands.noPermission"));
        checkOptions = translateWithPrefix(getString("commands.checkOptions"));
        invalidCheck = translateWithPrefix("commands.invalidCheck");
    }

    public String translateWithPrefix(String string) {
        return Color.translate(string.replaceAll("%prefix%", prefix));
    }

    private String getString(String path) {
        return Fiona.getInstance().getMessages().getString(path);
    }
}

