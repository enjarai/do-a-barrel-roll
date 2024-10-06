package nl.enjarai.doabarrelroll.util.key;

import nl.enjarai.doabarrelroll.api.key.InputContext;

import java.util.List;

public interface ContextualKeyBinding {
    void doABarrelRoll$addToContext(InputContext context);

    List<InputContext> doABarrelRoll$getContexts();
}
