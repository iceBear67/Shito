package shito.api.data;

import lombok.Data;

@Data
public class ShitoPreset {
    private final String id;
    private final String context;
    private final String description;
    private final String author;
}
