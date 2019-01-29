package cc.funkemunky.fiona.commands;

import cc.funkemunky.fiona.commands.fiona.FionaCommand;

import java.util.ArrayList;
import java.util.List;

public class FunkeCommandManager {
    public final List<FunkeCommand> commands;

    public FunkeCommandManager() {
        commands = new ArrayList<>();
        this.initialization();
    }

    private void initialization() {
        this.commands.add(new FionaCommand());
    }

    public void removeAllCommands() {
        commands.clear();
    }
}

