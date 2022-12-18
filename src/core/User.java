package core;

import java.io.Serializable;
import java.util.UUID;

public record User(UUID uuid, String username) implements Serializable {
}
