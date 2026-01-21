package com.odde.atddv2;

import com.github.leeonky.jfactory.DataRepository;
import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.util.Classes;

public class EntityFactory extends JFactory {

    public EntityFactory(DataRepository dataRepository) {
        super(dataRepository);
        configFactory();
    }

    private void configFactory() {
        Classes.subTypesOf(Spec.class, "com.odde.atddv2.spec").forEach(c -> register((Class) c));
    }
}
