package cc.funkemunky.fiona.commands.fiona;

import cc.funkemunky.fiona.commands.FunkeCommand;
import cc.funkemunky.fiona.commands.fiona.args.*;

public class FionaCommand
        extends FunkeCommand {
    public FionaCommand() {
        super("fiona", "Fiona", "The main command for the Fiona Anticheat.", "fiona.help");
    }

    @Override
    public void addArguments() {
        getArguments().add(new AlertsArgument());
        getArguments().add(new ReloadArgument());
        getArguments().add(new ToggleArgument());
        getArguments().add(new ExecutableArgument());
        getArguments().add(new CancellableArgument());
        getArguments().add(new BroadcastArgument());
        getArguments().add(new StatusArgument());
        getArguments().add(new ViewArgument());
        getArguments().add(new DebugArgument());
        getArguments().add(new DelayArgument());
        getArguments().add(new ProfileArgument());
        getArguments().add(new UpdateConfigArgument());
        getArguments().add(new SetHungerArgument());
        getArguments().add(new LagArgument());
        getArguments().add(new VelocityArgument());
        getArguments().add(new BoxWandArgument());
        getArguments().add(new SaveArgument());
        getArguments().add(new MenuArgument());
        getArguments().add(new LogArgument());
        getArguments().add(new DevArgument("dev", "dev", "The dev arg"));

    }
}

