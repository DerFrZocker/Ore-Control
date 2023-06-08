package de.derfrzocker.feature.api;

import de.derfrzocker.feature.api.util.traverser.message.MessageTraversAble;
import de.derfrzocker.feature.api.util.SaveAble;

public interface RuleTest extends MessageTraversAble, LocatedAble, SaveAble, Cloneable {

    RuleTestType getType();

    RuleTest clone();
}
