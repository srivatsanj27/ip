public class Ace {
    public static void main(String[] args) {
        Ui ui = new Ui();
        TaskManager taskManager = new TaskManager();

        ui.showWelcome();
        Storage.load(taskManager);

        while (true) {
            String input = ui.readCommand();
            ui.showLine();

            try {
                boolean isExit = Parser.parseCommand(input, taskManager, ui);
                if (isExit) {
                    break;
                }
            } catch (AceException exception) {
                ui.showError(exception.getMessage());
            }

            ui.showLine();
        }
    }
}