package de.derfrzocker.feature.api;

import de.derfrzocker.feature.api.util.MessageTraversAble;
import de.derfrzocker.feature.api.util.SaveAble;

public interface RuleTest extends MessageTraversAble, SaveAble, Cloneable {

    RuleTestType getType();

    RuleTest clone();
}
