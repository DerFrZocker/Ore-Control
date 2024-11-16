package de.derfrzocker.feature.api;

import de.derfrzocker.feature.api.util.SaveAble;

import java.util.Optional;

public class ExtraValues implements SaveAble {

    private Optional<Boolean> generateBigOreVeins = Optional.empty();

    private boolean dirty;

    public ExtraValues() {
    }

    public ExtraValues(Optional<Boolean> generateBigOreVeins) {
        this.generateBigOreVeins = generateBigOreVeins;
    }

    public Optional<Boolean> shouldGeneratedBigOreVeins() {
        return generateBigOreVeins;
    }

    public void setGenerateBigOreVeins(Optional<Boolean> generateBigOreVeins) {
        this.generateBigOreVeins = generateBigOreVeins;
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void saved() {
        dirty = false;
    }
}
