package io.github._4drian3d.unsignedvelocity.configuration;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public interface Configuration {
    boolean removeSignedKey();

    boolean removeSignedCommandInformation();

    static Configuration loadConfig(final Path path) throws IOException {
        final Path configPath = loadFiles(path);
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setPath(configPath)
                .build();

        final CommentedConfigurationNode loaded = loader.load();

        final boolean removeSignedKey = loaded.getNode("remove-signed-key-on-join")
                .getBoolean(false);
        final boolean removeSigneCommandInformation = loaded.getNode("remove-signed-command-information")
                .getBoolean(false);

        return new Configuration() {
            @Override
            public boolean removeSignedKey() {
                return removeSignedKey;
            }

            @Override
            public boolean removeSignedCommandInformation() {
                return removeSigneCommandInformation;
            }
        };
    }

    private static Path loadFiles(Path path) throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectory(path);
        }
        final Path configPath = path.resolve("config.conf");
        if (Files.notExists(configPath)) {
            try (var stream = Configuration.class.getClassLoader().getResourceAsStream("config.conf")) {
                Files.copy(Objects.requireNonNull(stream), configPath);
            }
        }
        return configPath;
    }
}
