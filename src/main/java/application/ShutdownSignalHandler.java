package application;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class ShutdownSignalHandler implements SignalHandler {

    // Static method to install the signal handler
    static void install(String signalName, SignalHandler handler) {
        Signal signal = new Signal(signalName);
        ShutdownSignalHandler diagnosticSignalHandler = new ShutdownSignalHandler();
        SignalHandler oldHandler = Signal.handle(signal, diagnosticSignalHandler);
        diagnosticSignalHandler.setHandler(handler);
        diagnosticSignalHandler.setOldHandler(oldHandler);
    }
    private SignalHandler oldHandler;
    private SignalHandler handler;

    private ShutdownSignalHandler() {
    }

    private void setOldHandler(SignalHandler oldHandler) {
        this.oldHandler = oldHandler;
    }

    private void setHandler(SignalHandler handler) {
        this.handler = handler;
    }
    // Signal handler method
    @Override
    public void handle(Signal sig) {
        System.out.println("Diagnostic Signal handler called for signal " + sig);
        try {
            handler.handle(sig);

            // Chain back to previous handler, if one exists
            if (oldHandler != SIG_DFL && oldHandler != SIG_IGN) {
                oldHandler.handle(sig);
            }

        } catch (Exception e) {
            System.out.println("Signal handler failed, reason " + e);
        }
    }
}
