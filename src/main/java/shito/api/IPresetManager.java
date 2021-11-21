package shito.api;

import shito.api.data.ShitoPreset;

import java.util.Collection;
import java.util.List;

public interface IPresetManager {
    Collection<? extends ShitoPreset> listPresets();
    void savePreset(ShitoPreset preset);
    void removePreset(ShitoPreset preset);
    ShitoPreset getPresetById(String id);
    boolean hasPreset(String id);
    void savePresets();
}
